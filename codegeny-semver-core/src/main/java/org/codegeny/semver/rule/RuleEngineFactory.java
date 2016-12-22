package org.codegeny.semver.rule;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

public class RuleEngineFactory {
	
	public RuleEngine createRuleEngine() {
		Set<MetaDataProvider> providers = new HashSet<>();
		ServiceLoader.load(MetaDataProvider.class).forEach(providers::add);
		MetaDataProvider provider = providers.isEmpty() ? DefaultMetaDataProvider.INSTANCE : new MetaDataProvider.Composite(providers);
		
		RuleEngine ruleEngine = new RuleEngine(provider);
		ServiceLoader.load(ClassRule.class).forEach(ruleEngine::addClassRule);
		ServiceLoader.load(MethodRule.class).forEach(ruleEngine::addMethodRule);
		return ruleEngine;
	}
}
