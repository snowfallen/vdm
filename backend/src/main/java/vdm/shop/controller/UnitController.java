package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vdm.shop.dto.unit.UnitRequestDto;
import vdm.shop.dto.unit.UnitResponseDto;
import vdm.shop.service.unit.UnitService;

@Tag(name = "Unit management", description = "Endpoints for units of measurement")
@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
@Slf4j
public class UnitController {
    private final UnitService unitService;

    @Operation(summary = "Create unit")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UnitResponseDto create(@RequestBody @Valid UnitRequestDto requestDto) {
        log.info("Creating unit: {}", requestDto);
        return unitService.create(requestDto);
    }

    @Operation(summary = "Get all units")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved units")
    @GetMapping
    public List<UnitResponseDto> getAll() {
        log.info("Fetching all units");
        return unitService.getAll();
    }

    @Operation(summary = "Get unit by ID")
    @GetMapping("/{id}")
    public UnitResponseDto getById(@PathVariable Long id) {
        log.info("Fetching unit by id: {}", id);
        return unitService.getById(id);
    }

    @Operation(summary = "Update unit by ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UnitResponseDto update(@PathVariable Long id,
                                   @RequestBody @Valid UnitRequestDto requestDto) {
        log.info("Updating unit with id: {}", id);
        return unitService.update(id, requestDto);
    }

    @Operation(summary = "Delete unit by ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UnitResponseDto delete(@PathVariable Long id) {
        log.info("Deleting unit with id: {}", id);
        return unitService.delete(id);
    }
}
