package mb.demo.applications.petstore.analyzer.integrated.cucumber.steps;

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
public class IntegratedStepDefs extends BaseCucumberStepDefs {

    private final PetApi petApiClient;
    private final EventsApi eventsApi;

    public IntegratedStepDefs(CamelContext camelContext,
                              ProducerTemplate producerTemplate,
                              TestDataHolder testDataHolder,
                              ObjectMapper objectMapper,
                              TestRestTemplate testRestTemplate,
                              final PetApi petApi,
                              final EventsApi eventsApi) {
        super(camelContext, producerTemplate, testDataHolder, objectMapper, testRestTemplate);
        this.petApiClient = petApi;
        this.eventsApi = eventsApi;
    }

    @Given("I have access to the actual petstore")
    public void iHaveAccessToTheActualPetstore() {
        log.info("I have access to the actual petstore");
        boolean isSpy = Mockito.mockingDetails(petApiClient).isSpy();
        assertThat(isSpy).isFalse();
    }


    @And("I have access to the actual consumer")
    public void iHaveAccessToTheActualConsumer() {
        log.info("I have access to the actual consumer");
        boolean isSpy = Mockito.mockingDetails(eventsApi).isSpy();
        assertThat(isSpy).isFalse();
    }
}
