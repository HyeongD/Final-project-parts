package net.dheyeong.tutorials.springbootbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dheyeong.tutorials.springbootbackend.model.Employee;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {// JpaRepository<entity, PK type>type of entity+type of the primary key

}
