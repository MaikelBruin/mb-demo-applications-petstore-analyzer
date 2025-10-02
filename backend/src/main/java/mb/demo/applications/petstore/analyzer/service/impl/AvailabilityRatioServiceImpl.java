package mb.demo.applications.petstore.analyzer.service.impl;

import lombok.extern.slf4j.Slf4j;
import mb.demo.applications.petstore.analyzer.service.AvailabilityService;
import mb.demo.applications.petstore.analyzer.webapi.model.AvailabilityRatioResponse;
import mb.demos.openapi.generated.api.client.petstore.api.PetApi;
import mb.demos.openapi.generated.api.client.petstore.client.ApiException;
import mb.demos.openapi.generated.api.client.petstore.model.Pet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class AvailabilityRatioServiceImpl implements AvailabilityService {

    private final PetApi petApi;

    public AvailabilityRatioServiceImpl(PetApi petApi) {
        this.petApi = petApi;
    }

    @Override
    public AvailabilityRatioResponse getPetAvailabilityRatio() throws ApiException {
        List<Pet> availablePets = petApi.findPetsByStatus(Pet.StatusEnum.AVAILABLE.getValue());
        List<Pet> soldPets = petApi.findPetsByStatus(Pet.StatusEnum.SOLD.getValue());
        AvailabilityRatioResponse response = new AvailabilityRatioResponse();
        response.setAvailablePets(availablePets.size());
        response.setSoldPets(soldPets.size());
        response.setRatio(BigDecimal.valueOf((float) availablePets.size() / soldPets.size()));
        return response;
    }
}
