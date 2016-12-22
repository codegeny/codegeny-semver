package org.codegeny.semver.model;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class APIMethodVisitor extends MethodVisitor {
	
	private final API api;
	private final XMethod method;
	
	public APIMethodVisitor(API api, XMethod method) {
		super(Opcodes.ASM5);
		this.api = api;
		this.method = method;
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return APIAnnotationVisitor.of(api, desc, this.method::addAnnotation);
	}
}