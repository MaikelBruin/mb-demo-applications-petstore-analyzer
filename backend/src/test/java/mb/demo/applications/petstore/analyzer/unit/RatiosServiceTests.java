package mb.demo.applications.petstore.analyzer.unit;

import mb.demo.applications.petstore.analyzer.exceptions.ConsumerDownException;
import mb.demo.applications.petstore.analyzer.exceptions.SupplierDownException;
import mb.demo.applications.petstore.analyzer.services.EventService;
import mb.demo.applications.petstore.analyzer.services.RatiosService;
import mb.demo.applications.petstore.analyzer.services.impl.RatiosServiceImpl;
import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;
import mb.demos.openapi.generated.api.client.petstore.model.Pet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;


/**
 * Example unit tests: {@link RatiosServiceImpl} is tested in isolation, both of its collaborators are mocked.
 */
public class RatiosServiceTests {

    private RatiosService ratiosService;
    private EventService eventService;
    private PetApi petApi;

    @BeforeEach
    void setUp() {
        eventService = Mockito.mock(EventService.class);
        petApi = Mockito.mock(PetApi.class);
        ratiosService = new RatiosServiceImpl(petApi, eventService);
    }

    @Test
    void ratio_isTheNumberOfAvailablePetsDividedByTheNumberOfSoldPets() throws Exception {
        Mockito.when(petApi.findPetsByStatus("available")).thenReturn(pets(6));
        Mockito.when(petApi.findPetsByStatus("sold")).thenReturn(pets(4));

        AvailabilityRatioResponse response = ratiosService.getPetAvailabilityRatio();

        Assertions.assertThat(response.getAvailablePets()).isEqualTo(6);
        Assertions.assertThat(response.getSoldPets()).isEqualTo(4);
        Assertions.assertThat(response.getRatio()).isEqualByComparingTo(new BigDecimal("1.5"));
    }

    @Test
    void ratio_isZeroWhenNoPetsAreSold() throws Exception {
        Mockito.when(petApi.findPetsByStatus("available")).thenReturn(pets(6));
        Mockito.when(petApi.findPetsByStatus("sold")).thenReturn(Collections.emptyList());

        AvailabilityRatioResponse response = ratiosService.getPetAvailabilityRatio();

        Assertions.assertThat(response.getRatio()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    /**
     * Argument matchers: the stubs above only match the exact arguments "available" and "sold". Here we verify that
     * the petstore was asked for exactly those statuses, and that the event for the consumer got the right type and
     * the same response that is returned to the caller.
     */
    @Test
    void consumerIsNotifiedWithTheResponse() throws Exception {
        Mockito.when(petApi.findPetsByStatus("available")).thenReturn(pets(2));
        Mockito.when(petApi.findPetsByStatus("sold")).thenReturn(pets(1));

        AvailabilityRatioResponse response = ratiosService.getPetAvailabilityRatio();

        Mockito.verify(petApi).findPetsByStatus("available");
        Mockito.verify(petApi).findPetsByStatus("sold");
        Mockito.verify(petApi, Mockito.never()).findPetsByStatus("pending");
        Mockito.verify(eventService).processEvent(Mockito.eq("availability-ratio"), Mockito.same(response));
    }

    /**
     * Hitting an exception: the petstore is down. The service must not retry (a retry only makes sense for the
     * intermittent 500s), must not ask for the sold pets anymore and must not tell the consumer anything.
     */
    @Test
    void petstoreRespondsWith503_supplierDownExceptionIsThrownWithoutRetry() throws Exception {
        Mockito.when(petApi.findPetsByStatus("available")).thenThrow(new ApiException(503, "Service Unavailable"));

        Assertions.assertThatThrownBy(() -> ratiosService.getPetAvailabilityRatio())
                .isInstanceOfSatisfying(SupplierDownException.class, e -> Assertions.assertThat(e.getCode()).isEqualTo(503));

        Mockito.verify(petApi, Mockito.times(1)).findPetsByStatus(Mockito.anyString());
        Mockito.verifyNoInteractions(eventService);
    }

    @Test
    void consumerIsDown_consumerDownExceptionIsPassedOn() throws Exception {
        Mockito.when(petApi.findPetsByStatus(Mockito.anyString())).thenReturn(pets(1));
        Mockito.when(eventService.processEvent(Mockito.anyString(), Mockito.any()))
                .thenThrow(new ConsumerDownException("consumer is down"));

        Assertions.assertThatThrownBy(() -> ratiosService.getPetAvailabilityRatio()).isInstanceOf(ConsumerDownException.class);
    }

    /**
     * Hitting a specific code path: the retry loop. The first call fails with a (non 503) error, the second succeeds.
     * Note that this unit test takes about 2 seconds, because the service really waits between the attempts.
     */
    @Test
    void petstoreFailsOnceWithAnInternalServerError_requestIsRetried() throws Exception {
        Mockito.when(petApi.findPetsByStatus("available"))
                .thenThrow(new ApiException(500, "Internal Server Error"))
                .thenReturn(pets(3));
        Mockito.when(petApi.findPetsByStatus("sold")).thenReturn(pets(3));

        AvailabilityRatioResponse response = ratiosService.getPetAvailabilityRatio();

        Assertions.assertThat(response.getAvailablePets()).isEqualTo(3);
        Mockito.verify(petApi, Mockito.times(2)).findPetsByStatus("available");
    }

    private static List<Pet> pets(int count) {
        return IntStream.range(0, count).mapToObj(i -> new Pet().id((long) i).name("pet-" + i)).toList();
    }
}
