package org.codegeny.semver.model.anno;

import org.codegeny.semver.model.XAnnotation;
import org.codegeny.semver.model.XClass;

public interface XVisitor<R> {
	
	abstract class Adapter<R> implements XVisitor<R> {

		protected abstract R defaultValue();

		@Override
		public R visit(boolean value) {
			return defaultValue();
		}

		@Override
		public R visit(boolean[] values) {
			return defaultValue();
		}

		@Override
		public R visit(byte value) {
			return defaultValue();
		}

		@Override
		public R visit(byte[] values) {
			return defaultValue();
		}

		@Override
		public R visit(char value) {
			return defaultValue();
		}

		@Override
		public R visit(char[] values) {
			return defaultValue();
		}

		@Override
		public R visit(double value) {
			return defaultValue();
		}

		@Override
		public R visit(double[] values) {
			return defaultValue();
		}

		@Override
		public R visit(float value) {
			return defaultValue();
		}

		@Override
		public R visit(float[] value) {
			return defaultValue();
		}

		@Override
		public R visit(int value) {
			return defaultValue();
		}

		@Override
		public R visit(int[] values) {
			return defaultValue();
		}

		@Override
		public R visit(long value) {
			return defaultValue();
		}

		@Override
		public R visit(long[] values) {
			return defaultValue();
		}

		@Override
		public R visit(short value) {
			return defaultValue();
		}

		@Override
		public R visit(short[] values) {
			return defaultValue();
		}

		@Override
		public R visit(String value) {
			return defaultValue();
		}

		@Override
		public R visit(String[] values) {
			return defaultValue();
		}

		@Override
		public R visit(XClass value) {
			return defaultValue();
		}

		@Override
		public R visit(XClass enumType, String value) {
			return defaultValue();
		}

		@Override
		public R visit(XClass enumType, String[] values) {
			return defaultValue();
		}

		@Override
		public R visit(XClass annotationType, XAnnotation value) {
			return defaultValue();
		}

		@Override
		public R visit(XClass annotationType, XAnnotation[] values) {
			return defaultValue();
		}
		
		@Override
		public R visit(XClass[] values) {
			return defaultValue();
		}
	}
	
	R visit(boolean value);
	
	R visit(boolean[] values);
	
	R visit(byte value);
	
	R visit(byte[] values);
	
	R visit(char value);
	
	R visit(char[] values);
	
	R visit(double value);
	
	R visit(double[] values);
	
	R visit(float value);
	
	R visit(float[] value);
	
	R visit(int value);
	
	R visit(int[] values);
	
	R visit(long value);
	
	R visit(long[] values);
	
	R visit(short value);
	
	R visit(short[] values);
	
	R visit(String value);
	
	R visit(String[] values);
	
	R visit(XClass value);
	
	R visit(XClass enumType, String value);
	
	R visit(XClass enumType, String[] values);
	
	R visit(XClass annotationType, XAnnotation value);
	
	R visit(XClass annotationType, XAnnotation[] values);
	
	R visit(XClass[] values);

}
