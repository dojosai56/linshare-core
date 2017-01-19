/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml"
		})
public class MailContentBuildingServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{

	private static Logger logger = LoggerFactory.getLogger(MailContentBuildingServiceImplTest.class);

	@Autowired
	private MailBuildingService mailBuildingService;

	@Autowired
	private DomainBusinessService domainBusinessService;

	@Autowired
	private NotifierService notifierService;

	/**
	 * If you want send generated emails to a webmail, enable this field and
	 * configure file : src/test/resources/linshare-test.properties
	 * with properties :
	 * - mail.smtp.host=
	 * - mail.smtp.port=
	 */
	private boolean sendMail = false;

	private String recipientForSendMail = "bart.simpson@int1.linshare.dev";

	private void testMailGenerate(MailContainer mailContainer){
		Assert.assertNotNull(mailContainer);
		logger.debug("Subject: {}", mailContainer.getSubject());
		logger.debug("Content: {}", mailContainer.getContent());
		Assert.assertNotNull(mailContainer.getSubject());
		Assert.assertNotNull(mailContainer.getContent());
		Assert.assertFalse(mailContainer.getSubject().contains("${"));
		Assert.assertFalse(mailContainer.getSubject().contains("??"));
		Assert.assertFalse(mailContainer.getContent().contains("${"));
		Assert.assertFalse(mailContainer.getContent().contains("??"));
	}

	@Test
	public void testBuildMail() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		MailConfig cfg = domainBusinessService.getUniqueRootDomain().getCurrentMailConfiguration();
		for (MailContentType type : MailContentType.values()) {
			logger.info("Building mail {} ", type);
			if (mailBuildingService.fakeBuildIsSupported(type)){
				for (Language lang : Language.values()) {
					logger.info("Building mail {} with language {}", type, lang);
					List<ContextMetadata> contexts = mailBuildingService.getAvailableVariables(type);
					for (int flavor = 0; flavor < contexts.size(); flavor++) {
						MailContainerWithRecipient build = mailBuildingService.fakeBuild(type, cfg, lang, flavor);
						testMailGenerate(build);
						sendMail(build);
					}
				}
			} else {
				logger.warn("Building mail {} was skipped. Not yet supported ?", type);
			}
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	private void sendMail(MailContainerWithRecipient mail) {
		if (sendMail) {
			mail.setRecipient(recipientForSendMail);
			notifierService.sendNotification(mail);
		}
	}
}
