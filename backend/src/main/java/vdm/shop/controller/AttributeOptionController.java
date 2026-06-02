package vdm.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import vdm.shop.dto.attributeoption.AttributeOptionRequestDto;
import vdm.shop.dto.attributeoption.AttributeOptionResponseDto;
import vdm.shop.service.attributeoption.AttributeOptionService;

@Tag(name = "AttributeOption management",
        description = "Endpoints for attribute options (dictionary values)")
@RestController
@RequestMapping("/attribute-options")
@RequiredArgsConstructor
@Slf4j
public class AttributeOptionController {
    private final AttributeOptionService attributeOptionService;

    @Operation(summary = "Create option")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AttributeOptionResponseDto create(
            @RequestBody @Valid AttributeOptionRequestDto requestDto) {
        log.info("Creating attributeOption: {}", requestDto);
        return attributeOptionService.create(requestDto);
    }

    @Operation(summary = "Get all options by attribute ID")
    @GetMapping("/attribute/{attributeId}")
    public List<AttributeOptionResponseDto> getAllByAttributeId(
            @PathVariable Long attributeId) {
        log.info("Fetching options for attribute id: {}", attributeId);
        return attributeOptionService.getAllByAttributeId(attributeId);
    }

    @Operation(summary = "Get option by ID")
    @GetMapping("/{id}")
    public AttributeOptionResponseDto getById(@PathVariable Long id) {
        log.info("Fetching attributeOption by id: {}", id);
        return attributeOptionService.getById(id);
    }

    @Operation(summary = "Update option by ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AttributeOptionResponseDto update(
            @PathVariable Long id,
            @RequestBody @Valid AttributeOptionRequestDto requestDto) {
        log.info("Updating attributeOption with id: {}", id);
        return attributeOptionService.update(id, requestDto);
    }

    @Operation(summary = "Delete option by ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AttributeOptionResponseDto delete(@PathVariable Long id) {
        log.info("Deleting attributeOption with id: {}", id);
        return attributeOptionService.delete(id);
    }
}
