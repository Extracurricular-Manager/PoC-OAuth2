package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.ChildToDiet;
import com.mycompany.myapp.repository.ChildToDietRepository;
import com.mycompany.myapp.service.dto.ChildToDietDTO;
import com.mycompany.myapp.service.mapper.ChildToDietMapper;
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
 * Service Implementation for managing {@link ChildToDiet}.
 */
@Service
@Transactional
public class ChildToDietService {

    private final Logger log = LoggerFactory.getLogger(ChildToDietService.class);

    private final ChildToDietRepository childToDietRepository;

    private final ChildToDietMapper childToDietMapper;

    public ChildToDietService(ChildToDietRepository childToDietRepository, ChildToDietMapper childToDietMapper) {
        this.childToDietRepository = childToDietRepository;
        this.childToDietMapper = childToDietMapper;
    }

    /**
     * Save a childToDiet.
     *
     * @param childToDietDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ChildToDietDTO> save(ChildToDietDTO childToDietDTO) {
        log.debug("Request to save ChildToDiet : {}", childToDietDTO);
        return childToDietRepository.save(childToDietMapper.toEntity(childToDietDTO)).map(childToDietMapper::toDto);
    }

    /**
     * Partially update a childToDiet.
     *
     * @param childToDietDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ChildToDietDTO> partialUpdate(ChildToDietDTO childToDietDTO) {
        log.debug("Request to partially update ChildToDiet : {}", childToDietDTO);

        return childToDietRepository
            .findById(childToDietDTO.getId())
            .map(existingChildToDiet -> {
                childToDietMapper.partialUpdate(existingChildToDiet, childToDietDTO);

                return existingChildToDiet;
            })
            .flatMap(childToDietRepository::save)
            .map(childToDietMapper::toDto);
    }

    /**
     * Get all the childToDiets.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ChildToDietDTO> findAll() {
        log.debug("Request to get all ChildToDiets");
        return childToDietRepository.findAllWithEagerRelationships().map(childToDietMapper::toDto);
    }

    /**
     * Get all the childToDiets with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ChildToDietDTO> findAllWithEagerRelationships(Pageable pageable) {
        return childToDietRepository.findAllWithEagerRelationships(pageable).map(childToDietMapper::toDto);
    }

    /**
     * Returns the number of childToDiets available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return childToDietRepository.count();
    }

    /**
     * Get one childToDiet by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ChildToDietDTO> findOne(Long id) {
        log.debug("Request to get ChildToDiet : {}", id);
        return childToDietRepository.findOneWithEagerRelationships(id).map(childToDietMapper::toDto);
    }

    /**
     * Delete the childToDiet by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ChildToDiet : {}", id);
        return childToDietRepository.deleteById(id);
    }
}
