package aidyn.kelbetov.mapper;

import aidyn.kelbetov.dto.RegisterDto;
import aidyn.kelbetov.dto.UserDto;
import aidyn.kelbetov.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User entity);
    User toEntity(UserDto dto);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailConfirmed", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "confirmationToken", ignore = true)
    @Mapping(target = "tokenExpiry", ignore = true)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(dto.getPassword()))")
    @Mapping(target = "role", constant = "ROLE_STUDENT")
    User fromRegisterDto(RegisterDto dto, PasswordEncoder passwordEncoder);
}
