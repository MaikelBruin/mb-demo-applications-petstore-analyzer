package mb.demo.applications.petstore.analyzer.system.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.base.cucumber.TestDataHolder;
import mb.demo.applications.petstore.analyzer.base.cucumber.steps.BaseCucumberStepDefs;
import mb.demo.applications.petstore.analyzer.exceptions.ConsumerDownException;
import mb.demo.applications.petstore.analyzer.exceptions.SupplierDownException;
import mb.demo.applications.petstore.analyzer.exceptions.RestExceptionHandler;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApi;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApiMockUtil;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.model.EventRequestBody;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import mb.demos.openapi.generated.api.client.petstore.api.PetApiMockUtil;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;
import mb.demos.openapi.generated.api.client.petstore.client.ApiResponse;
import mb.demos.openapi.generated.api.client.petstore.model.Pet;
import mb.demos.openapi.generated.api.client.petstore.model.Tag;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.assertj.core.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Slf4j
public class SystemTestStepDefs extends BaseCucumberStepDefs {
    private final PetApi petApi;
    private final EventsApi eventsApi;
    private final RestExceptionHandler restExceptionHandler;
    private Integer lastHttpStatus;
    private String lastHttpBody;
    private PetApiMockUtil petApiMockUtil;
    private EventsApiMockUtil eventsApiMockUtil;

    public SystemTestStepDefs(CamelContext camelContext,
                              ProducerTemplate producerTemplate,
                              TestDataHolder testDataHolder,
                              ObjectMapper objectMapper,
                              TestRestTemplate testRestTemplate,
                              final PetApi petApi,
                              final EventsApi eventsApi,
                              final RestExceptionHandler restExceptionHandler) {
        super(camelContext, producerTemplate, testDataHolder, objectMapper, testRestTemplate);
        this.petApi = petApi;
        this.eventsApi = eventsApi;
        this.restExceptionHandler = restExceptionHandler;
        this.petApiMockUtil = new PetApiMockUtil(petApi);
        this.eventsApiMockUtil = new EventsApiMockUtil(eventsApi);

    }

    @Given("I have access to the mocked petstore")
    public void iHaveAccessToTheMockedPetstore() {
        log.info("I have access to the mocked petstore");
        Assertions.assertThat(petApi).isNotNull();
        boolean isMock = Mockito.mockingDetails(petApi).isMock();
        Assertions.assertThat(isMock).isTrue();
    }

    @And("I have access to the mocked consumer")
    public void iHaveAccessToTheMockedConsumer() {
        log.info("I have access to the mocked consumer");
        Assertions.assertThat(eventsApi).isNotNull();
        boolean isMock = Mockito.mockingDetails(eventsApi).isMock();
        Assertions.assertThat(isMock).isTrue();
    }

    @And("I configure default responses for the petstore")
    public void iConfigureDefaultResponsesForThePetstore() {
        petApiMockUtil.resetToDefaults();
    }

    @And("I configure default responses for the consumer")
    public void iConfigureDefaultResponsesForTheConsumer() {
        eventsApiMockUtil.resetToDefaults();
        Mockito.reset(restExceptionHandler);
    }

    @SneakyThrows
    @Given("I configured the consumer to respond with a teapot exception")
    public void iConfiguredTheConsumerToRespondWithATeapotException() {
        eventsApiMockUtil.stubProcessEventThrows(418, "I'm a teapot");
    }

    @SneakyThrows
    @Given("I configured the consumer to respond with a service unavailable exception")
    public void iConfiguredTheConsumerToRespondWithAServiceUnavailableException() {
        eventsApiMockUtil.stubProcessEventThrows(503, "Service Unavailable");
    }

    @SneakyThrows
    @Given("I configured the petstore to respond with a service unavailable exception")
    public void iConfiguredThePetstoreToRespondWithAServiceUnavailableException() {
        petApiMockUtil.stubFindPetsByTagsThrows(503, "Service Unavailable");
    }

    /**
     * The shared 'I get the total number of dogs' step can not be used for error responses: the rest template throws
     * on 4xx/5xx. Over HTTP only the mapped status is visible, not the exception type: use
     * 'the analyzer should have thrown a ConsumerDownException' to check that.
     */
    @When("I get the total number of dogs and capture the HTTP status")
    public void iGetTheTotalNumberOfDogsAndCaptureTheHttpStatus() {
        try {
            ResponseEntity<String> response = testRestTemplate.getForEntity("/api/totals/dogs", String.class);
            lastHttpStatus = response.getStatusCode().value();
            lastHttpBody = response.getBody();
        } catch (RestClientResponseException e) {
            lastHttpStatus = e.getStatusCode().value();
            lastHttpBody = e.getResponseBodyAsString();
        }
    }

