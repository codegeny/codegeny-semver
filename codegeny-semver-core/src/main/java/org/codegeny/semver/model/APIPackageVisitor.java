package org.codegeny.semver.model;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class APIPackageVisitor extends ClassVisitor {

	private final API api;
	private XPackage pakkage;

	public APIPackageVisitor(API api, XPackage pakkage) {
		super(Opcodes.ASM5);
		this.api = api;
		this.pakkage = pakkage;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return APIAnnotationVisitor.of(api, desc, this.pakkage::addAnnotation);
	}
}