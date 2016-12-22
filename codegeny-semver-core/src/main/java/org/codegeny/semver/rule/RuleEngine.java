package org.codegeny.semver.rule;

import java.util.HashSet;
import java.util.Set;

import org.codegeny.semver.Change;
import org.codegeny.semver.model.API;
import org.codegeny.semver.model.XClass;
import org.codegeny.semver.model.XMethod;

public class RuleEngine {

	private final Set<ClassRule> classRules = new HashSet<>();
	private final Set<MethodRule> methodRules = new HashSet<>();
	private final MetaDataProvider metaDataProvider;
	
	RuleEngine(MetaDataProvider metaDataProvider) {
		this.metaDataProvider = metaDataProvider;
	}

	void addClassRule(ClassRule classRule) {
		this.classRules.add(classRule);
	}
	
	void addMethodRule(MethodRule methodRule) {
		this.methodRules.add(methodRule);
	}
	
	Change compare(XClass before, XClass after) {
		return compare(before, after, classRules);
	}
	
	Change compare(XMethod before, XMethod after) {
		return compare(before, after, methodRules);
	}
	
	private <X> Change compare(X before, X after, Set<? extends Rule<? super X>> rules) {
		return rules.stream().map(r -> r.compare(before, after, metaDataProvider)).reduce(Change.PATCH, Change::combine);
	}
	
	public Change compare(API before, API after, Set<String> classNames) {
		Change change = Change.PATCH;
		for (String className : classNames) {
			
			XClass beforeClass = before.getClass(className);
			XClass afterClass = after.getClass(className);
			
			if (beforeClass == null && afterClass == null) {
				continue;
			}
			
			change = change.combine(compare(beforeClass, afterClass));
			
			if (change == Change.MAJOR) {
				return change;
			}
		}
		return change;
	}
}
