package fr.periscol.backend.service.mapper;

import fr.periscol.backend.domain.Family;
import fr.periscol.backend.service.dto.FamilyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Family} and its DTO {@link FamilyDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FamilyMapper extends EntityMapper<FamilyDTO, Family> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FamilyDTO toDtoId(Family family);
}
