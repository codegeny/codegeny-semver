package org.codegeny.semver.checkers;

import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Stream;

import org.codegeny.semver.Change;
import org.codegeny.semver.MethodChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class AddUncheckedExceptionChecker implements MethodChangeChecker {

	@Override
	public Change check(Method previous, Method current) {
		if (previous == null || current == null) {
			return Change.PATCH;
		}
		
		Set<String> previousCheckedNames = getCheckedNames(previous);
		Set<String> currentCheckedNames = getCheckedNames(current);
		
		if (!previousCheckedNames.containsAll(currentCheckedNames)) {
			return Change.MAJOR; 
		}
		
		return Change.PATCH;
	}
	
	private boolean isChecked(Class<?> type) {
		return Exception.class.isAssignableFrom(type) && !RuntimeException.class.isAssignableFrom(type);
	}
	
	private Set<String> getCheckedNames(Method method) {
		return Stream.of(method.getExceptionTypes()).filter(this::isChecked).map(Class::getName).collect(toSet());
	}
	
}
