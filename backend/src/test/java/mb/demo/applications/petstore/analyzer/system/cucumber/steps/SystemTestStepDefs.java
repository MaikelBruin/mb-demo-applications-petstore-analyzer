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
import mb.demo.applications.petstore.analyzer.exceptions.RestExceptionHandler;
import mb.demo.applications.petstore.analyzer.system.cucumber.config.SystemTestMockData;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.api.EventsApi;
import mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.model.EventRequestBody;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;
import mb.demos.openapi.generated.api.client.petstore.client.ApiResponse;
import mb.demos.openapi.generated.api.client.petstore.model.ModelApiResponse;
import mb.demos.openapi.generated.api.client.petstore.model.Pet;
import mb.demos.openapi.generated.api.client.petstore.model.Tag;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestClientResponseException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class SystemTestStepDefs extends BaseCucumberStepDefs {
    private final PetApi petApi;
    private final EventsApi eventsApi;
    private final RestExceptionHandler restExceptionHandler;
    private Integer lastHttpStatus;

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
    }

    @Given("I have access to the mocked petstore")
    public void iHaveAccessToTheMockedPetstore() {
        log.info("I have access to the mocked petstore");
        assertThat(petApi).isNotNull();
        boolean isMock = Mockito.mockingDetails(petApi).isMock();
        assertThat(isMock).isTrue();
    }

    @And("I have access to the mocked consumer")
    public void iHaveAccessToTheMockedConsumer() {
        log.info("I have access to the mocked consumer");
        assertThat(eventsApi).isNotNull();
        boolean isMock = Mockito.mockingDetails(eventsApi).isMock();
        assertThat(isMock).isTrue();
    }

    @And("I configure default responses for the consumer")
    public void iConfigureDefaultResponsesForTheConsumer() {
        Mockito.reset(eventsApi);
        Mockito.reset(restExceptionHandler);
    }

    @SneakyThrows
    @Given("I configured the consumer to respond with a teapot exception")
    public void iConfiguredTheConsumerToRespondWithATeapotException() {
        Mockito.when(eventsApi.processEvent(Mockito.any(EventRequestBody.class)))
                .thenThrow(new mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException(418, "I'm a teapot"));
    }

    @SneakyThrows
    @Given("I configured the consumer to respond with a service unavailable exception")
    public void iConfiguredTheConsumerToRespondWithAServiceUnavailableException() {
        Mockito.when(eventsApi.processEvent(Mockito.any(EventRequestBody.class)))
                .thenThrow(new mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException(503, "Service Unavailable"));
    }

    /**
     * The shared 'I get the total number of dogs' step can not be used for error responses: the rest template throws
     * on 4xx/5xx. Over HTTP only the mapped status is visible, not the exception type: use
     * 'the analyzer should have thrown a ConsumerDownException' to check that.
     */
    @When("I get the total number of dogs and capture the HTTP status")
    public void iGetTheTotalNumberOfDogsAndCaptureTheHttpStatus() {
        try {
            lastHttpStatus = testRestTemplate.getForEntity("/api/totals/dogs", String.class).getStatusCode().value();
        } catch (RestClientResponseException e) {
            lastHttpStatus = e.getStatusCode().value();
        }
    }

    @Then("the analyzer should have responded with HTTP status {int}")
    public void theAnalyzerShouldHaveRespondedWithHttpStatus(int expectedStatus) {
        assertThat(lastHttpStatus).isEqualTo(expectedStatus);
    }

    /**
     * Spring only calls RestExceptionHandler.handleConsumerDown for a ConsumerDownException, so a recorded call with
     * that argument proves which exception type the analyzer threw.
     */
    @Then("the analyzer should have thrown a ConsumerDownException")
    public void theAnalyzerShouldHaveThrownAConsumerDownException() {
        ArgumentCaptor<ConsumerDownException> captor = ArgumentCaptor.forClass(ConsumerDownException.class);
        Mockito.verify(restExceptionHandler).handleConsumerDown(captor.capture());
        assertThat(captor.getValue().getCode()).isEqualTo(503);
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

    @SneakyThrows
    @Given("I configured the petstore find pets by tags {string} response to return an empty array")
    public void iConfiguredThePetstoreFindPetsByTagsResponseToReturnAnEmptyArray(String tags) {
        List<String> tagList = Arrays.stream(tags.split(",")).toList();
        Mockito.when(petApi.findPetsByTagsWithHttpInfo(tagList)).thenReturn(new ApiResponse<>(200, new HashMap<>(), Collections.emptyList()));
        Mockito.when(petApi.findPetsByTags(tagList)).thenReturn(Collections.emptyList());
    }

    @SneakyThrows
    @Given("I configured the petstore find pets by tags {string} response to return a non-empty array")
    public void iConfiguredThePetstoreFindPetsByTagsResponseToReturnANonEmptyArray(String tags) {
        List<String> tagList = Arrays.stream(tags.split(",")).toList();
        Pet rat = new Pet()
                .tags(Collections.singletonList(new Tag().id(1L).name("rat")))
                .name("scruffy")
                .id(947L)
                .status(Pet.StatusEnum.AVAILABLE);
        Mockito.when(petApi.findPetsByTagsWithHttpInfo(tagList)).thenReturn(new ApiResponse<>(200, new HashMap<>(), Collections.singletonList(rat)));
        Mockito.when(petApi.findPetsByTags(tagList)).thenReturn(Collections.singletonList(rat));
    }


    @And("I configure default responses for the petstore")
    public void iConfigureDefaultResponsesForThePetstore() throws ApiException {
        HashMap<String, List<String>> defaultHeaders = SystemTestMockData.DEFAULT_HEADERS;
        Mockito.reset(petApi);
        ApiResponse<Pet> addPetApiResponse = new ApiResponse(200, defaultHeaders, SystemTestMockData.addPetResponseExample);
        Mockito.doReturn(addPetApiResponse).when(petApi).addPetWithHttpInfo(Mockito.nullable(Pet.class));
        Mockito.doReturn(SystemTestMockData.addPetResponseExample).when(petApi).addPet(Mockito.nullable(Pet.class));

        ApiResponse<Void> deletePetApiResponse = new ApiResponse(200, defaultHeaders);
        Mockito.doReturn(deletePetApiResponse).when(petApi).deletePetWithHttpInfo(Mockito.nullable(Long.class), Mockito.nullable(String.class));
        Mockito.doNothing().when(petApi).deletePet(Mockito.nullable(Long.class), Mockito.nullable(String.class));

        ApiResponse<List<Pet>> findPetsByStatusApiResponse = new ApiResponse(200, defaultHeaders, SystemTestMockData.findPetsByStatusResponseExample);
        Mockito.doReturn(findPetsByStatusApiResponse).when(petApi).findPetsByStatusWithHttpInfo(Mockito.nullable(String.class));
        Mockito.doReturn(SystemTestMockData.findPetsByStatusResponseExample).when(petApi).findPetsByStatus(Mockito.nullable(String.class));

        ApiResponse<List<Pet>> findPetsByTagsApiResponse = new ApiResponse(200, defaultHeaders, SystemTestMockData.findPetsByTagsResponseExample);
        Mockito.doReturn(findPetsByTagsApiResponse).when(petApi).findPetsByTagsWithHttpInfo(Mockito.nullable(List.class));
        Mockito.doReturn(SystemTestMockData.findPetsByTagsResponseExample).when(petApi).findPetsByTags(Mockito.nullable(List.class));

        ApiResponse<Pet> getPetByIdApiResponse = new ApiResponse(200, defaultHeaders, SystemTestMockData.getPetByIdResponseExample);
        Mockito.doReturn(getPetByIdApiResponse).when(petApi).getPetByIdWithHttpInfo(Mockito.nullable(Long.class));
        Mockito.doReturn(SystemTestMockData.getPetByIdResponseExample).when(petApi).getPetById(Mockito.nullable(Long.class));

        ApiResponse<Pet> updatePetApiResponse = new ApiResponse(200, defaultHeaders, SystemTestMockData.updatePetResponseExample);
        Mockito.doReturn(updatePetApiResponse).when(petApi).updatePetWithHttpInfo(Mockito.nullable(Pet.class));
        Mockito.doReturn(SystemTestMockData.updatePetResponseExample).when(petApi).updatePet(Mockito.nullable(Pet.class));

        ApiResponse<Pet> updatePetWithFormApiResponse = new ApiResponse(200, defaultHeaders, SystemTestMockData.updatePetWithFormResponseExample);
        Mockito.doReturn(updatePetWithFormApiResponse).when(petApi).updatePetWithFormWithHttpInfo(Mockito.nullable(Long.class), Mockito.nullable(String.class), Mockito.nullable(String.class));
        Mockito.doReturn(SystemTestMockData.updatePetWithFormResponseExample).when(petApi).updatePetWithForm(Mockito.nullable(Long.class), Mockito.nullable(String.class), Mockito.nullable(String.class));

        ApiResponse<ModelApiResponse> uploadFileApiResponse = new ApiResponse(200, defaultHeaders, SystemTestMockData.uploadFileResponseExample);
        Mockito.doReturn(uploadFileApiResponse).when(petApi).uploadFileWithHttpInfo(Mockito.nullable(Long.class), Mockito.nullable(String.class), Mockito.nullable(File.class));
        Mockito.doReturn(SystemTestMockData.uploadFileResponseExample).when(petApi).uploadFile(Mockito.nullable(Long.class), Mockito.nullable(String.class), Mockito.nullable(File.class));

    }
}
