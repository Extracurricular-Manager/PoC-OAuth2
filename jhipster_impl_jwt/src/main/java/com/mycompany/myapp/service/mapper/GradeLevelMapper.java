package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.GradeLevel;
import com.mycompany.myapp.service.dto.GradeLevelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GradeLevel} and its DTO {@link GradeLevelDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface GradeLevelMapper extends EntityMapper<GradeLevelDTO, GradeLevel> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GradeLevelDTO toDtoId(GradeLevel gradeLevel);
}
