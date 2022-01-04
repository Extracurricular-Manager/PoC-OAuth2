package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.GradeLevel;
import com.mycompany.myapp.repository.GradeLevelRepository;
import com.mycompany.myapp.service.dto.GradeLevelDTO;
import com.mycompany.myapp.service.mapper.GradeLevelMapper;
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
 * Service Implementation for managing {@link GradeLevel}.
 */
@Service
@Transactional
public class GradeLevelService {

    private final Logger log = LoggerFactory.getLogger(GradeLevelService.class);

    private final GradeLevelRepository gradeLevelRepository;

    private final GradeLevelMapper gradeLevelMapper;

    public GradeLevelService(GradeLevelRepository gradeLevelRepository, GradeLevelMapper gradeLevelMapper) {
        this.gradeLevelRepository = gradeLevelRepository;
        this.gradeLevelMapper = gradeLevelMapper;
    }

    /**
     * Save a gradeLevel.
     *
     * @param gradeLevelDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<GradeLevelDTO> save(GradeLevelDTO gradeLevelDTO) {
        log.debug("Request to save GradeLevel : {}", gradeLevelDTO);
        return gradeLevelRepository.save(gradeLevelMapper.toEntity(gradeLevelDTO)).map(gradeLevelMapper::toDto);
    }

    /**
     * Partially update a gradeLevel.
     *
     * @param gradeLevelDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<GradeLevelDTO> partialUpdate(GradeLevelDTO gradeLevelDTO) {
        log.debug("Request to partially update GradeLevel : {}", gradeLevelDTO);

        return gradeLevelRepository
            .findById(gradeLevelDTO.getId())
            .map(existingGradeLevel -> {
                gradeLevelMapper.partialUpdate(existingGradeLevel, gradeLevelDTO);

                return existingGradeLevel;
            })
            .flatMap(gradeLevelRepository::save)
            .map(gradeLevelMapper::toDto);
    }

    /**
     * Get all the gradeLevels.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<GradeLevelDTO> findAll() {
        log.debug("Request to get all GradeLevels");
        return gradeLevelRepository.findAll().map(gradeLevelMapper::toDto);
    }

    /**
     * Returns the number of gradeLevels available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return gradeLevelRepository.count();
    }

    /**
     * Get one gradeLevel by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<GradeLevelDTO> findOne(String id) {
        log.debug("Request to get GradeLevel : {}", id);
        return gradeLevelRepository.findById(id).map(gradeLevelMapper::toDto);
    }

    /**
     * Delete the gradeLevel by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete GradeLevel : {}", id);
        return gradeLevelRepository.deleteById(id);
    }
}
