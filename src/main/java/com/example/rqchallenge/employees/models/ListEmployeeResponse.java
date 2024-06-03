package com.example.rqchallenge.employees.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListEmployeeResponse extends BaseResponseEntity<List<Employee>> {
}
