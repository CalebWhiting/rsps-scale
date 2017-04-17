package com.rsscale.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Project: FOE Buddy
 *
 * @author Caleb Whiting
 */
public class MainArguments {

	private static final List<String> FALSE_VALUES = Arrays.asList("false", "no", "0");
	private static final List<String> TRUE_VALUES = Arrays.asList("true", "yes", "1");
	private static final Map<String, String> cache = new HashMap<>();
	private static String[] arguments;

	public static String[] getArguments() {
		return arguments;
	}

	public static void setArguments(String[] arguments) {
		MainArguments.arguments = arguments;
	}

	public static int indexOfKey(String key) {
		for (int i = 0; i < arguments.length; i++) {
			String argument = arguments[i];
			if (argument.startsWith(key + "=")) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isPresent(String key) {
		return indexOfKey(key) != -1;
	}

	public static String getValue(String key) {
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		String value = null;
		for (String argument : arguments) {
			if (argument.startsWith(key + "=")) {
				value = argument.substring(key.length() + 1);
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length() - 1);
				}
				break;
			}
		}
		cache.put(key, value);
		return value;
	}

	public static boolean getBooleanValue(String key, Boolean fallback) {
		if (!isPresent(key)) {
			return fallback;
		}
		String value = getValue(key).toLowerCase();
		if (TRUE_VALUES.contains(value)) {
			return true;
		}
		if (FALSE_VALUES.contains(value)) {
			return false;
		}
		throw new IllegalArgumentException(String.format("Unrecognised boolean keyword: '%s'", value));
	}

	private static <T> T getValue(String key, T fallback, Function<String, T> parse) {
		if (!isPresent(key)) {
			return fallback;
		}
		String value = getValue(key);
		return parse.apply(value);
	}

	public static Integer getIntegerValue(String key, Integer fallback) {
		return getValue(key, fallback, Integer::parseInt);
	}

	public static Long getLongValue(String key, Long fallback) {
		return getValue(key, fallback, Long::parseLong);
	}

	public static Float getFloatValue(String key, Float fallback) {
		return getValue(key, fallback, Float::parseFloat);
	}

	public static Double getDoubleValue(String key, Double fallback) {
		return getValue(key, fallback, Double::parseDouble);
	}

	public static Short getShortValue(String key, Short fallback) {
		return getValue(key, fallback, Short::parseShort);
	}

	public static Byte getByteValue(String key, Byte fallback) {
		return getValue(key, fallback, Byte::parseByte);
	}

	public static String getStringValue(String key, String fallback) {
		return getValue(key, fallback, String::valueOf);
	}

}
