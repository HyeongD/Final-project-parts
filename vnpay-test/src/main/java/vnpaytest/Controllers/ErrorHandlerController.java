package vnpaytest.Controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorHandlerController implements ErrorController {
	
	//If the error occurs, redirect the user to a custom error page showing the generic error message
	@RequestMapping("/error")
	@ResponseBody
	public String getErrorPath() {
		
		return "<center><h1>Something went wrong</h1></center>";
	}
}
