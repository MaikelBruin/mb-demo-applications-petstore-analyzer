package mb.demo.applications.petstore.analyzer.system.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import mb.demo.applications.petstore.analyzer.base.ContainerBootstrapper;
import mb.demo.applications.petstore.analyzer.exceptions.RestExceptionHandler;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**
 * The petstore and the consumer are replaced by WireMock servers (see {@link SystemTestWireMockConfiguration}), so
 * the real PetApi and EventsApi clients are used.
 */
@ActiveProfiles({"dev", "system", "test", "cucumber"})
@CucumberContextConfiguration
@Import(SystemTestWireMockConfiguration.class)
public class SystemTestConfiguration extends ContainerBootstrapper {

    /**
     * A spy (real behaviour, recorded calls) on the exception handler: lets scenarios verify which exception type the
     * analyzer threw, which is not visible in the HTTP response.
     */
    @MockitoSpyBean
    private RestExceptionHandler restExceptionHandler;

}
