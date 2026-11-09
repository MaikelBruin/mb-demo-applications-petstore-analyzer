# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A demo Spring Boot 3 / Java 21 service ("petstore analyzer") that aggregates data from the public
Swagger Petstore (`https://petstore3.swagger.io/api/v3`) and forwards each result as an event to a
second service, the "petstore analyzer consumer". Its purpose is to demonstrate two things:
consuming **generated** OpenAPI clients, and the difference between **unit / system / system-integration**
test layers (mocking the downstream clients vs. hitting them for real). Keep that demo intent in mind —
the layered test setup is the point of the repo, not incidental scaffolding. `TODO.md` tracks planned
additions (more mocking examples: exceptions, specific code paths, argument matchers, spies vs. mocks).

## Commands

Maven wrapper is committed (`./mvnw`); `mvn` works too.

```bash
mvn clean install                 # build + unit tests (surefire) + cucumber suites (failsafe)
mvn clean install -DskipTests=true
mvn clean install spring-boot:run # run locally (README); or: mvn -pl backend spring-boot:run
```

Running a subset:

```bash
mvn -pl backend test -Dtest=SampleUnitTest                          # single unit test (surefire)
mvn -pl backend verify -Dit.test=SystemTestRunner                   # only the system suite (failsafe)
mvn -pl backend verify -Dit.test=SystemIntegrationTestRunner        # only the system-integration suite
mvn -pl backend verify -Dit.test=SystemTestRunner \
  -Dcucumber.filter.name="Has available rats - no rats available"    # single scenario
```

Surefire **excludes** `**/*IntegrationTests.java` and `**/System*TestRunner.java`; failsafe includes only
those (`backend/pom.xml`). A new cucumber runner must therefore be named `System*TestRunner`, and a new
integration test class `*IntegrationTests`, or it will run under surefire instead of failsafe.

Once running: Swagger UI at `/api`, OpenAPI JSON at `/api-docs`, actuator at `/actuator`.

## Generated client dependencies

Two external artifacts, both in `groupId mb.demos.openapi.generated.api.client`, come from GitHub
Packages (`maven.pkg.github.com/MaikelBruin/mb-pipelines-openapi-generator`), so a `github` server entry
with credentials in `~/.m2/settings.xml` is required for a clean build (CI injects `MB_GITHUB_USERNAME` /
`MB_PAT`):

- `petstore` — client for the public petstore (`PetApi`, models). A copy of the jar also sits in
  `local-deps/petstore-1.0.27.jar` for offline installs.
- `petstore-analyzer-consumer` — client for the consumer service (`EventsApi`, `EventRequestBody`,
  `EventAcceptedResponse`, `ApiException`). Its version is a branch+commit stamp
  (`1.1.1-default-<sha>`), not a plain release.

## Architecture

Everything lives in `backend`; the `api` module contains only the OpenAPI contract
(`api/definition/petstore-analyzer-api.yaml`) — no Java.

**Contract-first web layer.** The openapi-generator plugin is configured in `backend/pom.xml` (not in
`api`) and generates *interfaces only* (`interfaceOnly=true`, `skipIfSpecIsUnchanged=true`) into
`backend/target/generated-sources/openapi`, packages `…analyzer.webapi.api` / `…webapi.model`. Adding
or changing an endpoint means: edit the yaml → rebuild → implement/adjust the generated `*Api`
interface in a controller. Response models (`TotalResponse`, `AvailabilityRatioResponse`,
`HasAvailableResponse`) are generated — never hand-write them.

**Request flow** (one chain per endpoint group: totals / ratios / available):

```
generated *Api interface
  → controllers/*RestController (extends BaseRestController, holds ProducerTemplate)
  → producerTemplate.requestBody("direct:<operationId>", body, ResponseType.class)
  → routes/*RouteBuilder  (Camel direct: route, .process(...) calls the service)
  → service/impl/*ServiceImpl
  → generated PetApi (Jersey client) → real petstore
  → EventService.processEvent(eventType, response) → generated EventsApi → consumer service
```

Route URIs are constants in `routes/RouteBuilderConstants` and are named after the spec's
`operationId`s; controllers and route builders must agree on the constant. Query/path parameters are
passed into the route as a `Map<String, Object>` body and unpacked in the processor.

**Downstream client wiring.** `config/clients/PetstoreClientConfiguration` and
`PetstoreAnalyzerConsumerClientConfiguration` build the `PetApi` / `EventsApi` beans from
`services.petstore.*` / `services.petstore-analyzer-consumer.*` (`base-url`, `connection-timeout`) in
`application.yaml`, each over a Jersey client using the shared `ObjectMapper` from
`config/ObjectMapperConfiguration` plus a logging `utils/JerseyRequestFilter`. The consumer defaults to
`http://localhost:8081`.

