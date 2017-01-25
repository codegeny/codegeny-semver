package org.codegeny.semver.classes1;

public class Constructors {
	
	static class AddConstructorIfNoConstructorsExist {}
	
	static class AddConstructorIfOtherConstructorsExist {
		
		public AddConstructorIfOtherConstructorsExist(int v) {}
	}
	
	static class NoChangeWithConstructors {
		
		public NoChangeWithConstructors(int v) {}
	}
	
	static class NoChangeWithoutConstructors {
		
		public NoChangeWithoutConstructors(int v) {}
	}
}
