package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.operational.dto.SupplyRequestDTO;
import com.br.manager.domain.operational.entity.Supply;
import com.br.manager.domain.operational.fixture.SupplyFixture;
import com.br.manager.domain.operational.mapper.SupplyMapper;
import com.br.manager.domain.operational.repository.SupplyRepository;
import com.br.manager.domain.operational.support.OperationalBViolations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplyServiceTest {

    @Mock
    private SupplyRepository supplyRepository;

    @Spy
    private SupplyMapper supplyMapper = new SupplyMapper();

    @InjectMocks
    private SupplyService supplyService;

    @Captor
    private ArgumentCaptor<List<Supply>> suppliesCaptor;

    @Test
    void shouldPersistAllReceivedSuppliesKeepingPdvIds() {
        List<SupplyRequestDTO> requests = List.of(SupplyFixture.validRequest(), SupplyFixture.validRequest());

        supplyService.saveAll(requests);

        verify(supplyRepository).saveAllAndFlush(suppliesCaptor.capture());
        assertThat(suppliesCaptor.getValue())
                .extracting(Supply::getId, Supply::getVolume, Supply::getTotalValue)
                .containsExactly(
                        tuple(requests.get(0).getId(), SupplyFixture.VOLUME, SupplyFixture.TOTAL_VALUE),
                        tuple(requests.get(1).getId(), SupplyFixture.VOLUME, SupplyFixture.TOTAL_VALUE));
    }

    @Test
    void shouldIgnoreNullBatch() {
        supplyService.saveAll(null);

        verifyNoInteractions(supplyRepository);
    }

    @Test
    void shouldIgnoreEmptyBatch() {
        supplyService.saveAll(List.of());

        verifyNoInteractions(supplyRepository);
    }

    @Test
    void shouldConvertConstraintViolationsIntoBusinessException() {
        when(supplyRepository.saveAllAndFlush(anyList())).thenThrow(OperationalBViolations.violationsOf(new Supply()));
        List<SupplyRequestDTO> requests = List.of(SupplyFixture.validRequest());

        assertThatThrownBy(() -> supplyService.saveAll(requests))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("O volume é obrigatório.");
    }

    @Test
    void shouldWrapPersistenceErrorInBusinessException() {
        when(supplyRepository.saveAllAndFlush(anyList())).thenThrow(new DataIntegrityViolationException("fk_supplies_tank"));
        List<SupplyRequestDTO> requests = List.of(SupplyFixture.validRequest());

        assertThatThrownBy(() -> supplyService.saveAll(requests))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Erro ao salvar abastecimentos recebidos do PDV")
                .hasCauseInstanceOf(DataIntegrityViolationException.class);
    }
}
