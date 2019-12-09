package service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sys.AppError;
import sys.AppSysConstants;

@Path("customer")
@Stateless
public class CustomerResource {

	private ObjectMapper MAPPER = new ObjectMapper();

	@PersistenceContext
	private EntityManager em;

	@Inject
	JMSContext jmsContext;

	@Resource(mappedName = "java:/jms/queue/appQueue")
	private Queue queue;

	@Inject
	Validator validator;

	@Context
	private SecurityContext sc;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response ping() {
		return Response.ok("PONG").build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response create(Customer customer) {

		List<String> violations = validator.validate(customer).stream().map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());
		if (!violations.isEmpty()) {
			AppError error = new AppError(AppSysConstants.APPLICATION_ERROR, violations);
			return Response.ok(error).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		} else {
			em.persist(customer);
			return Response.ok(customer).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	@Path("async")
	public Response createAsync(Customer customer) throws JsonProcessingException {

		List<String> violations = validator.validate(customer).stream().map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());
		if (!violations.isEmpty()) {
			AppError error = new AppError(AppSysConstants.APPLICATION_ERROR, violations);
			return Response.ok(error).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		} else {
			String json = MAPPER.writeValueAsString(customer);
			jmsContext.createProducer().setDeliveryMode(DeliveryMode.PERSISTENT).send(queue, json);
			return Response.ok().header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response update(Customer customer) {

		List<String> violations = validator.validate(customer).stream().map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());
		if (!violations.isEmpty()) {
			AppError error = new AppError(AppSysConstants.APPLICATION_ERROR, violations);
			return Response.ok(error).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		}
		if (customer.getId() == null) {
			AppError error = new AppError(AppSysConstants.APPLICATION_ERROR, "Id is null cannot update");
			return Response.ok(error).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		}
		Customer fetched = em.find(Customer.class, customer.getId());
		if (fetched == null) {
			AppError error = new AppError(AppSysConstants.APPLICATION_ERROR,
					"Record not found. Cannot update nonexisting data");
			return Response.ok(error).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		}
		em.merge(customer);
		return Response.ok(customer).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	@Path("{id}")
	public Response delete(@PathParam("id") Long id) {

		if (id == null) {
			AppError error = new AppError(AppSysConstants.APPLICATION_ERROR, "Id is null cannot delete");
			return Response.ok(error).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		}
		Customer fetched = em.find(Customer.class, id);
		if (fetched != null) {
			em.remove(fetched);
		}
		return Response.ok().header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	@Path("browse/queue")
	public Response browseQueue() throws JMSException {

		List<String> msgs = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		Enumeration messageEnumeration;
		QueueBrowser browser = jmsContext.createBrowser(queue);
		messageEnumeration = browser.getEnumeration();
		if (messageEnumeration != null) {
			while (messageEnumeration.hasMoreElements()) {
				TextMessage textMessage = (TextMessage) messageEnumeration.nextElement();
				String msgTxt = textMessage.getText();
				msgs.add(msgTxt);
			}
		}
		return Response.ok(msgs).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	@Path("{id}")
	public Response getById(@PathParam("id") Long id) {

		if (id == null) {
			AppError error = new AppError(AppSysConstants.APPLICATION_ERROR, "id is not valid");
			return Response.ok(error).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		}
		Customer fetched = em.find(Customer.class, id);
		return Response.ok(fetched).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
	}

	// FOR TESTING REMOVE LATER (also the tests of course

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	@Path("throwruntime")
	public Response throwruntime(Customer customer) throws Exception {
		throw new RuntimeException("RuntimeException thrown bu the programmer with intention");
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	@Path("testdto")
	public Response testdto(TestDto testDto) {

		List<String> violations = validator.validate(testDto).stream().map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());
		if (violations.isEmpty()) {
			return Response.ok(testDto).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		} else {
			AppError error = new AppError(AppSysConstants.APPLICATION_ERROR, violations);
			return Response.ok(error).header("MDC", MDC.get(AppSysConstants.REQUEST_ID)).build();
		}
	}

}
