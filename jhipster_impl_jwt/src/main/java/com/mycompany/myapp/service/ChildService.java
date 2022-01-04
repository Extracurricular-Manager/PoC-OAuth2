package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Child;
import com.mycompany.myapp.repository.ChildRepository;
import com.mycompany.myapp.service.dto.ChildDTO;
import com.mycompany.myapp.service.mapper.ChildMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<ChildDTO> save(ChildDTO childDTO) {
        log.debug("Request to save Child : {}", childDTO);
        return childRepository.save(childMapper.toEntity(childDTO)).map(childMapper::toDto);
    }

    /**
     * Partially update a child.
     *
     * @param childDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ChildDTO> partialUpdate(ChildDTO childDTO) {
        log.debug("Request to partially update Child : {}", childDTO);

        return childRepository
            .findById(childDTO.getId())
            .map(existingChild -> {
                childMapper.partialUpdate(existingChild, childDTO);

                return existingChild;
            })
            .flatMap(childRepository::save)
            .map(childMapper::toDto);
    }

    /**
     * Get all the children.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ChildDTO> findAll() {
        log.debug("Request to get all Children");
        return childRepository.findAllWithEagerRelationships().map(childMapper::toDto);
    }

    /**
     * Get all the children with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ChildDTO> findAllWithEagerRelationships(Pageable pageable) {
        return childRepository.findAllWithEagerRelationships(pageable).map(childMapper::toDto);
    }

    /**
     * Returns the number of children available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return childRepository.count();
    }

    /**
     * Get one child by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ChildDTO> findOne(Long id) {
        log.debug("Request to get Child : {}", id);
        return childRepository.findOneWithEagerRelationships(id).map(childMapper::toDto);
    }

    /**
     * Delete the child by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Child : {}", id);
        return childRepository.deleteById(id);
    }
}
