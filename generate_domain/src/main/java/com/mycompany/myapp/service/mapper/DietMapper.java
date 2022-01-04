package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Diet;
import com.mycompany.myapp.service.dto.DietDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Diet} and its DTO {@link DietDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DietMapper extends EntityMapper<DietDTO, Diet> {
    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<DietDTO> toDtoIdSet(Set<Diet> diet);
}
