package org.linagora.linshare.webservice.userv2.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.LogCriteriaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.LogDto;
import org.linagora.linshare.core.facade.webservice.user.LogEntryFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv2.LogRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/logs")
@Api(value = "/rest/user/logs", description = "User history service.")
public class LogRestServiceImpl extends WebserviceBase implements LogRestService{

	private LogEntryFacade logEntryFacade;

	public LogRestServiceImpl(final LogEntryFacade logEntryFacade) {
		this.logEntryFacade = logEntryFacade;
	}

	@Path("/")
	@ApiOperation(value = "Search the user history with specified criteria.", response = LogDto.class, responseContainer = "List")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public List<LogDto> query(
			@ApiParam(value = "Criteria to search for.", required = true) LogCriteriaDto criteria)
			throws BusinessException {
		return logEntryFacade.query(criteria);
	}

}
