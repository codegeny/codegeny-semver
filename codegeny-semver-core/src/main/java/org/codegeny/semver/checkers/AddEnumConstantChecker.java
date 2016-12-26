package org.codegeny.semver.checkers;

import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.stream.Stream;

import org.codegeny.semver.Change;
import org.codegeny.semver.ClassChangeChecker;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class AddEnumConstantChecker implements ClassChangeChecker {

	@Override
	public Change check(Class<?> previous, Class<?> current) {
		if (previous == null || current == null) {
			return NOT_APPLICABLE;
		}
		if (!previous.isEnum() || !current.isEnum()) {
			return NOT_APPLICABLE;
		}
		Set<String> previousConstants = Stream.of(previous.getEnumConstants()).map(Enum.class::cast).map(Enum::name).collect(toSet());
		Set<String> currentConstants = Stream.of(current.getEnumConstants()).map(Enum.class::cast).map(Enum::name).collect(toSet());
		if (!previousConstants.containsAll(currentConstants)) {
			return Change.MINOR;
		}
		return NOT_APPLICABLE;
	}
}
