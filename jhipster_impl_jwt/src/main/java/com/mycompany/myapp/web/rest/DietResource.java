package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.DietRepository;
import com.mycompany.myapp.service.DietService;
import com.mycompany.myapp.service.dto.DietDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Diet}.
 */
@RestController
@RequestMapping("/api")
public class DietResource {

    private final Logger log = LoggerFactory.getLogger(DietResource.class);

    private static final String ENTITY_NAME = "diet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DietService dietService;

    private final DietRepository dietRepository;

    public DietResource(DietService dietService, DietRepository dietRepository) {
        this.dietService = dietService;
        this.dietRepository = dietRepository;
    }

    /**
     * {@code POST  /diets} : Create a new diet.
     *
     * @param dietDTO the dietDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dietDTO, or with status {@code 400 (Bad Request)} if the diet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/diets")
    public Mono<ResponseEntity<DietDTO>> createDiet(@RequestBody DietDTO dietDTO) throws URISyntaxException {
        log.debug("REST request to save Diet : {}", dietDTO);
        if (dietDTO.getId() != null) {
            throw new BadRequestAlertException("A new diet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return dietService
            .save(dietDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/diets/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /diets/:id} : Updates an existing diet.
     *
     * @param id the id of the dietDTO to save.
     * @param dietDTO the dietDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dietDTO,
     * or with status {@code 400 (Bad Request)} if the dietDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dietDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/diets/{id}")
    public Mono<ResponseEntity<DietDTO>> updateDiet(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DietDTO dietDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Diet : {}, {}", id, dietDTO);
        if (dietDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dietDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return dietRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return dietService
                    .save(dietDTO)
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
     * {@code PATCH  /diets/:id} : Partial updates given fields of an existing diet, field will ignore if it is null
     *
     * @param id the id of the dietDTO to save.
     * @param dietDTO the dietDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dietDTO,
     * or with status {@code 400 (Bad Request)} if the dietDTO is not valid,
     * or with status {@code 404 (Not Found)} if the dietDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the dietDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/diets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DietDTO>> partialUpdateDiet(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DietDTO dietDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Diet partially : {}, {}", id, dietDTO);
        if (dietDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dietDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return dietRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<DietDTO> result = dietService.partialUpdate(dietDTO);

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
     * {@code GET  /diets} : get all the diets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of diets in body.
     */
    @GetMapping("/diets")
    public Mono<List<DietDTO>> getAllDiets() {
        log.debug("REST request to get all Diets");
        return dietService.findAll().collectList();
    }

    /**
     * {@code GET  /diets} : get all the diets as a stream.
     * @return the {@link Flux} of diets.
     */
    @GetMapping(value = "/diets", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<DietDTO> getAllDietsAsStream() {
        log.debug("REST request to get all Diets as a stream");
        return dietService.findAll();
    }

    /**
     * {@code GET  /diets/:id} : get the "id" diet.
     *
     * @param id the id of the dietDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dietDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/diets/{id}")
    public Mono<ResponseEntity<DietDTO>> getDiet(@PathVariable Long id) {
        log.debug("REST request to get Diet : {}", id);
        Mono<DietDTO> dietDTO = dietService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dietDTO);
    }

    /**
     * {@code DELETE  /diets/:id} : delete the "id" diet.
     *
     * @param id the id of the dietDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/diets/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteDiet(@PathVariable Long id) {
        log.debug("REST request to delete Diet : {}", id);
        return dietService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
