package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.UserLinkInputDTO;
import com.br.manager.domain.organization.dto.UserLinkResponseDTO;
import com.br.manager.domain.organization.entity.UserLink;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserLinkMapper {
    public abstract UserLink userLinkInputDTOToUserLink(UserLinkInputDTO inputDTO);

    public abstract UserLinkResponseDTO userLinkToUserLinkResponseDTO(UserLink userLink);

    public abstract List<UserLinkResponseDTO> listUserLinkToListUserLinkResponseDTO(List<UserLink> userLinks);

    public abstract void updateUserLinkFromDto(UserLinkInputDTO dto, @MappingTarget UserLink entity);
}
