package org.codegeny.semver.example.subpackage;

import org.codegeny.semver.PublicAPI;

public class MySubService {
	
	public interface InnerType {
		
		void helloWorld(String message);
	}

	public void doSomething() {}
	
	@PublicAPI(exclude = true)
	public void hideMe() {}
}
