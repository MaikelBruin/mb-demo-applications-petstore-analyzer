package mb.demo.applications.petstore.analyzer.systemintegration.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.base.cucumber.TestDataHolder;
import mb.demo.applications.petstore.analyzer.base.cucumber.steps.BaseCucumberStepDefs;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApi;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.model.EventRequestBody;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.assertj.core.api.Assertions;
import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.springframework.boot.test.web.client.TestRestTemplate;


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
        Assertions.assertThat(isMock).isFalse();
    }

    /**
     * The actual consumer is allowed to be a spy (it calls the real client and only records invocations), but not a
     * stubbed one: a stub would replace the real behaviour. Invocations of earlier scenarios are cleared so that
     * scenarios can count from zero; stubs are untouched, so the check above can not be hidden by that.
     */
    @And("I have access to the actual consumer")
    public void iHaveAccessToTheActualConsumer() {
        log.info("I have access to the actual consumer");
        MockingDetails mockingDetails = Mockito.mockingDetails(eventsApi);
        Assertions.assertThat(mockingDetails.isSpy()).as("consumer client should be a spy").isTrue();
        Assertions.assertThat(mockingDetails.getStubbings()).as("consumer client stubbings").isEmpty();
        Mockito.clearInvocations(eventsApi);
    }

    @SneakyThrows
    @Then("the consumer should be notified {int} times")
    public void theConsumerShouldBeNotifiedTimes(int expectedNumberOfNotifications) {
        Mockito.verify(eventsApi, Mockito.times(expectedNumberOfNotifications)).processEvent(Mockito.any(EventRequestBody.class));
    }
}
