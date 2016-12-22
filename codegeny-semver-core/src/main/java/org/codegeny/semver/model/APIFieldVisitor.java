package org.codegeny.semver.model;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class APIFieldVisitor extends FieldVisitor {
	
	private final API api;
	private final XField field;
	
	public APIFieldVisitor(API api, XField field) {
		super(Opcodes.ASM5);
		this.api = api;
		this.field = field;
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return APIAnnotationVisitor.of(api, desc, this.field::addAnnotation);
	}
}