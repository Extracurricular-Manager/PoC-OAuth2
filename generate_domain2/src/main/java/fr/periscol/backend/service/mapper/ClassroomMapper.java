package fr.periscol.backend.service.mapper;

import fr.periscol.backend.domain.Classroom;
import fr.periscol.backend.service.dto.ClassroomDTO;
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
