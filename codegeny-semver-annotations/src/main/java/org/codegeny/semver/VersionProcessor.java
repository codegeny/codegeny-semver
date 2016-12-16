package org.codegeny.semver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.kohsuke.MetaInfServices;

@MetaInfServices(Processor.class)
public class VersionProcessor extends AbstractProcessor {
			
	private static boolean filter(Element e) {
		PublicAPI publicAPI = e.getAnnotation(PublicAPI.class);
		return publicAPI == null ? e.getModifiers().contains(Modifier.PUBLIC): !publicAPI.exclude();
	}
	
	private final ElementVisitor<Void, Set<Integer>> hasher = new ElementVisitor<Void, Set<Integer>>() {

		@Override
		public Void visit(Element e) {
			return null;
		}

		@Override
		public Void visit(Element e, Set<Integer> p) {
			return null;
		}

		@Override
		public Void visitExecutable(ExecutableElement e, Set<Integer> p) {
			
			String fullyQualifiedClassName = e.getEnclosingElement().accept(namer, null);
			String methodName = e.getSimpleName().toString();

			// hash methodName
			// hash parameter types
			// hash return type
			// hash thrown types
			
			int hash = Objects.hash(fullyQualifiedClassName, methodName);
			
			p.add(hash);
			
			return null;
		}

		@Override
		public Void visitPackage(PackageElement e, Set<Integer> p) {
			e.getEnclosedElements().stream().filter(VersionProcessor::filter).forEach(element -> element.accept(this, p));
			return null;
		}

		@Override
		public Void visitType(TypeElement e, Set<Integer> p) {
			e.getEnclosedElements().stream().filter(VersionProcessor::filter).forEach(element -> element.accept(this, p));
			return null;
		}

		@Override
		public Void visitTypeParameter(TypeParameterElement e, Set<Integer> p) {
			return null;
		}

		@Override
		public Void visitUnknown(Element e, Set<Integer> p) {
			return null;
		}

		@Override
		public Void visitVariable(VariableElement e, Set<Integer> p) {
			return null;
		}
	};

	private final ElementVisitor<String, Void> namer = new ElementVisitor<String, Void>() {

		@Override
		public String visit(Element e) {
			return null;
		}

		@Override
		public String visit(Element e, Void p) {
			return null;
		}

		@Override
		public String visitExecutable(ExecutableElement e, Void p) {
			return null;
		}

		@Override
		public String visitPackage(PackageElement e, Void p) {
			return e.getSimpleName().toString();
		}

		@Override
		public String visitType(TypeElement e, Void p) {
			return String.format("%s.%s", e.getEnclosingElement().accept(this, null), e.getSimpleName().toString());
		}

		@Override
		public String visitTypeParameter(TypeParameterElement e, Void p) {
			return null;
		}

		@Override
		public String visitUnknown(Element e, Void p) {
			return null;
		}

		@Override
		public String visitVariable(VariableElement e, Void p) {
			return null;
		}
	};

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Collections.singleton(PublicAPI.class.getName());
	}
	
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		
		if (roundEnv.processingOver() || roundEnv.errorRaised()) {
			return false;
		}
		
		Set<Integer> hashes = new HashSet<>();
		roundEnv.getElementsAnnotatedWith(PublicAPI.class).stream().filter(VersionProcessor::filter).forEach(element -> element.accept(this.hasher, hashes));
		
		try {
			
			FileObject file = super.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/semver.properties");
			try (PrintWriter printer = new PrintWriter(file.openWriter())) {
				hashes.stream().map(Integer::toHexString).forEach(printer::println);		
			}
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
			super.processingEnv.getMessager().printMessage(Kind.ERROR, "Failed to write semver.properties: " + ioException.getMessage()); 
		}
		
		return false;
	}
}
