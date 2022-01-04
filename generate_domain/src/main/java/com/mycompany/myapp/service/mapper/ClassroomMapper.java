package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Classroom;
import com.mycompany.myapp.service.dto.ClassroomDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Classroom} and its DTO {@link ClassroomDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ClassroomMapper extends EntityMapper<ClassroomDTO, Classroom> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClassroomDTO toDtoId(Classroom classroom);
}
