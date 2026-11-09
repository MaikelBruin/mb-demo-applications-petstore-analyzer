package mb.demo.applications.petstore.analyzer.system.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import mb.demo.applications.petstore.analyzer.base.ContainerBootstrapper;
import mb.demo.applications.petstore.analyzer.exceptions.RestExceptionHandler;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApi;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@ActiveProfiles({"dev", "system", "test", "cucumber"})
@CucumberContextConfiguration
public class SystemTestConfiguration extends ContainerBootstrapper {

    @MockitoBean
    private PetApi petApi;

    @MockitoBean
    private EventsApi eventsApi;

    /**
     * A spy (real behaviour, recorded calls) on the exception handler: lets scenarios verify which exception type the
     * analyzer threw, which is not visible in the HTTP response.
     */
    @MockitoSpyBean
    private RestExceptionHandler restExceptionHandler;

}
