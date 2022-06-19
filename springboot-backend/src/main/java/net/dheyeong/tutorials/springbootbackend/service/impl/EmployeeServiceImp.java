package net.dheyeong.tutorials.springbootbackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dheyeong.tutorials.springbootbackend.model.Employee;
import net.dheyeong.tutorials.springbootbackend.repository.EmployeeRepository;
import net.dheyeong.tutorials.springbootbackend.service.EmployeeService;

@Service
// no need to add @Transactional annotation because spring JPA handles it
public class EmployeeServiceImp implements EmployeeService {
//setter based dependency when you have optional params
//constructor based dependency when you have a mandatory parameters	
	private EmployeeRepository employeeRepository;
	
	//@Autowired we don't need to add autowired
	public EmployeeServiceImp(EmployeeRepository employeeRepository) {
		super();
		this.employeeRepository = employeeRepository;
	}
	@Override
	public Employee saveEmployee(Employee employee) {
		// TODO Auto-generated method stub
		return employeeRepository.save(employee);
	}

}
