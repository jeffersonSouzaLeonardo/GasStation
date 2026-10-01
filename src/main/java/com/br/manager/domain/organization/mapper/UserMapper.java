package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.UserInputDTO;
import com.br.manager.domain.organization.dto.UserResponseDTO;
import com.br.manager.domain.organization.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public abstract User userInputDTOToUser(UserInputDTO inputDTO);

    public abstract UserResponseDTO userToUserResponseDTO(User user);

    public abstract List<UserResponseDTO> listUserToListUserResponseDTO(List<User> users);

    public abstract void updateUserFromDto(UserInputDTO dto, @MappingTarget User entity);
}
