/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
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

package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class LogCriteriaDto extends org.linagora.linshare.core.facade.webservice.common.dto.LogCriteriaDto{

	@Schema(description = "List of actor's mail")
	private List<String> actorMails; // The selected user

	@Schema(description = "First name of the actor")
	private String actorFirstName;

	@Schema(description = "Last name of the actor")
	private String actorLastName;

	@Schema(description = "Domain of the actor")
	private String actorDomain;

	public LogCriteriaDto() {
	}

	public LogCriteriaDto(List<String> actorMails, String actorFirstName,
			String actorLastName, String actorDomain) {
		this.actorMails = actorMails;
		this.actorFirstName = actorFirstName;
		this.actorLastName = actorLastName;
		this.actorDomain = actorDomain;
	}

	public List<String> getActorMails() {
		return actorMails;
	}

	public void setActorMails(List<String> mails) {
		this.actorMails = mails;
	}

	public String getActorFirstName() {
		return actorFirstName;
	}

	public void setActorFirstName(String firstname) {
		this.actorFirstName = firstname;
	}

	public String getActorLastName() {
		return actorLastName;
	}

	public void setActorLastName(String lastname) {
		this.actorLastName = lastname;
	}

	public String getActorDomain() {
		return actorDomain;
	}

	public void setActorDomain(String actorDomain) {
		this.actorDomain = actorDomain;
	}
}
