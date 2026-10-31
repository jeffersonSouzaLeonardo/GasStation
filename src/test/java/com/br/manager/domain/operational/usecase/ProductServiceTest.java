package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.service.SynchronizeService;
import com.br.manager.domain.operational.dto.ProductInputDTO;
import com.br.manager.domain.operational.dto.ProductResponseDTO;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.fixture.ProductFixture;
import com.br.manager.domain.operational.mapper.ProductMapper;
import com.br.manager.domain.operational.repository.ProductRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.br.manager.domain.operational.support.OperationalATestData.constraintViolationOf;
import static com.br.manager.domain.operational.support.OperationalATestData.synchronizeAt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final String TABLE = SynchronizeNameTableEnum.PRODUCT.name();
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SynchronizeService synchronizeService;

    @Spy
    private ProductMapper productMapper = new ProductMapper();

    @InjectMocks
    private ProductService productService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnProduct() {
            when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> {
                Product product = invocation.getArgument(0);
                product.setId(UUID.randomUUID());
                return product;
            });

            ProductResponseDTO response = productService.create(ProductFixture.fuelInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getName()).isEqualTo(ProductFixture.FUEL_NAME);
            assertThat(response.getProductType()).isEqualTo(ProductTypeEnum.FUEL);
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Product invalid = ProductFixture.persistedFuel();
            invalid.setName(null);
            when(productRepository.saveAndFlush(any(Product.class))).thenThrow(constraintViolationOf(invalid));

            assertThatThrownBy(() -> productService.create(ProductFixture.inputWithoutName()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Product name is required.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(productRepository.saveAndFlush(any(Product.class)))
                    .thenThrow(new DataIntegrityViolationException("db error"));

            assertThatThrownBy(() -> productService.create(ProductFixture.fuelInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProductFixture.FUEL_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingProduct() {
            Product existing = ProductFixture.persistedFuel();
            ProductInputDTO input = ProductFixture.convenienceInput();
            input.setId(existing.getId());
            when(productRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productRepository.saveAndFlush(existing)).thenReturn(existing);

            ProductResponseDTO response = productService.update(input);

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getName()).isEqualTo(ProductFixture.CONVENIENCE_NAME);
            assertThat(response.getProductType()).isEqualTo(ProductTypeEnum.CONVENIENCE);
        }

        @Test
        void shouldThrowNotFoundWhenProductDoesNotExist() {
            ProductInputDTO input = ProductFixture.fuelInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> productService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(productRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Product existing = ProductFixture.persistedFuel();
            ProductInputDTO input = ProductFixture.inputWithoutName();
            input.setId(existing.getId());
            Product invalid = ProductFixture.persistedFuel();
            invalid.setName(null);
            when(productRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productRepository.saveAndFlush(existing)).thenThrow(constraintViolationOf(invalid));

            assertThatThrownBy(() -> productService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Product name is required.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Product existing = ProductFixture.persistedFuel();
            ProductInputDTO input = ProductFixture.fuelInput();
            input.setId(existing.getId());
            when(productRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db error"));

            assertThatThrownBy(() -> productService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .isNotInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(ProductFixture.FUEL_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveProducts() {
            when(productRepository.findAllByActiveTrue()).thenReturn(List.of(ProductFixture.persistedFuel()));

            assertThat(productService.findAll()).hasSize(1);
        }

        @Test
        void shouldListOnlyFuelProducts() {
            when(productRepository.findByProductTypeAndActiveTrue(ProductTypeEnum.FUEL))
                    .thenReturn(List.of(ProductFixture.persistedFuel()));

            assertThat(productService.findProductTypeFuel())
                    .extracting(ProductResponseDTO::getProductType)
                    .containsExactly(ProductTypeEnum.FUEL);
        }

        @Test
        void shouldSearchByName() {
            when(productRepository.findByNameContainingIgnoreCaseAndActiveTrue("gasolina"))
                    .thenReturn(List.of(ProductFixture.persistedFuel()));

            assertThat(productService.findByDescription("gasolina")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(productRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(productService.findByDescription(" ")).isEmpty();
            verify(productRepository, never()).findByNameContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindById() {
            Product existing = ProductFixture.persistedFuel();
            when(productRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(productService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> productService.find(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldReturnEntityById() {
            Product existing = ProductFixture.persistedFuel();
            when(productRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(productService.getProductEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> productService.getProductEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateAndMarkDeletionDate() {
            Product existing = ProductFixture.persistedFuel();
            when(productRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            productService.delete(existing.getId());

            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(productRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownProduct() {
            UUID id = UUID.randomUUID();
            when(productRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Product existing = ProductFixture.persistedFuel();
            when(productRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(productRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> productService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ChangedSinceSynchronize {

        @Test
        void shouldReturnRecordsChangedSinceLastSynchronize() {
            LocalDateTime lastSynchronize = LocalDateTime.of(2026, 5, 10, 8, 30);
            Product changed = ProductFixture.persistedFuel();
            when(synchronizeService.findByTableName(TABLE)).thenReturn(synchronizeAt(lastSynchronize));
            when(productRepository.findAllChangedSince(lastSynchronize)).thenReturn(List.of(changed));

            List<ProductResponseDTO> result = productService.findChangedSinceSynchronize(TABLE);

            assertThat(result).extracting(ProductResponseDTO::getId).containsExactly(changed.getId());
        }

        @Test
        void shouldUseDefaultDateWhenTableWasNeverSynchronized() {
            when(synchronizeService.findByTableName(TABLE)).thenReturn(synchronizeAt(null));
            when(productRepository.findAllChangedSince(DEFAULT_SINCE)).thenReturn(List.of());

            assertThat(productService.findChangedSinceSynchronize(TABLE)).isEmpty();
            verify(productRepository).findAllChangedSince(DEFAULT_SINCE);
        }
    }
}
