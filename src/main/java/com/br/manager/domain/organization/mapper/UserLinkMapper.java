package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.UserLinkInputDTO;
import com.br.manager.domain.organization.dto.UserLinkResponseDTO;
import com.br.manager.domain.organization.entity.UserLink;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserLinkMapper {

    UserLink userLinkInputDTOToUserLink(UserLinkInputDTO inputDTO);

    UserLinkResponseDTO userLinkToUserLinkResponseDTO(UserLink userLink);

    List<UserLinkResponseDTO> listUserLinkToListUserLinkResponseDTO(List<UserLink> userLinks);

    void updateUserLinkFromDto(UserLinkInputDTO dto, @MappingTarget UserLink entity);
}
