/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.service.impl;

import org.jsoup.helper.Validate;
import org.linagora.linshare.core.domain.constants.ExceptionStatisticType;
import org.linagora.linshare.core.domain.constants.ExceptionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ExceptionStatisticService;
import org.linagora.linshare.mongo.entities.ExceptionStatistic;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.ExceptionStatisticMongoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ExceptionStatisticServiceImpl implements ExceptionStatisticService {

	private ExceptionStatisticMongoRepository exceptionStatisticMongoRepository;

	private AccountService accountService;

	public ExceptionStatisticServiceImpl(ExceptionStatisticMongoRepository exceptionStatisticMongoRepository,
			AccountService accountService) {
		this.exceptionStatisticMongoRepository = exceptionStatisticMongoRepository;
		this.accountService = accountService;
	}

	@Override
	public ExceptionStatistic createExceptionStatistic(BusinessErrorCode errorCode, StackTraceElement[] stackTrace,
			ExceptionType type) {
		User authUser = checkAuthentication();
		Validate.notNull(type);
		return exceptionStatisticMongoRepository
				.insert(new ExceptionStatistic(1L, getParentDomainUuid(), errorCode, stackTrace,
						new AccountMto(authUser), type, ExceptionStatisticType.ONESHOT));
	}

	protected User checkAuthentication() throws BusinessException {
		User authUser = getAuthentication();
		if (authUser == null) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return authUser;
	}

	protected User getAuthentication() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = (auth != null) ? auth.getName() : null;
		if (name == null) {
			return null;
		}
		User user = (User) accountService.findByLsUuid(name);
		return user;
	}

	protected String getParentDomainUuid() {
		String parentDomainUuid = null;
		User authUser = checkAuthentication();
		AbstractDomain parentDomain = authUser.getDomain().getParentDomain();
		if (parentDomain != null) {
			parentDomainUuid = parentDomain.getUuid();
		}
		return parentDomainUuid;
	}
}