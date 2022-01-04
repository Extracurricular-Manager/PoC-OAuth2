package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ChildToDiet;
import com.mycompany.myapp.service.dto.ChildToDietDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChildToDiet} and its DTO {@link ChildToDietDTO}.
 */
@Mapper(componentModel = "spring", uses = { ChildMapper.class, DietMapper.class })
public interface ChildToDietMapper extends EntityMapper<ChildToDietDTO, ChildToDiet> {
    @Mapping(target = "idChildren", source = "idChildren", qualifiedByName = "idSet")
    @Mapping(target = "idDiets", source = "idDiets", qualifiedByName = "idSet")
    ChildToDietDTO toDto(ChildToDiet s);

    @Mapping(target = "removeIdChild", ignore = true)
    @Mapping(target = "removeIdDiet", ignore = true)
    ChildToDiet toEntity(ChildToDietDTO childToDietDTO);
}
