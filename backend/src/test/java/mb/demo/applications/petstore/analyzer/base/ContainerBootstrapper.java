package mb.demo.applications.petstore.analyzer.base;

import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.PetstoreAnalyzerApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {PetstoreAnalyzerApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class ContainerBootstrapper {

}
