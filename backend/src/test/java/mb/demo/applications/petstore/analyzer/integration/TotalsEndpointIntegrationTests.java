package mb.demo.applications.petstore.analyzer.integration;

import mb.demo.applications.petstore.analyzer.PetstoreAnalyzerApplication;
import mb.demo.applications.petstore.analyzer.exceptions.ConsumerDownException;
import mb.demo.applications.petstore.analyzer.service.TotalsService;
import mb.demo.applications.petstore.analyzer.webapi.model.TotalResponse;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Example of an integration test: it checks the integration between multiple units of the system.
 * <p>
 * Real (integrated): {@code TotalsRestController}, the camel route {@code TotalsRouteBuilder} and the
 * {@code RestExceptionHandler}, wired together by Spring. A request goes through the controller, over the camel route
 * to the service, and the result or exception goes back the same way.
 * <br>
 * Mocked (outside the scope of this test): {@link TotalsService}, a part of this application. This lets us decide what
 * the service returns or throws without caring about how it does its work, which is covered by other tests. The
 * generated clients ({@code PetApi}, {@code EventsApi}) are not part of this application and are never reached,
 * because the service that uses them is mocked.
 * <p>
 * Compare with the other layers: a unit test would test one class in isolation, the system tests boot the whole
 * application and only mock the generated clients, calling it over HTTP.
 * <p>
 * The name ends with 'IntegrationTests' so that it is run by failsafe instead of surefire (see backend/pom.xml).
 */
@SpringBootTest(classes = PetstoreAnalyzerApplication.class)
@AutoConfigureMockMvc
class TotalsEndpointIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TotalsService totalsService;

    @Test
    void getTotalNumberOfDogs_returnsWhatTheServiceReturns() throws Exception {
        Mockito.when(totalsService.getTotalNumberOfDogs()).thenReturn(new TotalResponse().total(3));

        mockMvc.perform(get("/api/totals/dogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3));
    }

    @Test
    void findTotalNumberOfPets_passesTheTagParameterOnToTheService() throws Exception {
        Mockito.when(totalsService.findTotalNumberOfPets("rat")).thenReturn(new TotalResponse().total(7));

        mockMvc.perform(get("/api/totals").param("tag", "rat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(7));

        Mockito.verify(totalsService).findTotalNumberOfPets("rat");
    }

    @Test
    void consumerDown_isMappedToServiceUnavailable() throws Exception {
        Mockito.when(totalsService.getTotalNumberOfDogs()).thenThrow(new ConsumerDownException("consumer is down"));

        mockMvc.perform(get("/api/totals/dogs"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }

    /**
     * Exceptions without a mapping are not handled by the application. MockMvc has no servlet container to turn them
     * into a 500 response (a real server does), so they surface as the cause of a ServletException. The cause being the
     * original exception shows that it is not wrapped in a camel exception on its way back through the route.
     */
    @Test
    void otherServiceExceptions_areNotMapped_andReachTheServletAsTheOriginalException() throws Exception {
        Mockito.when(totalsService.getTotalNumberOfDogs()).thenThrow(new ApiException("Could not retrieve pets"));

        assertThatThrownBy(() -> mockMvc.perform(get("/api/totals/dogs")))
                .hasCauseInstanceOf(ApiException.class)
                .hasRootCauseMessage("Could not retrieve pets");
    }
}