    /**
     * The message of the exception is what an API caller gets as the 'detail' of the error response (a ProblemDetail).
     */
    @SneakyThrows
    @Then("the error message of the response should be {string}")
    public void theErrorMessageOfTheResponseShouldBe(String expectedMessage) {
        Assertions.assertThat(lastHttpBody).as("response body").isNotBlank();
        Assertions.assertThat(objectMapper.readTree(lastHttpBody).path("detail").asText()).isEqualTo(expectedMessage);
    }

    @Then("the analyzer should have responded with HTTP status {int}")
    public void theAnalyzerShouldHaveRespondedWithHttpStatus(int expectedStatus) {
        Assertions.assertThat(lastHttpStatus).isEqualTo(expectedStatus);
    }

    /**
     * Spring only calls RestExceptionHandler.handleConsumerDown for a ConsumerDownException, so a recorded call with
     * that argument proves which exception type the analyzer threw.
     */
    @Then("the analyzer should have thrown a ConsumerDownException")
    public void theAnalyzerShouldHaveThrownAConsumerDownException() {
        ArgumentCaptor<ConsumerDownException> captor = ArgumentCaptor.forClass(ConsumerDownException.class);
        Mockito.verify(restExceptionHandler).handleConsumerDown(captor.capture());
        Assertions.assertThat(captor.getValue().getCode()).isEqualTo(503);
    }

    /**
     * Same approach as for the ConsumerDownException: the handler is only called for a SupplierDownException.
     */
    @Then("the analyzer should have thrown a SupplierDownException")
    public void theAnalyzerShouldHaveThrownASupplierDownException() {
        ArgumentCaptor<SupplierDownException> captor = ArgumentCaptor.forClass(SupplierDownException.class);
        Mockito.verify(restExceptionHandler).handleSupplierDown(captor.capture());
        Assertions.assertThat(captor.getValue().getCode()).isEqualTo(503);
    }

    @Then("the consumer should not have received any event")
    public void theConsumerShouldNotHaveReceivedAnyEvent() {
        Mockito.verifyNoInteractions(eventsApi);
    }

    @SneakyThrows
    @Then("the consumer should have received an event of type {string}")
    public void theConsumerShouldHaveReceivedAnEventOfType(String eventType) {
        Mockito.verify(eventsApi).processEvent(Mockito.argThat(body -> eventType.equals(body.getEventType())));
    }

    @SneakyThrows
    @Given("I configured the petstore find pets by status {string} response to return an empty array")
    public void iConfiguredThePetstoreFindPetsByStatusResponseToReturnAnEmptyArray(String inputStatus) {
        Mockito.when(petApi.findPetsByStatusWithHttpInfo(inputStatus)).thenReturn(new ApiResponse<>(200, new HashMap<>(), Collections.emptyList()));
        Mockito.when(petApi.findPetsByStatus(inputStatus)).thenReturn(Collections.emptyList());
    }

    @Given("I configured the petstore find pets by tags {string} response to return an empty array")
    public void iConfiguredThePetstoreFindPetsByTagsResponseToReturnAnEmptyArray(String tags) {
        // the mock util stubs findPetsByTags for any tags, not only for the given ones
        petApiMockUtil.stubFindPetsByTags(Collections.emptyList());
    }

    @Given("I configured the petstore find pets by tags {string} response to return a non-empty array")
    public void iConfiguredThePetstoreFindPetsByTagsResponseToReturnANonEmptyArray(String tags) {
        Pet rat = new Pet()
                .tags(Collections.singletonList(new Tag().id(1L).name("rat")))
                .name("scruffy")
                .id(947L)
                .status(Pet.StatusEnum.AVAILABLE);
        // the mock util stubs findPetsByTags for any tags, not only for the given ones
        petApiMockUtil.stubFindPetsByTags(Collections.singletonList(rat));
    }

    @SneakyThrows
    @Then("the consumer should be notified {int} times")
    public void theConsumerShouldBeNotifiedTimes(int expectedNumberOfNotifications) {
        Mockito.verify(eventsApi, Mockito.times(expectedNumberOfNotifications)).processEvent(Mockito.any(EventRequestBody.class));
    }
}
