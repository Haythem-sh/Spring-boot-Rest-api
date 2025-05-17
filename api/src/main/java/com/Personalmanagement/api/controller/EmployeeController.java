package com.Personalmanagement.api.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Personalmanagement.api.model.Employee;
import com.Personalmanagement.api.service.EmployeeService;

@RestController
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	/**
	 * Create - Add a new employee
	 * @param employee An object employee
	 * @return The employee object saved
	 */
	@PostMapping("/employee")
	public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
		Employee savedEmployee = employeeService.saveEmployee(employee);
		// Consider returning HttpStatus.CREATED (201) for new resource creation
		return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
	}


	/**
	 * Read - Get one employee
	 * @param id The id of the employee
	 * @return An Employee object fulfilled or 404 if not found
	 */
	@GetMapping("/employee/{id}")
	public ResponseEntity<Employee> getEmployee(@PathVariable("id") final Long id) {
		Optional<Employee> employee = employeeService.getEmployee(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * Read - Get all employees
	 * @return - An Iterable object of Employee fulfilled
	 */
	@GetMapping("/employees")
	public Iterable<Employee> getEmployees() {
		return employeeService.getEmployees();
	}

	/**
	 * Update - Update an existing employee
	 * @param id - The id of the employee to update
	 * @param employeeDetails - The employee object updated
	 * @return The updated employee object or 404 if not found
	 */
	@PutMapping("/employee/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable("id") final Long id, @RequestBody Employee employeeDetails) {
		Optional<Employee> e = employeeService.getEmployee(id);
		if(e.isPresent()) {
			Employee currentEmployee = e.get();

			String firstName = employeeDetails.getFirstName();
			if(firstName != null) {
				currentEmployee.setFirstName(firstName);
			}
			String lastName = employeeDetails.getLastName();
			if(lastName != null) {
				currentEmployee.setLastName(lastName);;
			}
			String mail = employeeDetails.getMail();
			if(mail != null) {
				currentEmployee.setMail(mail);
			}
			String password = employeeDetails.getPassword();
			if(password != null) {
				currentEmployee.setPassword(password);;
			}
			employeeService.saveEmployee(currentEmployee);
			return ResponseEntity.ok(currentEmployee);
		} else {
			return ResponseEntity.notFound().build();
		}
	}


	/**
	 * Delete - Delete an employee
	 * @param id - The id of the employee to delete
	 * @return 204 No Content if successful, 404 if not found
	 */
	@DeleteMapping("/employee/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable("id") final Long id) {
		try {
			// First check if employee exists to return a more specific 404
			// if EmployeeService.deleteEmployee doesn't throw a specific exception for not found.
			// However, Spring Data JPA's deleteById throws EmptyResultDataAccessException if not found.
			employeeService.deleteEmployee(id);
			return ResponseEntity.noContent().build(); // 204 No Content
		} catch (EmptyResultDataAccessException ex) {
			return ResponseEntity.notFound().build(); // 404 Not Found
		}
	}

}