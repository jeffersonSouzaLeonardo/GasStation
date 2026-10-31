package com.br.manager.domain.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void removeMaskShouldKeepOnlyDigits() {
        assertEquals("11987654321", StringUtils.removeMask("(11) 98765-4321"));
    }

    @Test
    void normalizeShouldRemoveAccents() {
        assertEquals("Sao Paulo", StringUtils.normalize("São Paulo"));
    }

    @Test
    void trimToNullShouldReturnNullForBlankValues() {
        assertNull(StringUtils.trimToNull("   "));
    }

    @Test
    void trimToNullShouldReturnNullForNull() {
        assertNull(StringUtils.trimToNull(null));
    }

    @Test
    void trimToNullShouldTrimNonBlankValues() {
        assertEquals("Posto", StringUtils.trimToNull("  Posto  "));
    }

    @Test
    void hasTextShouldBeFalseForNullAndBlank() {
        assertFalse(StringUtils.hasText(null));
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void hasTextShouldBeTrueForNonBlankValue() {
        assertTrue(StringUtils.hasText(" a "));
    }

    @Test
    void emptyIfNullShouldReturnEmptyForNull() {
        assertEquals("", StringUtils.emptyIfNull(null));
    }

    @Test
    void emptyIfNullShouldKeepValue() {
        assertEquals(" valor ", StringUtils.emptyIfNull(" valor "));
    }

    @Test
    void onlyDigitsShouldRemoveNonDigits() {
        assertEquals("12345678000190", StringUtils.onlyDigits("12.345.678/0001-90"));
    }

    @Test
    void onlyDigitsShouldReturnEmptyForNull() {
        assertEquals("", StringUtils.onlyDigits(null));
    }

    @Test
    void removeMaskShouldReturnEmptyForNull() {
        assertEquals("", StringUtils.removeMask(null));
    }

    @Test
    void onlyLettersShouldKeepAccentedLetters() {
        assertEquals("SãoPaulo", StringUtils.onlyLetters("São Paulo - 123!"));
    }

    @Test
    void onlyLettersShouldReturnEmptyForNull() {
        assertEquals("", StringUtils.onlyLetters(null));
    }

    @Test
    void normalizeShouldReturnEmptyForNull() {
        assertEquals("", StringUtils.normalize(null));
    }

    @Test
    void capitalizeShouldUpperFirstAndLowerRest() {
        assertEquals("Gasolina", StringUtils.capitalize("  gASOLINA "));
    }

    @Test
    void capitalizeShouldUpperSingleCharacter() {
        assertEquals("A", StringUtils.capitalize("a"));
    }

    @Test
    void capitalizeShouldReturnEmptyForBlank() {
        assertEquals("", StringUtils.capitalize("  "));
    }

    @Test
    void equalsIgnoreCaseShouldIgnoreCaseAndSurroundingSpaces() {
        assertTrue(StringUtils.equalsIgnoreCase(" Diesel ", "DIESEL"));
    }

    @Test
    void equalsIgnoreCaseShouldTreatNullAndBlankAsEqual() {
        assertTrue(StringUtils.equalsIgnoreCase(null, "  "));
    }

    @Test
    void equalsIgnoreCaseShouldBeFalseForDifferentValues() {
        assertFalse(StringUtils.equalsIgnoreCase("Diesel", "Etanol"));
    }

    @Test
    void equalsIgnoreCaseShouldBeFalseWhenOnlyOneIsNull() {
        assertFalse(StringUtils.equalsIgnoreCase(null, "Etanol"));
    }
}
