package com.br.manager.support.contract;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Gera valores de exemplo, nao nulos e distintos dos defaults, para um tipo Java.
 * Usado pelos verificadores de contrato de DTOs.
 */
public final class SampleValorFactory {

    private static final Map<Class<?>, Supplier<Object>> SAMPLES = Map.ofEntries(
            Map.entry(String.class, () -> "sample"),
            Map.entry(UUID.class, UUID::randomUUID),
            Map.entry(BigDecimal.class, () -> new BigDecimal("12.345")),
            Map.entry(Integer.class, () -> 7),
            Map.entry(int.class, () -> 7),
            Map.entry(Long.class, () -> 7L),
            Map.entry(long.class, () -> 7L),
            Map.entry(Double.class, () -> 7.5d),
            Map.entry(double.class, () -> 7.5d),
            Map.entry(Boolean.class, () -> Boolean.FALSE),
            Map.entry(boolean.class, () -> Boolean.TRUE),
            Map.entry(LocalDate.class, () -> LocalDate.of(2026, 1, 15)),
            Map.entry(LocalDateTime.class, () -> LocalDateTime.of(2026, 1, 15, 10, 30)),
            Map.entry(Instant.class, () -> Instant.parse("2026-01-15T10:30:00Z")),
            Map.entry(OffsetDateTime.class, () -> OffsetDateTime.of(2026, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC))
    );

    private SampleValorFactory() {
    }

    public static Object sampleFor(Class<?> type, Type genericType) {
        Supplier<Object> fornecedor = SAMPLES.get(type);
        if (fornecedor != null) {
            return fornecedor.get();
        }
        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return constants[constants.length - 1];
        }
        if (List.class.isAssignableFrom(type)) {
            return sampleList(genericType);
        }
        if (Set.class.isAssignableFrom(type)) {
            return Set.of();
        }
        return instantiate(type);
    }

    private static List<Object> sampleList(Type genericType) {
        List<Object> list = new ArrayList<>();
        if (genericType instanceof ParameterizedType parameterized
                && parameterized.getActualTypeArguments()[0] instanceof Class<?> elementType) {
            list.add(sampleFor(elementType, elementType));
        }
        return list;
    }

    private static Object instantiate(Class<?> type) {
        try {
            if (type.isRecord()) {
                return RecordFactory.newInstanceWithSamples(type);
            }
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Nao foi possivel gerar valor de exemplo para " + type.getName(), e);
        }
    }
}
