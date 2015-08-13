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
package org.linagora.linshare.core.batches.impl;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UserService;

public class MarkUserToPurgeBatchImpl extends GenericBatchImpl {

	protected final UserService service;

	protected final Integer cron;// 7 for 7 days

	public MarkUserToPurgeBatchImpl(final UserService userService,
			AccountRepository<Account> accountRepository,
			Integer cron) {
		super(accountRepository);
		this.service = userService;
		this.cron = cron;
	}

	@Override
	public List<String> getAll() {
		logger.info("MarkUserToDeleteBatchImpl job starting ...");
		Calendar expiryLimit = Calendar.getInstance();
		expiryLimit.add(Calendar.DAY_OF_YEAR, -cron);
		List<String> allUsers = service.findAllDeletedAccountsToPurge(expiryLimit.getTime());
		logger.info(allUsers.size()
				+ " destroyed user(s) have been found. If any, they will be moved into users to purge");
		return allUsers;
	}

	@Override
	public Context execute(String identifier, long total,
			long position) throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		User resource = service.findDeleted(actor, identifier);
		Context context = new AccountBatchResultContext(resource);
		try {
			logInfo(total, position, "processing user : " + resource.getAccountReprentation());
			service.markToPurge(actor, resource.getLsUuid());
			logger.info("expired user set to purge (purge_step to 1) : "
					+ resource.getAccountReprentation());
		} catch (BusinessException businessException) {
			logError(total, position,
					"Error while trying to mark expired users to purge");
			logger.info("Error occured while cleaning outdated users ",
					businessException);
			BatchBusinessException exception = new BatchBusinessException(
					context, "Error while trying to move the expired user into those to purge");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total,
			long position) {
		AccountBatchResultContext guestContext = (AccountBatchResultContext) context;
		Account user = guestContext.getResource();
		logInfo(total, position, "The User "
				+ user.getAccountReprentation()
				+ " has been successfully placed into users to purge ");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String resource,
			long total, long position) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account user = context.getResource();
		logError(
				total,
				position,
				"cleaning User has failed : "
						+ user.getAccountReprentation());
		logger.error(
				"Error occured while cleaning outdated user "
						+ user.getAccountReprentation()
						+ ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors,
			long total) {
		long success = total - errors - unhandled_errors;
		logger.info(success
				+ " user(s) have been marked to purge.");
		if (errors > 0) {
			logger.error(errors
					+ " user(s) failed to be marked to purge.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " user(s) failed to be removed (unhandled error).");
		}
		logger.info("MarkUserToPurgeBatchImpl job terminated.");
	}
}
