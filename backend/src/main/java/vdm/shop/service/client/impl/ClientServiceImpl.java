package vdm.shop.service.client.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdm.shop.dto.client.ClientRegistrationRequestDto;
import vdm.shop.dto.client.ClientRequestDto;
import vdm.shop.dto.client.ClientResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.ClientMapper;
import vdm.shop.model.Client;
import vdm.shop.model.User;
import vdm.shop.repository.client.ClientRepository;
import vdm.shop.repository.user.UserRepository;
import vdm.shop.service.client.ClientService;
import vdm.shop.service.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {
    private static final String CLIENT_NOT_FOUND_BY_ID = "Client not found by id: ";
    private static final String CLIENT_NOT_FOUND_BY_USER_ID = "Client not found for user id: ";

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ClientResponseDto create(ClientRegistrationRequestDto requestDto) {
        log.info("Creating client");
        User user = this.userService.createUser(requestDto.getUserRegistrationData());
        Client client = clientMapper.toModel(requestDto.getClientData());
        client.setUser(user);
        client.setCreatedAt(LocalDateTime.now());
        client.setModifiedAt(LocalDateTime.now());
        Client savedClient = clientRepository.save(client);
        log.info("Client created successfully with ID: {}", savedClient.getId());
        return clientMapper.toDto(savedClient);
    }

    @Override
    public Page<ClientResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all clients with pageable: {}", pageable);
        return clientMapper.toDtoPage(clientRepository.findAll(pageable));
    }

    @Override
    public ClientResponseDto getById(Long id) {
        log.info("Fetching client by ID: {}", id);
        return clientMapper.toDto(getClientById(id));
    }

    @Override
    public ClientResponseDto getByUserId(Long userId) {
        log.info("Fetching client by user ID: {}", userId);
        Client client = clientRepository.findByUserId(userId).orElseThrow(
                () -> {
                    log.error(CLIENT_NOT_FOUND_BY_USER_ID + userId);
                    return new EntityNotFoundException(CLIENT_NOT_FOUND_BY_USER_ID + userId);
                });
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientResponseDto updateByAdmin(Long clientId, ClientRequestDto requestDto) {
        log.info("Updating client by admin. Client ID: {}", clientId);
        Client client = getClientById(clientId);
        clientMapper.updateClientFromDto(requestDto, client);
        client.setModifiedAt(LocalDateTime.now());
        Client updatedClient = clientRepository.save(client);
        log.info("Client with ID: {} updated successfully by admin.", clientId);
        return clientMapper.toDto(updatedClient);
    }

    @Override
    @Transactional
    public ClientResponseDto updateOwnData(Long userId, ClientRequestDto requestDto) {
        log.info("Updating own client data for user ID: {}", userId);
        Client client = clientRepository.findByUserId(userId).orElseThrow(
                () -> {
                    log.error(CLIENT_NOT_FOUND_BY_USER_ID + userId);
                    return new EntityNotFoundException(CLIENT_NOT_FOUND_BY_USER_ID + userId);
                });
        clientMapper.updateClientFromDto(requestDto, client);
        client.setModifiedAt(LocalDateTime.now());
        Client updatedClient = clientRepository.save(client);
        log.info("Client data for user ID: {} updated successfully.", userId);
        return clientMapper.toDto(updatedClient);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting client with ID: {}", id);
        Client client = getClientById(id);
        User associatedUser = client.getUser();

        if (associatedUser == null) {
            // Це дивна ситуація, але про всяк випадок
            log.warn("Client with ID: {} has no associated user. Deleting only client.", id);
            clientRepository.deleteById(id);
            log.info("Client with ID: {} deleted (no user found).", id);
            return;
        }

        // Видаляємо клієнта
        clientRepository.deleteById(id);
        log.info("Client with ID: {} deleted.", id);

        // Видаляємо пов'язаного юзера
        // Перевіряємо, чи юзер існує перед видаленням (про всяк випадок)
        if (userRepository.existsById(associatedUser.getId())) {
            userRepository.deleteById(associatedUser.getId());
            log.info("Associated user with ID: {} deleted.", associatedUser.getId());
        } else {
            log.warn("Associated user with ID: {} for client ID: {} not found in repository.",
                    associatedUser.getId(), id);
        }
    }

    private Client getClientById(Long id) {
        return clientRepository.findById(id).orElseThrow(
                () -> {
                    log.error(CLIENT_NOT_FOUND_BY_ID + id);
                    return new EntityNotFoundException(CLIENT_NOT_FOUND_BY_ID + id);
                });
    }
}
