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
package org.linagora.linshare.batches;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.linagora.linshare.utils.LoggerParent;

// TODO: Full refactoring needed ! Tests are not human readable, neither understandable ! :(
@ExtendWith(SpringExtension.class)
@Transactional
@Sql({
	"/import-tests-operationHistory.sql",
	"/import-tests-quota.sql",
	"/import-tests-stat.sql" })
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml"})
public class DailyBatchTest extends LoggerParent {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	@Qualifier("statisticDailyUserBatch")
	private GenericBatch dailyUserBatch;

	@Autowired
	@Qualifier("statisticDailyDomainBatch")
	private GenericBatch dailyDomainBatch;

	@Autowired
	private UserDailyStatBusinessService userdailyStatBusinessService;

	@Autowired
	private AccountQuotaBusinessService accountQuotaBusinessService;

	@Autowired
	private DomainQuotaBusinessService domainQuotaBusinessService;

	@Autowired
	private ContainerQuotaBusinessService ensembleQuotaBusinessService;

	@Autowired
	private ThreadDailyStatBusinessService threadDailyStatBusinessService;

	@Autowired
	@Qualifier("statisticDailyThreadBatch")
	private GenericBatch dailyThreadBatch;
	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private OperationHistoryBusinessService operationHistoryBusinessService;

	@Autowired
	private BatchHistoryBusinessService batchHistoryBusinessService;

	LoadingServiceTestDatas dates;
	private User jane;

