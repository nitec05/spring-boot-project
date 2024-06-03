package com.example.rqchallenge;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.rqchallenge.employees.controller.EmployeeController;
import com.example.rqchallenge.employees.models.CreateEmployeeResponse;
import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.models.EmployeeResponse;
import com.example.rqchallenge.employees.models.ListEmployeeResponse;

/**
 * TODO: Negative test cases are to be done.
 */
@SpringBootTest
class RqChallengeApplicationTests {

    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void getById() {
        EmployeeResponse response = new EmployeeResponse();
        Employee employeeData = Employee.builder().age("32").name("fake-name").id("test123").build();
        response.setData(employeeData);
        response.setStatus("success");
        when(restTemplate.getForObject(any(), any())).thenReturn(response);
        ResponseEntity<Employee> employeeReturned = employeeController.getEmployeeById("test123");
        Assertions.assertNotNull(employeeReturned);
        Assertions.assertEquals(employeeReturned.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(employeeReturned.getBody());
        Assertions.assertEquals(employeeData.getId(), employeeReturned.getBody().getId());
        Assertions.assertEquals(employeeData.getName(), employeeReturned.getBody().getName());
        Assertions.assertEquals(employeeData.getAge(), employeeReturned.getBody().getAge());
    }


    @Test
    void getAllEmployees() throws IOException {
        ListEmployeeResponse response = new ListEmployeeResponse();
        Employee employeeData1 = Employee.builder().age("32").name("fake-name").id("test123").build();
        Employee employeeData2 = Employee.builder().age("34").name("fake-name-2").id("test111").build();
        response.setData(List.of(employeeData1, employeeData2));
        response.setStatus("success");
        when(restTemplate.getForObject(any(), any())).thenReturn(response);
        ResponseEntity<List<Employee>> employeesReturned = employeeController.getAllEmployees();
        Assertions.assertNotNull(employeesReturned);
        Assertions.assertEquals(employeesReturned.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(employeesReturned.getBody());
        Assertions.assertEquals(2, employeesReturned.getBody().size());
        Map<String, String> mapForAssertions = new HashMap<>();
        mapForAssertions.put(employeeData1.getId(), employeeData1.getName());
        mapForAssertions.put(employeeData2.getId(), employeeData2.getName());
        employeesReturned.getBody().stream().forEach(e -> {
            Assertions.assertTrue(mapForAssertions.containsKey(e.getId()));
        });
       
    }

    @Test
    void createEmployee() throws IOException {
        CreateEmployeeResponse response = new CreateEmployeeResponse();
        Map<String, Object> input = Map.of("id", "test11111", "name", "fake-test-22", "age", "39", "salary", "777777");
        response.setStatus("success");
        response.setData(input);
        when(restTemplate.postForEntity(any(), any(), any())).thenReturn(ResponseEntity.status(HttpStatus.OK).body(response));
        ResponseEntity<Employee> employeeReturned = employeeController.createEmployee(input);
        Assertions.assertNotNull(employeeReturned);
        Assertions.assertEquals(employeeReturned.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(employeeReturned.getBody());
        Assertions.assertEquals(input.get("id"), employeeReturned.getBody().getId());
        Assertions.assertEquals(input.get("age"), employeeReturned.getBody().getAge());
        Assertions.assertEquals(input.get("name"), employeeReturned.getBody().getName());
        Assertions.assertEquals(input.get("salary"), employeeReturned.getBody().getSalary());
       
    }

    @Test
    void deleteEmployee() throws IOException {
        String id = "test77777";
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Assertions.assertNotNull(arg0);
            return null;
        }).when(restTemplate).delete(any(URI.class));

        ResponseEntity<String> idResponseEntity = employeeController.deleteEmployeeById(id);

        Assertions.assertEquals(HttpStatus.OK, idResponseEntity.getStatusCode());
        Assertions.assertEquals(id, idResponseEntity.getBody());

    }

}
