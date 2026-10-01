package com.br.manager.domain.common;

import java.text.Normalizer;
import java.util.Objects;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String onlyDigits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    public static String removeMask(String value) {
        return onlyDigits(value);
    }

    public static String onlyLetters(String value) {
        return value == null ? "" : value.replaceAll("[^\\p{L}]", "");
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    public static String capitalize(String value) {
        String text = trimToNull(value);
        if (text == null) {
            return "";
        }

        if (text.length() == 1) {
            return text.toUpperCase();
        }

        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public static boolean equalsIgnoreCase(String first, String second) {
        return Objects.equals(trimToNull(first), trimToNull(second))
                || (first != null && second != null && first.trim().equalsIgnoreCase(second.trim()));
    }
}
