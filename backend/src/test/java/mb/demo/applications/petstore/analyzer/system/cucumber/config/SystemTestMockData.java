package mb.demo.applications.petstore.analyzer.system.cucumber.config;

/**
 * Paths and JSON response bodies for the WireMock stubs. The paths are relative to the base urls of the services,
 * exactly as the generated clients append them.
 */
public final class SystemTestMockData {

    // ---------------------------------------------------------------- petstore

    public static final String FIND_PETS_BY_TAGS_PATH = "/pet/findByTags";
    public static final String FIND_PETS_BY_STATUS_PATH = "/pet/findByStatus";
    /** Matches every endpoint of the petstore PetApi. */
    public static final String ANY_PET_PATH_REGEX = "/pet.*";

    public static final String PET_LIST_JSON = """
            [ {
              "id" : 10,
              "name" : "doggie",
              "category" : { "id" : 1, "name" : "Dogs" },
              "photoUrls" : [ "photoUrls", "photoUrls" ],
              "tags" : [ { "id" : 0, "name" : "name" }, { "id" : 0, "name" : "name" } ],
              "status" : "available"
            }, {
              "id" : 10,
              "name" : "doggie",
              "category" : { "id" : 1, "name" : "Dogs" },
              "photoUrls" : [ "photoUrls", "photoUrls" ],
              "tags" : [ { "id" : 0, "name" : "name" }, { "id" : 0, "name" : "name" } ],
              "status" : "available"
            } ]
            """;

    public static final String AVAILABLE_RAT_LIST_JSON = """
            [ {
              "id" : 947,
              "name" : "scruffy",
              "photoUrls" : [ ],
              "tags" : [ { "id" : 1, "name" : "rat" } ],
              "status" : "available"
            } ]
            """;

    public static final String EMPTY_LIST_JSON = "[ ]";

    // ---------------------------------------------------------------- consumer

    public static final String PROCESS_EVENT_PATH = "/api/process/event";

    public static final String EVENT_ACCEPTED_JSON = """
            {
              "eventId" : "3fa85f64-5717-4562-b3fc-2c963f66afa6",
              "receivedAt" : "2026-01-01T12:00:00.000Z"
            }
            """;

    private SystemTestMockData() {
    }
}
