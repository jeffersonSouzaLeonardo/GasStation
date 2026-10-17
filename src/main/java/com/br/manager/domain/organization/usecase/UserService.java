package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.service.SincronizeService;
import com.br.manager.domain.organization.dto.UserInputDTO;
import com.br.manager.domain.organization.dto.UserResponseDTO;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.mapper.UserMapper;
import com.br.manager.domain.organization.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SincronizeService sincronizeService;

    public UserResponseDTO create(UserInputDTO inputDTO) {
        try {
            User user = userMapper.userInputDTOToUser(inputDTO);
            return userMapper.userToUserResponseDTO(userRepository.saveAndFlush(user));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String name = inputDTO != null && StringUtils.hasText(inputDTO.getName()) ? inputDTO.getName() : "";
            throw new BusinessException("Erro ao salvar usuário " + name, e);
        }
    }

    public UserResponseDTO update(UserInputDTO inputDTO) {
        try {
            User user = userRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (user == null) {
                throw new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getId()));
            }
            userMapper.updateUserFromDto(inputDTO, user);
            return userMapper.userToUserResponseDTO(userRepository.saveAndFlush(user));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException e) {
            throw e;
        } catch (Exception e) {
            String name = inputDTO != null && StringUtils.hasText(inputDTO.getName()) ? inputDTO.getName() : "";
            throw new BusinessException("Erro ao salvar usuário " + name, e);
        }
    }

    public List<UserResponseDTO> findAll() {
        return userMapper.listUserToListUserResponseDTO(userRepository.findAllByActiveTrue());
    }

    public List<UserResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        return userMapper.listUserToListUserResponseDTO(
                userRepository.findByNameContainingIgnoreCaseAndActiveTrue(description));
    }

    public List<UserResponseDTO> findByName(String name) {
        return findByDescription(name);
    }

    public UserResponseDTO find(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", id)));
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", id));
        }
        return userMapper.userToUserResponseDTO(user);
    }

    public void delete(UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", id)));
            user.setActive(false);
            user.setDeletedAt(LocalDateTime.now());
            userRepository.saveAndFlush(user);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao excluir usuário", e);
        }
    }

    public User getUserEntityById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", id)));
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", id));
        }
        return user;
    }

    /**
     * Retorna usuários criados, atualizados ou deletados após a última sincronização da tabela informada.
     * Aceita tableName como string que corresponde a um valor do enum SincronizeNameTableEnum (ex: "USER").
     */
    public List<UserResponseDTO> findChangedSinceSincronize(String tableName) {
        var sincronize = sincronizeService.findByTableName(tableName);
        var since = sincronize.getLastSincronize();
        if (since == null) {
            // se não houver data, considerar desde a época Unix para garantir consistência
            since = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        }
        List<User> changed = userRepository.findAllChangedSince(since);
        return userMapper.listUserToListUserResponseDTO(changed);
    }
}

