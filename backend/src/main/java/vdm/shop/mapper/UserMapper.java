package vdm.shop.mapper;

import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.user.UserRegistrationRequestDto;
import vdm.shop.dto.user.UserResponseDto;
import vdm.shop.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    String CAN_T_GET_ROLE_FOR_USER_WITH_ID = "Can't get role for user with id:";

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    @Mapping(target = "roleId", ignore = true)
    UserResponseDto toDto(User user);

    @Mapping(target = "roleId", ignore = true)
    List<UserResponseDto> toDtoList(Page<User> user);

    default Page<UserResponseDto> toDtoPage(Page<User> payments) {
        List<UserResponseDto> dtoList = payments.map(this::toDto).getContent();
        return new PageImpl<>(dtoList, payments.getPageable(), payments.getTotalElements());
    }

    @AfterMapping
    default void setRoleId(@MappingTarget UserResponseDto userResponseDto, User user) {
        Long roleId = user.getRoles().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        CAN_T_GET_ROLE_FOR_USER_WITH_ID + user.getId())
                ).getId();
        userResponseDto.setRoleId(roleId);
    }
}
