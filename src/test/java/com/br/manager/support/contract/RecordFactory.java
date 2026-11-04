package com.br.manager.support.contract;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

/**
 * Cria instancias de records preenchidas com valores de exemplo.
 */
final class RecordFactory {

    private RecordFactory() {
    }

    static Object[] sampleArguments(Class<?> recordType) {
        return Arrays.stream(recordType.getRecordComponents())
                .map(component -> SampleValorFactory.sampleFor(component.getType(), component.getGenericType()))
                .toArray();
    }

    static Object newInstance(Class<?> recordType, Object[] arguments) throws ReflectiveOperationException {
        Class<?>[] parameterTypes = Arrays.stream(recordType.getRecordComponents())
                .map(RecordComponent::getType)
                .toArray(Class<?>[]::new);
        Constructor<?> constructor = recordType.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(arguments);
    }

    static Object newInstanceWithSamples(Class<?> recordType) throws ReflectiveOperationException {
        return newInstance(recordType, sampleArguments(recordType));
    }
}
