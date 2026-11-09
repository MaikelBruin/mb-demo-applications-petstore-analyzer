package mb.demo.applications.petstore.analyzer.systemintegration.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import mb.demo.applications.petstore.analyzer.base.ContainerBootstrapper;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"dev", "systemintegration", "test", "cucumber"})
@CucumberContextConfiguration
public class SystemIntegrationTestConfiguration extends ContainerBootstrapper {

}
