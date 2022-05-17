# Instructions for building, testing, and deploying

### Building

Run maven as usual to get the dependencies and compile the code.

```bash
mvn clean compile
````<Xra>

### Testing

Most of the tests use the [Maven Integration Testing Framework (ITF)](https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/usersguide.html).
Each of the ITF tests runs a full maven build. The tests use WireMock to perform some stubs and assert on the requests being done.

```bash
mvn clean compile verify
```

To run just some integration tests (e.g., the ones under th "com.xblend.it.export_features.XrayDatacenterIT" class),

```bash
 mvn "-Dit.test=com.xblend.it.export_features.XrayDatacenterIT.*" -DfailIfNoTests=false clean compile verify
```

More info [here](https://maven.apache.org/surefire/maven-failsafe-plugin/examples/single-test.html).

To debug the maven integration tests and see the maven stdout/stderr, we need to look under the folder `target/maven-it`. As an example, let's say that we run the maven IT test named "single_feature_by_issueKeys" contained in the com.xblend.it.XrayDatacenterIT class. In this case, we could analyze the logs and the project directory used by the maven instance inside the folder `target/maven-it/com/xblend/it/export_features/XrayDatacenterIT/single_feature_by_issueKeys/`.

### Deploying/Releasing

First, build, run the integration tests, and deploy.

```bash
mvn clean compile verify package deploy
```

The `deploy` will upload the artifact to GH, in this case, but won't create any tags or other assets.

Go to GH page of this project and create the release by hand, using the tag following semantic versioning.
Tag can be created at the moment of the creation of the release (easier) or it can be created locally and push to GH.

Example:

```bash
git tag -a "0.5.0" -m "v0.5.0"
git push origin --tags
```
