package vdm.shop.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import vdm.shop.config.MapperConfig;
import vdm.shop.dto.unit.UnitRequestDto;
import vdm.shop.dto.unit.UnitResponseDto;
import vdm.shop.model.Unit;

@Mapper(config = MapperConfig.class)
public interface UnitMapper {
    Unit toModel(UnitRequestDto requestDto);

    UnitResponseDto toDto(Unit unit);

    List<UnitResponseDto> toDtoList(List<Unit> units);
}
