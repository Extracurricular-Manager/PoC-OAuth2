package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ChildRepository;
import com.mycompany.myapp.service.ChildService;
import com.mycompany.myapp.service.dto.ChildDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Child}.
 */
@RestController
@RequestMapping("/api")
public class ChildResource {

    private final Logger log = LoggerFactory.getLogger(ChildResource.class);

    private static final String ENTITY_NAME = "child";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChildService childService;

    private final ChildRepository childRepository;

    public ChildResource(ChildService childService, ChildRepository childRepository) {
        this.childService = childService;
        this.childRepository = childRepository;
    }

    /**
     * {@code POST  /children} : Create a new child.
     *
     * @param childDTO the childDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new childDTO, or with status {@code 400 (Bad Request)} if the child has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/children")
    public Mono<ResponseEntity<ChildDTO>> createChild(@RequestBody ChildDTO childDTO) throws URISyntaxException {
        log.debug("REST request to save Child : {}", childDTO);
        if (childDTO.getId() != null) {
            throw new BadRequestAlertException("A new child cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return childService
            .save(childDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/children/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /children/:id} : Updates an existing child.
     *
     * @param id the id of the childDTO to save.
     * @param childDTO the childDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated childDTO,
     * or with status {@code 400 (Bad Request)} if the childDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the childDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/children/{id}")
    public Mono<ResponseEntity<ChildDTO>> updateChild(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChildDTO childDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Child : {}, {}", id, childDTO);
        if (childDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, childDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return childRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return childService
                    .save(childDTO)
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
     * {@code PATCH  /children/:id} : Partial updates given fields of an existing child, field will ignore if it is null
     *
     * @param id the id of the childDTO to save.
     * @param childDTO the childDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated childDTO,
     * or with status {@code 400 (Bad Request)} if the childDTO is not valid,
     * or with status {@code 404 (Not Found)} if the childDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the childDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/children/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ChildDTO>> partialUpdateChild(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChildDTO childDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Child partially : {}, {}", id, childDTO);
        if (childDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, childDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return childRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ChildDTO> result = childService.partialUpdate(childDTO);

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
     * {@code GET  /children} : get all the children.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of children in body.
     */
    @GetMapping("/children")
    public Mono<List<ChildDTO>> getAllChildren() {
        log.debug("REST request to get all Children");
        return childService.findAll().collectList();
    }

    /**
     * {@code GET  /children} : get all the children as a stream.
     * @return the {@link Flux} of children.
     */
    @GetMapping(value = "/children", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChildDTO> getAllChildrenAsStream() {
        log.debug("REST request to get all Children as a stream");
        return childService.findAll();
    }

    /**
     * {@code GET  /children/:id} : get the "id" child.
     *
     * @param id the id of the childDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the childDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/children/{id}")
    public Mono<ResponseEntity<ChildDTO>> getChild(@PathVariable Long id) {
        log.debug("REST request to get Child : {}", id);
        Mono<ChildDTO> childDTO = childService.findOne(id);
        return ResponseUtil.wrapOrNotFound(childDTO);
    }

    /**
     * {@code DELETE  /children/:id} : delete the "id" child.
     *
     * @param id the id of the childDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/children/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteChild(@PathVariable Long id) {
        log.debug("REST request to delete Child : {}", id);
        return childService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
