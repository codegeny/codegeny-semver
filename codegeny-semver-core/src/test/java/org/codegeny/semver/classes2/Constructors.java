package org.codegeny.semver.classes2;

public class Constructors {
	
	static class AddConstructorIfNoConstructorsExist {
		
		public AddConstructorIfNoConstructorsExist(int v) {}
	}
	
	static class AddConstructorIfOtherConstructorsExist {
		
		public AddConstructorIfOtherConstructorsExist(int v) {}
		
		public AddConstructorIfOtherConstructorsExist(long v) {}
	}
	
	static class NoChangeWithConstructors {
		
		public NoChangeWithConstructors(int v) {}
	}
	
	static class NoChangeWithoutConstructors {
		
		public NoChangeWithoutConstructors(int v) {}
	}
}
