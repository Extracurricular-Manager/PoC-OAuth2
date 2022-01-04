package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Diet;
import com.mycompany.myapp.repository.DietRepository;
import com.mycompany.myapp.service.dto.DietDTO;
import com.mycompany.myapp.service.mapper.DietMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Diet}.
 */
@Service
@Transactional
public class DietService {

    private final Logger log = LoggerFactory.getLogger(DietService.class);

    private final DietRepository dietRepository;

    private final DietMapper dietMapper;

    public DietService(DietRepository dietRepository, DietMapper dietMapper) {
        this.dietRepository = dietRepository;
        this.dietMapper = dietMapper;
    }

    /**
     * Save a diet.
     *
     * @param dietDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DietDTO> save(DietDTO dietDTO) {
        log.debug("Request to save Diet : {}", dietDTO);
        return dietRepository.save(dietMapper.toEntity(dietDTO)).map(dietMapper::toDto);
    }

    /**
     * Partially update a diet.
     *
     * @param dietDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<DietDTO> partialUpdate(DietDTO dietDTO) {
        log.debug("Request to partially update Diet : {}", dietDTO);

        return dietRepository
            .findById(dietDTO.getId())
            .map(existingDiet -> {
                dietMapper.partialUpdate(existingDiet, dietDTO);

                return existingDiet;
            })
            .flatMap(dietRepository::save)
            .map(dietMapper::toDto);
    }

    /**
     * Get all the diets.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DietDTO> findAll() {
        log.debug("Request to get all Diets");
        return dietRepository.findAll().map(dietMapper::toDto);
    }

    /**
     * Returns the number of diets available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return dietRepository.count();
    }

    /**
     * Get one diet by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<DietDTO> findOne(Long id) {
        log.debug("Request to get Diet : {}", id);
        return dietRepository.findById(id).map(dietMapper::toDto);
    }

    /**
     * Delete the diet by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Diet : {}", id);
        return dietRepository.deleteById(id);
    }
}
