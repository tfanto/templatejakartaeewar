package sys;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.MDC;

@Provider
public class AppExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {

		AppError error = new AppError(AppSysConstants.APPLICATION_ERROR, ExceptionUtils.getRootCauseMessage(exception));
		return Response.ok(error).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();

	}

}
