package org.codegeny.semver.checkers;

import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Executable;
import java.util.Set;
import java.util.stream.Stream;

import org.codegeny.semver.Change;
import org.codegeny.semver.ExecutableChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class RemoveCheckedExceptionChecker implements ExecutableChangeChecker {

	@Override
	public Change check(Executable previous, Executable current) {
		if (previous == null || current == null) {
			return Change.PATCH;
		}
		
		Set<String> previousCheckedNames = getCheckedNames(previous);
		Set<String> currentCheckedNames = getCheckedNames(current);
		
		if (!currentCheckedNames.containsAll(previousCheckedNames)) {
			return Change.MAJOR; 
		}
		
		return Change.PATCH;
	}
	
	private boolean isChecked(Class<?> type) {
		return Exception.class.isAssignableFrom(type) && !RuntimeException.class.isAssignableFrom(type);
	}
	
	private Set<String> getCheckedNames(Executable executable) {
		return Stream.of(executable.getExceptionTypes()).filter(this::isChecked).map(Class::getName).collect(toSet());
	}
	
}
