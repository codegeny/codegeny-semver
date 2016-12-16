![Status experimental](https://img.shields.io/badge/status-experimental-red.svg)

# codegeny-semver

This library tries to compute complient [semantic versions](http://semver.org) for your maven project.

## Annotate your public API

For that, you must annotate every package, type or method that is part of the api that will be used by external parties with `@PublicAPI`.

```java
@PublicAPI
public class MyService {
	
	public void someMethod() { ... }

	@PublicAPI(exclude = true)
	public void somePublicMethodThatShouldNotBePublic() { ... }
}
```

If you annotate a package (in `package-info.java`), all its classes will be considered annotated with `PublicAPI` (but not its sub-packages).
If you annotate a type with `PublicAPI`, any public member it contains will be considered as part of the public api.

You can exclude some methods/types from the public api with `@PublicAPI(exclude = true)` (for example, if you need a default constructor for technical reasons but it should not be part of the api).
You could also force a protected/private member to be part of the public api by annotating it.

For the moment, each part of the public api (type, field, method, constructor) will be hashed in `META-INF/semver.properties`. This is an *experimental* way of creating the fingerprint for the public api.
In the future, a better format needs to be found (I am working on it).

| previous hashes | current hashes | result
| --------------- | -------------- | ------
| 1 2 3           | 1 2     5      | major version (previous hash 3 could not be found in current version, this breaks the public api)
| 1 2 3           | 1 2 3 4        | minor version (all previous hashes were found in the current version, but new ones were added, the public api is still compatible but has been augmented with new classes/methods...)
| 1 2 3           | 1 2 3          | patch version (the two hash sets are identical, the public API for the two versions are identical)

To annotate your project, you will need to import the following dependency:

```xml
<dependency>
	<groupId>org.codegeny</groupId>
	<artifactId>codegeny-semver-annotations</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<optional>true</optional>
</dependency>
```

This jar only contains the needed annotation and an annotation processor to generate the `META-INF/semver.properties`.

## Compute semantic version numbers in your maven build

When you need to release your project, you'll have to use the following maven pugin:

```xml
<plugin>
	<groupId>org.codegeny</groupId>
	<artifactId>codegeny-semver-maven-plugin</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<executions>
		<execution>
			<goals>
				<goal>version</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

This plugin will retrieve the previous release of your project and compare the `META-INF/semver.properties`.

Let's consider you are working on version `1.2.3-SNAPSHOT` and want to release; the plugin will compare this version with the previous one (which is `1.2.2`) and give the versions for your release and the  next snapshot:

| version  | major change     | minor change     | patch change     |
| -------- | ---------------- | ---------------- | ---------------- |
| release  | `2.0.0`          | `1.3.0`          | `1.2.3`          |
| snapshot | `2.0.1-SNAPSHOT` | `1.3.1-SNAPSHOT` | `1.2.4-SNAPSHOT` |

Those versions numbers will be exported as system properties to `semver.releaseVersion` and `semver.snapshotVersion`.
In turn, these properties can be used by the `maven-release-plugin` to perform the actual release.

## Things to do

- [ ] Find a better format for the public API than hashes
- [ ] Allow the maven plugin to declare which dependencies are re-exported in the public API (meaning that the version calculator must take account of the possible change of versions in those dependencies)
