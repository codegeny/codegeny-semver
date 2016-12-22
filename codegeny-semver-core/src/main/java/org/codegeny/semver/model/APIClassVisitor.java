package org.codegeny.semver.model;

import java.util.stream.Stream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

public class APIClassVisitor extends ClassVisitor {

	private final API api;
	private XClass klass;

	public APIClassVisitor(API api) {
		super(Opcodes.ASM5);
		this.api = api;
	}

	private XType from(Type type) {
		switch (type.getSort()) {
		case Type.OBJECT:
			return new XClassType(api.getClass(type.getClassName()));
		case Type.BOOLEAN:
			return XBasicType.BOOLEAN;
		case Type.BYTE:
			return XBasicType.BYTE;
		case Type.CHAR:
			return XBasicType.CHAR;
		case Type.DOUBLE:
			return XBasicType.DOUBLE;
		case Type.FLOAT:
			return XBasicType.FLOAT;
		case Type.INT:
			return XBasicType.INT;
		case Type.LONG:
			return XBasicType.LONG;
		case Type.SHORT:
			return XBasicType.SHORT;
		case Type.VOID:
			return XBasicType.VOID;
		case Type.ARRAY:
			XType result = from(type.getElementType());
			for (int i = 0; i < type.getDimensions(); i++) {
				result = result.toArray();
			}
			return result;
		default:
			throw new IllegalArgumentException("Unknown type " + type);
		}
	}

	private XKind kind(int access) {
		if ((access & Opcodes.ACC_ENUM) != 0) {
			return XKind.ENUM;
		} else if ((access & Opcodes.ACC_ANNOTATION) != 0) {
			return XKind.ANNOTATION;
		} else if ((access & Opcodes.ACC_INTERFACE) != 0) {
			return XKind.INTERFACE;
		} else {
			return XKind.CLASS;
		}
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//		System.out.println(name);
		name = name.replace('/', '.');
		int index = name.lastIndexOf('.');
		String packageName = index < 0 ? "" : name.substring(0, index);
		XPackage pakkage = api.getPackage(packageName);
		api.addClass(this.klass = new XClass(name, kind(access), pakkage));
		if (signature != null) {
			new SignatureReader(signature).accept(APISignatureVisitors.forClass(api::getClass, this.klass));
		} else {
			if (superName != null) {
				this.klass.setSuperClass(new XClassType(api.getClass(superName)));
			}
			if (interfaces != null) {
				Stream.of(interfaces).map(n -> new XClassType(api.getClass(n))).forEach(this.klass::addInterface);
			}
		}
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return APIAnnotationVisitor.of(api, desc, this.klass::addAnnotation);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//		System.out.printf("%s.%s%n", this.klass.getName(), name);
		XMethod method = new XMethod(name);
		if (signature != null) {
			new SignatureReader(signature).accept(APISignatureVisitors.forMethod(api::getClass, method));
		} else {
			method.setReturnType(from(Type.getReturnType(desc)));
			Stream.of(Type.getArgumentTypes(desc)).map(this::from).forEach(method::addParameterType);
			if (exceptions != null) {
				Stream.of(exceptions).map(n -> new XClassType(api.getClass(n))).forEach(method::addExceptionType);
			}
		}
		klass.addMethod(method);
		return new APIMethodVisitor(api, method);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
//		System.out.printf("%s.%s%n", this.klass.getName(), name);
		XField field = new XField(name);
		if (signature != null) {
			new SignatureReader(signature).accept(APISignatureVisitors.forField(api::getClass, field));
		} else {
			field.setType(from(Type.getType(desc)));
		}
		klass.addField(field);
		return new APIFieldVisitor(api, field);
	}
}