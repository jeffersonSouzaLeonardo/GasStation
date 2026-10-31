package com.br.manager.contract;

import com.br.manager.support.contract.DtoContractVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Garante o contrato (getters/setters ou accessors de record) de TODOS os DTOs da aplicacao.
 * Novos DTOs em pacotes "*.dto" (ou com sufixo DTO) sao cobertos automaticamente.
 */
class DtoContractTest {

    private static final String BASE_PACKAGE = "com.br.manager.domain";
    private static final Pattern DTO_PATTERN = Pattern.compile(".*\\.dto\\..*|.*DTO");
    private static final int MINIMUM_EXPECTED_DTOS = 70;

    static Stream<Class<?>> allDtos() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new RegexPatternTypeFilter(DTO_PATTERN));
        return scanner.findCandidateComponents(BASE_PACKAGE).stream()
                .<Class<?>>map(definition -> ClassUtils.resolveClassName(
                        definition.getBeanClassName(), DtoContractTest.class.getClassLoader()))
                .sorted(Comparator.comparing(Class::getName));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allDtos")
    void shouldRespectDtoContract(Class<?> dtoType) throws ReflectiveOperationException {
        DtoContractVerifier.verify(dtoType);
    }

    @Test
    void shouldDiscoverAllApplicationDtos() {
        assertThat(allDtos().count()).isGreaterThanOrEqualTo(MINIMUM_EXPECTED_DTOS);
    }
}
