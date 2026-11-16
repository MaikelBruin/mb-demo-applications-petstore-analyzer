package mb.demo.applications.petstore.analyzer.system.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.base.cucumber.TestDataHolder;
import mb.demo.applications.petstore.analyzer.base.cucumber.steps.BaseCucumberStepDefs;
import mb.demo.applications.petstore.analyzer.exceptions.ConsumerDownException;
import mb.demo.applications.petstore.analyzer.exceptions.RestExceptionHandler;
import mb.demo.applications.petstore.analyzer.exceptions.SupplierDownException;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.assertj.core.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.havingExactly;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestMockData.ANY_PET_PATH_REGEX;
import static mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestMockData.AVAILABLE_RAT_LIST_JSON;
import static mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestMockData.EMPTY_LIST_JSON;
import static mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestMockData.EVENT_ACCEPTED_JSON;
import static mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestMockData.FIND_PETS_BY_STATUS_PATH;
import static mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestMockData.FIND_PETS_BY_TAGS_PATH;
import static mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestMockData.PET_LIST_JSON;
import static mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestMockData.PROCESS_EVENT_PATH;
import static mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestWireMockConfiguration.CONSUMER_WIREMOCK;
import static mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestWireMockConfiguration.PETSTORE_WIREMOCK;


/**
 * The petstore and the consumer are WireMock servers: stubs describe HTTP requests and responses, not Java method
 * calls. That is why a single stub per endpoint is enough (no separate stubs for e.g. findPetsByTags and
 * findPetsByTagsWithHttpInfo), and why error scenarios return an HTTP status instead of throwing an ApiException: the
 * generated client turns it into the exception, exactly like in production.
 */
@Slf4j
public class SystemTestStepDefs extends BaseCucumberStepDefs {
    private final WireMockServer petstoreWireMock;
    private final WireMockServer consumerWireMock;
    private final RestExceptionHandler restExceptionHandler;
    private Integer lastHttpStatus;
    private String lastHttpBody;

    public SystemTestStepDefs(CamelContext camelContext,
                              ProducerTemplate producerTemplate,
                              TestDataHolder testDataHolder,
                              ObjectMapper objectMapper,
                              TestRestTemplate testRestTemplate,
                              @Qualifier(PETSTORE_WIREMOCK) final WireMockServer petstoreWireMock,
                              @Qualifier(CONSUMER_WIREMOCK) final WireMockServer consumerWireMock,
                              final RestExceptionHandler restExceptionHandler) {
        super(camelContext, producerTemplate, testDataHolder, objectMapper, testRestTemplate);
        this.petstoreWireMock = petstoreWireMock;
        this.consumerWireMock = consumerWireMock;
        this.restExceptionHandler = restExceptionHandler;
    }

    @Given("I have access to the mocked petstore")
    public void iHaveAccessToTheMockedPetstore() {
        log.info("I have access to the mocked petstore at '{}'", petstoreWireMock.baseUrl());
        Assertions.assertThat(petstoreWireMock.isRunning()).isTrue();
    }

    @And("I have access to the mocked consumer")
    public void iHaveAccessToTheMockedConsumer() {
        log.info("I have access to the mocked consumer at '{}'", consumerWireMock.baseUrl());
        Assertions.assertThat(consumerWireMock.isRunning()).isTrue();
    }

    /**
     * resetAll() removes the stubs and the recorded requests of the previous scenario: the servers live as long as
     * the spring context, which is shared by all scenarios.
     */
    @And("I configure default responses for the petstore")
    public void iConfigureDefaultResponsesForThePetstore() {
        petstoreWireMock.resetAll();
        petstoreWireMock.stubFor(get(urlPathEqualTo(FIND_PETS_BY_TAGS_PATH)).willReturn(okJson(PET_LIST_JSON)));
        petstoreWireMock.stubFor(get(urlPathEqualTo(FIND_PETS_BY_STATUS_PATH)).willReturn(okJson(PET_LIST_JSON)));
    }

