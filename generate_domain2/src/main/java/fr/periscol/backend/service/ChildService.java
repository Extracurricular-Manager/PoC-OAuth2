package fr.periscol.backend.service;

import fr.periscol.backend.domain.Child;
import fr.periscol.backend.repository.ChildRepository;
import fr.periscol.backend.service.dto.ChildDTO;
import fr.periscol.backend.service.mapper.ChildMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Child}.
 */
@Service
@Transactional
public class ChildService {

    private final Logger log = LoggerFactory.getLogger(ChildService.class);

    private final ChildRepository childRepository;

    private final ChildMapper childMapper;

    public ChildService(ChildRepository childRepository, ChildMapper childMapper) {
        this.childRepository = childRepository;
        this.childMapper = childMapper;
    }

    /**
     * Save a child.
     *
     * @param childDTO the entity to save.
     * @return the persisted entity.
     */
    public ChildDTO save(ChildDTO childDTO) {
        log.debug("Request to save Child : {}", childDTO);
        Child child = childMapper.toEntity(childDTO);
        child = childRepository.save(child);
        return childMapper.toDto(child);
    }

    /**
     * Partially update a child.
     *
     * @param childDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ChildDTO> partialUpdate(ChildDTO childDTO) {
        log.debug("Request to partially update Child : {}", childDTO);

        return childRepository
            .findById(childDTO.getId())
            .map(existingChild -> {
                childMapper.partialUpdate(existingChild, childDTO);

                return existingChild;
            })
            .map(childRepository::save)
            .map(childMapper::toDto);
    }

    /**
     * Get all the children.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ChildDTO> findAll() {
        log.debug("Request to get all Children");
        return childRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(childMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the children with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ChildDTO> findAllWithEagerRelationships(Pageable pageable) {
        return childRepository.findAllWithEagerRelationships(pageable).map(childMapper::toDto);
    }

    /**
     * Get one child by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ChildDTO> findOne(Long id) {
        log.debug("Request to get Child : {}", id);
        return childRepository.findOneWithEagerRelationships(id).map(childMapper::toDto);
    }

    /**
     * Delete the child by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Child : {}", id);
        childRepository.deleteById(id);
    }
}
