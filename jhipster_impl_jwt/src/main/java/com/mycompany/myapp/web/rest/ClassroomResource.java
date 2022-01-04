package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ClassroomRepository;
import com.mycompany.myapp.service.ClassroomService;
import com.mycompany.myapp.service.dto.ClassroomDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Classroom}.
 */
@RestController
@RequestMapping("/api")
public class ClassroomResource {

    private final Logger log = LoggerFactory.getLogger(ClassroomResource.class);

    private static final String ENTITY_NAME = "classroom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClassroomService classroomService;

    private final ClassroomRepository classroomRepository;

    public ClassroomResource(ClassroomService classroomService, ClassroomRepository classroomRepository) {
        this.classroomService = classroomService;
        this.classroomRepository = classroomRepository;
    }

    /**
     * {@code POST  /classrooms} : Create a new classroom.
     *
     * @param classroomDTO the classroomDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classroomDTO, or with status {@code 400 (Bad Request)} if the classroom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/classrooms")
    public Mono<ResponseEntity<ClassroomDTO>> createClassroom(@RequestBody ClassroomDTO classroomDTO) throws URISyntaxException {
        log.debug("REST request to save Classroom : {}", classroomDTO);
        if (classroomDTO.getId() != null) {
            throw new BadRequestAlertException("A new classroom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return classroomService
            .save(classroomDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/classrooms/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /classrooms/:id} : Updates an existing classroom.
     *
     * @param id the id of the classroomDTO to save.
     * @param classroomDTO the classroomDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classroomDTO,
     * or with status {@code 400 (Bad Request)} if the classroomDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classroomDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/classrooms/{id}")
    public Mono<ResponseEntity<ClassroomDTO>> updateClassroom(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClassroomDTO classroomDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Classroom : {}, {}", id, classroomDTO);
        if (classroomDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classroomDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return classroomRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return classroomService
                    .save(classroomDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /classrooms/:id} : Partial updates given fields of an existing classroom, field will ignore if it is null
     *
     * @param id the id of the classroomDTO to save.
     * @param classroomDTO the classroomDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classroomDTO,
     * or with status {@code 400 (Bad Request)} if the classroomDTO is not valid,
     * or with status {@code 404 (Not Found)} if the classroomDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the classroomDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/classrooms/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ClassroomDTO>> partialUpdateClassroom(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClassroomDTO classroomDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Classroom partially : {}, {}", id, classroomDTO);
        if (classroomDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classroomDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return classroomRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ClassroomDTO> result = classroomService.partialUpdate(classroomDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /classrooms} : get all the classrooms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of classrooms in body.
     */
    @GetMapping("/classrooms")
    public Mono<List<ClassroomDTO>> getAllClassrooms() {
        log.debug("REST request to get all Classrooms");
        return classroomService.findAll().collectList();
    }

    /**
     * {@code GET  /classrooms} : get all the classrooms as a stream.
     * @return the {@link Flux} of classrooms.
     */
    @GetMapping(value = "/classrooms", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ClassroomDTO> getAllClassroomsAsStream() {
        log.debug("REST request to get all Classrooms as a stream");
        return classroomService.findAll();
    }

    /**
     * {@code GET  /classrooms/:id} : get the "id" classroom.
     *
     * @param id the id of the classroomDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classroomDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/classrooms/{id}")
    public Mono<ResponseEntity<ClassroomDTO>> getClassroom(@PathVariable Long id) {
        log.debug("REST request to get Classroom : {}", id);
        Mono<ClassroomDTO> classroomDTO = classroomService.findOne(id);
        return ResponseUtil.wrapOrNotFound(classroomDTO);
    }

    /**
     * {@code DELETE  /classrooms/:id} : delete the "id" classroom.
     *
     * @param id the id of the classroomDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/classrooms/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteClassroom(@PathVariable Long id) {
        log.debug("REST request to delete Classroom : {}", id);
        return classroomService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
