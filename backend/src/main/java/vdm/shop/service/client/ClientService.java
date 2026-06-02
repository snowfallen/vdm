package vdm.shop.service.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vdm.shop.dto.client.ClientRegistrationRequestDto;
import vdm.shop.dto.client.ClientRequestDto;
import vdm.shop.dto.client.ClientResponseDto;

public interface ClientService {
    ClientResponseDto create(ClientRegistrationRequestDto requestDto);

    Page<ClientResponseDto> getAll(Pageable pageable);

    ClientResponseDto getById(Long id);

    ClientResponseDto getByUserId(Long userId);

    ClientResponseDto updateByAdmin(Long clientId, ClientRequestDto requestDto);

    ClientResponseDto updateOwnData(Long userId, ClientRequestDto requestDto);

    void delete(Long id);
}