**Consumer events.** Every service method ends by calling `EventService.processEvent(...)` with the
response it is about to return. `EventServiceImpl` is a no-op returning `null` when
`toggles.send-to-consumer` is false. Error mapping to know about:
- consumer HTTP 418 → swallowed, a fake `EventAcceptedResponse` is returned ("teapot");
- consumer HTTP 503, or a `ProcessingException` caused by `ConnectException` (consumer not running; the
  Jersey client throws this *unchecked*, not an `ApiException`) → `ConsumerDownException` (extends the
  consumer client's `ApiException`, code 503);
- anything else → `RuntimeException`.
Controllers call routes through `BaseRestController.requestBody(...)`, not `producerTemplate.requestBody`
directly: the latter wraps service exceptions in a `CamelExecutionException`, the helper rethrows the original
(via `Exchange.getException()`). `exceptions/RestExceptionHandler` (`@RestControllerAdvice`) then maps
`ConsumerDownException` (consumer 503 or unreachable) and `SupplierDownException` (petstore 503) to HTTP 503; any other
exception is still a generic 500.
With the toggle on and no mock, the app needs the consumer running on 8081 — so the system-integration
suite (below) requires it, or set `toggles.send-to-consumer=false`.

**Retry loops.** Every petstore-calling service method wraps its `petApi` call in a hand-rolled retry
loop (5 attempts, 2s apart) because the public petstore intermittently returns 500s. This is
deliberate — see commits "debug 500 response from petstore api" and "fix nullpointer when petstore keeps
responding with 500". When adding a service method, follow the same pattern *and* null-check the result
after the loop before dereferencing it. First thing in each `catch`, call
`SupplierDownException.throwIfServiceUnavailable(e)`: a petstore 503 is thrown immediately instead of retried.
The consumer call is *not* retried.

## Test layers

Four layers, all under `backend/src/test/java/…/analyzer/`:

| Layer | Location | Runner | Downstream clients |
|---|---|---|---|
| unit | `unit/` | surefire | n/a |
| integration | `integration/` (`TotalsEndpointIntegrationTests`) | failsafe, `-Dit.test=TotalsEndpointIntegrationTests` | none reached: `TotalsService` (an application unit, not the generated clients) is a `@MockitoBean`; real controller + camel route + `RestExceptionHandler` via `MockMvc` |
| system | `system/`, `features/system/` | `SystemTestRunner` | `PetApi` and `EventsApi` are `@MockitoBean`s |
| system-integration | `systemintegration/`, `features/systemintegration/` | `SystemIntegrationTestRunner` | real public petstore (network, flaky) and the real consumer |

(`TODO.md` / older commits call these "isolated" and "integrated"; the packages, profile and class names
were renamed to `system` / `systemintegration`. Feature file names still say `…IsolatedTests` /
`…IntegratedTests`.)

Both cucumber layers boot the full app with `@SpringBootTest(webEnvironment = RANDOM_PORT)` from a
`*ContainerBootstrapper` and exercise it **over HTTP** via `TestRestTemplate` — not by calling
services directly. The structural differences:

- `SystemTestContainerBootstrapper` activates `dev, system, test, cucumber`. Its
  `SystemTestConfiguration` (`@CucumberContextConfiguration`) declares `@MockitoBean PetApi` and
  `@MockitoBean EventsApi`, replacing the real beans. The generated petstore jar's mock support
  (`PetApiMockConfiguration`, `PetApiMockProvider`, `PetApiResponseExamples`) is no longer component-scanned
  here; example payloads now live in `SystemTestMockData`.
- `SystemIntegrationTestContainerBootstrapper` activates `dev, test, cucumber` (no `system`), mocks
  nothing, and so keeps both real clients.

There are no `application-<profile>.yaml` files; profiles exist purely for bean/test selection.

**Cucumber glue conventions.**

- Glue packages are listed explicitly on each runner: `…analyzer.base` plus the layer's own package.
  A new step-def package will not be discovered unless added there.
- Shared steps (all the `When`/`Then` HTTP calls and assertions) live in
  `base/cucumber/steps/PetstoreAnalyzerStepDefs`; layer-specific `Given`s (mock stubbing, "I have
  access to the actual/mocked petstore") live in `SystemTestStepDefs` / `SystemIntegrationTestStepDefs`.
  Put anything reusable in `base` so both suites get it.
- Step-def classes extend `BaseCucumberStepDefs` and receive collaborators through **constructor
  injection** (cucumber-spring), including `TestDataHolder` — a `@ScenarioScope` bean from
  `BaseTestConfiguration` used to carry the response from a `When` step to the `Then` steps.
- Now that `PetApi` is a plain Mockito mock (not a spy), stubbing the plain method is what the service
  calls; existing steps stub both the plain and `*WithHttpInfo` variants (e.g.
  `findPetsByTags` and `findPetsByTagsWithHttpInfo`) — keep doing that, and use `Mockito.nullable(...)`
  matchers for default stubs.
- The shared scenarios are duplicated verbatim in both feature files (marked with `### same tests as
  … ###` banners); when changing one, change the other.
