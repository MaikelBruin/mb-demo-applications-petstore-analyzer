package mb.demo.applications.petstore.analyzer.isolated.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.base.cucumber.TestDataHolder;
import mb.demo.applications.petstore.analyzer.base.cucumber.steps.BaseCucumberStepDefs;
import mb.demos.openapi.generated.api.client.petstore.api.PetApiClient;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.mockito.Mockito;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class IsolatedStepDefs extends BaseCucumberStepDefs {
    private final PetApiClient petApiClient;

    public IsolatedStepDefs(CamelContext camelContext, ProducerTemplate producerTemplate, TestDataHolder testDataHolder, ObjectMapper objectMapper, TestRestTemplate testRestTemplate, final PetApiClient petApiClient) {
        super(camelContext, producerTemplate, testDataHolder, objectMapper, testRestTemplate);
        this.petApiClient = petApiClient;
    }

    @Given("I have access to the mocked petstore")
    public void iHaveAccessToTheMockedPetstore() {
        log.info("I have access to the mocked petstore");
        assertThat(petApiClient).isNotNull();
        boolean isSpy = Mockito.mockingDetails(petApiClient).isSpy();
        assertThat(isSpy).isTrue();
    }
}
