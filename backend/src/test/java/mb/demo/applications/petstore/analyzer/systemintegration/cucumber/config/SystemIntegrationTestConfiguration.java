package mb.demo.applications.petstore.analyzer.systemintegration.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import mb.demo.applications.petstore.analyzer.base.ContainerBootstrapper;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApi;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@ActiveProfiles({"dev", "systemintegration", "test", "cucumber"})
@CucumberContextConfiguration
public class SystemIntegrationTestConfiguration extends ContainerBootstrapper {

    /**
     * A spy calls the real consumer client, but records the invocations so scenarios can count them. It must not be
     * stubbed: see 'I have access to the actual consumer'.
     */
    @MockitoSpyBean
    private EventsApi eventsApi;

}
