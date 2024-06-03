package com.example.rqchallenge.employees.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.rqchallenge.employees.IEmployeeController;
import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.service.EmployeeService;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class EmployeeController implements IEmployeeController {

    private @NonNull EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        log.debug("Method:getAllEmployees");
        return employeeService.getAllEmployees();
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        log.debug("Method:getEmployeeById: {}", id);
        return employeeService.getById(id);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        log.debug("Method:getEmployeesByNameSearch: {}", searchString);
        return employeeService.getEmployeesByName(searchString);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.debug("Method:getHighestSalaryOfEmployees");
        return employeeService.getHighestSalaryOfEmployees();
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.debug("Method:getTopTenHighestEarningEmployeeNames");
        return employeeService.getTop10SalaryEmployees();
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
       return employeeService.createEmployee(employeeInput);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return employeeService.deleteEmployee(id);
    }

}
