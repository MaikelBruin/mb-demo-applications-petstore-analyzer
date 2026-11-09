package mb.demo.applications.petstore.analyzer.system.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import mb.demo.applications.petstore.analyzer.system.bootstrap.SystemTestContainerBootstrapper;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApi;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
public class SystemTestConfiguration extends SystemTestContainerBootstrapper {

    @MockitoBean
    private PetApi petApi;

    @MockitoBean
    private EventsApi eventsApi;


}
