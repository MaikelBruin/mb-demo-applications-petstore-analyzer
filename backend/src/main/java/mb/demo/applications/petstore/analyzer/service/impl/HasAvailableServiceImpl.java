package mb.demo.applications.petstore.analyzer.service.impl;

import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.service.HasAvailableService;
import mb.demo.applications.petstore.analyzer.webapi.model.HasAvailableResponse;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;
import mb.demos.openapi.generated.api.client.petstore.model.Pet;
import mb.demos.openapi.generated.api.client.petstore.model.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HasAvailableServiceImpl implements HasAvailableService {

    private final PetApi petApi;

    public HasAvailableServiceImpl(PetApi petApi) {
        this.petApi = petApi;
    }

    @Override
    public HasAvailableResponse getHasAvailableRats() throws ApiException, InterruptedException {
        int retries = 0;
        int maxRetries = 5;
        int pollInterval = 2000;
        List<Pet> rats = null;

        while (retries < maxRetries && rats == null) {
            try {
                rats = petApi.findPetsByTags(List.of("rat", "rats"));
            } catch (Exception e) {
                retries++;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving pets with tags failed, retrying another '{}' times... message: '{}'", retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        if (rats == null) {
            throw new ApiException("Could not retrieve pets by tag from petstore api!");
        }

        HasAvailableResponse hasAvailableResponse = new HasAvailableResponse();
        List<Pet> availableRats = rats
                .stream()
                .filter(pet -> pet.getStatus() != null)
                .filter(pet -> pet.getStatus().equals(Pet.StatusEnum.AVAILABLE))
                .toList();
        boolean hasRats = !availableRats.isEmpty();
        hasAvailableResponse.setHasAvailable(hasRats);
        return hasAvailableResponse;

    }
}
