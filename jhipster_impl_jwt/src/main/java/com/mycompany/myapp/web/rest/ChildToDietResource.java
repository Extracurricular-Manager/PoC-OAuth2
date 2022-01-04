package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ChildToDietRepository;
import com.mycompany.myapp.service.ChildToDietService;
import com.mycompany.myapp.service.dto.ChildToDietDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ChildToDiet}.
 */
@RestController
@RequestMapping("/api")
public class ChildToDietResource {

    private final Logger log = LoggerFactory.getLogger(ChildToDietResource.class);

    private static final String ENTITY_NAME = "childToDiet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChildToDietService childToDietService;

    private final ChildToDietRepository childToDietRepository;

    public ChildToDietResource(ChildToDietService childToDietService, ChildToDietRepository childToDietRepository) {
        this.childToDietService = childToDietService;
        this.childToDietRepository = childToDietRepository;
    }

    /**
     * {@code POST  /child-to-diets} : Create a new childToDiet.
     *
     * @param childToDietDTO the childToDietDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new childToDietDTO, or with status {@code 400 (Bad Request)} if the childToDiet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/child-to-diets")
    public Mono<ResponseEntity<ChildToDietDTO>> createChildToDiet(@RequestBody ChildToDietDTO childToDietDTO) throws URISyntaxException {
        log.debug("REST request to save ChildToDiet : {}", childToDietDTO);
        if (childToDietDTO.getId() != null) {
            throw new BadRequestAlertException("A new childToDiet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return childToDietService
            .save(childToDietDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/child-to-diets/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /child-to-diets/:id} : Updates an existing childToDiet.
     *
     * @param id the id of the childToDietDTO to save.
     * @param childToDietDTO the childToDietDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated childToDietDTO,
     * or with status {@code 400 (Bad Request)} if the childToDietDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the childToDietDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/child-to-diets/{id}")
    public Mono<ResponseEntity<ChildToDietDTO>> updateChildToDiet(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChildToDietDTO childToDietDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ChildToDiet : {}, {}", id, childToDietDTO);
        if (childToDietDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, childToDietDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return childToDietRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return childToDietService
                    .save(childToDietDTO)
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
     * {@code PATCH  /child-to-diets/:id} : Partial updates given fields of an existing childToDiet, field will ignore if it is null
     *
     * @param id the id of the childToDietDTO to save.
     * @param childToDietDTO the childToDietDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated childToDietDTO,
     * or with status {@code 400 (Bad Request)} if the childToDietDTO is not valid,
     * or with status {@code 404 (Not Found)} if the childToDietDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the childToDietDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/child-to-diets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ChildToDietDTO>> partialUpdateChildToDiet(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChildToDietDTO childToDietDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ChildToDiet partially : {}, {}", id, childToDietDTO);
        if (childToDietDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, childToDietDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return childToDietRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ChildToDietDTO> result = childToDietService.partialUpdate(childToDietDTO);

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
     * {@code GET  /child-to-diets} : get all the childToDiets.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of childToDiets in body.
     */
    @GetMapping("/child-to-diets")
    public Mono<List<ChildToDietDTO>> getAllChildToDiets(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all ChildToDiets");
        return childToDietService.findAll().collectList();
    }

    /**
     * {@code GET  /child-to-diets} : get all the childToDiets as a stream.
     * @return the {@link Flux} of childToDiets.
     */
    @GetMapping(value = "/child-to-diets", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChildToDietDTO> getAllChildToDietsAsStream() {
        log.debug("REST request to get all ChildToDiets as a stream");
        return childToDietService.findAll();
    }

    /**
     * {@code GET  /child-to-diets/:id} : get the "id" childToDiet.
     *
     * @param id the id of the childToDietDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the childToDietDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/child-to-diets/{id}")
    public Mono<ResponseEntity<ChildToDietDTO>> getChildToDiet(@PathVariable Long id) {
        log.debug("REST request to get ChildToDiet : {}", id);
        Mono<ChildToDietDTO> childToDietDTO = childToDietService.findOne(id);
        return ResponseUtil.wrapOrNotFound(childToDietDTO);
    }

    /**
     * {@code DELETE  /child-to-diets/:id} : delete the "id" childToDiet.
     *
     * @param id the id of the childToDietDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/child-to-diets/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteChildToDiet(@PathVariable Long id) {
        log.debug("REST request to delete ChildToDiet : {}", id);
        return childToDietService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
