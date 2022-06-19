package com.dheyeong.springboot.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//@Controller
//@ResponseBody //Annotation that indicates a method return value should be bound to the webresponse body.
@RestController //A convenience annotation that is itself annotated with @Controller and @ResponseBody.
public class HelloWorldController {
	
	// Get http method
	// http://localhost:9090/hello-world
	@GetMapping("/hello-world")
	public String helloWorld() {
		return "Hello World!";
	}
}
