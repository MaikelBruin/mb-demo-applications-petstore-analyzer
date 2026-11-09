package mb.demo.applications.petstore.analyzer.systemintegration.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import mb.demo.applications.petstore.analyzer.systemintegration.bootstrap.SystemIntegrationTestContainerBootstrapper;

@CucumberContextConfiguration
public class SystemIntegrationTestConfiguration extends SystemIntegrationTestContainerBootstrapper {

}
