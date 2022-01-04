package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Child;
import com.mycompany.myapp.service.dto.ChildDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Child} and its DTO {@link ChildDTO}.
 */
@Mapper(componentModel = "spring", uses = { ClassroomMapper.class, FamilyMapper.class, GradeLevelMapper.class, DietMapper.class })
public interface ChildMapper extends EntityMapper<ChildDTO, Child> {
    @Mapping(target = "classroom", source = "classroom", qualifiedByName = "id")
    @Mapping(target = "adelphie", source = "adelphie", qualifiedByName = "id")
    @Mapping(target = "gradeLevel", source = "gradeLevel", qualifiedByName = "id")
    @Mapping(target = "diets", source = "diets", qualifiedByName = "idSet")
    ChildDTO toDto(Child s);

    @Mapping(target = "removeDiet", ignore = true)
    Child toEntity(ChildDTO childDTO);
}
