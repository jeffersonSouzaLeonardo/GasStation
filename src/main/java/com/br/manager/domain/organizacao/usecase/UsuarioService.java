package com.br.manager.domain.organizacao.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.organizacao.dto.UsuarioInputDTO;
import com.br.manager.domain.organizacao.dto.UsuarioResponseDTO;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.mapper.UsuarioMapper;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.br.manager.domain.comum.UtilStrings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private ServicoSincronizacao sincronizacaoService;

    @Transactional
    public UsuarioResponseDTO create(UsuarioInputDTO inputDTO) {
        try {
            Usuario usuario = usuarioMapper.usuarioInputDTOToUsuario(inputDTO);
            return usuarioMapper.usuarioToUsuarioResponseDTO(usuarioRepository.saveAndFlush(usuario));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String nome = inputDTO != null && UtilStrings.hasText(inputDTO.getNome()) ? inputDTO.getNome() : "";
            throw new ExcecaoNegocio("Erro ao salvar usuário " + nome, e);
        }
    }

    @Transactional
    public UsuarioResponseDTO update(UsuarioInputDTO inputDTO) {
        try {
            Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (usuario == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getId()));
            }
            usuarioMapper.updateUsuarioFromDto(inputDTO, usuario);
            return usuarioMapper.usuarioToUsuarioResponseDTO(usuarioRepository.saveAndFlush(usuario));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado e) {
            throw e;
        } catch (Exception e) {
            String nome = inputDTO != null && UtilStrings.hasText(inputDTO.getNome()) ? inputDTO.getNome() : "";
            throw new ExcecaoNegocio("Erro ao salvar usuário " + nome, e);
        }
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAll() {
        return usuarioMapper.listUsuarioToListUsuarioResponseDTO(usuarioRepository.findAllByAtivoTrue());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        return usuarioMapper.listUsuarioToListUsuarioResponseDTO(
                usuarioRepository.findByNameContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findByName(String nome) {
        return findByDescricao(nome);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO find(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", id)));
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", id));
        }
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuario);
    }

    @Transactional
    public void delete(UUID id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", id)));
            usuario.setAtivo(false);
            usuario.setExcluidoEm(LocalDateTime.now());
            usuarioRepository.saveAndFlush(usuario);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado e) {
            throw e;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir usuário", e);
        }
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioEntityById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", id)));
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", id));
        }
        return usuario;
    }

    /**
     * Retorna usuários criados, atualizados ou deletados após a última sincronização da tabela informada.
     * Aceita tabelaName como string que corresponde a um valor do enum SincronizeNameTabelaEnum (ex: "USUARIO").
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        var sincronizacao = sincronizacaoService.findByTabelaName(tabelaName);
        var since = sincronizacao.getUltimaSincronizacao();
        if (since == null) {
            // se não houver data, considerar desde a época Unix para garantir consistência
            since = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        }
        List<Usuario> changed = usuarioRepository.findAllChangedSince(since);
        return usuarioMapper.listUsuarioToListUsuarioResponseDTO(changed);
    }
}

