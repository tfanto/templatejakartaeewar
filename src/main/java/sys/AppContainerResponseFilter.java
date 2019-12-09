package sys;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Provider
public class AppContainerResponseFilter implements ContainerResponseFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppContainerResponseFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("METHOD  : {}", requestContext.getMethod());
			LOGGER.trace("PATH    : {}", requestContext.getUriInfo().getPath());
			LOGGER.trace("STATUS  : {}", String.valueOf(responseContext.getStatus()));
			LOGGER.trace("MSG     : {}" + responseContext.getStatusInfo().toString());
			LOGGER.trace("{} : {}", AppSysConstants.REQUEST_ID, MDC.get(AppSysConstants.REQUEST_ID));
		}

		// last in callchain should remove this after last log
		if (MDC.get(AppSysConstants.IN_CALL_CHAIN).equals(AppSysConstants.NO)) {
			requestContext.getHeaders().remove(AppSysConstants.REQUEST_ID);
			responseContext.getHeaders().remove(AppSysConstants.REQUEST_ID);
		}

	}

}
