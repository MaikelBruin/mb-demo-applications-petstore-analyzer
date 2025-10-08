package mb.demo.applications.petstore.analyzer.service.impl;

import lombok.extern.slf4j.Slf4j;
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

    public TotalsServiceImpl(PetApi petApi) {
        this.petApi = petApi;
    }

    @Override
    public TotalResponse getTotalNumberOfDogs() throws ApiException {
        List<Pet> dogs = petApi.findPetsByTags(List.of("dog"));
        TotalResponse response = new TotalResponse();
        response.setTotal(dogs.size());
        return response;
    }

    @Override
    public TotalResponse getTotalNumberOfCats() throws ApiException {
        List<Pet> dogs = petApi.findPetsByTags(List.of("cat"));
        TotalResponse response = new TotalResponse();
        response.setTotal(dogs.size());
        return response;
    }

    @Override
    public TotalResponse getTotalNumberOfAvailablePets() throws ApiException {
        List<Pet> available = petApi.findPetsByStatus("available");
        TotalResponse response = new TotalResponse();
        response.setTotal(available.size());
        return response;
    }
}
