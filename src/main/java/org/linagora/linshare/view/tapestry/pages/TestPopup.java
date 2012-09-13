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

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.batches.ShareManagementBatch;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.impl.UserAndDomainMultiServiceImpl;
import org.linagora.linshare.view.tapestry.components.PasswordPopup;
import org.linagora.linshare.view.tapestry.components.WindowWithEffects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test showing how to use the PasswordPopup
 * @author ncharles
 *
 */
public class TestPopup {

	private static final Logger logger = LoggerFactory.getLogger(UserAndDomainMultiServiceImpl.class);
	
	@Inject
    private BusinessMessagesManagementService businessMessagesManagementService;
	
	@Inject
	private Messages messages;
	
	@Component
	private PasswordPopup passwordPopup; 

    
    private final String intendedPassword = "bob";
    
    @Inject
    private  UserService userService;
    
 
	@Component(parameters = {"style=dialog", "show=false","width=360", "height=335"})
	private WindowWithEffects dialog;
	
	@Component(parameters = {"style=alert", "show=false","width=360", "height=335"})
	private WindowWithEffects alert;
	
	@Component(parameters = {"style=alert_lite", "show=false","width=360", "height=335"})
	private WindowWithEffects alert_lite;
	
	@Component(parameters = {"style=alphacube", "show=false","width=360", "height=335"})
	private WindowWithEffects alphacube;

	@Component(parameters = {"style=mac_os_x", "show=false","width=360", "height=335"})
	private WindowWithEffects mac_os_x;
	
	@Component(parameters = {"style=blur_os_x", "show=false","width=360", "height=335"})
	private WindowWithEffects blur_os_x;
	
	@Component(parameters = {"style=mac_os_x_dialog", "show=false","width=360", "height=335"})
	private WindowWithEffects mac_os_x_dialog;
	
	@Component(parameters = {"style=nuncio", "show=false","width=360", "height=335"})
	private WindowWithEffects nuncio;
	
	@Component(parameters = {"style=spread", "show=false","width=360", "height=335"})
	private WindowWithEffects spread;
	
	@Component(parameters = {"style=darkX", "show=false","width=360", "height=335"})
	private WindowWithEffects darkX;
	
	@Component(parameters = {"style=greenlighting", "show=false","width=360", "height=335"})
	private WindowWithEffects greenlighting;
	
	@Component(parameters = {"style=bluelighting", "show=false","width=360", "height=335"})
	private WindowWithEffects bluelighting;
	
	@Component(parameters = {"style=greylighting", "show=false","width=360", "height=335"})
	private WindowWithEffects greylighting;
	
	@Component(parameters = {"style=darkbluelighting", "show=false","width=360", "height=335"})
	private WindowWithEffects darkbluelighting;
	
	Zone onValidateFormFromPasswordPopup()
	{

		if (intendedPassword.equals(passwordPopup.getPassword())) {
			passwordPopup.getFormPassword().clearErrors();
			return passwordPopup.formSuccess();
		} else {
			passwordPopup.getFormPassword().recordError(messages.get("testpopup.errormessage"));
			return passwordPopup.formFail();
		}	
		
	} 


	@Inject
	private ShareManagementBatch shareManagementBatch;
	
	@Inject
	private AnonymousUrlRepository anonymousUrlRepository;
	
	@Inject
	private DocumentEntryRepository documentEntryRepository;
	
	void onActionFromTest1()
    {
		logger.debug("begin method onActionFromTest1");
		shareManagementBatch.cleanOutdatedShares();
		logger.debug("endmethod onActionFromTest1");
    }
	
	
	void onActionFromTest2()
    {
		logger.debug("begin method onActionFromTest2");
		shareManagementBatch.notifyUpcomingOutdatedShares();
		
//		List<DocumentEntry> findAllExpiredEntries = documentEntryRepository.findAllExpiredEntries();
//		logger.debug("findAllExpiredEntries size : " + findAllExpiredEntries.size());
//		for (DocumentEntry documentEntry : findAllExpiredEntries) {
//			logger.debug("documentEntry found : " + documentEntry.getId() + ':' + documentEntry.getUuid());
//		}
//		logger.debug("endmethod onActionFromTest2");
    }
	
	
	
}
