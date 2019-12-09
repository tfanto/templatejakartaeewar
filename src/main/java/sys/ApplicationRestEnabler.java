package sys;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("rest")
public class ApplicationRestEnabler extends Application {

	private final Set<Object> singletons = new HashSet<>();
	private final Set<Class<?>> set = new HashSet<>();

	public ApplicationRestEnabler() {
		// mandatory
		//set.add(AppServletContextListener.class);
		//set.add(AppContainerRequestFilter.class);
		//set.add(AppContainerResponseFilter.class);
		//set.add(AppExceptionMapper.class);

		// the app
	}

	@Override
	public Set<Class<?>> getClasses() {
		return set;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

}
