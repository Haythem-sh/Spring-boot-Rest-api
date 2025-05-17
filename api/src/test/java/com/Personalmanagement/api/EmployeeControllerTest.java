package com.Personalmanagement.api;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql; // Import @Sql
import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.result.MockMvcResultHandlers; // Uncomment for printing request/response

@SpringBootTest
@AutoConfigureMockMvc
// Apply data.sql before each test method in this class to ensure a consistent state
// Ensure data.sql is in src/test/resources
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EmployeeControllerTest {

	@Autowired
	public MockMvc mockMvc;

	@Test
	public void testGetEmployees() throws Exception {
		mockMvc.perform(get("/employees"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].firstName", is("Laurent"))) // Example check for the first employee from data.sql
				.andExpect(jsonPath("$[1].firstName", is("Sophie")));  // Example check for second
	}

	@Test
	public void testCreateEmployee() throws Exception {
		String employeeJson = "{\"firstName\":\"Test\",\"lastName\":\"User\",\"mail\":\"test.user@example.com\",\"password\":\"testpassword\"}";

		mockMvc.perform(post("/employee")
						.contentType(MediaType.APPLICATION_JSON)
						.content(employeeJson))
				.andExpect(status().isCreated()) // Expect 201 Created
				.andExpect(jsonPath("$.id").exists()) // ID should be generated and returned
				.andExpect(jsonPath("$.firstName", is("Test")))
				.andExpect(jsonPath("$.lastName", is("User")))
				.andExpect(jsonPath("$.mail", is("test.user@example.com")));
	}

	@Test
	public void testUpdateEmployee() throws Exception {
		long employeeIdToUpdate = 1L; // Assumes an employee with ID 1 exists from data.sql
		String updatedEmployeeJson = "{\"firstName\":\"Laurent\",\"lastName\":\"GINA\",\"mail\":\"laurent.gina.updated@example.com\",\"password\":\"newpassword\"}";

		mockMvc.perform(put("/employee/" + employeeIdToUpdate)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updatedEmployeeJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is((int) employeeIdToUpdate)))
				.andExpect(jsonPath("$.mail", is("laurent.gina.updated@example.com")))
				.andExpect(jsonPath("$.password", is("newpassword"))) // Check the updated field
				.andExpect(jsonPath("$.firstName", is("Laurent")));
	}

	@Test
	public void testUpdateEmployee_NotFound() throws Exception {
		long nonExistentEmployeeId = 999L;
		String updatedEmployeeJson = "{\"firstName\":\"Ghost\",\"lastName\":\"User\",\"mail\":\"ghost@example.com\",\"password\":\"boo\"}";

		mockMvc.perform(put("/employee/" + nonExistentEmployeeId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updatedEmployeeJson))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteEmployee() throws Exception {
		long employeeIdToDelete = 2L; // Assumes an employee with ID 2 exists from data.sql

		mockMvc.perform(delete("/employee/" + employeeIdToDelete))
				.andExpect(status().isNoContent()); // Expect 204 No Content

		// Verify that the employee is actually deleted
		mockMvc.perform(get("/employee/" + employeeIdToDelete))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteEmployee_NotFound() throws Exception {
		long nonExistentEmployeeId = 999L;

		mockMvc.perform(delete("/employee/" + nonExistentEmployeeId))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetEmployeeById() throws Exception {
		long employeeIdToGet = 3L; // Assumes an employee with ID 3 exists from data.sql
		mockMvc.perform(get("/employee/" + employeeIdToGet)
						.contentType(MediaType.APPLICATION_JSON)) // Content type for GET is not strictly needed for request but good practice
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is((int) employeeIdToGet)))
				.andExpect(jsonPath("$.firstName", is("Agathe")))
				.andExpect(jsonPath("$.lastName", is("FEELING")));
	}

	@Test
	public void testGetEmployeeById_NotFound() throws Exception {
		long nonExistentEmployeeId = 999L;
		mockMvc.perform(get("/employee/" + nonExistentEmployeeId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}