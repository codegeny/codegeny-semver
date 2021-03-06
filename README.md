![Project status](https://img.shields.io/badge/status-experimental-red.svg)
[![Build Status](https://img.shields.io/travis/codegeny/codegeny-semver.svg)](https://travis-ci.org/codegeny/codegeny-semver)
[![Code Coverage](https://img.shields.io/codecov/c/github/codegeny/codegeny-semver.svg)](https://codecov.io/gh/codegeny/codegeny-semver)
[![Code Analysis](https://img.shields.io/codacy/grade/f714017b351e4d8981a6df40857e51f9.svg)](https://www.codacy.com/app/codegeny/codegeny-semver)

# codegeny-semver

This library tries to compute compliant [semantic versions](http://semver.org) for your maven project by following [these rules](https://wiki.eclipse.org/Evolving_Java-based_APIs_2).

## Rules

For the moment, only a few ones are implemented.

### API packages

- [ ] Add API package
- [ ] Delete API package
- [ ] Add API type to API package
- [ ] Delete API type from API package
- [ ] Add non-`public` (non-API) type to API package
- [ ] Delete non-`public` (non-API) type from API package
- [X] Change non-`public` (non-API) type in API package to make `public` (API)
- [X] Change `public` type in API package to make non-`public`
- [X] Change kind of API type (class, interface, enum, or annotation type)

### API interfaces

- [ ] Add `abstract` method
- [ ] Add `default` method
- [X] Add `static` method
- [ ] Delete API method 
- [ ] Move API method up type hierarchy
- [ ] Move API method down type hierarchy
- [ ] Add API field
- [ ] Delete API field
- [ ] Expand superinterface set (direct or inherited)
- [ ] Contract superinterface set (direct or inherited)
- [ ] Add, delete, or change `static` initializers
- [ ] Add API type member
- [ ] Delete API type member
- [ ] ~~Re-order field, method, and type member declarations~~
- [X] Add type parameter
- [X] Delete type parameter
- [ ] Re-order type parameters
- [X] Rename type parameter
- [ ] Add, delete, or change type bounds of type parameter
- [X] Add element to annotation type
- [X] Delete element from annotation type

#### API intefaces - methods

- [ ] Change formal parameter name
- [ ] ~~Change method name~~
- [ ] Add or delete formal parameter
- [ ] Change type of a formal parameter
- [ ] Change result type (including `void`)
- [X] Add checked exceptions thrown
- [ ] ~~Add unchecked exceptions thrown~~
- [X] Delete checked exceptions thrown
- [ ] ~~Delete unchecked exceptions thrown~~
- [ ] ~~Re-order list of exceptions thrown~~
- [X] Change `static` to non-`static`
- [X] Change non-`static` to `static`
- [ ] Change `default` to `abstract`
- [ ] Change `abstract` to `default`
- [X] Add type parameter
- [X] Delete type parameter
- [ ] Re-order type parameters
- [X] Rename type parameter
- [ ] Add, delete, or change type bounds of type parameter
- [X] Change last parameter from array type `T[]` to variable arity `T...`
- [X] Change last parameter from variable arity `T...` to array type `T[]`
- [X] Add `default` clause to annotation type element
- [X] Change `default` clause on annotation type element
- [X] Delete `default` clause from annotation type element

#### API intefaces - fields

- [ ] Change type of API field
- [ ] Change value of API field

## API classes

- [ ] Add API method
- [ ] Delete API method
- [ ] Move API method up type hierarchy
- [ ] Move API method down type hierarchy
- [ ] Add API constructor
- [ ] Delete API constructor
- [ ] Add API field
- [ ] Delete API field
- [ ] Expand superinterface set (direct or inherited)
- [ ] Contract superinterface set (direct or inherited)
- [ ] Expand superclass set (direct or inherited)
- [ ] Contract superclass set (direct or inherited)
- [ ] ~~Add, delete, or change `static` or instance initializers~~
- [ ] Add API type member
- [ ] Delete API type member
- [ ] ~~Re-order field, method, constructor, and type member declarations~~
- [ ] Add or delete non-API members; that is, `private` or `default` access fields, methods, constructors, and type members
- [X] Change `abstract` to non-`abstract`
- [X] Change non-`abstract` to `abstract`
- [X] Change `final` to non-`final`
- [X] Change non-`final` to `final`
- [X] Add type parameter
- [X] Delete type parameter
- [ ] Re-order type parameters
- [X] Rename type parameter
- [ ] Add, delete, or change type bounds of type parameter
- [ ] ~~Rename enum constant~~ _considered as add+delete_
- [ ] ~~Add, change, or delete enum constant arguments~~
- [ ] ~~Add, change, or delete enum constant class body~~
- [X] Add enum constant
- [X] Delete enum constant
- [X] Re-order enum constants

#### API classes - methods and constructors

- [ ] ~~Change body of method or constructor~~
- [ ] Change formal parameter name
- [ ] ~~Change method name~~ _considered as add+delete_
- [ ] Add or delete formal parameter
- [ ] Change type of a formal parameter
- [X] Change result type (including `void`)
- [X] Add checked exceptions thrown
- [ ] ~~Add unchecked exceptions thrown~~
- [X] Delete checked exceptions thrown
- [ ] ~~Delete unchecked exceptions thrown~~
- [ ] ~~Re-order list of exceptions thrown~~
- [X] Decrease access; that is, from `protected` access to `default` or `private` access; or from `public` access to `protected`, `default`, or `private` access
- [X] Increase access; that is, from `protected` access to `public` access
- [X] Change `abstract` to non-`abstract`
- [X] Change non-`abstract` to `abstract`
- [X] Change `final` to non-`final`
- [X] Change non-`final` to `final`
- [X] Change `static` to non-`static`
- [X] Change non-`static` to `static`
- [ ] ~~Change `native` to non-`native`~~
- [ ] ~~Change non-`native` to `native`~~
- [ ] ~~Change `synchronized` to non-`synchronized`~~
- [ ] ~~Change non-`synchronized` to `synchronized`~~
- [X] Add type parameter
- [X] Delete type parameter
- [ ] Re-order type parameters
- [X] Rename type parameter
- [ ] Add, delete, or change type bounds of type parameter
- [X] Change last parameter from array type `T[]` to variable arity `T...`
- [X] Change last parameter from variable arity `T...` to array type `T[]`

#### API classes - fields

- [X] Change type of API field
- [ ] Change value of API field
- [X] Decrease access; that is, from `protected` access to `default` or `private` access; or from `public` access to `protected`, `default`, or `private` access
- [X] Increase access; that is, from `protected` access to `public` access
- [ ] Change `final` to non-`final`
- [ ] Change non-`final` to `final`
- [X] Change `static` to non-`static`
- [X] Change non-`static` to `static`
- [ ] ~~Change `transient` to non-`transient`~~
- [ ] ~~Change non-`transient` to `transient`~~

#### API classes - type members

- [X] Decrease access; that is, from `protected` access to `default` or `private` access; or from `public` access to `protected`, `default`, or `private` access
- [X] Increase access; that is, from `protected` access to `public` access

### Non-API packages

- [ ] Add non-API package
- [ ] Delete non-API package
- [ ] Add class or interface to non-API package
- [ ] Delete class or interface in a non-API package
- [ ] Change existing class or interface in non-API package

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

This plugin will retrieve the previous release of your project and compare the classes.

Let's consider you are working on version `1.2.3-SNAPSHOT` and want to release; the plugin will compare this version with the previous one (which is `1.2.2`) and give the versions for your release and the next snapshot:

| version  | major change     | minor change     | patch change     |
| -------- | ---------------- | ---------------- | ---------------- |
| release  | `2.0.0`          | `1.3.0`          | `1.2.3`          |
| snapshot | `2.0.1-SNAPSHOT` | `1.3.1-SNAPSHOT` | `1.2.4-SNAPSHOT` |
