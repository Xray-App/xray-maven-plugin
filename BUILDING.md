# Instructions for building, testing, and deploying

### Building

Run maven as usual to get the dependencies and compile the code.

```bash
mvn clean compile
````

### Testing

Most of the tests use the [Maven Integration Testing Framework (ITF)](https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/usersguide.html).
Each of the ITF tests runs a full maven build. The tests use WireMock to perform some stubs and assert on the requests being done.

```bash
mvn clean compile verify
```

### Deploying/Releasing

First, build, run the integration tests, and deploy.

```bash
mvn clean compile verify package deploy
```

The `deploy` will upload the artifact to GH, in this case, but won't create any tags or other assets.

Go to GH page of this project and create the release by hand, using the tag following semantic versioning.
