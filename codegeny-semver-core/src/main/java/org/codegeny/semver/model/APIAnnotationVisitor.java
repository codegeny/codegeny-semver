package org.codegeny.semver.model;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class APIAnnotationVisitor extends AnnotationVisitor {
	
	public static APIAnnotationVisitor of(API api, String desc, Consumer<XAnnotation> consumer) {
		Map<String, XAttribute> attributes = new HashMap<>();
		return new APIAnnotationVisitor(api, attributes::put, () -> consumer.accept(new XAnnotation(api.getClass(Type.getType(desc).getClassName()), attributes)));
	}
	
	private final API api;
	private final BiConsumer<String, XAttribute> collector;
	private final Runnable finisher;
	
	private APIAnnotationVisitor(API api, BiConsumer<String, XAttribute> collector, Runnable finisher) {
		super(Opcodes.ASM5);
		this.api = api;
		this.collector = collector;
		this.finisher = finisher;
	}
	
	@Override
	public void visit(String name, Object value) {
		if (value instanceof Type) {
			collector.accept(name, new XClassAttribute(api.getClass(((Type) value).getClassName())));
		} else if (value instanceof String) {
			collector.accept(name, new XStringAttribute((String) value));
		} else if (value instanceof int[]) {
			XClass intClass = api.getClass("int");
			collector.accept(name,  new XArrayAttribute(IntStream.of((int[]) value).mapToObj(i -> new XPrimitiveAttribute(intClass, i)).collect(toList())));
		} else {
			collector.accept(name, new XPrimitiveAttribute(api.getClass(value.getClass().getName()), value));
		}
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		return of(api, desc, a -> collector.accept(name, new XAnnotationAttribute(a)));
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		List<XAttribute> values = new LinkedList<>();
		return new APIAnnotationVisitor(api, (n, a) -> values.add(a), () -> collector.accept(name, new XArrayAttribute(values)));
	}

	@Override
	public void visitEnd() {
		finisher.run();
	}
	
	@Override
	public void visitEnum(String name, String desc, String value) {
		collector.accept(name, new XEnumAttribute(api.getClass(Type.getType(desc).getClassName()), value));
	}
}