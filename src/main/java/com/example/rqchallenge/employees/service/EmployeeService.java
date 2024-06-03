package com.example.rqchallenge.employees.service;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.rqchallenge.employees.models.CreateEmployeeRequest;
import com.example.rqchallenge.employees.models.CreateEmployeeResponse;
import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.models.EmployeeResponse;
import com.example.rqchallenge.employees.models.ListEmployeeResponse;
import com.example.rqchallenge.helpers.JsonHelper;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {

    private static final String EMPLOYEE_GET_PATH = "/api/v1/employee/%s";
    private static final String EMPLOYEE_GET_ALL_PATH = "/api/v1/employees";
    private static final String EMPLOYEE_CREATE_PATH = "/api/v1/create";
    private static final String EMPLOYEE_DELETE_PATH = "/api/v1/delete/%s";

    /** RestTemplate to call external service */
    private @NonNull RestTemplate restTemplate;

    /** BaseURL to call external service on the base path */
    private @NonNull String baseUrl;

    @Autowired
    public EmployeeService(RestTemplate restTemplate, @Value("${api.base.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * 
     * @param id
     * @return ResponseEntity<Employee>
     */
    public ResponseEntity<Employee> getById(String id) {
        if (id == null || id == "") {
            log.error("Id provided is null or empty", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        String employeeGetURLPath = baseUrl.concat(String.format(EMPLOYEE_GET_PATH, id));
        EmployeeResponse response = this.restTemplate.getForObject(URI.create(employeeGetURLPath),
                EmployeeResponse.class);
        return isSuccess(response.getStatus()) ? buildResponse(response.getData(), HttpStatus.OK)
                : buildResponse(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<Employee>> getAllEmployees() {
        ListEmployeeResponse response = getAll();
        return isSuccess(response.getStatus()) ? buildResponse(response.getData(), HttpStatus.OK)
                : buildResponse(null, HttpStatus.NOT_FOUND);
    }

    /**
     * 
     * @param name
     * @return ResponseEntity<List<Employee>>
     */
    // TODO: Note: Inefficient external call: REST api should provide search
    public ResponseEntity<List<Employee>> getEmployeesByName(String name) {
        ListEmployeeResponse response = getAll();
        if (response.getData() == null || response.getData().isEmpty()) {
            log.error("No data found");
            return buildResponse(null, HttpStatus.NOT_FOUND);
        }

        List<Employee> filteredEmployees = response.getData().stream().filter(t -> t.equals(name))
                .collect(Collectors.toList());
        return isSuccess(response.getStatus()) ? buildResponse(filteredEmployees, HttpStatus.OK)
                : buildResponse(null, HttpStatus.NOT_FOUND);
    }

    /**
     * 
     * @return ResponseEntity<Integer>
     */
    // TODO: Note: Inefficient external call: REST api should provide pagination and
    // sorting functionality
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {

        ListEmployeeResponse response = getAll();
        if (response.getData() == null || response.getData().isEmpty()) {
            log.error("No data found");
            return buildResponse(null, HttpStatus.NOT_FOUND);
        }
        if (isSuccess(response.getStatus())) {
            sortDescending(response.getData());
            return buildResponse(Integer.parseInt(response.getData().get(0).getSalary()), HttpStatus.OK);
        }

        return buildResponse(null, HttpStatus.NOT_FOUND);
    }

    // TODO: Note: Inefficient external call: REST api should provide pagination and
    // sorting functionality
    public ResponseEntity<List<String>> getTop10SalaryEmployees() {
        ListEmployeeResponse response = getAll();
        if (response.getData() == null || response.getData().isEmpty()) {
            log.error("No data found");
            return buildResponse(null, HttpStatus.NOT_FOUND);
        }
        if (isSuccess(response.getStatus())) {
            sortDescending(response.getData());
            List<Employee> subEmployees = response.getData();
            if (response.getData().size() > 10) {
                subEmployees = response.getData().subList(0, 10);
            }
            List<String> employeeNames = subEmployees.stream().map(e -> e.getName()).collect(Collectors.toList());
            return buildResponse(employeeNames, HttpStatus.OK);
        }

        return buildResponse(null, HttpStatus.NOT_FOUND);
    }

    /**
     * @param input
     * @return
     */
    public ResponseEntity<Employee> createEmployee(Map<String, Object> input) {
        if (input == null || input.isEmpty())
            return buildResponse(null, HttpStatus.NOT_FOUND);
        CreateEmployeeRequest newEmployeePayload = JsonHelper.toObject(input, CreateEmployeeRequest.class);
        ResponseEntity<CreateEmployeeResponse> response = this.restTemplate.postForEntity(
                URI.create(baseUrl.concat(EMPLOYEE_CREATE_PATH)),
                newEmployeePayload,
                CreateEmployeeResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody().getData();
            Employee employeeOutput = Employee.builder()
                    .age(String.valueOf(body.get("age")))
                    .salary(String.valueOf(body.get("salary")))
                    .name(String.valueOf(body.get("name")))
                    .id(String.valueOf(body.get("id")))
                    .build();
            return buildResponse(employeeOutput, HttpStatus.OK);
        }
        return buildResponse(null, HttpStatus.NOT_FOUND);
    }

    /**
     * 
     * @param id
     * @return
     */
    public ResponseEntity<String> deleteEmployee(String id) {
        if (id == null || id == "") {
            log.error("Id provided is null or empty", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        String employeeDeleteURLPath = baseUrl.concat(String.format(EMPLOYEE_DELETE_PATH, id));
        this.restTemplate.delete(URI.create(employeeDeleteURLPath));
        return buildResponse(id, HttpStatus.OK);
    }

    private void sortDescending(List<Employee> data) {
        Collections.sort(data, new Comparator<Employee>() {
            public int compare(Employee o1, Employee o2) {
                if (o1 == null || o2 == null) {
                    return 0;
                }
                if (o1 != null && o1.equals(o2)) {
                    return 0;
                }
                Integer o1Salary = Integer.parseInt(o1.getSalary());
                Integer o2Salary = Integer.parseInt(o2.getSalary());
                if (o2Salary > o1Salary) {
                    return 1;
                } else if (o1Salary == o2Salary) {
                    return 0;
                } else {
                    return -1;
                }

            };
        });
    }

    private ListEmployeeResponse getAll() {
        String employeeGetAllPath = baseUrl.concat(EMPLOYEE_GET_ALL_PATH);
        ListEmployeeResponse response = this.restTemplate.getForObject(URI.create(employeeGetAllPath),
                ListEmployeeResponse.class);
        return response;
    }

    private static boolean isSuccess(String status) {
        return "success".equals(status);
    }

    private static <T> ResponseEntity<T> buildResponse(T data, HttpStatus status) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<T>(data, httpHeaders, status);
    }

}
