package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.UserLinkInputDTO;
import com.br.manager.domain.organization.dto.UserLinkResponseDTO;
import com.br.manager.domain.organization.entity.UserLink;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserLinkMapper {
    public UserLink userLinkInputDTOToUserLink(UserLinkInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        UserLink entity = new UserLink();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public UserLinkResponseDTO userLinkToUserLinkResponseDTO(UserLink userLink) {
        if (userLink == null) {
            return null;
        }
        UserLinkResponseDTO dto = new UserLinkResponseDTO();
        BeanUtils.copyProperties(userLink, dto);
        return dto;
    }

    public List<UserLinkResponseDTO> listUserLinkToListUserLinkResponseDTO(List<UserLink> userLinks) {
        if (userLinks == null) {
            return null;
        }
        return userLinks.stream().map(this::userLinkToUserLinkResponseDTO).toList();
    }

    public void updateUserLinkFromDto(UserLinkInputDTO dto, UserLink entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
