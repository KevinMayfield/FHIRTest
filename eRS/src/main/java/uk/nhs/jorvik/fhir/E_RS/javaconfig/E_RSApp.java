package uk.nhs.jorvik.fhir.E_RS.javaconfig;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.spring.javaconfig.Main;
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
}
