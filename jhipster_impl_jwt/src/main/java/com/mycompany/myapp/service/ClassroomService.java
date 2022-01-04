package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Classroom;
import com.mycompany.myapp.repository.ClassroomRepository;
import com.mycompany.myapp.service.dto.ClassroomDTO;
import com.mycompany.myapp.service.mapper.ClassroomMapper;
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
 * Service Implementation for managing {@link Classroom}.
 */
@Service
@Transactional
public class ClassroomService {

    private final Logger log = LoggerFactory.getLogger(ClassroomService.class);

    private final ClassroomRepository classroomRepository;

    private final ClassroomMapper classroomMapper;

    public ClassroomService(ClassroomRepository classroomRepository, ClassroomMapper classroomMapper) {
        this.classroomRepository = classroomRepository;
        this.classroomMapper = classroomMapper;
    }

    /**
     * Save a classroom.
     *
     * @param classroomDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ClassroomDTO> save(ClassroomDTO classroomDTO) {
        log.debug("Request to save Classroom : {}", classroomDTO);
        return classroomRepository.save(classroomMapper.toEntity(classroomDTO)).map(classroomMapper::toDto);
    }

    /**
     * Partially update a classroom.
     *
     * @param classroomDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ClassroomDTO> partialUpdate(ClassroomDTO classroomDTO) {
        log.debug("Request to partially update Classroom : {}", classroomDTO);

        return classroomRepository
            .findById(classroomDTO.getId())
            .map(existingClassroom -> {
                classroomMapper.partialUpdate(existingClassroom, classroomDTO);

                return existingClassroom;
            })
            .flatMap(classroomRepository::save)
            .map(classroomMapper::toDto);
    }

    /**
     * Get all the classrooms.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ClassroomDTO> findAll() {
        log.debug("Request to get all Classrooms");
        return classroomRepository.findAll().map(classroomMapper::toDto);
    }

    /**
     * Returns the number of classrooms available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return classroomRepository.count();
    }

    /**
     * Get one classroom by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ClassroomDTO> findOne(Long id) {
        log.debug("Request to get Classroom : {}", id);
        return classroomRepository.findById(id).map(classroomMapper::toDto);
    }

    /**
     * Delete the classroom by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Classroom : {}", id);
        return classroomRepository.deleteById(id);
    }
}
