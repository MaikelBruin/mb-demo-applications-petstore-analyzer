package mb.demo.applications.petstore.analyzer.integrated;

import io.cucumber.spring.CucumberContextConfiguration;
import mb.demo.applications.petstore.analyzer.integrated.bootstrap.IntegratedContainerBootstrapper;
import org.springframework.context.annotation.Configuration;

@CucumberContextConfiguration
public class IntegratedTestConfiguration extends IntegratedContainerBootstrapper {

}
