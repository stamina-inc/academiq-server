package com.stamina_inc.academiq_auth.mapper;

import com.stamina_inc.academiq_auth.domain.dto.MemberDTO;
import com.stamina_inc.academiq_auth.domain.entities.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberDTO toDTO(Member member);

}
