# mb-demo-applications-petstore-analyzer

## TODO
- use newer version of extra mocks and show mock util classes

## DONE
- debug 500 response from petstore api
- add dependency on generated petstore client
- implement endpoint
- create api and backend module
- add openapi spec
- add openapi generator plugin for api module
- add unit tests
- add isolated tests
- add integrated tests
- create default mock configurations for petstore and consumer
  - mock the latest method instead of withHttpInfo because we are using mocks now instead of spies
- add more tests to show mocking examples
    - hit exceptions
    - hit specific code paths
    - argument matchers
- show example of when spies can be useful instead of mocks
