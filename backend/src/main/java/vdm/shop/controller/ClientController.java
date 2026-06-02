package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.dto.client.ClientRegistrationRequestDto;
import vdm.shop.dto.client.ClientRequestDto;
import vdm.shop.dto.client.ClientResponseDto;
import vdm.shop.exception.RegistrationException;
import vdm.shop.model.User;
import vdm.shop.service.client.ClientService;

@Tag(name = "Client Management", description = "Endpoints for managing client data")
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    private static final long CLIENT_ROLE_ID = 2L;
    private final ClientService clientService;

    @Operation(summary = "Register a new client (User + Client Profile)",
            description = "Public endpoint to register a new user with "
                    + "CLIENT role and create their client profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409",
                    description = "Conflict - User (email/phone) already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponseDto create(@RequestBody @Valid ClientRegistrationRequestDto requestDto)
            throws RegistrationException {
        log.info("Client registration request for email: {}",
                requestDto.getUserRegistrationData().getEmail());
        requestDto.getUserRegistrationData().setRoleId(CLIENT_ROLE_ID);
        return this.clientService.create(requestDto);
    }

    @Operation(summary = "Get all clients (Admin only)",
            description = "Fetches a paginated list of all clients. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved list of clients"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ClientResponseDto> getAllClients(Pageable pageable) {
        log.info("Admin request to get all clients. Pageable: {}", pageable);
        return clientService.getAll(pageable);
    }

    @Operation(summary = "Get client by ID (Admin only)",
            description = "Fetches client details by their ID. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved client"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClientResponseDto getClientById(@PathVariable Long id) {
        log.info("Admin request to get client by ID: {}", id);
        return clientService.getById(id);
    }

    @Operation(summary = "Get own client data",
            description = "Fetches the client data for the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved client data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Client data not found for this user")
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ClientResponseDto getOwnClientData(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("Client request to get own data. User ID: {}", user.getId());
        return clientService.getByUserId(user.getId());
    }

    @Operation(summary = "Update client data by ID (Admin only)",
            description = "Updates client data by their ID. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client data updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClientResponseDto updateClientByAdmin(@PathVariable Long id,
                                                 @RequestBody @Valid ClientRequestDto requestDto) {
        log.info("Admin request to update client data. Client ID: {}, Data: {}", id, requestDto);
        return clientService.updateByAdmin(id, requestDto);
    }

    @Operation(summary = "Update own client data",
            description = "Allows the authenticated client to update their own data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client data updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Client data not found for this user")
    })
    @PutMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ClientResponseDto updateOwnClientData(Authentication authentication,
                                                 @RequestBody @Valid ClientRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        log.info("Client request to update own data. User ID: {}, Data: {}",
                user.getId(), requestDto);
        return clientService.updateOwnData(user.getId(), requestDto);
    }

    @Operation(summary = "Delete client by ID (Admin only)",
            description = "Deletes a client by their ID. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteClient(@PathVariable Long id) {
        log.info("Admin request to delete client with ID: {}", id);
        clientService.delete(id);
    }
}
