package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Family;
import com.mycompany.myapp.repository.FamilyRepository;
import com.mycompany.myapp.service.dto.FamilyDTO;
import com.mycompany.myapp.service.mapper.FamilyMapper;
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
 * Service Implementation for managing {@link Family}.
 */
@Service
@Transactional
public class FamilyService {

    private final Logger log = LoggerFactory.getLogger(FamilyService.class);

    private final FamilyRepository familyRepository;

    private final FamilyMapper familyMapper;

    public FamilyService(FamilyRepository familyRepository, FamilyMapper familyMapper) {
        this.familyRepository = familyRepository;
        this.familyMapper = familyMapper;
    }

    /**
     * Save a family.
     *
     * @param familyDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FamilyDTO> save(FamilyDTO familyDTO) {
        log.debug("Request to save Family : {}", familyDTO);
        return familyRepository.save(familyMapper.toEntity(familyDTO)).map(familyMapper::toDto);
    }

    /**
     * Partially update a family.
     *
     * @param familyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FamilyDTO> partialUpdate(FamilyDTO familyDTO) {
        log.debug("Request to partially update Family : {}", familyDTO);

        return familyRepository
            .findById(familyDTO.getId())
            .map(existingFamily -> {
                familyMapper.partialUpdate(existingFamily, familyDTO);

                return existingFamily;
            })
            .flatMap(familyRepository::save)
            .map(familyMapper::toDto);
    }

    /**
     * Get all the families.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FamilyDTO> findAll() {
        log.debug("Request to get all Families");
        return familyRepository.findAll().map(familyMapper::toDto);
    }

    /**
     * Returns the number of families available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return familyRepository.count();
    }

    /**
     * Get one family by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<FamilyDTO> findOne(Long id) {
        log.debug("Request to get Family : {}", id);
        return familyRepository.findById(id).map(familyMapper::toDto);
    }

    /**
     * Delete the family by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Family : {}", id);
        return familyRepository.deleteById(id);
    }
}
