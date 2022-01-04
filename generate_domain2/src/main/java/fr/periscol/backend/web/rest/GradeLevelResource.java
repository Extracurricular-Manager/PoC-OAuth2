package fr.periscol.backend.web.rest;

import fr.periscol.backend.repository.GradeLevelRepository;
import fr.periscol.backend.service.GradeLevelService;
import fr.periscol.backend.service.dto.GradeLevelDTO;
import fr.periscol.backend.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link fr.periscol.backend.domain.GradeLevel}.
 */
@RestController
@RequestMapping("/api")
public class GradeLevelResource {

    private final Logger log = LoggerFactory.getLogger(GradeLevelResource.class);

    private static final String ENTITY_NAME = "gradeLevel";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GradeLevelService gradeLevelService;

    private final GradeLevelRepository gradeLevelRepository;

    public GradeLevelResource(GradeLevelService gradeLevelService, GradeLevelRepository gradeLevelRepository) {
        this.gradeLevelService = gradeLevelService;
        this.gradeLevelRepository = gradeLevelRepository;
    }

    /**
     * {@code POST  /grade-levels} : Create a new gradeLevel.
     *
     * @param gradeLevelDTO the gradeLevelDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gradeLevelDTO, or with status {@code 400 (Bad Request)} if the gradeLevel has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/grade-levels")
    public ResponseEntity<GradeLevelDTO> createGradeLevel(@RequestBody GradeLevelDTO gradeLevelDTO) throws URISyntaxException {
        log.debug("REST request to save GradeLevel : {}", gradeLevelDTO);
        if (gradeLevelDTO.getId() != null) {
            throw new BadRequestAlertException("A new gradeLevel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GradeLevelDTO result = gradeLevelService.save(gradeLevelDTO);
        return ResponseEntity
            .created(new URI("/api/grade-levels/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /grade-levels/:id} : Updates an existing gradeLevel.
     *
     * @param id the id of the gradeLevelDTO to save.
     * @param gradeLevelDTO the gradeLevelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gradeLevelDTO,
     * or with status {@code 400 (Bad Request)} if the gradeLevelDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gradeLevelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/grade-levels/{id}")
    public ResponseEntity<GradeLevelDTO> updateGradeLevel(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody GradeLevelDTO gradeLevelDTO
    ) throws URISyntaxException {
        log.debug("REST request to update GradeLevel : {}, {}", id, gradeLevelDTO);
        if (gradeLevelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gradeLevelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gradeLevelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GradeLevelDTO result = gradeLevelService.save(gradeLevelDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, gradeLevelDTO.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /grade-levels/:id} : Partial updates given fields of an existing gradeLevel, field will ignore if it is null
     *
     * @param id the id of the gradeLevelDTO to save.
     * @param gradeLevelDTO the gradeLevelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gradeLevelDTO,
     * or with status {@code 400 (Bad Request)} if the gradeLevelDTO is not valid,
     * or with status {@code 404 (Not Found)} if the gradeLevelDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the gradeLevelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/grade-levels/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GradeLevelDTO> partialUpdateGradeLevel(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody GradeLevelDTO gradeLevelDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update GradeLevel partially : {}, {}", id, gradeLevelDTO);
        if (gradeLevelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gradeLevelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gradeLevelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GradeLevelDTO> result = gradeLevelService.partialUpdate(gradeLevelDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, gradeLevelDTO.getId())
        );
    }

    /**
     * {@code GET  /grade-levels} : get all the gradeLevels.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of gradeLevels in body.
     */
    @GetMapping("/grade-levels")
    public List<GradeLevelDTO> getAllGradeLevels() {
        log.debug("REST request to get all GradeLevels");
        return gradeLevelService.findAll();
    }

    /**
     * {@code GET  /grade-levels/:id} : get the "id" gradeLevel.
     *
     * @param id the id of the gradeLevelDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gradeLevelDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/grade-levels/{id}")
    public ResponseEntity<GradeLevelDTO> getGradeLevel(@PathVariable String id) {
        log.debug("REST request to get GradeLevel : {}", id);
        Optional<GradeLevelDTO> gradeLevelDTO = gradeLevelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gradeLevelDTO);
    }

    /**
     * {@code DELETE  /grade-levels/:id} : delete the "id" gradeLevel.
     *
     * @param id the id of the gradeLevelDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/grade-levels/{id}")
    public ResponseEntity<Void> deleteGradeLevel(@PathVariable String id) {
        log.debug("REST request to delete GradeLevel : {}", id);
        gradeLevelService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
