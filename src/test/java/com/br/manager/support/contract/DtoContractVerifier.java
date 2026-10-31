package com.br.manager.support.contract;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Verifica o contrato estrutural de um DTO:
 * <ul>
 *     <li>JavaBean: construtor padrao e cada setter reflete no getter correspondente;</li>
 *     <li>record: cada accessor devolve o valor recebido no construtor e equals/hashCode sao consistentes.</li>
 * </ul>
 */
public final class DtoContractVerifier {

    private DtoContractVerifier() {
    }

    public static void verify(Class<?> dtoType) throws ReflectiveOperationException {
        if (dtoType.isRecord()) {
            verifyRecord(dtoType);
        } else {
            verifyJavaBean(dtoType);
        }
    }

    private static void verifyJavaBean(Class<?> dtoType) throws ReflectiveOperationException {
        assertThatCode(() -> BeanUtils.instantiateClass(dtoType))
                .as("%s deve possuir construtor padrao", dtoType.getSimpleName())
                .doesNotThrowAnyException();

        Object instance = BeanUtils.instantiateClass(dtoType);
        for (PropertyDescriptor property : readWriteProperties(dtoType)) {
            Method setter = property.getWriteMethod();
            Object sample = SampleValueFactory.sampleFor(property.getPropertyType(), setter.getGenericParameterTypes()[0]);

            setter.invoke(instance, sample);

            assertThat(property.getReadMethod().invoke(instance))
                    .as("%s.%s deve retornar o valor atribuido", dtoType.getSimpleName(), property.getName())
                    .isEqualTo(sample);
        }
    }

    private static List<PropertyDescriptor> readWriteProperties(Class<?> dtoType) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(dtoType))
                .filter(property -> property.getReadMethod() != null && property.getWriteMethod() != null)
                .toList();
    }

    private static void verifyRecord(Class<?> recordType) throws ReflectiveOperationException {
        Object[] arguments = RecordFactory.sampleArguments(recordType);
        Object instance = RecordFactory.newInstance(recordType, arguments);
        Object sameValues = RecordFactory.newInstance(recordType, arguments);

        RecordComponent[] components = recordType.getRecordComponents();
        for (int index = 0; index < components.length; index++) {
            Method accessor = components[index].getAccessor();
            accessor.setAccessible(true);
            assertThat(accessor.invoke(instance))
                    .as("%s.%s deve retornar o valor do construtor", recordType.getSimpleName(), components[index].getName())
                    .isEqualTo(arguments[index]);
        }

        assertThat(instance).isEqualTo(sameValues);
        assertThat(instance.hashCode()).isEqualTo(sameValues.hashCode());
        assertThat(instance.toString()).startsWith(recordType.getSimpleName());
    }
}
