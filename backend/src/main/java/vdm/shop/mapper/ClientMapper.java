package vdm.shop.mapper;

import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.client.ClientRequestDto;
import vdm.shop.dto.client.ClientResponseDto;
import vdm.shop.model.Client;

@Mapper(config = MapperConfig.class, uses = UserMapper.class)
public interface ClientMapper {

    @Mapping(target = "userId", ignore = true)
    ClientResponseDto toDto(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    Client toModel(ClientRequestDto requestDto);

    @AfterMapping
    default void setUserId(@MappingTarget ClientResponseDto responseDto, Client client) {
        if (client.getUser() != null) {
            responseDto.setUserId(client.getUser().getId());
        }
    }

    // Метод для маппінгу оновлення, щоб не затирати user, createdAt
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true) // Оновиться в сервісі
    void updateClientFromDto(ClientRequestDto dto, @MappingTarget Client entity);

    default Page<ClientResponseDto> toDtoPage(Page<Client> clients) {
        List<ClientResponseDto> dtoList = clients.stream()
                .map(this::toDto)
                .toList();
        return new PageImpl<>(dtoList, clients.getPageable(), clients.getTotalElements());
    }
}
