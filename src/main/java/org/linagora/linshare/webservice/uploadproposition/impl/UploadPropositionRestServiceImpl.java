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

package org.linagora.linshare.webservice.uploadproposition.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadproposition.UploadPropositionFacade;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionDto;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionFilterDto;
import org.linagora.linshare.webservice.uploadproposition.UploadPropositionRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadPropositionRestServiceImpl implements
		UploadPropositionRestService {

	private final UploadPropositionFacade uploadPropositionFacade;

	public UploadPropositionRestServiceImpl(
			UploadPropositionFacade uploadPropositionFacade) {
		super();
		this.uploadPropositionFacade = uploadPropositionFacade;
	}

	@GET
	@Path("/filters")
	@Operation(summary = "Find all upload proposition filters.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadPropositionFilterDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<UploadPropositionFilterDto> findAllFilters()
			throws BusinessException {
		return uploadPropositionFacade.findAll();
	}

	@GET
	@Path("/recipients/{userMail}")
	@Operation(summary = "Check if it is a valid user.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Boolean.class))),
			responseCode = "200"
		)
	})
	@Override
	public void checkIfValidRecipient(
			@PathParam(value = "userMail") String userMail,
			@QueryParam(value = "userDomain") String userDomain) throws BusinessException {
		uploadPropositionFacade.checkIfValidRecipient(userMail,
				userDomain);
	}

	@POST
	@Path("/propositions")
	@Operation(summary = "Create a new upload proposition.")
	@Override
	public void create(UploadPropositionDto dto) throws BusinessException {
		uploadPropositionFacade.create(dto);
	}

}
