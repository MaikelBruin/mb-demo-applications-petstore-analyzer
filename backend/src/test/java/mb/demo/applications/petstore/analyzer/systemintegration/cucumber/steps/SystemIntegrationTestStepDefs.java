package mb.demo.applications.petstore.analyzer.systemintegration.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.base.cucumber.TestDataHolder;
import mb.demo.applications.petstore.analyzer.base.cucumber.steps.BaseCucumberStepDefs;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApi;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.mockito.Mockito;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class SystemIntegrationTestStepDefs extends BaseCucumberStepDefs {

    private final PetApi petApi;
    private final EventsApi eventsApi;

    public SystemIntegrationTestStepDefs(CamelContext camelContext,
                                         ProducerTemplate producerTemplate,
                                         TestDataHolder testDataHolder,
                                         ObjectMapper objectMapper,
                                         TestRestTemplate testRestTemplate,
                                         final PetApi petApi,
                                         final EventsApi eventsApi) {
        super(camelContext, producerTemplate, testDataHolder, objectMapper, testRestTemplate);
        this.petApi = petApi;
        this.eventsApi = eventsApi;
    }

    @Given("I have access to the actual petstore")
    public void iHaveAccessToTheActualPetstore() {
        log.info("I have access to the actual petstore");
        boolean isMock = Mockito.mockingDetails(petApi).isMock();
        assertThat(isMock).isFalse();
    }


    @And("I have access to the actual consumer")
    public void iHaveAccessToTheActualConsumer() {
        log.info("I have access to the actual consumer");
        boolean isMock = Mockito.mockingDetails(eventsApi).isMock();
        assertThat(isMock).isFalse();
    }
}
