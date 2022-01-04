package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.GradeLevel;
import com.mycompany.myapp.repository.GradeLevelRepository;
import com.mycompany.myapp.service.EntityManager;
import com.mycompany.myapp.service.dto.GradeLevelDTO;
import com.mycompany.myapp.service.mapper.GradeLevelMapper;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link GradeLevelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class GradeLevelResourceIT {

    private static final String ENTITY_API_URL = "/api/grade-levels";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private GradeLevelRepository gradeLevelRepository;

    @Autowired
    private GradeLevelMapper gradeLevelMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private GradeLevel gradeLevel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GradeLevel createEntity(EntityManager em) {
        GradeLevel gradeLevel = new GradeLevel();
        return gradeLevel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GradeLevel createUpdatedEntity(EntityManager em) {
        GradeLevel gradeLevel = new GradeLevel();
        return gradeLevel;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(GradeLevel.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        gradeLevel = createEntity(em);
    }

    @Test
    void createGradeLevel() throws Exception {
        int databaseSizeBeforeCreate = gradeLevelRepository.findAll().collectList().block().size();
        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeCreate + 1);
        GradeLevel testGradeLevel = gradeLevelList.get(gradeLevelList.size() - 1);
    }

    @Test
    void createGradeLevelWithExistingId() throws Exception {
        // Create the GradeLevel with an existing ID
        gradeLevel.setId("existing_id");
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        int databaseSizeBeforeCreate = gradeLevelRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllGradeLevelsAsStream() {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.save(gradeLevel).block();

        List<GradeLevel> gradeLevelList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(GradeLevelDTO.class)
            .getResponseBody()
            .map(gradeLevelMapper::toEntity)
            .filter(gradeLevel::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(gradeLevelList).isNotNull();
        assertThat(gradeLevelList).hasSize(1);
        GradeLevel testGradeLevel = gradeLevelList.get(0);
    }

    @Test
    void getAllGradeLevels() {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.save(gradeLevel).block();

        // Get all the gradeLevelList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(gradeLevel.getId()));
    }

    @Test
    void getGradeLevel() {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.save(gradeLevel).block();

        // Get the gradeLevel
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, gradeLevel.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(gradeLevel.getId()));
    }

    @Test
    void getNonExistingGradeLevel() {
        // Get the gradeLevel
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewGradeLevel() throws Exception {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.save(gradeLevel).block();

        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().collectList().block().size();

        // Update the gradeLevel
        GradeLevel updatedGradeLevel = gradeLevelRepository.findById(gradeLevel.getId()).block();
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(updatedGradeLevel);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, gradeLevelDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
        GradeLevel testGradeLevel = gradeLevelList.get(gradeLevelList.size() - 1);
    }

    @Test
    void putNonExistingGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().collectList().block().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, gradeLevelDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().collectList().block().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().collectList().block().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateGradeLevelWithPatch() throws Exception {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.save(gradeLevel).block();

        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().collectList().block().size();

        // Update the gradeLevel using partial update
        GradeLevel partialUpdatedGradeLevel = new GradeLevel();
        partialUpdatedGradeLevel.setId(gradeLevel.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGradeLevel.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedGradeLevel))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
        GradeLevel testGradeLevel = gradeLevelList.get(gradeLevelList.size() - 1);
    }

    @Test
    void fullUpdateGradeLevelWithPatch() throws Exception {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.save(gradeLevel).block();

        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().collectList().block().size();

        // Update the gradeLevel using partial update
        GradeLevel partialUpdatedGradeLevel = new GradeLevel();
        partialUpdatedGradeLevel.setId(gradeLevel.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGradeLevel.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedGradeLevel))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
        GradeLevel testGradeLevel = gradeLevelList.get(gradeLevelList.size() - 1);
    }

    @Test
    void patchNonExistingGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().collectList().block().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, gradeLevelDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().collectList().block().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().collectList().block().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteGradeLevel() {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.save(gradeLevel).block();

        int databaseSizeBeforeDelete = gradeLevelRepository.findAll().collectList().block().size();

        // Delete the gradeLevel
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, gradeLevel.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll().collectList().block();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
