package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.StationProductInputDTO;
import com.br.manager.domain.operational.dto.StationProductResponseDTO;
import com.br.manager.domain.operational.entity.StationProduct;
import com.br.manager.domain.operational.fixture.StationProductFixture;
import com.br.manager.domain.operational.mapper.StationProductMapper;
import com.br.manager.domain.operational.repository.StationProductRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.br.manager.domain.operational.support.OperationalATestData.constraintViolationOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationProductServiceTest {

    private static final String PRODUCT_REQUIRED = "O ID do produto é obrigatório.";

    @Mock
    private StationProductRepository stationProductRepository;

    @Spy
    private StationProductMapper stationProductMapper = new StationProductMapper();

    @InjectMocks
    private StationProductService stationProductService;

    private static StationProduct withoutProduct() {
        StationProduct invalid = StationProductFixture.persistedEntity();
        invalid.setProductId(null);
        return invalid;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnStationProduct() {
            UUID productId = UUID.randomUUID();
            when(stationProductRepository.saveAndFlush(any(StationProduct.class))).thenAnswer(invocation -> {
                StationProduct entity = invocation.getArgument(0);
                entity.setId(UUID.randomUUID());
                return entity;
            });

            StationProductResponseDTO response = stationProductService.create(StationProductFixture.validInput(productId));

            assertThat(response.getId()).isNotNull();
            assertThat(response.getProductId()).isEqualTo(productId);
            assertThat(response.getMinStock()).isEqualTo(StationProductFixture.MIN_STOCK);
            assertThat(response.getReorderPoint()).isEqualTo(StationProductFixture.REORDER_POINT);
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            when(stationProductRepository.saveAndFlush(any(StationProduct.class))).thenThrow(constraintViolationOf(withoutProduct()));

            assertThatThrownBy(() -> stationProductService.create(StationProductFixture.validInput(null)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PRODUCT_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            UUID productId = UUID.randomUUID();
            when(stationProductRepository.saveAndFlush(any(StationProduct.class)))
                    .thenThrow(new DataIntegrityViolationException("fk product"));

            assertThatThrownBy(() -> stationProductService.create(StationProductFixture.validInput(productId)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(productId.toString())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingStationProduct() {
            StationProduct existing = StationProductFixture.persistedEntity();
            StationProductInputDTO input = StationProductFixture.validInput(existing.getProductId());
            input.setId(existing.getId());
            input.setReorderPoint(BigDecimal.TEN);
            when(stationProductRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(stationProductRepository.saveAndFlush(existing)).thenReturn(existing);

            StationProductResponseDTO response = stationProductService.update(input);

            assertThat(response.getReorderPoint()).isEqualTo(BigDecimal.TEN);
        }

        @Test
        void shouldThrowNotFoundWhenStationProductDoesNotExist() {
            StationProductInputDTO input = StationProductFixture.validInput(UUID.randomUUID());
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> stationProductService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(stationProductRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            StationProduct existing = StationProductFixture.persistedEntity();
            StationProductInputDTO input = StationProductFixture.validInput(null);
            input.setId(existing.getId());
            when(stationProductRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(stationProductRepository.saveAndFlush(existing)).thenThrow(constraintViolationOf(withoutProduct()));

            assertThatThrownBy(() -> stationProductService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PRODUCT_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            StationProduct existing = StationProductFixture.persistedEntity();
            StationProductInputDTO input = StationProductFixture.validInput(existing.getProductId());
            input.setId(existing.getId());
            when(stationProductRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(stationProductRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> stationProductService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveStationProducts() {
            when(stationProductRepository.findAllByActiveTrue()).thenReturn(List.of(StationProductFixture.persistedEntity()));

            assertThat(stationProductService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByProductIdWhenDescriptionIsUuid() {
            StationProduct existing = StationProductFixture.persistedEntity();
            when(stationProductRepository.findByProductIdAndActiveTrue(existing.getProductId())).thenReturn(List.of(existing));

            assertThat(stationProductService.findByDescription(existing.getProductId().toString()))
                    .extracting(StationProductResponseDTO::getId)
                    .containsExactly(existing.getId());
        }

        @Test
        void shouldReturnEmptyWhenDescriptionIsNotUuid() {
            assertThat(stationProductService.findByDescription("gasolina")).isEmpty();
            verify(stationProductRepository, never()).findByProductIdAndActiveTrue(any());
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(stationProductRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(stationProductService.findByDescription("")).isEmpty();
            verify(stationProductRepository).findAllByActiveTrue();
        }

        @Test
        void shouldFindById() {
            StationProduct existing = StationProductFixture.persistedEntity();
            when(stationProductRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(stationProductService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> stationProductService.find(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldReturnEntityById() {
            StationProduct existing = StationProductFixture.persistedEntity();
            when(stationProductRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(stationProductService.getStationProductEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> stationProductService.getStationProductEntityById(id))
                    .isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateStationProduct() {
            StationProduct existing = StationProductFixture.persistedEntity();
            when(stationProductRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            stationProductService.delete(existing.getId());

            ArgumentCaptor<StationProduct> captor = ArgumentCaptor.forClass(StationProduct.class);
            verify(stationProductRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownStationProduct() {
            UUID id = UUID.randomUUID();
            when(stationProductRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> stationProductService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            StationProduct existing = StationProductFixture.persistedEntity();
            when(stationProductRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(stationProductRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> stationProductService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
