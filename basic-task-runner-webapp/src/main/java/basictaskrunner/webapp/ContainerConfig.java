package basictaskrunner.webapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.undertow.Undertow.Builder;

@Configuration
public class ContainerConfig {
	
	/**
	 *  Spring Boot has a predefined server.port which is automatically used; 
	 *  but i don't like such magic, better define explicitly.
	 */
	@Value("${http.port}")
	private int httpPort;
	
	/**
	 * Number of worker threads just for serving HTTP requests. 
	 * Only need few, because our design is to quickly pass off to the 
	 * application {@link CommandDispatcher}.
	 */
	@Value("${max.http.worker.threads}")
	private int maxWorkerThreads;

	@Bean
	public UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
		UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
		factory.setPort(httpPort);
		factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
			@Override
			public void customize(Builder builder) {
				builder.setWorkerThreads(maxWorkerThreads);
			}
		});
		return factory;
	}
}
