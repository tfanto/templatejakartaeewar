package sys;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@PreMatching
@Provider
@Priority((Priorities.AUTHENTICATION))
public class AppContainerRequestFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppContainerRequestFilter.class);

	@Context
	UriInfo uriInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		LOGGER.trace("AppContainerRequestFilter " + uriInfo.getPath());

		String mdcValue = requestContext.getHeaderString(AppSysConstants.REQUEST_ID);
		if (mdcValue == null) {
			mdcValue = UUID.randomUUID().toString();
			requestContext.getHeaders().add(AppSysConstants.REQUEST_ID, mdcValue);
			MDC.put(AppSysConstants.IN_CALL_CHAIN, AppSysConstants.NO);
		}
		else {
			MDC.put(AppSysConstants.IN_CALL_CHAIN, AppSysConstants.YES);			
		}
		MDC.put(AppSysConstants.REQUEST_ID, mdcValue);

		String jwtString = requestContext.getHeaderString("Authorization");
		if (jwtString == null) {
			// throw new AppException(403, "Forbidden");
			// requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}

	}

}