    @And("I configure default responses for the consumer")
    public void iConfigureDefaultResponsesForTheConsumer() {
        consumerWireMock.resetAll();
        consumerWireMock.stubFor(post(urlPathEqualTo(PROCESS_EVENT_PATH)).willReturn(aResponse()
                .withStatus(202)
                .withHeader("Content-Type", "application/json")
                .withBody(EVENT_ACCEPTED_JSON)));
        Mockito.reset(restExceptionHandler);
    }

    /**
     * When several stubs match a request, WireMock uses the one added last: so this overrides the default response.
     */
    @Given("I configured the consumer to respond with a teapot exception")
    public void iConfiguredTheConsumerToRespondWithATeapotException() {
        consumerWireMock.stubFor(post(urlPathEqualTo(PROCESS_EVENT_PATH)).willReturn(aResponse().withStatus(418)));
    }

    @Given("I configured the consumer to respond with a service unavailable exception")
    public void iConfiguredTheConsumerToRespondWithAServiceUnavailableException() {
        consumerWireMock.stubFor(post(urlPathEqualTo(PROCESS_EVENT_PATH)).willReturn(serviceUnavailable()));
    }

    @Given("I configured the petstore to respond with a service unavailable exception")
    public void iConfiguredThePetstoreToRespondWithAServiceUnavailableException() {
        petstoreWireMock.stubFor(any(urlPathMatching(ANY_PET_PATH_REGEX)).willReturn(serviceUnavailable()));
    }

    @Given("I configured the petstore find pets by status {string} response to return an empty array")
    public void iConfiguredThePetstoreFindPetsByStatusResponseToReturnAnEmptyArray(String inputStatus) {
        petstoreWireMock.stubFor(get(urlPathEqualTo(FIND_PETS_BY_STATUS_PATH))
                .withQueryParam("status", equalTo(inputStatus))
                .willReturn(okJson(EMPTY_LIST_JSON)));
    }

    /**
     * The generated client sends a list as a repeated query parameter: ?tags=rat&tags=rats.
     */
    @Given("I configured the petstore find pets by tags {string} response to return an empty array")
    public void iConfiguredThePetstoreFindPetsByTagsResponseToReturnAnEmptyArray(String tags) {
        petstoreWireMock.stubFor(get(urlPathEqualTo(FIND_PETS_BY_TAGS_PATH))
                .withQueryParam("tags", havingExactly(tags.split(",")))
                .willReturn(okJson(EMPTY_LIST_JSON)));
    }

    @Given("I configured the petstore find pets by tags {string} response to return a non-empty array")
    public void iConfiguredThePetstoreFindPetsByTagsResponseToReturnANonEmptyArray(String tags) {
        petstoreWireMock.stubFor(get(urlPathEqualTo(FIND_PETS_BY_TAGS_PATH))
                .withQueryParam("tags", havingExactly(tags.split(",")))
                .willReturn(okJson(AVAILABLE_RAT_LIST_JSON)));
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

    /**
     * WireMock records every request it receives, so it can be verified just like the calls on a Mockito mock.
     */
    @Then("the consumer should not have received any event")
    public void theConsumerShouldNotHaveReceivedAnyEvent() {
        consumerWireMock.verify(0, anyRequestedFor(anyUrl()));
    }

    @Then("the consumer should have received an event of type {string}")
    public void theConsumerShouldHaveReceivedAnEventOfType(String expectedEventType) {
        consumerWireMock.verify(1, postRequestedFor(urlPathEqualTo(PROCESS_EVENT_PATH))
                .withRequestBody(matchingJsonPath("$.eventType", equalTo(expectedEventType))));
    }

    @Then("the consumer should be notified {int} times")
    public void theConsumerShouldBeNotifiedTimes(int expectedNumberOfNotifications) {
        consumerWireMock.verify(expectedNumberOfNotifications, postRequestedFor(urlPathEqualTo(PROCESS_EVENT_PATH)));
    }
}
