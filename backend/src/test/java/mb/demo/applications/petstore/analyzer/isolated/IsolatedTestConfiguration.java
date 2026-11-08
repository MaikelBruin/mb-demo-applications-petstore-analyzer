package mb.demo.applications.petstore.analyzer.isolated;

import io.cucumber.spring.CucumberContextConfiguration;
import mb.demo.applications.petstore.analyzer.isolated.bootstrap.IsolatedContainerBootstrapper;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApi;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@CucumberContextConfiguration
public class IsolatedTestConfiguration extends IsolatedContainerBootstrapper {

    @MockitoBean
    private PetApi petApi;

    @MockitoBean
    private EventsApi eventsApi;


}
