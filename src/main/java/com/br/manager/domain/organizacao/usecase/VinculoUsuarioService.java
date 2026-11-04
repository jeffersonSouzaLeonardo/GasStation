package com.br.manager.domain.organizacao.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.dto.VinculoUsuarioInputDTO;
import com.br.manager.domain.organizacao.dto.VinculoUsuarioResponseDTO;
import com.br.manager.domain.organizacao.entity.VinculoUsuario;
import com.br.manager.domain.organizacao.enums.TipoCargoEnum;
import com.br.manager.domain.organizacao.mapper.VinculoUsuarioMapper;
import com.br.manager.domain.organizacao.repository.VinculoUsuarioRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.util.List;
import java.util.UUID;

@Service
public class VinculoUsuarioService {

    @Autowired
    private VinculoUsuarioRepository usuarioVinculoRepository;

    @Autowired
    private VinculoUsuarioMapper usuarioVinculoMapper;

    public VinculoUsuarioResponseDTO create(VinculoUsuarioInputDTO inputDTO) {
        try {
            VinculoUsuario usuarioVinculo = usuarioVinculoMapper.usuarioVinculoInputDTOToVinculoUsuario(inputDTO);
            return usuarioVinculoMapper.usuarioVinculoToVinculoUsuarioResponseDTO(usuarioVinculoRepository.saveAndFlush(usuarioVinculo));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String cargo = inputDTO != null && inputDTO.getCargo() != null ? inputDTO.getCargo().name() : "";
            throw new ExcecaoNegocio("Erro ao salvar vínculo de usuário " + cargo, e);
        }
    }

    public VinculoUsuarioResponseDTO update(VinculoUsuarioInputDTO inputDTO) {
        try {
            VinculoUsuario usuarioVinculo = usuarioVinculoRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (usuarioVinculo == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Vínculo de usuário com ID %s não encontrado", inputDTO.getId()));
            }
            usuarioVinculoMapper.updateVinculoUsuarioFromDto(inputDTO, usuarioVinculo);
            return usuarioVinculoMapper.usuarioVinculoToVinculoUsuarioResponseDTO(usuarioVinculoRepository.saveAndFlush(usuarioVinculo));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado e) {
            throw e;
        } catch (Exception e) {
            String cargo = inputDTO != null && inputDTO.getCargo() != null ? inputDTO.getCargo().name() : "";
            throw new ExcecaoNegocio("Erro ao salvar vínculo de usuário " + cargo, e);
        }
    }

    public List<VinculoUsuarioResponseDTO> findAll() {
        return usuarioVinculoMapper.listVinculoUsuarioToListVinculoUsuarioResponseDTO(usuarioVinculoRepository.findAllByAtivoTrue());
    }

    public List<VinculoUsuarioResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        return usuarioVinculoMapper.listVinculoUsuarioToListVinculoUsuarioResponseDTO(
                usuarioVinculoRepository.findByCargoTextAndAtivoTrue(descricao));
    }

    public List<VinculoUsuarioResponseDTO> findByName(String cargoName) {
        return findByDescricao(cargoName);
    }

    public List<VinculoUsuarioResponseDTO> findByCargo(TipoCargoEnum cargo) {
        return usuarioVinculoMapper.listVinculoUsuarioToListVinculoUsuarioResponseDTO(usuarioVinculoRepository.findByCargoAndAtivoTrue(cargo));
    }

    public VinculoUsuarioResponseDTO find(UUID id) {
        VinculoUsuario usuarioVinculo = usuarioVinculoRepository.findByIdAndAtivoTrue(id);
        if (usuarioVinculo == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Vínculo de usuário com ID %s não encontrado", id));
        }
        return usuarioVinculoMapper.usuarioVinculoToVinculoUsuarioResponseDTO(usuarioVinculo);
    }

    public void delete(UUID id) {
        try {
            VinculoUsuario usuarioVinculo = usuarioVinculoRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Vínculo de usuário com ID %s não encontrado", id)));
            usuarioVinculo.setAtivo(false);
            usuarioVinculoRepository.saveAndFlush(usuarioVinculo);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado e) {
            throw e;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir vínculo de usuário", e);
        }
    }

    public VinculoUsuario getVinculoUsuarioEntityById(UUID id) {
        VinculoUsuario usuarioVinculo = usuarioVinculoRepository.findByIdAndAtivoTrue(id);
        if (usuarioVinculo == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Vínculo de usuário com ID %s não encontrado", id));
        }
        return usuarioVinculo;
    }
}
