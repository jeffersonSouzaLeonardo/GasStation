package com.br.manager.domain.common.service;

import com.br.manager.domain.common.dto.SincronizeDTO;
import com.br.manager.domain.common.entity.Synchronize;
import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.repository.SynchronizeRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SynchronizeServiceTest {

    private static final SynchronizeNameTableEnum TABLE = SynchronizeNameTableEnum.PRODUCT;
    private static final String INVALID_TABLE = "INEXISTENTE";
    private static final LocalDateTime INFORMED_DATE = LocalDateTime.of(2026, 7, 15, 10, 30);

    @Mock
    private SynchronizeRepository synchronizeRepository;

    @InjectMocks
    private SynchronizeService synchronizeService;

    private static Synchronize synchronizeOf(SynchronizeNameTableEnum table) {
        Synchronize synchronize = new Synchronize();
        ReflectionTestUtils.setField(synchronize, "nameTable", table);
        return synchronize;
    }

    private static SincronizeDTO dto(String nameTable, LocalDateTime lastSincronize) {
        SincronizeDTO dto = new SincronizeDTO();
        dto.setNameTable(nameTable);
        dto.setLastSincronize(lastSincronize);
        return dto;
    }

    @Nested
    class FindByTableName {

        @Test
        void shouldFindSynchronizeIgnoringCaseAndSpaces() {
            Synchronize synchronize = synchronizeOf(TABLE);
            when(synchronizeRepository.findByNameTable(TABLE)).thenReturn(Optional.of(synchronize));

            assertThat(synchronizeService.findByTableName("  product ")).isSameAs(synchronize);
        }

        @Test
        void shouldThrowNotFoundWhenTableNameIsBlank() {
            assertThatThrownBy(() -> synchronizeService.findByTableName(" "))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessage("Nome da tabela não informado");
            verifyNoInteractions(synchronizeRepository);
        }

        @Test
        void shouldThrowNotFoundWhenTableNameIsInvalid() {
            assertThatThrownBy(() -> synchronizeService.findByTableName(INVALID_TABLE))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(INVALID_TABLE);
            verifyNoInteractions(synchronizeRepository);
        }

        @Test
        void shouldThrowNotFoundWhenSynchronizeRowDoesNotExist() {
            when(synchronizeRepository.findByNameTable(TABLE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> synchronizeService.findByTableName(TABLE.name()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(TABLE.name());
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateWithInformedDate() {
            when(synchronizeRepository.findByNameTable(TABLE)).thenReturn(Optional.of(synchronizeOf(TABLE)));

            Synchronize updated = synchronizeService.update(dto("product", INFORMED_DATE));

            assertThat(updated.getLastSynchronize()).isEqualTo(INFORMED_DATE);
            assertThat(updated.getNameTable()).isEqualTo(TABLE);
            verify(synchronizeRepository).updateLastSynchronize(TABLE, INFORMED_DATE);
        }

        @Test
        void shouldUpdateWithCurrentDateWhenDateIsNotInformed() {
            LocalDateTime before = LocalDateTime.now();
            when(synchronizeRepository.findByNameTable(TABLE)).thenReturn(Optional.of(synchronizeOf(TABLE)));

            synchronizeService.update(dto(TABLE.name(), null));

            ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(synchronizeRepository).updateLastSynchronize(eq(TABLE), captor.capture());
            assertThat(captor.getValue()).isAfterOrEqualTo(before).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        void shouldThrowNotFoundWhenDtoIsNull() {
            assertThatThrownBy(() -> synchronizeService.update(null))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessage("SincronizeDTO não informado");
            verifyNoInteractions(synchronizeRepository);
        }

        @Test
        void shouldThrowNotFoundWhenTableNameIsBlank() {
            assertThatThrownBy(() -> synchronizeService.update(dto(null, INFORMED_DATE)))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessage("Nome da tabela não informado");
            verifyNoInteractions(synchronizeRepository);
        }

        @Test
        void shouldThrowNotFoundWhenTableNameIsInvalid() {
            assertThatThrownBy(() -> synchronizeService.update(dto(INVALID_TABLE, INFORMED_DATE)))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(INVALID_TABLE);
            verifyNoInteractions(synchronizeRepository);
        }

        @Test
        void shouldThrowNotFoundWhenSynchronizeRowDoesNotExist() {
            when(synchronizeRepository.findByNameTable(TABLE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> synchronizeService.update(dto(TABLE.name(), INFORMED_DATE)))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(TABLE.name());
            verify(synchronizeRepository, never()).updateLastSynchronize(any(), any());
        }
    }
}
