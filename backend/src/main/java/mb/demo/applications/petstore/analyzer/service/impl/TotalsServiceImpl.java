package mb.demo.applications.petstore.analyzer.service.impl;

import mb.demo.applications.petstore.analyzer.exceptions.SupplierDownException;
import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.service.EventService;
import mb.demo.applications.petstore.analyzer.service.TotalsService;
import mb.demo.applications.petstore.analyzer.webapi.model.TotalResponse;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;
import mb.demos.openapi.generated.api.client.petstore.model.Pet;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TotalsServiceImpl implements TotalsService {

    private final PetApi petApi;
    private EventService eventService;

    public TotalsServiceImpl(PetApi petApi, EventService eventService) {
        this.petApi = petApi;
        this.eventService = eventService;
    }

    @Override
    public TotalResponse getTotalNumberOfDogs() throws InterruptedException, ApiException, mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException {
        int retries = 0;
        int maxRetries = 5;
        int pollInterval = 2000;
        List<Pet> dogs = null;

        while (retries < maxRetries && dogs == null) {
            try {
                dogs = petApi.findPetsByTags(List.of("dog"));
            } catch (Exception e) {
                SupplierDownException.throwIfServiceUnavailable(e);
                retries++;
                if (retries == maxRetries) throw e;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving total number of dogs failed, retrying another '{}' times... message: '{}'", retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        TotalResponse response = new TotalResponse();
        response.setTotal(dogs.size());
        eventService.processEvent("total-dogs", response);
        return response;
    }

    @Override
    public TotalResponse getTotalNumberOfCats() throws InterruptedException, ApiException, mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException {
        int retries = 0;
        int maxRetries = 5;
        int pollInterval = 2000;
        List<Pet> cats = null;

        while (retries < maxRetries && cats == null) {
            try {
                cats = petApi.findPetsByTags(List.of("cat"));
            } catch (Exception e) {
                SupplierDownException.throwIfServiceUnavailable(e);
                retries++;
                if (retries == maxRetries) throw e;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving total number of cats failed, retrying another '{}' times... message: '{}'", retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        TotalResponse response = new TotalResponse();
        response.setTotal(cats.size());
        eventService.processEvent("total-cats", response);
        return response;
    }

    @Override
    public TotalResponse getTotalNumberOfAvailablePets() throws InterruptedException, ApiException, mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException {
        int retries = 0;
        int maxRetries = 5;
        int pollInterval = 2000;
        List<Pet> available = null;

        while (retries < maxRetries && available == null) {
            try {
                available = petApi.findPetsByStatus("available");
            } catch (Exception e) {
                SupplierDownException.throwIfServiceUnavailable(e);
                retries++;
                if (retries == maxRetries) throw e;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving total number of available pets failed, retrying another '{}' times... message: '{}'", retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        TotalResponse response = new TotalResponse();
        response.setTotal(available.size());
        eventService.processEvent("total-available", response);
        return response;
    }

    @Override
    public TotalResponse findTotalNumberOfPets(String tag) throws InterruptedException, ApiException, mb.demos.openapi.generated.api.client.petstore.analyzer.consumer.client.ApiException {
        int retries = 0;
        int maxRetries = 5;
        int pollInterval = 2000;
        List<Pet> petsByTag = null;

        while (retries < maxRetries && petsByTag == null) {
            try {
                petsByTag = petApi.findPetsByTags(List.of(tag));
            } catch (Exception e) {
                SupplierDownException.throwIfServiceUnavailable(e);
                retries++;
                if (retries == maxRetries) throw e;
                int retriesRemaining = maxRetries - retries;
                log.warn("retrieving total number of pets with tag '{}' failed, retrying another '{}' times... message: '{}'", tag, retriesRemaining, e.getMessage());
                Thread.sleep(pollInterval);
            }
        }

        TotalResponse response = new TotalResponse();
        response.setTotal(petsByTag.size());
        eventService.processEvent("total-pets", response);
        return response;
    }
}
