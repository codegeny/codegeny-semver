package org.codegeny.semver.example;

import java.io.File;

import org.codegeny.semver.PublicAPI;

@PublicAPI
public class MyService {
	
	public void someMethod(int hello, String world) {}
	
	@PublicAPI(exclude = true)
	public void someHiddenMethod(File file) {}
	
//	public void newMethod() {}
}