	@BeforeEach
	public void setUp (){
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test() {
		BatchRunContext batchRunContext = new BatchRunContext();
		List<String> listThreadIdentifier = dailyThreadBatch.getAll(batchRunContext);
		assertEquals(2, listThreadIdentifier.size());

		WorkGroup workGroup1 = (WorkGroup) accountRepository.findByLsUuid(listThreadIdentifier.get(0));
		WorkGroup workGroup2 = (WorkGroup) accountRepository.findByLsUuid(listThreadIdentifier.get(1));

		List<OperationHistory> listOperationHistory = operationHistoryBusinessService.find(workGroup1, null, null, new Date());
		assertEquals(3, listOperationHistory.size());

		listOperationHistory = operationHistoryBusinessService.find(workGroup2, null, null, new Date());
		assertEquals(2, listOperationHistory.size());

		dailyThreadBatch.execute(batchRunContext, workGroup1.getLsUuid(), listThreadIdentifier.size(), 0);

		Calendar d = Calendar.getInstance();
		d.add(Calendar.DATE, -1);
		List<ThreadDailyStat> listThreaddailyStat = threadDailyStatBusinessService.findBetweenTwoDates(workGroup1, d.getTime(), new Date());

		assertEquals(1, listThreaddailyStat.size());
		ThreadDailyStat threadDailyStat = listThreaddailyStat.get(0);
		assertEquals(workGroup1, threadDailyStat.getAccount());
		assertEquals(3, (long) threadDailyStat.getOperationCount());
		assertEquals(1000, (long) threadDailyStat.getActualOperationSum());
		assertEquals(2, (long) threadDailyStat.getCreateOperationCount());
		assertEquals(600, (long) threadDailyStat.getCreateOperationSum());
		assertEquals(1, (long) threadDailyStat.getDeleteOperationCount());
		assertEquals(-300, (long) threadDailyStat.getDeleteOperationSum());
		assertEquals(300, (long) threadDailyStat.getDiffOperationSum());

		AccountQuota quota = accountQuotaBusinessService.find(workGroup1);
		assertNotNull(quota);
		assertEquals(1000, (long) quota.getCurrentValue());
		assertEquals(700, (long) quota.getLastValue());
		assertEquals(1000, (long) quota.getQuota());
		assertEquals(800, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getMaxFileSize());

		dailyThreadBatch.execute(batchRunContext, workGroup2.getLsUuid(), listThreadIdentifier.size(), 1);

		batchHistoryBusinessService.findByUuid(workGroup2.getLsUuid());

		listThreaddailyStat = threadDailyStatBusinessService.findBetweenTwoDates(workGroup2, d.getTime(), new Date());

		assertEquals(1, listThreaddailyStat.size());
		threadDailyStat = listThreaddailyStat.get(0);
		assertEquals(workGroup2, threadDailyStat.getAccount());
		assertEquals(2, (long) threadDailyStat.getOperationCount());
		assertEquals(900, (long) threadDailyStat.getActualOperationSum());
		assertEquals(2, (long) threadDailyStat.getCreateOperationCount());
		assertEquals(400, (long) threadDailyStat.getCreateOperationSum());
		assertEquals(0, (long) threadDailyStat.getDeleteOperationCount());
		assertEquals(0, (long) threadDailyStat.getDeleteOperationSum());
		assertEquals(400, (long) threadDailyStat.getDiffOperationSum());

		quota = accountQuotaBusinessService.find(workGroup2);
		assertNotNull(quota);
		assertEquals(900, (long) quota.getCurrentValue());
		assertEquals(500, (long) quota.getLastValue());
		assertEquals(1300, (long) quota.getQuota());
		assertEquals(1000, (long) quota.getQuotaWarning());
		assertEquals(6, (long) quota.getMaxFileSize());

		listOperationHistory = operationHistoryBusinessService.find(jane, null, null, new Date());
		assertNotEquals(0, listOperationHistory.size());

		dailyUserBatch.execute(batchRunContext, jane.getLsUuid(), 10, 1);

		List<UserDailyStat> listUserdailyStat = userdailyStatBusinessService.findBetweenTwoDates(jane, d.getTime(), new Date());

		assertEquals(1, listUserdailyStat.size());
		UserDailyStat userDailyStat = listUserdailyStat.get(0);
		assertEquals(jane, userDailyStat.getAccount());
		assertEquals(3, (long) userDailyStat.getOperationCount());
		assertEquals(1100, (long) userDailyStat.getActualOperationSum());
		assertEquals(2, (long) userDailyStat.getCreateOperationCount());
		assertEquals(600, (long) userDailyStat.getCreateOperationSum());
		assertEquals(1, (long) userDailyStat.getDeleteOperationCount());
		assertEquals(-300, (long) userDailyStat.getDeleteOperationSum());
		assertEquals(300, (long) userDailyStat.getDiffOperationSum());

		quota = accountQuotaBusinessService.find(jane);
		assertNotNull(quota);
		assertEquals(1100, (long) quota.getCurrentValue());
		assertEquals(800, (long) quota.getLastValue());
		assertEquals(1600, (long) quota.getQuota());
		assertEquals(1480, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getMaxFileSize());

		listOperationHistory = operationHistoryBusinessService.find(jane, null, null, new Date());
		assertEquals(0, listOperationHistory.size());

		dailyDomainBatch.execute(batchRunContext, jane.getDomain().getUuid(), 20, 1);

		ContainerQuota ensembleQuota = ensembleQuotaBusinessService.find(jane.getDomain(), ContainerQuotaType.USER);
		quota = accountQuotaBusinessService.find(jane);
		assertNotNull(ensembleQuota);
		assertEquals(496, (long) ensembleQuota.getLastValue());
		// sum of all users : jane : 1100 + john 900 = 2000
		assertEquals(2000, (long) ensembleQuota.getCurrentValue());
		assertEquals(1900, (long) ensembleQuota.getQuota());
		assertEquals(1300, (long) ensembleQuota.getQuotaWarning());
		assertEquals(5, (long) ensembleQuota.getDefaultMaxFileSize());

		ensembleQuota = ensembleQuotaBusinessService.find(jane.getDomain(), ContainerQuotaType.WORK_GROUP);
		assertNotNull(ensembleQuota);

		assertEquals(900, (long) ensembleQuota.getLastValue());
		// sum of all workgroups : 1900 ? is it right ?
		assertEquals(1900, (long) ensembleQuota.getCurrentValue());
		assertEquals(2000, (long) ensembleQuota.getQuota());
		assertEquals(1500, (long) ensembleQuota.getQuotaWarning());
		assertEquals(5, (long) ensembleQuota.getDefaultMaxFileSize());

		DomainQuota quotaD = domainQuotaBusinessService.find(jane.getDomain());
		assertNotNull(quotaD);
		assertEquals(1096, (long) quotaD.getLastValue());
		// container user 2000  + container workgroup 1900 = 3900
		assertEquals(3900, (long) quotaD.getCurrentValue());
		assertEquals(1900, (long) quotaD.getQuota());
		assertEquals(1800, (long) quotaD.getQuotaWarning());

		quotaD = domainQuotaBusinessService.findRootQuota();
		assertNotNull(quotaD);
		// cf sql import-tests-quota.sql
		assertEquals(1096, (long) quotaD.getCurrentValue());
		assertEquals(100, (long) quotaD.getLastValue());
		assertEquals(2300, (long) quotaD.getQuota());
		assertEquals(2000, (long) quotaD.getQuotaWarning());
	}
}
