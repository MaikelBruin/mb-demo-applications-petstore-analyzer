package mb.demo.applications.petstore.analyzer.base.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.base.cucumber.TestDataHolder;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import mb.demos.openapi.generated.api.client.petstore.api.PetApiClient;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.ExtractingResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Slf4j
public abstract class BaseCucumberStepDefs {
    protected final CamelContext camelContext;
    protected final ProducerTemplate producerTemplate;
    protected final TestRestTemplate testRestTemplate;
    protected final TestDataHolder testDataHolder;
    protected final ObjectMapper objectMapper;
    protected final PetApi petApi;

    public BaseCucumberStepDefs(final CamelContext camelContext, final ProducerTemplate producerTemplate, final TestDataHolder testDataHolder, final ObjectMapper objectMapper, final TestRestTemplate testRestTemplate, final PetApi petApi) {
        this.camelContext = camelContext;
        this.producerTemplate = producerTemplate;
        this.testRestTemplate = testRestTemplate;
        this.testDataHolder = testDataHolder;
        this.objectMapper = objectMapper;
        this.petApi = petApi;
        RestTemplate restTemplate = testRestTemplate.getRestTemplate();
        restTemplate.setErrorHandler(new ExtractingResponseErrorHandler());
    }
}
