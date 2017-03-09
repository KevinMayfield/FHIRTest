package uk.nhs.jorvik.fhirTest.javaconfig;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.spring.javaconfig.Main;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;




@Configuration
@ComponentScan
public class CareConnectPatientApp extends CamelConfiguration {
	
	public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.setConfigClass(CareConnectPatientApp.class);
        main.run();
    }
	
	@Override
    protected void setupCamelContext(CamelContext camelContext) throws Exception {
		
		/*
        // setup the ActiveMQ component
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("vm://localhost?broker.persistent=false&broker.useJmx=false");

        // and register it into the CamelContext
        JmsComponent answer = new JmsComponent();
        answer.setConnectionFactory(connectionFactory);
        camelContext.addComponent("jms", answer);
        
        */
    }
}
