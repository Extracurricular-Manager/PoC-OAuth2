package fr.periscol.backend.service;

import fr.periscol.backend.domain.GradeLevel;
import fr.periscol.backend.repository.GradeLevelRepository;
import fr.periscol.backend.service.dto.GradeLevelDTO;
import fr.periscol.backend.service.mapper.GradeLevelMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public GradeLevelDTO save(GradeLevelDTO gradeLevelDTO) {
        log.debug("Request to save GradeLevel : {}", gradeLevelDTO);
        GradeLevel gradeLevel = gradeLevelMapper.toEntity(gradeLevelDTO);
        gradeLevel = gradeLevelRepository.save(gradeLevel);
        return gradeLevelMapper.toDto(gradeLevel);
    }

    /**
     * Partially update a gradeLevel.
     *
     * @param gradeLevelDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GradeLevelDTO> partialUpdate(GradeLevelDTO gradeLevelDTO) {
        log.debug("Request to partially update GradeLevel : {}", gradeLevelDTO);

        return gradeLevelRepository
            .findById(gradeLevelDTO.getId())
            .map(existingGradeLevel -> {
                gradeLevelMapper.partialUpdate(existingGradeLevel, gradeLevelDTO);

                return existingGradeLevel;
            })
            .map(gradeLevelRepository::save)
            .map(gradeLevelMapper::toDto);
    }

    /**
     * Get all the gradeLevels.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GradeLevelDTO> findAll() {
        log.debug("Request to get all GradeLevels");
        return gradeLevelRepository.findAll().stream().map(gradeLevelMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one gradeLevel by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GradeLevelDTO> findOne(String id) {
        log.debug("Request to get GradeLevel : {}", id);
        return gradeLevelRepository.findById(id).map(gradeLevelMapper::toDto);
    }

    /**
     * Delete the gradeLevel by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete GradeLevel : {}", id);
        gradeLevelRepository.deleteById(id);
    }
}
