package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.FamilyRepository;
import com.mycompany.myapp.service.FamilyService;
import com.mycompany.myapp.service.dto.FamilyDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Family}.
 */
@RestController
@RequestMapping("/api")
public class FamilyResource {

    private final Logger log = LoggerFactory.getLogger(FamilyResource.class);

    private static final String ENTITY_NAME = "family";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FamilyService familyService;

    private final FamilyRepository familyRepository;

    public FamilyResource(FamilyService familyService, FamilyRepository familyRepository) {
        this.familyService = familyService;
        this.familyRepository = familyRepository;
    }

    /**
     * {@code POST  /families} : Create a new family.
     *
     * @param familyDTO the familyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new familyDTO, or with status {@code 400 (Bad Request)} if the family has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/families")
    public Mono<ResponseEntity<FamilyDTO>> createFamily(@RequestBody FamilyDTO familyDTO) throws URISyntaxException {
        log.debug("REST request to save Family : {}", familyDTO);
        if (familyDTO.getId() != null) {
            throw new BadRequestAlertException("A new family cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return familyService
            .save(familyDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/families/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /families/:id} : Updates an existing family.
     *
     * @param id the id of the familyDTO to save.
     * @param familyDTO the familyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated familyDTO,
     * or with status {@code 400 (Bad Request)} if the familyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the familyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/families/{id}")
    public Mono<ResponseEntity<FamilyDTO>> updateFamily(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FamilyDTO familyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Family : {}, {}", id, familyDTO);
        if (familyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, familyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return familyRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return familyService
                    .save(familyDTO)
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
     * {@code PATCH  /families/:id} : Partial updates given fields of an existing family, field will ignore if it is null
     *
     * @param id the id of the familyDTO to save.
     * @param familyDTO the familyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated familyDTO,
     * or with status {@code 400 (Bad Request)} if the familyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the familyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the familyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/families/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<FamilyDTO>> partialUpdateFamily(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FamilyDTO familyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Family partially : {}, {}", id, familyDTO);
        if (familyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, familyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return familyRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<FamilyDTO> result = familyService.partialUpdate(familyDTO);

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
     * {@code GET  /families} : get all the families.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of families in body.
     */
    @GetMapping("/families")
    public Mono<List<FamilyDTO>> getAllFamilies() {
        log.debug("REST request to get all Families");
        return familyService.findAll().collectList();
    }

    /**
     * {@code GET  /families} : get all the families as a stream.
     * @return the {@link Flux} of families.
     */
    @GetMapping(value = "/families", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<FamilyDTO> getAllFamiliesAsStream() {
        log.debug("REST request to get all Families as a stream");
        return familyService.findAll();
    }

    /**
     * {@code GET  /families/:id} : get the "id" family.
     *
     * @param id the id of the familyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the familyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/families/{id}")
    public Mono<ResponseEntity<FamilyDTO>> getFamily(@PathVariable Long id) {
        log.debug("REST request to get Family : {}", id);
        Mono<FamilyDTO> familyDTO = familyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(familyDTO);
    }

    /**
     * {@code DELETE  /families/:id} : delete the "id" family.
     *
     * @param id the id of the familyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/families/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteFamily(@PathVariable Long id) {
        log.debug("REST request to delete Family : {}", id);
        return familyService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
