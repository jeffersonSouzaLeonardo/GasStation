package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.UserInputDTO;
import com.br.manager.domain.organization.dto.UserResponseDTO;
import com.br.manager.domain.organization.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public User userInputDTOToUser(UserInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        User entity = new User();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public UserResponseDTO userToUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDTO dto = new UserResponseDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    public List<UserResponseDTO> listUserToListUserResponseDTO(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream().map(this::userToUserResponseDTO).toList();
    }

    public void updateUserFromDto(UserInputDTO dto, User entity) {
        if (dto == null || entity == null) {
            return;
        }
        // Copy only non-null properties from dto to entity to avoid overwriting timestamps unintentionally
        BeanUtils.copyProperties(dto, entity);
    }
}
