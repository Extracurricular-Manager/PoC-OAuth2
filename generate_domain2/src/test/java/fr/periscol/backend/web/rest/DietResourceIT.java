package fr.periscol.backend.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.periscol.backend.IntegrationTest;
import fr.periscol.backend.domain.Diet;
import fr.periscol.backend.repository.DietRepository;
import fr.periscol.backend.service.dto.DietDTO;
import fr.periscol.backend.service.mapper.DietMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DietResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restDietMockMvc;

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

    @BeforeEach
    public void initTest() {
        diet = createEntity(em);
    }

    @Test
    @Transactional
    void createDiet() throws Exception {
        int databaseSizeBeforeCreate = dietRepository.findAll().size();
        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);
        restDietMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dietDTO)))
            .andExpect(status().isCreated());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeCreate + 1);
        Diet testDiet = dietList.get(dietList.size() - 1);
        assertThat(testDiet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDiet.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createDietWithExistingId() throws Exception {
        // Create the Diet with an existing ID
        diet.setId(1L);
        DietDTO dietDTO = dietMapper.toDto(diet);

        int databaseSizeBeforeCreate = dietRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDietMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dietDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDiets() throws Exception {
        // Initialize the database
        dietRepository.saveAndFlush(diet);

        // Get all the dietList
        restDietMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getDiet() throws Exception {
        // Initialize the database
        dietRepository.saveAndFlush(diet);

        // Get the diet
        restDietMockMvc
            .perform(get(ENTITY_API_URL_ID, diet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(diet.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingDiet() throws Exception {
        // Get the diet
        restDietMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDiet() throws Exception {
        // Initialize the database
        dietRepository.saveAndFlush(diet);

        int databaseSizeBeforeUpdate = dietRepository.findAll().size();

        // Update the diet
        Diet updatedDiet = dietRepository.findById(diet.getId()).get();
        // Disconnect from session so that the updates on updatedDiet are not directly saved in db
        em.detach(updatedDiet);
        updatedDiet.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        DietDTO dietDTO = dietMapper.toDto(updatedDiet);

        restDietMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dietDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dietDTO))
            )
            .andExpect(status().isOk());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
        Diet testDiet = dietList.get(dietList.size() - 1);
        assertThat(testDiet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDiet.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDietMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dietDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dietDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDietMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dietDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDietMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dietDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDietWithPatch() throws Exception {
        // Initialize the database
        dietRepository.saveAndFlush(diet);

        int databaseSizeBeforeUpdate = dietRepository.findAll().size();

        // Update the diet using partial update
        Diet partialUpdatedDiet = new Diet();
        partialUpdatedDiet.setId(diet.getId());

        partialUpdatedDiet.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restDietMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDiet))
            )
            .andExpect(status().isOk());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
        Diet testDiet = dietList.get(dietList.size() - 1);
        assertThat(testDiet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDiet.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateDietWithPatch() throws Exception {
        // Initialize the database
        dietRepository.saveAndFlush(diet);

        int databaseSizeBeforeUpdate = dietRepository.findAll().size();

        // Update the diet using partial update
        Diet partialUpdatedDiet = new Diet();
        partialUpdatedDiet.setId(diet.getId());

        partialUpdatedDiet.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restDietMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDiet))
            )
            .andExpect(status().isOk());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
        Diet testDiet = dietList.get(dietList.size() - 1);
        assertThat(testDiet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDiet.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDietMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dietDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dietDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDietMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dietDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDiet() throws Exception {
        int databaseSizeBeforeUpdate = dietRepository.findAll().size();
        diet.setId(count.incrementAndGet());

        // Create the Diet
        DietDTO dietDTO = dietMapper.toDto(diet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDietMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dietDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Diet in the database
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDiet() throws Exception {
        // Initialize the database
        dietRepository.saveAndFlush(diet);

        int databaseSizeBeforeDelete = dietRepository.findAll().size();

        // Delete the diet
        restDietMockMvc
            .perform(delete(ENTITY_API_URL_ID, diet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Diet> dietList = dietRepository.findAll();
        assertThat(dietList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
