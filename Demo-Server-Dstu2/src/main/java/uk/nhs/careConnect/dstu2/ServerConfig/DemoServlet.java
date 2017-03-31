package uk.nhs.careConnect.dstu2.ServerConfig;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.narrative.INarrativeGenerator;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import uk.nhs.careConnect.dstu2.provider.CodeSystemResourceProvider;
import uk.nhs.careConnect.dstu2.provider.ValueSetResourceProvider;
import ca.uhn.fhir.rest.server.IResourceProvider;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
public class DemoServlet extends RestfulServer {
	
	private static final long serialVersionUID = 1L;
	
	//@Autowired
	//private EntityManagerFactory entityManagerFactory;

	
	//@Autowired
//	private SessionFactory sessionFactory;
	
	
	// KGM 7/2/2017 Disabled lineprivate WebApplicationContext myAppCtx;
	/**
	 * Constructor
	 */
	public DemoServlet() {
		super(FhirContext.forDstu3()); // Support DSTU2
	}
	//private static final Logger log = LoggerFactory.getLogger(uk.nhs.leedsth.fhir.hapifhirServerConfig.JorvikServlet.class);
	/**
	 * This method is called automatically when the
	 * servlet is initializing.
	 */
	
	@Override
	public void initialize() throws ServletException {
		super.initialize();
		
		FhirVersionEnum fhirVersion = FhirVersionEnum.DSTU3;
		setFhirContext(new FhirContext(fhirVersion));
		
	// KGM 7/2/2017 Disabled line	this.myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		
		List<IResourceProvider> providers = new ArrayList<IResourceProvider>();
		//providers.add(new DocumentReferenceResourceProvider());
		//providers.add(new PatientResourceProvider());
		
		//providers.add(new ConceptMapResourceProvider());
		providers.add(new ValueSetResourceProvider());
		//providers.add(new EpisodeOfCareResourceProvider());
		providers.add(new CodeSystemResourceProvider());
		
		setResourceProviders(providers);
		
		
		/*
		 * Use a narrative generator. This is a completely optional step, 
		 * but can be useful as it causes HAPI to generate narratives for
		 * resources which don't otherwise have one.
		 */
		INarrativeGenerator narrativeGen = new DefaultThymeleafNarrativeGenerator();
		FhirContext ctx = getFhirContext();
		
		ctx.setNarrativeGenerator(narrativeGen);
		setDefaultResponseEncoding(EncodingEnum.JSON);
		
		/*
		 * This server interceptor causes the server to return nicely
		 * formatter and coloured responses instead of plain JSON/XML if
		 * the request is coming from a browser window. It is optional,
		 * but can be nice for testing.
		 */
		registerInterceptor(new ResponseHighlighterInterceptor());
		
		/*
		 * Tells the server to return pretty-printed responses by default
		 */
		setDefaultPrettyPrint(true);
		
	}
	
}
