package vdm.shop.service.unit;

import java.util.List;
import vdm.shop.dto.unit.UnitRequestDto;
import vdm.shop.dto.unit.UnitResponseDto;

public interface UnitService {
    UnitResponseDto create(UnitRequestDto requestDto);

    List<UnitResponseDto> getAll();

    UnitResponseDto getById(Long id);

    UnitResponseDto update(Long id, UnitRequestDto requestDto);

    UnitResponseDto delete(Long id);
}
