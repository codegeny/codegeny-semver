package org.codegeny.semver.classes2;

public class Constructors {
	
	static class AddConstructorIfNoConstructorsExist {
		
		public AddConstructorIfNoConstructorsExist(int v) {}
	}
	
	static class AddConstructorIfOtherConstructorsExist {
		
		public AddConstructorIfOtherConstructorsExist(int v) {}
		
		public AddConstructorIfOtherConstructorsExist(long v) {}
	}
}
