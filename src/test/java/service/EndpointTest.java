package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;

public class EndpointTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	String endpoint = "http://localhost:8080/templatejakartaeewar/rest";

	private WebTarget webTarget() {
		ClientBuilder clientBuilder = ClientBuilder.newBuilder();
		clientBuilder.connectTimeout(30, TimeUnit.SECONDS);
		clientBuilder.readTimeout(30, TimeUnit.SECONDS);
		Client client = clientBuilder.build();
		return client.target(endpoint + "/customer");
	}

	//@formatter:off
	
	
	private String getToken() {	
		return "A_VERY_STUPID_NON_JWT";
	}
		
	@Test
	public void pingTest() {
		Response response = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getToken())
				.get();

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		String str = response.readEntity(String.class);
		assertEquals("PONG",  str);
	}
	
	@Test
	public void createTest() {

		String customerNumber = UUID.randomUUID().toString();
		Customer entity = new Customer();
		entity.setCustomernumber(customerNumber);
		entity.setName("NAME_" + customerNumber);
		entity.setChangedby("test");
		
		
		Response response = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity));

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		Customer created = response.readEntity(Customer.class);
		assertNotNull(created);
	}
	
	@Test
	public void createAndGetTest() {

		String customerNumber = UUID.randomUUID().toString();
		Customer entity = new Customer();
		entity.setCustomernumber(customerNumber);
		entity.setName("NAME_" + customerNumber);
		entity.setChangedby("test");
		
		
		Response response = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity));

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		Customer created = response.readEntity(Customer.class);
		assertNotNull(created);
		
		Long createdId = created.getId();
		Response getResponse = webTarget()
				.path(String.valueOf(createdId))
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.get();
		
		Customer retrieved = getResponse.readEntity(Customer.class);
		assertNotNull(retrieved);
		assertEquals(createdId, retrieved.getId());
	}
	
	@Test
	public void createGetUpdateAndGetTest() {

		String customerNumber = UUID.randomUUID().toString();
		Customer entity = new Customer();
		entity.setCustomernumber(customerNumber);
		entity.setName("NAME_" + customerNumber);
		entity.setChangedby("test");
		
		
		// create it
		Response response = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity));

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		Customer created = response.readEntity(Customer.class);
		assertNotNull(created);
		
		
		// fetch it
		Long createdId = created.getId();
		Response getResponse = webTarget()
				.path(String.valueOf(createdId))
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.get();
		
		Customer retrieved = getResponse.readEntity(Customer.class);
		assertNotNull(retrieved);
		assertEquals(createdId, retrieved.getId());

		// update it
		String newValue = "CHANGED " + retrieved.getName();
		retrieved.setName(newValue);
		Response updateresponse = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.put(Entity.json(retrieved));
		
		// fetch it to check change
		getResponse = webTarget()
				.path(String.valueOf(createdId))
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.get();

		retrieved = getResponse.readEntity(Customer.class);
		assertNotNull(retrieved);
		assertEquals(createdId, retrieved.getId());
		assertEquals(newValue, retrieved.getName());
	}
	
	@Test
	public void createGetUpdate2TimesAndGetTest() {

		String customerNumber = UUID.randomUUID().toString();
		Customer entity = new Customer();
		entity.setCustomernumber(customerNumber);
		entity.setName("NAME_" + customerNumber);
		entity.setChangedby("test");
		
		
		// create it
		Response response = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity));

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		Customer created = response.readEntity(Customer.class);
		assertNotNull(created);
		
		
		// fetch it
		Long createdId = created.getId();
		Response getResponse = webTarget()
				.path(String.valueOf(createdId))
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.get();
		
		Customer retrieved = getResponse.readEntity(Customer.class);
		assertNotNull(retrieved);
		assertEquals(createdId, retrieved.getId());

		// update it
		String newValue = "C" + retrieved.getName();
		retrieved.setName(newValue);
		Response updateresponse = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.put(Entity.json(retrieved));
		
		getResponse = webTarget()
				.path(String.valueOf(createdId))
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.get();
		retrieved = getResponse.readEntity(Customer.class);

		
		newValue = "C" + retrieved.getName();
		retrieved.setName(newValue);
		updateresponse = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.put(Entity.json(retrieved));
		
		// fetch it to check change
		getResponse = webTarget()
				.path(String.valueOf(createdId))
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.get();

		retrieved = getResponse.readEntity(Customer.class);
		assertNotNull(retrieved);
		assertEquals(createdId, retrieved.getId());
		assertEquals(newValue, retrieved.getName());
	}
	
	@Test
	public void provoceraPositiveFileLocking() throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String customerNumber = UUID.randomUUID().toString();
		Customer entity = new Customer();
		entity.setCustomernumber(customerNumber);
		entity.setName("NAME_" + customerNumber);
		entity.setChangedby("test");
		
		
		// create it
		Response response = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity));

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		Customer created = response.readEntity(Customer.class);
		assertNotNull(created);
		
		
		// fetch first
		Long createdId = created.getId();
		         response = webTarget()
				.path(String.valueOf(createdId))
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.get();
		
		Customer customer1 = response.readEntity(Customer.class);
		assertNotNull(customer1);
		assertEquals(createdId, customer1.getId());
		
		// fetch second
		response = webTarget()
				.path(String.valueOf(createdId))
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.get();
		
		Customer customer2 = response.readEntity(Customer.class);
		assertNotNull(customer2);
		assertEquals(createdId, customer2.getId());
		

		// update second  this should work
		String newValue = "CHANGED " + customer1.getName();
		customer1.setName(newValue);
		Response updateresponse = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.put(Entity.json(customer1));
		
		
		// update second  this should work
		String anotherValue = "AV " + customer1.getChangedby();
		customer2.setChangedby(anotherValue);
		updateresponse = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.put(Entity.json(customer2));
		
		assertTrue(updateresponse != null);
		assertEquals(200,  updateresponse.getStatus());
		JsonNode jsonNode = updateresponse.readEntity(JsonNode.class);
		JsonNode code = jsonNode.path("code");
		if(code instanceof MissingNode) {
			assertTrue(true);
			TestDto receivedDto = mapper.readValue(jsonNode.traverse(), TestDto.class);
			assertNotNull(receivedDto);
		}else {			
			assertTrue(true);
		}
	}
			
	@Test
	public void getNonExistingTest() {
		Long id = 5678L;
		Response getResponse = webTarget()
				.path(String.valueOf(id))
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.get();
		
		Customer retrieved = getResponse.readEntity(Customer.class);
		assertTrue(retrieved == null);
	}
			
	@Test
	public void createTestAsynk() {

		String customerNumber = UUID.randomUUID().toString();
		Customer entity = new Customer();
		entity.setCustomernumber(customerNumber);
		entity.setName("NAME_" + customerNumber);
		entity.setChangedby("test");
		
		
		Response response = webTarget()
				.path("async")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity));

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
	}
				
	@Test
	public void createOneConstraintError() throws JsonParseException, JsonMappingException, IOException {

		String customerNumber = UUID.randomUUID().toString();
		Customer entity = new Customer();
		entity.setCustomernumber(customerNumber);
		entity.setName("");
		entity.setChangedby("test");
				
		Response response = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity), Response.class);

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		
		JsonNode jsonNode = response.readEntity(JsonNode.class);
		int code = jsonNode.path("code").intValue();
		JsonNode descriptionsNode = jsonNode.path("descriptions");
		if(descriptionsNode instanceof ArrayNode) {
			ArrayNode sn = (ArrayNode) descriptionsNode;			
			int n = sn.size();
			assertEquals(1, n);						
		}
		assertEquals(500,  code);
	}
		
	@Test
	public void createTwoConstraintError() throws JsonParseException, JsonMappingException, IOException {

		Customer entity = new Customer();
		entity.setCustomernumber("123456789012345678901234567890123456789012345678901");  // length > 50
		entity.setName("");
		entity.setChangedby("test");
				
		Response response = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity), Response.class);

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		
		JsonNode jsonNode = response.readEntity(JsonNode.class);
		int code = jsonNode.path("code").intValue();
		JsonNode descriptionsNode = jsonNode.path("descriptions");
		if(descriptionsNode instanceof ArrayNode) {
			ArrayNode sn = (ArrayNode) descriptionsNode;			
			int n = sn.size();
			assertEquals(2, n);						
		}
		assertEquals(500,  code);
	}

	@Test
	public void throwNormalException() throws JsonParseException, JsonMappingException, IOException {

		Customer entity = new Customer();
		entity.setCustomernumber("123456789012345678901234567890123456789012345678901");  // length > 50
		entity.setName("");
		entity.setChangedby("test");
				
		Response response = webTarget()
				.path("throwsomething")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity), Response.class);

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());

		JsonNode jsonNode = response.readEntity(JsonNode.class);
		int code = jsonNode.path("code").intValue();
		JsonNode descriptionsNode = jsonNode.path("descriptions");
		if(descriptionsNode instanceof ArrayNode) {
			ArrayNode sn = (ArrayNode) descriptionsNode;			
			int n = sn.size();
			assertEquals(1, n);						
		}
		assertEquals(500,  code);
	}
	
	@Test
	public void throwRuntimeException() throws JsonParseException, JsonMappingException, IOException {

		Customer entity = new Customer();
		entity.setCustomernumber("123456789012345678901234567890123456789012345678901");  // length > 50
		entity.setName("");
		entity.setChangedby("test");
				
		Response response = webTarget()
				.path("throwruntime")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity), Response.class);

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());

		JsonNode jsonNode = response.readEntity(JsonNode.class);
		int code = jsonNode.path("code").intValue();
		JsonNode descriptionsNode = jsonNode.path("descriptions");
		if(descriptionsNode instanceof ArrayNode) {
			ArrayNode sn = (ArrayNode) descriptionsNode;			
			int n = sn.size();
			assertEquals(1, n);						
		}
		assertEquals(500,  code);
	}
	
	
	@Test
	public void createDuplicationTest() {

		String customerNumber = UUID.randomUUID().toString();
		Customer entity = new Customer();
		entity.setCustomernumber(customerNumber);
		entity.setName("NAME_" + customerNumber);
		entity.setChangedby("test");
		
		
		Response response = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity));

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		Customer created = response.readEntity(Customer.class);
		assertNotNull(created);
		
		// try to create it again
		 response = webTarget()
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(entity));

			assertTrue(response != null);
			assertEquals(200,  response.getStatus());

			JsonNode jsonNode = response.readEntity(JsonNode.class);
			int code = jsonNode.path("code").intValue();
			JsonNode descriptionsNode = jsonNode.path("descriptions");
			if(descriptionsNode instanceof ArrayNode) {
				ArrayNode sn = (ArrayNode) descriptionsNode;			
				int n = sn.size();
				assertEquals(1, n);						
			}
			assertEquals(500,  code);
						
	}
		
	@Test
	public void testdto() {

		TestDto testDto = new TestDto();
		testDto.setAge(19);
		testDto.setEmail("acme@com.se");
		testDto.setFirstName("Kalle");
		testDto.setLastName("Kula");
		testDto.setKommentar("kommentar om kalle kula");
		
		
		Response response = webTarget()
				.path("testdto")
				.request(MediaType.APPLICATION_JSON)				
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(testDto));

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		TestDto created = response.readEntity(TestDto.class);
		assertNotNull(created);
	}
			
	@Test
	public void testdtoONEError() {

		TestDto testDto = new TestDto();
		testDto.setAge(10);         // <<<<<<<<<<<<<<<  HERE
		testDto.setEmail("acme@com.se");
		testDto.setFirstName("Kalle");
		testDto.setLastName("Kula");
		testDto.setKommentar("kommentar om kalle kula");
		
		
		Response response = webTarget()
				.path("testdto")
				.request(MediaType.APPLICATION_JSON)				
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(testDto));

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		JsonNode jsonNode = response.readEntity(JsonNode.class);
		int code = jsonNode.path("code").intValue();
		JsonNode descriptionsNode = jsonNode.path("descriptions");
		if(descriptionsNode instanceof ArrayNode) {
			ArrayNode sn = (ArrayNode) descriptionsNode;			
			int n = sn.size();
			assertEquals(1, n);						
		}
		assertEquals(500,  code);
	}
	
	@Test
	public void testdtoButStartLookForError() {

		TestDto testDto = new TestDto();
		testDto.setAge(19);
		testDto.setEmail("kalle@kula.se");
		testDto.setFirstName("Kalle");
		testDto.setLastName("Kula");
		testDto.setKommentar("kommentar om kalle kula");
		
		
		Response response = webTarget()
				.path("testdto")
				.request(MediaType.APPLICATION_JSON)				
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(testDto));
		response.bufferEntity();

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		JsonNode jsonNode = response.readEntity(JsonNode.class);
		JsonNode code = jsonNode.path("code");
		if(code instanceof MissingNode) {
			assertTrue(true);
			TestDto receivedDto = response.readEntity(TestDto.class);
			assertNotNull(receivedDto);
			assertEquals(testDto.getEmail(), receivedDto.getEmail());
		}else {			
			assertTrue(false);
		}
	}
	
	// alternativ solution  using ObjectMapper instead  ONLY read Response ONCE !!!!
	@Test
	public void testdtoButStartLookForErrorUsingObjectMapper() throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();

		TestDto testDto = new TestDto();
		testDto.setAge(19);
		testDto.setEmail("kalle@kula.se");
		testDto.setFirstName("Kalle");
		testDto.setLastName("Kula");
		testDto.setKommentar("kommentar om kalle kula");
		
		
		Response response = webTarget()
				.path("testdto")
				.request(MediaType.APPLICATION_JSON)				
				.header(HttpHeaders.AUTHORIZATION,  getToken())
				.post(Entity.json(testDto));

		assertTrue(response != null);
		assertEquals(200,  response.getStatus());
		JsonNode jsonNode = response.readEntity(JsonNode.class);
		JsonNode code = jsonNode.path("code");
		if(code instanceof MissingNode) {
			assertTrue(true);
			TestDto receivedDto = mapper.readValue(jsonNode.traverse(), TestDto.class);
			assertNotNull(receivedDto);
			assertEquals(testDto.getEmail(), receivedDto.getEmail());
		}else {			
			assertTrue(false);
		}
	}

	
	// @formatter:on

}
