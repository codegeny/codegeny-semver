package org.codegeny.semver.model;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

public class APISignatureVisitors {

	private static abstract class AbstractSignatureVisitor extends SignatureVisitor {

		private AbstractSignatureVisitor() {
			super(Opcodes.ASM5);
		}

		@Override
		public SignatureVisitor visitArrayType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visitBaseType(char descriptor) {
			throw new UnsupportedOperationException();
		}

		@Override
		public SignatureVisitor visitClassBound() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visitClassType(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visitEnd() {
			throw new UnsupportedOperationException();
		}

		@Override
		public SignatureVisitor visitExceptionType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visitFormalTypeParameter(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visitInnerClassType(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public SignatureVisitor visitInterface() {
			throw new UnsupportedOperationException();
		}

		@Override
		public SignatureVisitor visitInterfaceBound() {
			throw new UnsupportedOperationException();
		}

		@Override
		public SignatureVisitor visitParameterType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public SignatureVisitor visitReturnType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public SignatureVisitor visitSuperclass() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visitTypeArgument() {
			throw new UnsupportedOperationException();
		}

		@Override
		public SignatureVisitor visitTypeArgument(char wildcard) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visitTypeVariable(String name) {
			throw new UnsupportedOperationException();
		}
	}

	private static abstract class ParameterizableSignatureVisitor extends AbstractSignatureVisitor {

		private final Function<String, XClass> classes;
		private final Consumer<? super XTypeParameter> parameterizable;
		private XTypeParameter typeParameter;

		private ParameterizableSignatureVisitor(Function<String, XClass> classes,
				Consumer<? super XTypeParameter> parameterizable) {
			this.classes = classes;
			this.parameterizable = parameterizable;
		}

		@Override
		public SignatureVisitor visitClassBound() {
			return forConsumer(classes, typeParameter::setClassBound);
		}

		@Override
		public void visitFormalTypeParameter(String name) {
			parameterizable.accept(this.typeParameter = new XTypeParameter(name));
		}

		@Override
		public SignatureVisitor visitInterfaceBound() {
			return forConsumer(classes, typeParameter::addInterfaceBound);
		}
	}

	private static final Map<Character, XBasicType> PRIMITIVES = new HashMap<>();

	static {
		PRIMITIVES.put('V', XBasicType.VOID);
		PRIMITIVES.put('I', XBasicType.INT);
		PRIMITIVES.put('J', XBasicType.LONG);
		PRIMITIVES.put('F', XBasicType.FLOAT);
		PRIMITIVES.put('D', XBasicType.DOUBLE);
		PRIMITIVES.put('B', XBasicType.BYTE);
		PRIMITIVES.put('C', XBasicType.CHAR);
		PRIMITIVES.put('S', XBasicType.SHORT);
		PRIMITIVES.put('Z', XBasicType.BOOLEAN);
	}

	public static SignatureVisitor forClass(Function<String, XClass> classes, XClass klass) {
		return new ParameterizableSignatureVisitor(classes, klass::addTypeParameter) {

			@Override
			public SignatureVisitor visitInterface() {
				return forConsumer(classes, klass::addInterface);
			}

			@Override
			public SignatureVisitor visitSuperclass() {
				return forConsumer(classes, klass::setSuperClass);
			}
		};
	}

	private static SignatureVisitor forConsumer(Function<String, XClass> classes, Consumer<? super XType> consumer) {
		return new AbstractSignatureVisitor() {

			private final Deque<XClassType> classType = new LinkedList<>();

			@Override
			public SignatureVisitor visitArrayType() {
				return forConsumer(classes, type -> consumer.accept(type.toArray()));
			}

			@Override
			public void visitBaseType(char descriptor) {
				consumer.accept(PRIMITIVES.get(descriptor));
			}

			@Override
			public void visitClassType(String name) {
				classType.push(new XClassType(classes.apply(name)));
			}

			@Override
			public void visitEnd() {
				consumer.accept(classType.pop());
			}

			@Override
			public void visitTypeArgument() {
				classType.element().addTypeArgument();
			}

			@Override
			public SignatureVisitor visitTypeArgument(char wildcard) {
				return forConsumer(classes, type -> classType.element().addTypeArgument(wildcard(wildcard), type));
			}

			@Override
			public void visitTypeVariable(String name) {
				consumer.accept(new XTypeVariable(name));
			}
		};
	}

	public static SignatureVisitor forField(Function<String, XClass> classes, XField field) {
		return new AbstractSignatureVisitor() {

			@Override
			public SignatureVisitor visitSuperclass() {
				return forConsumer(classes, field::setType);
			}
		};
	}

	public static SignatureVisitor forMethod(Function<String, XClass> classes, XMethod method) {
		return new ParameterizableSignatureVisitor(classes, method::addTypeParameter) {

			@Override
			public SignatureVisitor visitExceptionType() {
				return forConsumer(classes, method::addExceptionType);
			}

			@Override
			public SignatureVisitor visitParameterType() {
				return forConsumer(classes, method::addParameterType);
			}

			@Override
			public SignatureVisitor visitReturnType() {
				return forConsumer(classes, method::setReturnType);
			}
		};
	}

	private static XWildcard wildcard(char wildcard) {
		switch (wildcard) {
		case SignatureVisitor.INSTANCEOF:
			return XWildcard.EXACT;
		case SignatureVisitor.SUPER:
			return XWildcard.SUPER;
		case SignatureVisitor.EXTENDS:
			return XWildcard.EXTENDS;
		}
		throw new IllegalArgumentException("Unknown wildcard " + wildcard);
	}
}