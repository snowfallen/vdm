package vdm.shop.service.unit.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vdm.shop.dto.unit.UnitRequestDto;
import vdm.shop.dto.unit.UnitResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.UnitMapper;
import vdm.shop.model.Unit;
import vdm.shop.repository.unit.UnitRepository;
import vdm.shop.service.unit.UnitService;

@RequiredArgsConstructor
@Service
@Slf4j
public class UnitServiceImpl implements UnitService {
    private static final String NOT_FOUND = "Unit not found by id: ";
    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;

    @Override
    public UnitResponseDto create(UnitRequestDto requestDto) {
        log.info("Creating unit: {}", requestDto.symbol());
        Unit saved = unitRepository.save(unitMapper.toModel(requestDto));
        log.info("Unit created with id: {}", saved.getId());
        return unitMapper.toDto(saved);
    }

    @Override
    public List<UnitResponseDto> getAll() {
        log.info("Fetching all units");
        return unitMapper.toDtoList(unitRepository.findAll());
    }

    @Override
    public UnitResponseDto getById(Long id) {
        log.info("Fetching unit by id: {}", id);
        return unitMapper.toDto(getUnit(id));
    }

    @Override
    public UnitResponseDto update(Long id, UnitRequestDto requestDto) {
        log.info("Updating unit with id: {}", id);
        Unit unit = getUnit(id);
        unit.setSymbol(requestDto.symbol());
        unit.setDescription(requestDto.description());
        Unit updated = unitRepository.save(unit);
        log.info("Unit with id: {} updated", id);
        return unitMapper.toDto(updated);
    }

    @Override
    public UnitResponseDto delete(Long id) {
        log.info("Deleting unit with id: {}", id);
        Unit unit = getUnit(id);
        UnitResponseDto dto = unitMapper.toDto(unit);
        unitRepository.delete(unit);
        log.info("Unit with id: {} deleted", id);
        return dto;
    }

    private Unit getUnit(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND + id));
    }
}
