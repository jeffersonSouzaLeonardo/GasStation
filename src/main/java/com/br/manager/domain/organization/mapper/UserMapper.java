package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.UserInputDTO;
import com.br.manager.domain.organization.dto.UserResponseDTO;
import com.br.manager.domain.organization.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userInputDTOToUser(UserInputDTO inputDTO);

    UserResponseDTO userToUserResponseDTO(User user);

    List<UserResponseDTO> listUserToListUserResponseDTO(List<User> users);

    void updateUserFromDto(UserInputDTO dto, @MappingTarget User entity);
}
