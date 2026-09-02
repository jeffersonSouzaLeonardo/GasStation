package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.dto.UserLinkInputDTO;
import com.br.manager.domain.organization.dto.UserLinkResponseDTO;
import com.br.manager.domain.organization.entity.UserLink;
import com.br.manager.domain.organization.enums.RoleTypeEnum;
import com.br.manager.domain.organization.mapper.UserLinkMapper;
import com.br.manager.domain.organization.repository.UserLinkRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class UserLinkService {

    @Autowired
    private UserLinkRepository userLinkRepository;

    @Autowired
    private UserLinkMapper userLinkMapper;

    public UserLinkResponseDTO create(UserLinkInputDTO inputDTO) {
        try {
            UserLink userLink = userLinkMapper.userLinkInputDTOToUserLink(inputDTO);
            if (userLink.getId() == null) {
                userLink.setId(UUID.randomUUID());
            }
            return userLinkMapper.userLinkToUserLinkResponseDTO(userLinkRepository.saveAndFlush(userLink));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String role = inputDTO != null && inputDTO.getRole() != null ? inputDTO.getRole().name() : "";
            throw new BusinessException("Error while saving user-link " + role, e);
        }
    }

    public UserLinkResponseDTO update(UserLinkInputDTO inputDTO) {
        try {
            UserLink userLink = userLinkRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (userLink == null) {
                throw new NotFoundBusinessException(String.format("User link with ID %s not found", inputDTO.getId()));
            }
            userLinkMapper.updateUserLinkFromDto(inputDTO, userLink);
            return userLinkMapper.userLinkToUserLinkResponseDTO(userLinkRepository.saveAndFlush(userLink));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException e) {
            throw e;
        } catch (Exception e) {
            String role = inputDTO != null && inputDTO.getRole() != null ? inputDTO.getRole().name() : "";
            throw new BusinessException("Error while saving user-link " + role, e);
        }
    }

    public List<UserLinkResponseDTO> findAll() {
        return userLinkMapper.listUserLinkToListUserLinkResponseDTO(userLinkRepository.findAllByActiveTrue());
    }

    public List<UserLinkResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        return userLinkMapper.listUserLinkToListUserLinkResponseDTO(
                userLinkRepository.findByRoleTextAndActiveTrue(description));
    }

    public List<UserLinkResponseDTO> findByName(String roleName) {
        return findByDescription(roleName);
    }

    public List<UserLinkResponseDTO> findByRole(RoleTypeEnum role) {
        return userLinkMapper.listUserLinkToListUserLinkResponseDTO(userLinkRepository.findByRoleAndActiveTrue(role));
    }

    public UserLinkResponseDTO find(UUID id) {
        UserLink userLink = userLinkRepository.findByIdAndActiveTrue(id);
        if (userLink == null) {
            throw new NotFoundBusinessException(String.format("User link with ID %s not found", id));
        }
        return userLinkMapper.userLinkToUserLinkResponseDTO(userLink);
    }

    public void delete(UUID id) {
        try {
            UserLink userLink = userLinkRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("User link with ID %s not found", id)));
            userLink.setActive(false);
            userLinkRepository.saveAndFlush(userLink);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting user-link", e);
        }
    }

    public UserLink getUserLinkEntityById(UUID id) {
        UserLink userLink = userLinkRepository.findByIdAndActiveTrue(id);
        if (userLink == null) {
            throw new NotFoundBusinessException(String.format("User link with ID %s not found", id));
        }
        return userLink;
    }
}
