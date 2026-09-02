package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.dto.UserInputDTO;
import com.br.manager.domain.organization.dto.UserResponseDTO;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.mapper.UserMapper;
import com.br.manager.domain.organization.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserResponseDTO create(UserInputDTO inputDTO) {
        try {
            User user = userMapper.userInputDTOToUser(inputDTO);
            if (user.getId() == null) {
                user.setId(UUID.randomUUID());
            }
            return userMapper.userToUserResponseDTO(userRepository.saveAndFlush(user));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String name = inputDTO != null && StringUtils.hasText(inputDTO.getName()) ? inputDTO.getName() : "";
            throw new BusinessException("Error while saving user " + name, e);
        }
    }

    public UserResponseDTO update(UserInputDTO inputDTO) {
        try {
            User user = userRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (user == null) {
                throw new NotFoundBusinessException(String.format("User with ID %s not found", inputDTO.getId()));
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
            throw new BusinessException("Error while saving user " + name, e);
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
        User user = userRepository.findByIdAndActiveTrue(id);
        if (user == null) {
            throw new NotFoundBusinessException(String.format("User with ID %s not found", id));
        }
        return userMapper.userToUserResponseDTO(user);
    }

    public void delete(UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("User with ID %s not found", id)));
            user.setActive(false);
            userRepository.saveAndFlush(user);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting user", e);
        }
    }

    public User getUserEntityById(UUID id) {
        User user = userRepository.findByIdAndActiveTrue(id);
        if (user == null) {
            throw new NotFoundBusinessException(String.format("User with ID %s not found", id));
        }
        return user;
    }
}
