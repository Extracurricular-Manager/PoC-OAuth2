package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Diet;
import com.mycompany.myapp.repository.DietRepository;
import com.mycompany.myapp.service.EntityManager;
import com.mycompany.myapp.service.dto.DietDTO;
import com.mycompany.myapp.service.mapper.DietMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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
 * Integration tests for the {@link DietResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class DietResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/diets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private DietMapper dietMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Diet diet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Diet createEntity(EntityManager em) {
        Diet diet = new Diet().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return diet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Diet createUpdatedEntity(EntityManager em) {
        Diet diet = new Diet().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return diet;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Diet.class).block();
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
        diet = createEntity(em);
    }

    @Test
    void createDiet() throws Exception {
        int databaseSizeBeforeCreate = dietRepository.findAll().collectList().block().size();
        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dietDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeCreate + 1);
        Diet testDiet = dietList.get(dietList.size() - 1);
        assertThat(testDiet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDiet.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createDietWithExistingId() throws Exception {
        // Create the Diet with an existing ID
        diet.setId(1L);
        DietDTO dietDTO = dietMapper.toDto(diet);

        int databaseSizeBeforeCreate = dietRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dietDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDietsAsStream() {
        // Initialize the database
        dietRepository.save(diet).block();

        List<Diet> dietList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(DietDTO.class)
            .getResponseBody()
            .map(dietMapper::toEntity)
            .filter(diet::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(dietList).isNotNull();
        assertThat(dietList).hasSize(1);
        Diet testDiet = dietList.get(0);
        assertThat(testDiet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDiet.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllDiets() {
        // Initialize the database
        dietRepository.save(diet).block();

        // Get all the dietList
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
            .value(hasItem(diet.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getDiet() {
        // Initialize the database
        dietRepository.save(diet).block();

        // Get the diet
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, diet.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(diet.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingDiet() {
        // Get the diet
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewDiet() throws Exception {
        // Initialize the database
        dietRepository.save(diet).block();

        int databaseSizeBeforeUpdate = dietRepository.findAll().collectList().block().size();

        // Update the diet
        Diet updatedDiet = dietRepository.findById(diet.getId()).block();
        updatedDiet.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        DietDTO dietDTO = dietMapper.toDto(updatedDiet);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, dietDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dietDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
        Diet testDiet = dietList.get(dietList.size() - 1);
        assertThat(testDiet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDiet.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().collectList().block().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, dietDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dietDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().collectList().block().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dietDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().collectList().block().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(dietDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDietWithPatch() throws Exception {
        // Initialize the database
        dietRepository.save(diet).block();

        int databaseSizeBeforeUpdate = dietRepository.findAll().collectList().block().size();

        // Update the diet using partial update
        Diet partialUpdatedDiet = new Diet();
        partialUpdatedDiet.setId(diet.getId());

        partialUpdatedDiet.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDiet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDiet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
        Diet testDiet = dietList.get(dietList.size() - 1);
        assertThat(testDiet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDiet.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateDietWithPatch() throws Exception {
        // Initialize the database
        dietRepository.save(diet).block();

        int databaseSizeBeforeUpdate = dietRepository.findAll().collectList().block().size();

        // Update the diet using partial update
        Diet partialUpdatedDiet = new Diet();
        partialUpdatedDiet.setId(diet.getId());

        partialUpdatedDiet.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDiet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDiet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
        Diet testDiet = dietList.get(dietList.size() - 1);
        assertThat(testDiet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDiet.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().collectList().block().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, dietDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(dietDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().collectList().block().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(dietDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().collectList().block().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(dietDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDiet() {
        // Initialize the database
        dietRepository.save(diet).block();

        int databaseSizeBeforeDelete = dietRepository.findAll().collectList().block().size();

        // Delete the diet
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, diet.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Diet> dietList = dietRepository.findAll().collectList().block();
        assertThat(dietList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
