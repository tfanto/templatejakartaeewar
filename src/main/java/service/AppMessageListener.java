package service;

import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/appQueue"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class AppMessageListener implements MessageListener {

	private ObjectMapper MAPPER = new ObjectMapper();
	private static final Logger LOGGER = LoggerFactory.getLogger(AppMessageListener.class);
	@PersistenceContext
	private EntityManager em;

	@Override
	public void onMessage(Message message) {

		if (message instanceof TextMessage) {
			TextMessage msg = (TextMessage) message;
			try {
				String json = msg.getBody(String.class);
				Customer customer = MAPPER.readValue(json, Customer.class);
				em.persist(customer);
			} catch (JMSException | IOException e) {
				LOGGER.error(e.toString(), e);
				throw new RuntimeException(e.toString());
			}
		}
	}

}
