package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.operacional.dto.AbastecimentoRequestDTO;
import com.br.manager.domain.operacional.mapper.AbastecimentoMapper;
import com.br.manager.domain.operacional.repository.AbastecimentoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AbastecimentoService {

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    @Autowired
    private AbastecimentoMapper abastecimentoMapper;

    /**
     * Persiste os abastecimentos recebidos do PDV. A operação é idempotente:
     * reenvios do mesmo lote apenas atualizam os registros já existentes.
     */
    public void saveAll(List<AbastecimentoRequestDTO> abastecimentos) {
        if (abastecimentos == null || abastecimentos.isEmpty()) {
            return;
        }
        try {
            abastecimentoRepository.saveAllAndFlush(abastecimentoMapper.listAbastecimentoRequestDTOToListAbastecimento(abastecimentos));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar abastecimentos recebidos do PDV", e);
        }
    }
}
