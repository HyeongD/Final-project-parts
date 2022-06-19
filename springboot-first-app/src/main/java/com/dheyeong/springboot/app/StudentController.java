package com.dheyeong.springboot.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

	//http://localhost:8080/student
	@GetMapping("/student")
	public Student getStudent() {
		return new Student("Dhe_yeong", "Tchalla");
	}
	
	//http://localhost:8080/students
	@GetMapping("/students")
	public List<Student> getStudents(){
		List<Student> students = new ArrayList<>();
		students.add(new Student("Dhe_yeong", "Tchalla"));
		students.add(new Student("Lara", "Croft"));
		students.add(new Student("Tony", "Chris"));
		students.add(new Student("Jacob", "Ayden"));
		students.add(new Student("Sum", "Naresh"));
		return students;
	}
	
	//using pathVariable
	//http://localhost:8080/student/Lara/Croft
	// @PathVariable annotation
	@GetMapping("/student/{firstName1}/{lastName2}/")   
	public Student StudentPathVariable(@PathVariable("firstName1") String firstName, @PathVariable("lastName2") String lastName) {
		return new Student(firstName, lastName);
	}
	
	//build rest API to handle query parameters
	// http://localhost:8080/student?firstName= Dhe yeong&lastname= Tchalla
	//@RequestParam Annotation which indicates that a method parameter should be bound to a web request parameter. 
	@GetMapping("/student/query")
	public Student studentQueryParam(
			@RequestParam(name = "firstName") String firstName,
			@RequestParam(name = "lastName")  String lastName) {
		return new Student(firstName, lastName);
	}
}
