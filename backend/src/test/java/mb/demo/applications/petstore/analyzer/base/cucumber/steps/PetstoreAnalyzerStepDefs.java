package mb.demo.applications.petstore.analyzer.base.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.base.cucumber.TestDataHolder;
import mb.demos.openapi.generated.api.client.petstore.api.PetApiClient;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.ExtractingResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class PetstoreAnalyzerStepDefs extends BaseCucumberStepDefs {

    public PetstoreAnalyzerStepDefs(CamelContext camelContext, ProducerTemplate producerTemplate, TestDataHolder testDataHolder, ObjectMapper objectMapper, TestRestTemplate testRestTemplate, PetApiClient petApiClient) {
        super(camelContext, producerTemplate, testDataHolder, objectMapper, testRestTemplate, petApiClient);
    }

    @Given("I have access to the petstore")
    public void iHaveAccessToThePetstore() {
        assertThat(petApiClient).isNotNull();
    }
}
