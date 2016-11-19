package basictaskrunner.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import basictaskrunner.webapp.commands.CommandDef;
import basictaskrunner.webapp.commands.CommandRegistry;

/**
 * Not much MVC in this app as this is mainly SPA.
 * Only a REST endpoint to serve the initial command defs.
 */
@Controller
public class MainController {

//	@RequestMapping("/test")
//	public String test() {
//		return "index.html";
//	}
	
	@RequestMapping(
			value="/commanddefs",
			method=RequestMethod.GET,
			produces={"application/json"})
	public @ResponseBody CommandDef[] getCommandDefs() {
		return CommandRegistry.Defs;
	}
}
