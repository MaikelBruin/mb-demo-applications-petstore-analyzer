package mb.demo.applications.petstore.analyzer.service.impl;

import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.service.TotalsService;
import mb.demo.applications.petstore.analyzer.webapi.model.TotalResponse;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import mb.demos.openapi.generated.api.client.petstore.model.Pet;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TotalsServiceImpl implements TotalsService {

    private final PetApi petApi;

    public TotalsServiceImpl(PetApi petApi) {
        this.petApi = petApi;
    }

    @Override
    public TotalResponse getTotalNumberOfDogs() throws InterruptedException {
        int retries = 0;
        int maxRetries = 5;
        int pollInterval = 2000;
        List<Pet> dogs = null;

        while (retries < maxRetries && dogs == null) {
            try {
                dogs = petApi.findPetsByTags(List.of("dog"));
            } catch (Exception e) {
                retries++;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving total number of dogs failed, retrying another '{}' times... message: '{}'", retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        TotalResponse response = new TotalResponse();
        response.setTotal(dogs.size());
        return response;
    }

    @Override
    public TotalResponse getTotalNumberOfCats() throws InterruptedException {
        int retries = 0;
        int maxRetries = 5;
        int pollInterval = 2000;
        List<Pet> cats = null;

        while (retries < maxRetries && cats == null) {
            try {
                cats = petApi.findPetsByTags(List.of("cat"));
            } catch (Exception e) {
                retries++;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving total number of cats failed, retrying another '{}' times... message: '{}'", retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        TotalResponse response = new TotalResponse();
        response.setTotal(cats.size());
        return response;
    }

    @Override
    public TotalResponse getTotalNumberOfAvailablePets() throws InterruptedException {
        int retries = 0;
        int maxRetries = 5;
        int pollInterval = 2000;
        List<Pet> available = null;

        while (retries < maxRetries && available == null) {
            try {
                available = petApi.findPetsByStatus("available");
            } catch (Exception e) {
                retries++;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving total number of available pets failed, retrying another '{}' times... message: '{}'", retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        TotalResponse response = new TotalResponse();
        response.setTotal(available.size());
        return response;
    }

    @Override
    public TotalResponse findTotalNumberOfPets(String tag) throws InterruptedException {
        int retries = 0;
        int maxRetries = 5;
        int pollInterval = 2000;
        List<Pet> petsByTag = null;

        while (retries < maxRetries && petsByTag == null) {
            try {
                petsByTag = petApi.findPetsByTags(List.of(tag));
            } catch (Exception e) {
                retries++;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving total number of pets with tag '{}' failed, retrying another '{}' times... message: '{}'", tag, retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        TotalResponse response = new TotalResponse();
        response.setTotal(petsByTag.size());
        return response;
    }
}
