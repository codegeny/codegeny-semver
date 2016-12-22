package org.codegeny.semver.model;

import java.util.Comparator;

public class APITest {

	public static void main(String[] args) {
		
		APIClassPath classPath = APIClassPath.classLoader(Thread.currentThread().getContextClassLoader());
		
		API api = new API(classPath);
		
//		api.getClass("org.codegeny.semver.model.TestClass");
		api.getClass("org.codegeny.semver.model.package-info");
		
		api.getClasses().stream().sorted(Comparator.comparing(XNamed::getName)).forEach(c -> {
			System.out.println("===============================");
			c.getAnnotations().forEach(System.out::println);
			System.out.println(c);
			c.getFields().stream().sorted(Comparator.comparing(XNamed::getName)).forEach(m -> {
				m.getAnnotations().forEach(a -> System.out.printf("   %s%n", a));
				System.out.printf(" + %s%n", m);
			});
			c.getMethods().stream().sorted(Comparator.comparing(XNamed::getName)).forEach(m -> {
				m.getAnnotations().forEach(a -> System.out.printf("   %s%n", a));
				System.out.printf(" - %s%n", m);
			});
		});
		
		api.getPackages().stream().sorted(Comparator.comparing(XNamed::getName)).forEach(System.out::println);
	}
}
