package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.operacional.dto.TanqueInputDTO;
import com.br.manager.domain.operacional.dto.TanqueResponseDTO;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.entity.Tanque;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.mapper.TanqueMapper;
import com.br.manager.domain.operacional.repository.TanqueRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TanqueService {

    @Autowired
    private TanqueRepository tanqueRepository;

    @Autowired
    private TanqueMapper tanqueMapper;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ServicoSincronizacao sincronizacaoService;

    public TanqueResponseDTO create(TanqueInputDTO inputDTO) {
        try {
            Produto produto = produtoService.getProdutoEntityById(inputDTO.getProdutoId());
            if (produto.getProdutoType() != ProdutoTypeEnum.COMBUSTIVEL) {
                throw new ExcecaoNegocio("Apenas produtos do tipo COMBUSTÍVEL podem ser atribuídos a um tanque.");
            }
            Tanque tanque = tanqueMapper.tanqueInputToTanqueEntity(inputDTO);
            return tanqueMapper.tanqueEntityToTanqueResponseDTO(tanqueRepository.saveAndFlush(tanque));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String tanqueCode = inputDTO != null && UtilStrings.hasText(inputDTO.getCodigo()) ? inputDTO.getCodigo() : "";
            throw new ExcecaoNegocio("Erro ao salvar tanque " + tanqueCode, e);
        }
    }

    public TanqueResponseDTO update(TanqueInputDTO inputDTO) {
        try {
            Tanque tanque = tanqueRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (tanque == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Tanque com ID %s não encontrado", inputDTO.getId()));
            }
            Produto produto = produtoService.getProdutoEntityById(inputDTO.getProdutoId());
            if (produto.getProdutoType() != ProdutoTypeEnum.COMBUSTIVEL) {
                throw new ExcecaoNegocio("Apenas produtos do tipo COMBUSTÍVEL podem ser atribuídos a um tanque.");
            }
            tanqueMapper.updateTanqueFromDto(inputDTO, tanque);
            return tanqueMapper.tanqueEntityToTanqueResponseDTO(tanqueRepository.saveAndFlush(tanque));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            String tanqueCode = inputDTO != null && UtilStrings.hasText(inputDTO.getCodigo()) ? inputDTO.getCodigo() : "";
            throw new ExcecaoNegocio("Erro ao atualizar tanque " + tanqueCode, e);
        }
    }

    public List<TanqueResponseDTO> findAll() {
        return tanqueMapper.listTanqueEntityToListTanqueResponseDTO(tanqueRepository.findAllByAtivoTrue());
    }

    public List<TanqueResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        return tanqueMapper.listTanqueEntityToListTanqueResponseDTO(
                tanqueRepository.findByCodeContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    public TanqueResponseDTO find(UUID id) {
        Tanque tanque = tanqueRepository.findByIdAndAtivoTrue(id);
        if (tanque == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Tanque com ID %s não encontrado", id));
        }
        return tanqueMapper.tanqueEntityToTanqueResponseDTO(tanque);
    }

    public void delete(UUID id) {
        try {
            Tanque tanque = tanqueRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Tanque com ID %s não encontrado", id)));
            tanque.setAtivo(false);
            tanque.setExcluidoEm(LocalDateTime.now());
            tanqueRepository.saveAndFlush(tanque);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir tanque", e);
        }
    }

    public Tanque getTanqueEntityById(UUID id) {
        Tanque tanque = tanqueRepository.findByIdAndAtivoTrue(id);
        if (tanque == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Tanque com ID %s não encontrado", id));
        }
        return tanque;
    }

    /**
     * Retorna registros criados, atualizados ou deletados após a última sincronização da tabela informada.
     */
    public List<TanqueResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        var sincronizacao = sincronizacaoService.findByTabelaName(tabelaName);
        var since = sincronizacao.getUltimaSincronizacao();
        if (since == null) {
            // se não houver data, considerar desde a época Unix para garantir consistência
            since = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        }
        List<Tanque> changed = tanqueRepository.findAllChangedSince(since);
        return tanqueMapper.listTanqueEntityToListTanqueResponseDTO(changed);
    }
}
