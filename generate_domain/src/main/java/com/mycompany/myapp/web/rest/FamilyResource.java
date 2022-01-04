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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

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
    public ResponseEntity<FamilyDTO> createFamily(@RequestBody FamilyDTO familyDTO) throws URISyntaxException {
        log.debug("REST request to save Family : {}", familyDTO);
        if (familyDTO.getId() != null) {
            throw new BadRequestAlertException("A new family cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FamilyDTO result = familyService.save(familyDTO);
        return ResponseEntity
            .created(new URI("/api/families/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
    public ResponseEntity<FamilyDTO> updateFamily(
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

        if (!familyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FamilyDTO result = familyService.save(familyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, familyDTO.getId().toString()))
            .body(result);
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
    public ResponseEntity<FamilyDTO> partialUpdateFamily(
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

        if (!familyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FamilyDTO> result = familyService.partialUpdate(familyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, familyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /families} : get all the families.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of families in body.
     */
    @GetMapping("/families")
    public List<FamilyDTO> getAllFamilies() {
        log.debug("REST request to get all Families");
        return familyService.findAll();
    }

    /**
     * {@code GET  /families/:id} : get the "id" family.
     *
     * @param id the id of the familyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the familyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/families/{id}")
    public ResponseEntity<FamilyDTO> getFamily(@PathVariable Long id) {
        log.debug("REST request to get Family : {}", id);
        Optional<FamilyDTO> familyDTO = familyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(familyDTO);
    }

    /**
     * {@code DELETE  /families/:id} : delete the "id" family.
     *
     * @param id the id of the familyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/families/{id}")
    public ResponseEntity<Void> deleteFamily(@PathVariable Long id) {
        log.debug("REST request to delete Family : {}", id);
        familyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
