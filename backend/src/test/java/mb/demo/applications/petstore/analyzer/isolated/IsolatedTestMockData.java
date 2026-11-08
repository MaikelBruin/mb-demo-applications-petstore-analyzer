package mb.demo.applications.petstore.analyzer.isolated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mb.demos.openapi.generated.api.client.petstore.model.ModelApiResponse;
import mb.demos.openapi.generated.api.client.petstore.model.Pet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class IsolatedTestMockData {

    public static final HashMap<String, List<String>> DEFAULT_HEADERS = new HashMap<String, List<String>>() {
        {
            this.put("Content-Type", Collections.singletonList("application/json"));
            this.put("Accept", Collections.singletonList("application/json"));
            this.put("X-Source-System", Collections.singletonList("eu-nl-PetApi"));
            this.put("X-Author-Id", Collections.singletonList("eu-nl-PetApi"));
            this.put("X-Correlation-Id", Collections.singletonList("PetApi-correlation-id"));
        }
    };

    public static Pet addPetResponseExample;
    public static List<Pet> findPetsByStatusResponseExample;
    public static List<Pet> findPetsByTagsResponseExample;
    public static Pet getPetByIdResponseExample;
    public static Pet updatePetResponseExample;
    public static Pet updatePetWithFormResponseExample;
    public static ModelApiResponse uploadFileResponseExample;


    static {
        try {
            addPetResponseExample = new ObjectMapper().readValue("{\n  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],\n  \"name\" : \"doggie\",\n  \"id\" : 10,\n  \"category\" : {\n    \"name\" : \"Dogs\",\n    \"id\" : 1\n  },\n  \"tags\" : [ {\n    \"name\" : \"name\",\n    \"id\" : 0\n  }, {\n    \"name\" : \"name\",\n    \"id\" : 0\n  } ],\n  \"status\" : \"available\"\n}\n", Pet.class);
            Pet[] findPetsByStatusResponseExampleArray = new ObjectMapper().readValue("                [ {\n  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],\n  \"name\" : \"doggie\",\n  \"id\" : 10,\n  \"category\" : {\n    \"name\" : \"Dogs\",\n    \"id\" : 1\n  },\n  \"tags\" : [ {\n    \"name\" : \"name\",\n    \"id\" : 0\n  }, {\n    \"name\" : \"name\",\n    \"id\" : 0\n  } ],\n  \"status\" : \"available\"\n}, {\n  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],\n  \"name\" : \"doggie\",\n  \"id\" : 10,\n  \"category\" : {\n    \"name\" : \"Dogs\",\n    \"id\" : 1\n  },\n  \"tags\" : [ {\n    \"name\" : \"name\",\n    \"id\" : 0\n  }, {\n    \"name\" : \"name\",\n    \"id\" : 0\n  } ],\n  \"status\" : \"available\"\n} ]\n", Pet[].class);
            findPetsByStatusResponseExample = Arrays.stream(findPetsByStatusResponseExampleArray).toList();
            Pet[] findPetsByTagsResponseExampleArray = new ObjectMapper().readValue("                [ {\n  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],\n  \"name\" : \"doggie\",\n  \"id\" : 10,\n  \"category\" : {\n    \"name\" : \"Dogs\",\n    \"id\" : 1\n  },\n  \"tags\" : [ {\n    \"name\" : \"name\",\n    \"id\" : 0\n  }, {\n    \"name\" : \"name\",\n    \"id\" : 0\n  } ],\n  \"status\" : \"available\"\n}, {\n  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],\n  \"name\" : \"doggie\",\n  \"id\" : 10,\n  \"category\" : {\n    \"name\" : \"Dogs\",\n    \"id\" : 1\n  },\n  \"tags\" : [ {\n    \"name\" : \"name\",\n    \"id\" : 0\n  }, {\n    \"name\" : \"name\",\n    \"id\" : 0\n  } ],\n  \"status\" : \"available\"\n} ]\n", Pet[].class);
            findPetsByTagsResponseExample = Arrays.stream(findPetsByTagsResponseExampleArray).toList();
            getPetByIdResponseExample = new ObjectMapper().readValue("                                {\n  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],\n  \"name\" : \"doggie\",\n  \"id\" : 10,\n  \"category\" : {\n    \"name\" : \"Dogs\",\n    \"id\" : 1\n  },\n  \"tags\" : [ {\n    \"name\" : \"name\",\n    \"id\" : 0\n  }, {\n    \"name\" : \"name\",\n    \"id\" : 0\n  } ],\n  \"status\" : \"available\"\n}\n", Pet.class);
            updatePetResponseExample = new ObjectMapper().readValue("                                {\n  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],\n  \"name\" : \"doggie\",\n  \"id\" : 10,\n  \"category\" : {\n    \"name\" : \"Dogs\",\n    \"id\" : 1\n  },\n  \"tags\" : [ {\n    \"name\" : \"name\",\n    \"id\" : 0\n  }, {\n    \"name\" : \"name\",\n    \"id\" : 0\n  } ],\n  \"status\" : \"available\"\n}\n", Pet.class);
            updatePetWithFormResponseExample = new ObjectMapper().readValue("                                {\n  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],\n  \"name\" : \"doggie\",\n  \"id\" : 10,\n  \"category\" : {\n    \"name\" : \"Dogs\",\n    \"id\" : 1\n  },\n  \"tags\" : [ {\n    \"name\" : \"name\",\n    \"id\" : 0\n  }, {\n    \"name\" : \"name\",\n    \"id\" : 0\n  } ],\n  \"status\" : \"available\"\n}\n", Pet.class);
            uploadFileResponseExample = new ObjectMapper().readValue("                                {\n  \"code\" : 0,\n  \"type\" : \"type\",\n  \"message\" : \"message\"\n}\n", ModelApiResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
