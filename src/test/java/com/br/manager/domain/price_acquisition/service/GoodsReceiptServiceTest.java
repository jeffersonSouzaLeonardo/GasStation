package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.operational.repository.SupplierRepository;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptResponseDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptFixture;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptItemFixture;
import com.br.manager.domain.price_acquisition.fixture.PriceAcquisitionReferenceFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderFixture;
import com.br.manager.domain.price_acquisition.mapper.GoodsReceiptMapper;
import com.br.manager.domain.price_acquisition.repository.GoodsReceiptItemRepository;
import com.br.manager.domain.price_acquisition.repository.GoodsReceiptRepository;
import com.br.manager.domain.price_acquisition.repository.PurchaseOrderRepository;
import com.br.manager.domain.price_acquisition.support.PriceAcquisitionViolations;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoodsReceiptServiceTest {

    @Mock
    private GoodsReceiptRepository goodsReceiptRepository;

    @Mock
    private GoodsReceiptItemRepository goodsReceiptItemRepository;

    @Mock
    private GoodsReceiptItemService goodsReceiptItemService;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private GoodsReceiptMapper goodsReceiptMapper = new GoodsReceiptMapper();

    @InjectMocks
    private GoodsReceiptService goodsReceiptService;

    private final Supplier supplier = PriceAcquisitionReferenceFixture.activeSupplier();
    private final User receiver = PriceAcquisitionReferenceFixture.activeUser();

    private GoodsReceiptInputDTO validInput() {
        return GoodsReceiptFixture.validInput(supplier.getId(), receiver.getId(),
                List.of(GoodsReceiptItemFixture.validInput(null, UUID.randomUUID(), null)));
    }

    private void stubReferences() {
        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
    }

    private void stubSaveAssigningId() {
        when(goodsReceiptRepository.saveAndFlush(any(GoodsReceipt.class))).thenAnswer(invocation -> {
            GoodsReceipt receipt = invocation.getArgument(0);
            assertThat(receipt.getId()).isNull();
            receipt.setId(UUID.randomUUID());
            return receipt;
        });
    }

    private GoodsReceipt stubExisting() {
        GoodsReceipt existing = GoodsReceiptFixture.persistedEntity(supplier, receiver);
        when(goodsReceiptRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        return existing;
    }

    private GoodsReceiptInputDTO updateInput(UUID id) {
        GoodsReceiptInputDTO input = new GoodsReceiptInputDTO();
        input.setId(id);
        return input;
    }

    @Nested
    class Create {

        @Test
        void shouldCreateReceiptWithReferencesAndItems() {
            GoodsReceiptInputDTO input = validInput();
            input.setId(UUID.randomUUID());
            stubReferences();
            ArgumentCaptor<GoodsReceipt> captor = ArgumentCaptor.forClass(GoodsReceipt.class);
            stubSaveAssigningId();
            when(goodsReceiptItemRepository.findByGoodsReceiptId(any()))
                    .thenAnswer(invocation -> {
                        GoodsReceipt owner = new GoodsReceipt();
                        owner.setId(invocation.getArgument(0));
                        return List.of(GoodsReceiptItemFixture.persistedEntity(owner));
                    });

            GoodsReceiptResponseDTO response = goodsReceiptService.create(input);

            verify(goodsReceiptRepository).saveAndFlush(captor.capture());
            GoodsReceipt saved = captor.getValue();
            assertThat(saved.getSupplier()).isSameAs(supplier);
            assertThat(saved.getReceivedBy()).isSameAs(receiver);
            assertThat(saved.getPurchaseOrder()).isNull();
            verify(goodsReceiptItemService).saveGoodsReceiptItems(saved.getId(), input.getItems());
            assertThat(response.getId()).isEqualTo(saved.getId());
            assertThat(response.getStatus()).isEqualTo(GoodsReceipt.Status.RECEIVED);
            assertThat(response.getReceivedAt()).isEqualTo(GoodsReceiptFixture.RECEIVED_AT);
            assertThat(response.getInvoiceNumber()).isEqualTo(GoodsReceiptFixture.INVOICE_NUMBER);
            assertThat(response.getFreightAmount()).isEqualByComparingTo(GoodsReceiptFixture.FREIGHT_AMOUNT);
            assertThat(response.getItems()).singleElement()
                    .satisfies(item -> assertThat(item.getGoodsReceiptId()).isEqualTo(saved.getId()));
        }

        @Test
        void shouldLinkPurchaseOrderWhenInformed() {
            PurchaseOrder purchaseOrder = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.SENT);
            GoodsReceiptInputDTO input = validInput();
            input.setPurchaseOrderId(purchaseOrder.getId());
            stubReferences();
            when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
            stubSaveAssigningId();

            assertThat(goodsReceiptService.create(input).getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "  ")
        void shouldDefaultStatusToDraftAndReceivedAtToNow(String status) {
            GoodsReceiptInputDTO input = validInput();
            input.setStatus(status);
            input.setReceivedAt(null);
            stubReferences();
            stubSaveAssigningId();
            LocalDateTime before = LocalDateTime.now();

            GoodsReceiptResponseDTO response = goodsReceiptService.create(input);

            assertThat(response.getStatus()).isEqualTo(GoodsReceipt.Status.DRAFT);
            assertThat(response.getReceivedAt()).isBetween(before, LocalDateTime.now());
        }

        @Test
        void shouldRejectNullPayload() {
            assertThatThrownBy(() -> goodsReceiptService.create(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O payload do recebimento de mercadorias é obrigatório.");
        }

        @Test
        void shouldRequireSupplier() {
            GoodsReceiptInputDTO input = validInput();
            input.setSupplierId(null);

            assertThatThrownBy(() -> goodsReceiptService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O ID do fornecedor é obrigatório.");
        }

        @Test
        void shouldRequireReceiver() {
            GoodsReceiptInputDTO input = validInput();
            input.setReceivedBy(null);

            assertThatThrownBy(() -> goodsReceiptService.create(input))
                    .hasMessage("O usuário responsável pelo recebimento é obrigatório.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = " ")
        void shouldRequireInvoiceNumber(String invoiceNumber) {
            GoodsReceiptInputDTO input = validInput();
            input.setInvoiceNumber(invoiceNumber);

            assertThatThrownBy(() -> goodsReceiptService.create(input))
                    .hasMessage("O número da nota fiscal é obrigatório.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = " ")
        void shouldRequireInvoiceKey(String invoiceKey) {
            GoodsReceiptInputDTO input = validInput();
            input.setInvoiceKey(invoiceKey);

            assertThatThrownBy(() -> goodsReceiptService.create(input))
                    .hasMessage("A chave da nota fiscal é obrigatória.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldRequireItems(List<GoodsReceiptItemInputDTO> items) {
            GoodsReceiptInputDTO input = validInput();
            input.setItems(items);

            assertThatThrownBy(() -> goodsReceiptService.create(input))
                    .hasMessage("O recebimento de mercadorias deve conter pelo menos um item.");
            verifyNoInteractions(goodsReceiptRepository);
        }

        @Test
        void shouldThrowNotFoundWhenSupplierDoesNotExist() {
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> goodsReceiptService.create(validInput()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(supplier.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenSupplierIsInactive() {
            Supplier inactive = PriceAcquisitionReferenceFixture.inactiveSupplier();
            GoodsReceiptInputDTO input = validInput();
            input.setSupplierId(inactive.getId());
            when(supplierRepository.findById(inactive.getId())).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> goodsReceiptService.create(input)).isInstanceOf(NotFoundBusinessException.class);
            verify(goodsReceiptRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenPurchaseOrderDoesNotExist() {
            GoodsReceiptInputDTO input = validInput();
            input.setPurchaseOrderId(UUID.randomUUID());
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
            when(purchaseOrderRepository.findById(input.getPurchaseOrderId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> goodsReceiptService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getPurchaseOrderId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenReceiverDoesNotExist() {
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
            when(userRepository.findById(receiver.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> goodsReceiptService.create(validInput()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(receiver.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenReceiverIsInactive() {
            User inactive = PriceAcquisitionReferenceFixture.inactiveUser();
            GoodsReceiptInputDTO input = validInput();
            input.setReceivedBy(inactive.getId());
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
            when(userRepository.findById(inactive.getId())).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> goodsReceiptService.create(input)).isInstanceOf(NotFoundBusinessException.class);
            verify(goodsReceiptRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            stubReferences();
            when(goodsReceiptRepository.saveAndFlush(any(GoodsReceipt.class)))
                    .thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> goodsReceiptService.create(validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            stubReferences();
            when(goodsReceiptRepository.saveAndFlush(any(GoodsReceipt.class))).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> goodsReceiptService.create(validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar recebimento de mercadorias " + GoodsReceiptFixture.INVOICE_NUMBER)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateFieldsReferencesAndReplaceItems() {
            GoodsReceipt existing = stubExisting();
            Supplier newSupplier = PriceAcquisitionReferenceFixture.activeSupplier();
            User newReceiver = PriceAcquisitionReferenceFixture.activeUser();
            PurchaseOrder purchaseOrder = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.SENT);
            GoodsReceiptInputDTO input = updateInput(existing.getId());
            input.setSupplierId(newSupplier.getId());
            input.setReceivedBy(newReceiver.getId());
            input.setPurchaseOrderId(purchaseOrder.getId());
            input.setInvoiceNumber(GoodsReceiptFixture.UPDATED_INVOICE_NUMBER);
            input.setStatus(GoodsReceipt.Status.PARTIALLY_RECEIVED);
            input.setReceivedAt(GoodsReceiptFixture.RECEIVED_AT.plusHours(2));
            List<GoodsReceiptItemInputDTO> items = List.of(GoodsReceiptItemFixture.validInput(existing.getId(), UUID.randomUUID(), null));
            input.setItems(items);
            when(supplierRepository.findById(newSupplier.getId())).thenReturn(Optional.of(newSupplier));
            when(userRepository.findById(newReceiver.getId())).thenReturn(Optional.of(newReceiver));
            when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
            when(goodsReceiptRepository.saveAndFlush(existing)).thenReturn(existing);
            when(goodsReceiptItemRepository.findByGoodsReceiptId(existing.getId()))
                    .thenReturn(List.of(GoodsReceiptItemFixture.persistedEntity(existing)));

            GoodsReceiptResponseDTO response = goodsReceiptService.update(input);

            assertThat(response.getSupplierId()).isEqualTo(newSupplier.getId());
            assertThat(response.getReceivedBy()).isEqualTo(newReceiver.getId());
            assertThat(response.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
            assertThat(response.getInvoiceNumber()).isEqualTo(GoodsReceiptFixture.UPDATED_INVOICE_NUMBER);
            assertThat(response.getInvoiceKey()).isEqualTo(GoodsReceiptFixture.INVOICE_KEY);
            assertThat(response.getStatus()).isEqualTo(GoodsReceipt.Status.PARTIALLY_RECEIVED);
            assertThat(response.getReceivedAt()).isEqualTo(GoodsReceiptFixture.RECEIVED_AT.plusHours(2));
            assertThat(response.getItems()).hasSize(1);
            verify(goodsReceiptItemService).replaceByGoodsReceipt(existing.getId(), items);
        }

        @Test
        void shouldKeepReferencesAndItemsWhenNotInformed() {
            GoodsReceipt existing = stubExisting();
            when(goodsReceiptRepository.saveAndFlush(existing)).thenReturn(existing);

            GoodsReceiptResponseDTO response = goodsReceiptService.update(updateInput(existing.getId()));

            assertThat(response.getSupplierId()).isEqualTo(supplier.getId());
            assertThat(response.getReceivedBy()).isEqualTo(receiver.getId());
            assertThat(response.getStatus()).isEqualTo(GoodsReceipt.Status.RECEIVED);
            verifyNoInteractions(supplierRepository, userRepository, purchaseOrderRepository, goodsReceiptItemService);
        }

        @Test
        void shouldRejectNullPayload() {
            assertThatThrownBy(() -> goodsReceiptService.update(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O payload do recebimento de mercadorias é obrigatório.");
        }

        @Test
        void shouldRequireId() {
            assertThatThrownBy(() -> goodsReceiptService.update(new GoodsReceiptInputDTO()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O ID do recebimento de mercadorias é obrigatório para atualização.");
            verifyNoInteractions(goodsReceiptRepository);
        }

        @Test
        void shouldThrowNotFoundWhenReceiptDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(goodsReceiptRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> goodsReceiptService.update(updateInput(id)))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldThrowNotFoundWhenNewSupplierIsInactive() {
            GoodsReceipt existing = stubExisting();
            Supplier inactive = PriceAcquisitionReferenceFixture.inactiveSupplier();
            GoodsReceiptInputDTO input = updateInput(existing.getId());
            input.setSupplierId(inactive.getId());
            when(supplierRepository.findById(inactive.getId())).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> goodsReceiptService.update(input)).isInstanceOf(NotFoundBusinessException.class);
            verify(goodsReceiptRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenNewSupplierDoesNotExist() {
            GoodsReceipt existing = stubExisting();
            GoodsReceiptInputDTO input = updateInput(existing.getId());
            input.setSupplierId(UUID.randomUUID());

            assertThatThrownBy(() -> goodsReceiptService.update(input)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldThrowNotFoundWhenNewPurchaseOrderDoesNotExist() {
            GoodsReceipt existing = stubExisting();
            GoodsReceiptInputDTO input = updateInput(existing.getId());
            input.setPurchaseOrderId(UUID.randomUUID());

            assertThatThrownBy(() -> goodsReceiptService.update(input)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldThrowNotFoundWhenNewReceiverIsInactive() {
            GoodsReceipt existing = stubExisting();
            User inactive = PriceAcquisitionReferenceFixture.inactiveUser();
            GoodsReceiptInputDTO input = updateInput(existing.getId());
            input.setReceivedBy(inactive.getId());
            when(userRepository.findById(inactive.getId())).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> goodsReceiptService.update(input)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldThrowNotFoundWhenNewReceiverDoesNotExist() {
            GoodsReceipt existing = stubExisting();
            GoodsReceiptInputDTO input = updateInput(existing.getId());
            input.setReceivedBy(UUID.randomUUID());

            assertThatThrownBy(() -> goodsReceiptService.update(input)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            GoodsReceipt existing = stubExisting();
            when(goodsReceiptRepository.saveAndFlush(existing)).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> goodsReceiptService.update(updateInput(existing.getId())))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            GoodsReceipt existing = stubExisting();
            GoodsReceiptInputDTO input = updateInput(existing.getId());
            input.setInvoiceNumber(GoodsReceiptFixture.UPDATED_INVOICE_NUMBER);
            when(goodsReceiptRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> goodsReceiptService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao atualizar recebimento de mercadorias " + GoodsReceiptFixture.UPDATED_INVOICE_NUMBER)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAll() {
            when(goodsReceiptRepository.findAll()).thenReturn(List.of(GoodsReceiptFixture.persistedEntity()));

            assertThat(goodsReceiptService.findAll()).hasSize(1);
        }

        @Test
        void shouldListBySupplier() {
            when(goodsReceiptRepository.findBySupplierId(supplier.getId()))
                    .thenReturn(List.of(GoodsReceiptFixture.persistedEntity(supplier, receiver)));

            assertThat(goodsReceiptService.findBySupplier(supplier.getId()))
                    .extracting(GoodsReceiptResponseDTO::getSupplierId)
                    .containsExactly(supplier.getId());
        }

        @Test
        void shouldListByPurchaseOrder() {
            UUID purchaseOrderId = UUID.randomUUID();
            when(goodsReceiptRepository.findByPurchaseOrderId(purchaseOrderId)).thenReturn(List.of());

            assertThat(goodsReceiptService.findByPurchaseOrder(purchaseOrderId)).isEmpty();
        }

        @Test
        void shouldFindById() {
            GoodsReceipt existing = stubExisting();

            assertThat(goodsReceiptService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> goodsReceiptService.find(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingReceipt() {
            GoodsReceipt existing = stubExisting();

            goodsReceiptService.delete(existing.getId());

            verify(goodsReceiptRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenReceiptDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> goodsReceiptService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
            verify(goodsReceiptRepository, never()).delete(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            GoodsReceipt existing = stubExisting();
            doThrow(PriceAcquisitionViolations.constraintViolationException()).when(goodsReceiptRepository).delete(existing);

            assertThatThrownBy(() -> goodsReceiptService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            GoodsReceipt existing = stubExisting();
            doThrow(new DataIntegrityViolationException("fk")).when(goodsReceiptRepository).delete(existing);

            assertThatThrownBy(() -> goodsReceiptService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao excluir recebimento de mercadorias")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
