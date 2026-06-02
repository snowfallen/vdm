package vdm.shop.service.attributeoption.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vdm.shop.dto.attributeoption.AttributeOptionRequestDto;
import vdm.shop.dto.attributeoption.AttributeOptionResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.AttributeOptionMapper;
import vdm.shop.model.Attribute;
import vdm.shop.model.AttributeOption;
import vdm.shop.repository.attribute.AttributeRepository;
import vdm.shop.repository.attributeoption.AttributeOptionRepository;
import vdm.shop.service.attributeoption.AttributeOptionService;

@RequiredArgsConstructor
@Service
@Slf4j
public class AttributeOptionServiceImpl implements AttributeOptionService {
    private static final String NOT_FOUND = "AttributeOption not found by id: ";
    private final AttributeOptionRepository attributeOptionRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeOptionMapper attributeOptionMapper;

    @Override
    public AttributeOptionResponseDto create(AttributeOptionRequestDto requestDto) {
        log.info("Creating option '{}' for attribute id: {}",
                requestDto.value(), requestDto.attributeId());
        AttributeOption option = attributeOptionMapper.toModel(requestDto);
        option.setAttribute(getAttribute(requestDto.attributeId()));
        AttributeOption saved = attributeOptionRepository.save(option);
        log.info("AttributeOption created with id: {}", saved.getId());
        return attributeOptionMapper.toDto(saved);
    }

    @Override
    public List<AttributeOptionResponseDto> getAllByAttributeId(Long attributeId) {
        log.info("Fetching options for attribute id: {}", attributeId);
        return attributeOptionMapper.toDtoList(
                attributeOptionRepository.findAllByAttributeId(attributeId));
    }

    @Override
    public AttributeOptionResponseDto getById(Long id) {
        log.info("Fetching attributeOption by id: {}", id);
        return attributeOptionMapper.toDto(getOption(id));
    }

    @Override
    public AttributeOptionResponseDto update(Long id, AttributeOptionRequestDto requestDto) {
        log.info("Updating attributeOption with id: {}", id);
        AttributeOption option = getOption(id);
        option.setValue(requestDto.value());
        option.setAttribute(getAttribute(requestDto.attributeId()));
        AttributeOption updated = attributeOptionRepository.save(option);
        log.info("AttributeOption with id: {} updated", id);
        return attributeOptionMapper.toDto(updated);
    }

    @Override
    public AttributeOptionResponseDto delete(Long id) {
        log.info("Deleting attributeOption with id: {}", id);
        AttributeOption option = getOption(id);
        AttributeOptionResponseDto dto = attributeOptionMapper.toDto(option);
        attributeOptionRepository.delete(option);
        log.info("AttributeOption with id: {} deleted", id);
        return dto;
    }

    private AttributeOption getOption(Long id) {
        return attributeOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND + id));
    }

    private Attribute getAttribute(Long id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Attribute not found by id: " + id));
    }
}
