package uk.nhs.jorvik.fhir.E_RS.javaconfig;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.spring.javaconfig.Main;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;





@Configuration
@ComponentScan
public class E_RSApp extends CamelConfiguration {
	
	public static void main(String[] args) throws Exception {
        Main main = new Main();
      
        main.setConfigClass(E_RSApp.class);
        main.run();
    }
	
	@Override
    protected void setupCamelContext(CamelContext camelContext) throws Exception {
		
		
    }
	
	public static ContextHandler buildSwaggerUI() throws Exception {
        ResourceHandler rh = new ResourceHandler();
        rh.setResourceBase(E_RSApp.class.getClassLoader()
            .getResource("META-INF/resources/webjars/swagger-ui/2.1.4")
            .toURI().toString());
        ContextHandler context = new ContextHandler();
        context.setContextPath("/docs/");
        context.setHandler(rh);
        return context;
    }
}
