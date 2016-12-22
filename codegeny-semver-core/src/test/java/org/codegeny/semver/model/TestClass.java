package org.codegeny.semver.model;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

@MyAnnotation(bluh = "helo", blarg = {Integer.class, Long.class}, bh = 1, ffhf = {1})
public class TestClass<X extends Cloneable & Serializable> implements Comparable<Consumer <? super X>> {
	
	public static abstract class Number implements Cloneable, Serializable {}
	
	public static class Long extends Number {}
	
	public class InnerClass {}
	
	public <Y extends X, R extends RuntimeException, T extends CharSequence> X bluh(Y param, Consumer<? super Function<?, ? extends X>>[] param2, Integer u) throws R {
		return param;
	}
	
	public static void main(String[] args) {
		
		TestClass<Number> tc = new TestClass<>();
		
		tc.bluh(new Long(), null, 12);
		
	}

	@Override
	public int compareTo(Consumer<? super X> o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
