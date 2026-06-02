package vdm.shop.service.attributeoption;

import java.util.List;
import vdm.shop.dto.attributeoption.AttributeOptionRequestDto;
import vdm.shop.dto.attributeoption.AttributeOptionResponseDto;

public interface AttributeOptionService {
    AttributeOptionResponseDto create(AttributeOptionRequestDto requestDto);

    List<AttributeOptionResponseDto> getAllByAttributeId(Long attributeId);

    AttributeOptionResponseDto getById(Long id);

    AttributeOptionResponseDto update(Long id, AttributeOptionRequestDto requestDto);

    AttributeOptionResponseDto delete(Long id);
}
