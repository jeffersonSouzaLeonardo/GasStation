package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.operational.dto.SupplyRequestDTO;
import com.br.manager.domain.operational.mapper.SupplyMapper;
import com.br.manager.domain.operational.repository.SupplyRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SupplyService {

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private SupplyMapper supplyMapper;

    /**
     * Persiste os abastecimentos recebidos do PDV. A operação é idempotente:
     * reenvios do mesmo lote apenas atualizam os registros já existentes.
     */
    public void saveAll(List<SupplyRequestDTO> supplies) {
        if (supplies == null || supplies.isEmpty()) {
            return;
        }
        try {
            supplyRepository.saveAllAndFlush(supplyMapper.listSupplyRequestDTOToListSupply(supplies));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            throw new BusinessException("Erro ao salvar abastecimentos recebidos do PDV", e);
        }
    }
}
