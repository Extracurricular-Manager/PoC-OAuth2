package fr.periscol.backend.service;

import fr.periscol.backend.domain.Family;
import fr.periscol.backend.repository.FamilyRepository;
import fr.periscol.backend.service.dto.FamilyDTO;
import fr.periscol.backend.service.mapper.FamilyMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public FamilyDTO save(FamilyDTO familyDTO) {
        log.debug("Request to save Family : {}", familyDTO);
        Family family = familyMapper.toEntity(familyDTO);
        family = familyRepository.save(family);
        return familyMapper.toDto(family);
    }

    /**
     * Partially update a family.
     *
     * @param familyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FamilyDTO> partialUpdate(FamilyDTO familyDTO) {
        log.debug("Request to partially update Family : {}", familyDTO);

        return familyRepository
            .findById(familyDTO.getId())
            .map(existingFamily -> {
                familyMapper.partialUpdate(existingFamily, familyDTO);

                return existingFamily;
            })
            .map(familyRepository::save)
            .map(familyMapper::toDto);
    }

    /**
     * Get all the families.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FamilyDTO> findAll() {
        log.debug("Request to get all Families");
        return familyRepository.findAll().stream().map(familyMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one family by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FamilyDTO> findOne(Long id) {
        log.debug("Request to get Family : {}", id);
        return familyRepository.findById(id).map(familyMapper::toDto);
    }

    /**
     * Delete the family by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Family : {}", id);
        familyRepository.deleteById(id);
    }
}
