package com.hsp.HospitalMS.mapper;

import com.hsp.HospitalMS.dtos.user.UserSignupDto;
import com.hsp.HospitalMS.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserSignupDto dto);

}
