/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.view.tapestry.pages;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.SecuredUrlFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.view.tapestry.components.PasswordPopup;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Download {

	private static final Logger logger = LoggerFactory.getLogger(Download.class);
	
	/***************************************************************************
	 * Properties
	 **************************************************************************/
	@Persist
	private String email;

	@Persist
	private String password;

	@Property
	private boolean passwordProtected;

	@Property
	private List<DocumentVo> documents;

	@Property
	private DocumentVo document;

	@Property
	private Integer index;

	@Property
	@Persist
	private String uuid;

	/***************************************************************************
	 * Service injection
	 **************************************************************************/
	@InjectComponent
	private PasswordPopup passwordPopup;

	@Inject
	private Messages messages;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private JavaScriptSupport renderSupport;

	@Inject
	private BusinessMessagesManagementService messagesManagementService;
	
    @Inject
    private RequestGlobals requestGlobals;
	
	@Inject @Symbol("linshare.logo.webapp.visible")
	@Property
	private boolean linshareLogoVisible;

	
	
	@Inject
	private DocumentFacade documentFacade;
	
	@Inject
	private SecuredUrlFacade securedUrlFacade;
	
	
	
	public String[] getContext() {
		return new String[] { uuid, index.toString()};
	}
	
	public boolean getContainsDocuments() {
		if (documents == null) 
			return false;
		else {
			return documents.size() >  0;
		}
	}

	public StreamResponse onActivate(String anonymousUrlUuid, Integer documentId) {
		logger.debug("documenbtId : " + String.valueOf(documentId));
		setCurrentAnonymousUrlUuid(anonymousUrlUuid);

		try {
			checkUrl(anonymousUrlUuid);
			if(!passwordProtected){
				documents = securedUrlFacade.getDocuments(anonymousUrlUuid, password);
				DocumentVo doc = documents.get(documentId);
				final InputStream file = securedUrlFacade.retrieveFileStream(anonymousUrlUuid, doc.getIdentifier(), password);
				return new FileStreamResponse(doc, file); 
			} else {
				return null;
			}
			
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
		return null;
	}

	/**
	 * this is the first method called in this page.
	 * @param anonymousUrlUuid
	 */
	public void onActivate(String anonymousUrlUuid) {
		setCurrentAnonymousUrlUuid(anonymousUrlUuid);
		try {
			checkUrl(anonymousUrlUuid);
			documents = securedUrlFacade.getDocuments(anonymousUrlUuid, password);
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
	}

	private void setCurrentAnonymousUrlUuid(String uuid) {
		logger.debug("uuid : " + uuid);
		//check current one with the old one
		if(this.uuid!=null && !this.uuid.equals(uuid)){
			this.password = null; //reset cached password
		}
		this.uuid = uuid;
	}
	
	
	private void checkUrl(String uuid) {
		try {
			if (!securedUrlFacade.exists(uuid, componentResources.getPageName().toLowerCase())) {
				String msg = "secure url does not exists";
				logger.error(msg);
				throw new BusinessException(BusinessErrorCode.WRONG_URL, msg);
			}
			this.passwordProtected = (securedUrlFacade.isPasswordProtected(uuid) && password == null);
			if (!securedUrlFacade.isValid(uuid, password)) {
				String msg = "the secured url is not valid";
				logger.error(msg);
				throw new BusinessException(msg);
			}
	
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
	}

	public StreamResponse onActionFromDownloadThemAll(String anonymousUrlUuid) throws IOException, BusinessException{
		
		setCurrentAnonymousUrlUuid(anonymousUrlUuid);
		try {
			checkUrl(anonymousUrlUuid);
			documents = securedUrlFacade.getDocuments(anonymousUrlUuid, password);
		
			if(!passwordProtected){
				return securedUrlFacade.retrieveArchiveZipStream(anonymousUrlUuid, password);
			}
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
		return null;
	}
	
	
	public Zone onValidateFormFromPasswordPopup() {
		if (securedUrlFacade.isValid(uuid, passwordPopup.getPassword())) {
			password = passwordPopup.getPassword();
			return passwordPopup.formSuccess();
		} else {
			passwordPopup
					.getFormPassword()
					.recordError(
							messages
									.get("components.download.passwordPopup.error.message"));
			return passwordPopup.formFail();
		}
	}

	@AfterRender
	void afterRender() {
		if (passwordProtected)
			renderSupport.addScript("window_passwordPopup.showCenter(true)");
	}
	
	public String getFriendlySize() {
		return FileUtils.getFriendlySize(document.getSize(), messages);
	}

}
