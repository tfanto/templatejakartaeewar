package sys;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class AppServletContextListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppServletContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		LOGGER.trace("contextInitialized");

		try {

			Properties props = new Properties();
			InputStream is = servletContextEvent.getServletContext().getResourceAsStream("WEB-INF/settings.properties");
			props.load(is);

		} catch (IOException e) {
			LOGGER.error(e.toString(), e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		LOGGER.trace("contextDestroyed");

	}
}
