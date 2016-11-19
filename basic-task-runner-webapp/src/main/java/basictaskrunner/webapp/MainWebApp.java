package basictaskrunner.webapp;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class MainWebApp {

	public static void main(String[] args) {
		// deliberately not use @ComponentScan as it is had to maintain.
		// better explicitly register the config classes.
		new SpringApplicationBuilder(
				MainWebApp.class, MainController.class, ContainerConfig.class, WebSocketConfig.class).run(args);
	}
}
