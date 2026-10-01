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
}
