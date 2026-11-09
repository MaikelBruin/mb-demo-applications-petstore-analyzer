package mb.demo.applications.petstore.analyzer.system.bootstrap;

import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.PetstoreAnalyzerApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {PetstoreAnalyzerApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"dev", "system", "test", "cucumber"})
@Slf4j
public class SystemTestContainerBootstrapper {

}
