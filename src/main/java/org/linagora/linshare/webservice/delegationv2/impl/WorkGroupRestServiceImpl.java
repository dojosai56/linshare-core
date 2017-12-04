package org.linagora.linshare.webservice.delegationv2.impl;
/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.delegation.WorkgroupFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegationv2.WorkGroupRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{ownerUuid}/workgroups")
@Api(value = "/rest/delegation/v2/{ownerUuid}/workgroups", basePath = "/rest/workgroups", description = "workgroups service.", produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WorkGroupRestServiceImpl extends WebserviceBase implements WorkGroupRestService {

	private final WorkgroupFacade workgroupFacade;

	public WorkGroupRestServiceImpl(final WorkgroupFacade workgroupFacade) {
		this.workgroupFacade = workgroupFacade;
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a workgroup.", response = WorkGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Workgroup not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public WorkGroupDto create(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Workgroup to create.", required = true) WorkGroupDto workgroup) throws BusinessException {
		return workgroupFacade.create(ownerUuid, workgroup);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Get a workgroup.", response = WorkGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Workgroup not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public WorkGroupDto find(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return workgroupFacade.find(ownerUuid, uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Get a workgroup.", response = WorkGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Workgroup not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public void head(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		workgroupFacade.find(ownerUuid, uuid);
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Get all workgroups.", response = WorkGroupDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Workgroup not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<WorkGroupDto> findAll(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid)
			throws BusinessException {
		return workgroupFacade.findAll(ownerUuid);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a workgroup.", response = WorkGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Workgroup or workgroup not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public WorkGroupDto delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Workgroup to delete.", required = true) WorkGroupDto workgroup) throws BusinessException {
		return workgroupFacade.delete(ownerUuid, workgroup);
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a workgroup.", response = WorkGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Workgroup or Workgroup not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public WorkGroupDto delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		WorkGroupDto tmp = new WorkGroupDto();
		tmp.setUuid(uuid);
		return workgroupFacade.delete(ownerUuid, tmp);
	}

	@Path("/{uuid}")
	@PUT
	@ApiOperation(value = "Update a workgroup.", response = WorkGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Workgroup or workgroup not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public WorkGroupDto update(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("uuid") String workgroupUuid,
			@ApiParam(value = "Workgroup to create.", required = true) WorkGroupDto workgroupDto) throws BusinessException {
		return workgroupFacade.update(ownerUuid, workgroupUuid, workgroupDto);
	}
}
