package vdm.shop.service.attribute.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vdm.shop.dto.attribute.AttributeRequestDto;
import vdm.shop.dto.attribute.AttributeResponseDto;
import vdm.shop.dto.attribute.AttributeWithOptionsResponseDto;
import vdm.shop.dto.attribute.SubCategoryFiltersResponseDto;
import vdm.shop.exception.EntityNotFoundException;
import vdm.shop.mapper.AttributeMapper;
import vdm.shop.model.Attribute;
import vdm.shop.model.Unit;
import vdm.shop.model.enumeration.AttributeDataType;
import vdm.shop.repository.attribute.AttributeRepository;
import vdm.shop.repository.attributeoption.AttributeOptionRepository;
import vdm.shop.repository.productattribute.ProductAttributeRepository;
import vdm.shop.repository.unit.UnitRepository;
import vdm.shop.service.attribute.AttributeService;

@RequiredArgsConstructor
@Service
@Slf4j
public class AttributeServiceImpl implements AttributeService {
    private static final String NOT_FOUND = "Attribute not found by id: ";
    private final AttributeRepository attributeRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final UnitRepository unitRepository;
    private final AttributeMapper attributeMapper;

    @Override
    public AttributeResponseDto create(AttributeRequestDto requestDto) {
        log.info("Creating attribute: {}", requestDto.name());
        Attribute attribute = attributeMapper.toModel(requestDto);
        if (requestDto.unitId() != null) {
            Unit unit = unitRepository.findById(requestDto.unitId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Unit not found by id: " + requestDto.unitId()));
            attribute.setUnit(unit);
        }
        Attribute saved = attributeRepository.save(attribute);
        log.info("Attribute created with id: {}", saved.getId());
        return attributeMapper.toDto(saved);
    }

    @Override
    public Page<AttributeResponseDto> getAll(Pageable pageable) {
        log.info("Fetching all attributes with pageable: {}", pageable);
        return attributeMapper.toDtoPage(attributeRepository.findAllWithUnit(pageable));
    }

    @Override
    public List<AttributeResponseDto> getAllList() {
        log.info("Fetching all attributes as list");
        return attributeMapper.toDtoList(attributeRepository.findAllWithUnit());
    }

    @Override
    public AttributeResponseDto getById(Long id) {
        log.info("Fetching attribute by id: {}", id);
        return attributeMapper.toDto(getAttribute(id));
    }

    @Override
    public AttributeResponseDto update(Long id, AttributeRequestDto requestDto) {
        log.info("Updating attribute with id: {}", id);
        Attribute attribute = getAttribute(id);
        attribute.setName(requestDto.name());
        attribute.setDataType(requestDto.dataType());
        if (requestDto.unitId() != null) {
            Unit unit = unitRepository.findById(requestDto.unitId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Unit not found by id: " + requestDto.unitId()));
            attribute.setUnit(unit);
        } else {
            attribute.setUnit(null);
        }
        Attribute updated = attributeRepository.save(attribute);
        log.info("Attribute with id: {} updated", id);
        return attributeMapper.toDto(updated);
    }

    @Override
    public AttributeResponseDto delete(Long id) {
        log.info("Deleting attribute with id: {}", id);
        Attribute attribute = getAttribute(id);
        AttributeResponseDto dto = attributeMapper.toDto(attribute);
        attributeRepository.delete(attribute);
        log.info("Attribute with id: {} deleted", id);
        return dto;
    }

    @Override
    public AttributeWithOptionsResponseDto getWithOptions(Long id) {
        log.info("Fetching attribute with options, id: {}", id);
        Attribute attribute = getAttribute(id);
        AttributeWithOptionsResponseDto dto = new AttributeWithOptionsResponseDto();
        dto.setId(attribute.getId());
        dto.setName(attribute.getName());
        dto.setDataType(attribute.getDataType());
        if (attribute.getUnit() != null) {
            dto.setUnitSymbol(attribute.getUnit().getSymbol());
        }
        if (attribute.getDataType() == AttributeDataType.DICT) {
            List<AttributeWithOptionsResponseDto.OptionDto> options =
                    attributeOptionRepository.findAllByAttributeId(id).stream()
                            .map(opt -> {
                                AttributeWithOptionsResponseDto.OptionDto o =
                                        new AttributeWithOptionsResponseDto.OptionDto();
                                o.setId(opt.getId());
                                o.setValue(opt.getValue());
                                return o;
                            }).toList();
            dto.setOptions(options);
        }
        return dto;
    }

    @Override
    public SubCategoryFiltersResponseDto getFiltersForSubCategory(Long subCategoryId) {
        log.info("Building filters for subCategory id: {}", subCategoryId);
        List<Object[]> rows = productAttributeRepository.findFiltersForSubCategory(subCategoryId);
        return buildFiltersResponse(subCategoryId, rows);
    }

    @Override
    public SubCategoryFiltersResponseDto getFiltersForProductGroup(Long productGroupId) {
        log.info("Building filters for productGroup id: {}", productGroupId);
        List<Object[]> rows = productAttributeRepository
                .findFiltersForProductGroup(productGroupId);
        return buildFiltersResponse(productGroupId, rows);
    }

    private SubCategoryFiltersResponseDto buildFiltersResponse(Long id, List<Object[]> rows) {
        Map<Long, SubCategoryFiltersResponseDto.FilterAttributeDto> map = new LinkedHashMap<>();

        for (Object[] row : rows) {
            Long attrId = toLong(row[0]);
            String attrName = (String) row[1];
            // row[2] може бути String (з CAST) або AttributeDataType enum — обробляємо обидва
            AttributeDataType dataType = toDataType(row[2]);
            String unitSymbol = (String) row[3]; // може бути null для DICT/TEXT
            String val = (String) row[4];

            SubCategoryFiltersResponseDto.FilterAttributeDto filter =
                    map.computeIfAbsent(attrId, k -> {
                        SubCategoryFiltersResponseDto.FilterAttributeDto f =
                                new SubCategoryFiltersResponseDto.FilterAttributeDto();
                        f.setAttributeId(attrId);
                        f.setAttributeName(attrName);
                        f.setDataType(dataType);
                        f.setUnitSymbol(unitSymbol);
                        f.setValues(new ArrayList<>());
                        return f;
                    });

            if (val == null) {
                continue;
            }

            if (dataType == AttributeDataType.NUMBER) {
                try {
                    double num = Double.parseDouble(val);
                    if (filter.getMinValue() == null
                            || num < Double.parseDouble(filter.getMinValue())) {
                        filter.setMinValue(val);
                    }
                    if (filter.getMaxValue() == null
                            || num > Double.parseDouble(filter.getMaxValue())) {
                        filter.setMaxValue(val);
                    }
                } catch (NumberFormatException ignored) {
                    filter.getValues().add(val);
                }
            } else {
                if (!filter.getValues().contains(val)) {
                    filter.getValues().add(val);
                }
            }
        }

        SubCategoryFiltersResponseDto response = new SubCategoryFiltersResponseDto();
        response.setSubCategoryId(id);
        response.setFilters(new ArrayList<>(map.values()));
        return response;
    }

    private Long toLong(Object val) {
        if (val instanceof Long l) {
            return l;
        }
        if (val instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(val.toString());
    }

    private AttributeDataType toDataType(Object val) {
        if (val instanceof AttributeDataType dt) {
            return dt;
        }
        return AttributeDataType.valueOf(val.toString());
    }

    private Attribute getAttribute(Long id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND + id));
    }
}
