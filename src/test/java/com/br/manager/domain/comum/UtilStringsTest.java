package com.br.manager.domain.comum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilStringsTest {

    @Test
    void removeMaskShouldKeepOnlyDigits() {
        assertEquals("11987654321", UtilStrings.removeMask("(11) 98765-4321"));
    }

    @Test
    void normalizeShouldRemoveAccents() {
        assertEquals("Sao Paulo", UtilStrings.normalize("São Paulo"));
    }

    @Test
    void trimToNullShouldReturnNullForVazioValues() {
        assertNull(UtilStrings.trimToNull("   "));
    }

    @Test
    void trimToNullShouldReturnNullForNull() {
        assertNull(UtilStrings.trimToNull(null));
    }

    @Test
    void trimToNullShouldTrimNonVazioValues() {
        assertEquals("Posto", UtilStrings.trimToNull("  Posto  "));
    }

    @Test
    void hasTextShouldBeFalseForNullAndVazio() {
        assertFalse(UtilStrings.hasText(null));
        assertFalse(UtilStrings.hasText("   "));
    }

    @Test
    void hasTextShouldBeTrueForNonVazioValor() {
        assertTrue(UtilStrings.hasText(" a "));
    }

    @Test
    void emptyIfNullShouldReturnEmptyForNull() {
        assertEquals("", UtilStrings.emptyIfNull(null));
    }

    @Test
    void emptyIfNullShouldKeepValor() {
        assertEquals(" valor ", UtilStrings.emptyIfNull(" valor "));
    }

    @Test
    void onlyDigitsShouldRemoveNonDigits() {
        assertEquals("12345678000190", UtilStrings.onlyDigits("12.345.678/0001-90"));
    }

    @Test
    void onlyDigitsShouldReturnEmptyForNull() {
        assertEquals("", UtilStrings.onlyDigits(null));
    }

    @Test
    void removeMaskShouldReturnEmptyForNull() {
        assertEquals("", UtilStrings.removeMask(null));
    }

    @Test
    void onlyLettersShouldKeepAccentedLetters() {
        assertEquals("SãoPaulo", UtilStrings.onlyLetters("São Paulo - 123!"));
    }

    @Test
    void onlyLettersShouldReturnEmptyForNull() {
        assertEquals("", UtilStrings.onlyLetters(null));
    }

    @Test
    void normalizeShouldReturnEmptyForNull() {
        assertEquals("", UtilStrings.normalize(null));
    }

    @Test
    void capitalizeShouldUpperFirstAndLowerRest() {
        assertEquals("Gasolina", UtilStrings.capitalize("  gASOLINA "));
    }

    @Test
    void capitalizeShouldUpperSingleCharacter() {
        assertEquals("A", UtilStrings.capitalize("a"));
    }

    @Test
    void capitalizeShouldReturnEmptyForVazio() {
        assertEquals("", UtilStrings.capitalize("  "));
    }

    @Test
    void equalsIgnoreCaseShouldIgnoreCaseAndSurroundingSpaces() {
        assertTrue(UtilStrings.equalsIgnoreCase(" Diesel ", "DIESEL"));
    }

    @Test
    void equalsIgnoreCaseShouldTreatNullAndVazioAsEqual() {
        assertTrue(UtilStrings.equalsIgnoreCase(null, "  "));
    }

    @Test
    void equalsIgnoreCaseShouldBeFalseForDifferentValues() {
        assertFalse(UtilStrings.equalsIgnoreCase("Diesel", "Etanol"));
    }

    @Test
    void equalsIgnoreCaseShouldBeFalseWhenOnlyOneIsNull() {
        assertFalse(UtilStrings.equalsIgnoreCase(null, "Etanol"));
    }
}
