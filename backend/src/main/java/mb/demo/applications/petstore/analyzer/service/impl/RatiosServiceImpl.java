package mb.demo.applications.petstore.analyzer.service.impl;

import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.service.RatiosService;
import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;
import mb.demos.openapi.generated.api.client.petstore.model.Pet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class RatiosServiceImpl implements RatiosService {

    private final PetApi petApi;

    public RatiosServiceImpl(PetApi petApi) {
        this.petApi = petApi;
    }

    @Override
    public AvailabilityRatioResponse getPetAvailabilityRatio() throws ApiException, InterruptedException {
        List<Pet> availablePets = null;
        List<Pet> soldPets = null;
        int retries = 0;
        int maxRetries = 5;
        int pollInterval = 2000;

        while (retries < maxRetries && availablePets == null) {
            try {
                availablePets = petApi.findPetsByStatus(Pet.StatusEnum.AVAILABLE.getValue());
            } catch (Exception e) {
                retries++;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving available pets failed, retrying another '{}' times... message: '{}'", retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        retries = 0;
        while (retries < maxRetries && soldPets == null) {
            try {
                soldPets = petApi.findPetsByStatus(Pet.StatusEnum.SOLD.getValue());
            } catch (Exception e) {
                retries++;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving sold pets failed, retrying another '{}' times... message: '{}'", retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        if (availablePets == null) {
            throw new ApiException("Could not get available pets after '" + maxRetries + "' retries");
        }

        if (soldPets == null) {
            throw new ApiException("Could not get sold pets after '" + maxRetries + "' retries");
        }


        AvailabilityRatioResponse response = new AvailabilityRatioResponse();
        response.setAvailablePets(availablePets.size());
        response.setSoldPets(soldPets.size());
        if (soldPets.isEmpty()) response.setRatio(BigDecimal.ZERO);
        else response.setRatio(BigDecimal.valueOf((float) availablePets.size() / soldPets.size()));
        return response;
    }

}
