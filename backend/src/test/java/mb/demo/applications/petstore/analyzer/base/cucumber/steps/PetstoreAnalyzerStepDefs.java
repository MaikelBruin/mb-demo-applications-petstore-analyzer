package mb.demo.applications.petstore.analyzer.base.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.base.cucumber.TestDataHolder;
import mb.demo.applications.petstore.analyzer.webapi.model.TotalResponse;
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

    @When("I get the endpoint {string} without parameters")
    public void iGetTheEndpointWithoutParameters(String endpoint) {

    }

    @When("I get the total number of dogs")
    public void iGetTheTotalNumberOfDogs() {
        log.info("getting total number of dogs...");
        String fullUri = "/api/totals/dogs";
        TotalResponse response = testRestTemplate.getForObject(fullUri, TotalResponse.class);
        testDataHolder.setTotalDogsResponse(response);
    }

    @Then("the total number of dogs response should not be null")
    public void theTotalNumberOfDogsResponseShouldNotBeNull() {
        assertThat(testDataHolder.getTotalDogsResponse()).isNotNull();
    }

    @When("I get the total number of cats")
    public void iGetTheTotalNumberOfCats() {
        log.info("getting total number of cats...");
        String fullUri = "/api/totals/cats";
        TotalResponse response = testRestTemplate.getForObject(fullUri, TotalResponse.class);
        testDataHolder.setTotalCatsResponse(response);
    }

    @Then("the total number of cats response should not be null")
    public void theTotalNumberOfCatsResponseShouldNotBeNull() {
        assertThat(testDataHolder.getTotalCatsResponse()).isNotNull();
    }

    @And("the total number of dogs should be equal than or greater than {int}")
    public void theTotalNumberOfDogsShouldBeEqualThanOrGreaterThan(int minimumNumberOfDogsExpected) {
        assertThat(testDataHolder.getTotalDogsResponse().getTotal()).isGreaterThanOrEqualTo(minimumNumberOfDogsExpected);
    }

    @And("the total number of cats should be equal than or greater than {int}")
    public void theTotalNumberOfCatsShouldBeEqualThanOrGreaterThan(int minimumNumberOfCatsExpected) {
        assertThat(testDataHolder.getTotalCatsResponse().getTotal()).isGreaterThanOrEqualTo(minimumNumberOfCatsExpected);
    }
}
