package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.GradeLevel;
import com.mycompany.myapp.repository.GradeLevelRepository;
import com.mycompany.myapp.service.dto.GradeLevelDTO;
import com.mycompany.myapp.service.mapper.GradeLevelMapper;
import java.util.List;
import java.util.UUID;
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
 * Integration tests for the {@link GradeLevelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restGradeLevelMockMvc;

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

    @BeforeEach
    public void initTest() {
        gradeLevel = createEntity(em);
    }

    @Test
    @Transactional
    void createGradeLevel() throws Exception {
        int databaseSizeBeforeCreate = gradeLevelRepository.findAll().size();
        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);
        restGradeLevelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gradeLevelDTO)))
            .andExpect(status().isCreated());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeCreate + 1);
        GradeLevel testGradeLevel = gradeLevelList.get(gradeLevelList.size() - 1);
    }

    @Test
    @Transactional
    void createGradeLevelWithExistingId() throws Exception {
        // Create the GradeLevel with an existing ID
        gradeLevel.setId("existing_id");
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        int databaseSizeBeforeCreate = gradeLevelRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGradeLevelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gradeLevelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGradeLevels() throws Exception {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.saveAndFlush(gradeLevel);

        // Get all the gradeLevelList
        restGradeLevelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gradeLevel.getId())));
    }

    @Test
    @Transactional
    void getGradeLevel() throws Exception {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.saveAndFlush(gradeLevel);

        // Get the gradeLevel
        restGradeLevelMockMvc
            .perform(get(ENTITY_API_URL_ID, gradeLevel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(gradeLevel.getId()));
    }

    @Test
    @Transactional
    void getNonExistingGradeLevel() throws Exception {
        // Get the gradeLevel
        restGradeLevelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGradeLevel() throws Exception {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.saveAndFlush(gradeLevel);

        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().size();

        // Update the gradeLevel
        GradeLevel updatedGradeLevel = gradeLevelRepository.findById(gradeLevel.getId()).get();
        // Disconnect from session so that the updates on updatedGradeLevel are not directly saved in db
        em.detach(updatedGradeLevel);
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(updatedGradeLevel);

        restGradeLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gradeLevelDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            )
            .andExpect(status().isOk());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
        GradeLevel testGradeLevel = gradeLevelList.get(gradeLevelList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGradeLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gradeLevelDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGradeLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGradeLevelMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gradeLevelDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGradeLevelWithPatch() throws Exception {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.saveAndFlush(gradeLevel);

        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().size();

        // Update the gradeLevel using partial update
        GradeLevel partialUpdatedGradeLevel = new GradeLevel();
        partialUpdatedGradeLevel.setId(gradeLevel.getId());

        restGradeLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGradeLevel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGradeLevel))
            )
            .andExpect(status().isOk());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
        GradeLevel testGradeLevel = gradeLevelList.get(gradeLevelList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateGradeLevelWithPatch() throws Exception {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.saveAndFlush(gradeLevel);

        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().size();

        // Update the gradeLevel using partial update
        GradeLevel partialUpdatedGradeLevel = new GradeLevel();
        partialUpdatedGradeLevel.setId(gradeLevel.getId());

        restGradeLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGradeLevel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGradeLevel))
            )
            .andExpect(status().isOk());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
        GradeLevel testGradeLevel = gradeLevelList.get(gradeLevelList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGradeLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, gradeLevelDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGradeLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGradeLevel() throws Exception {
        int databaseSizeBeforeUpdate = gradeLevelRepository.findAll().size();
        gradeLevel.setId(UUID.randomUUID().toString());

        // Create the GradeLevel
        GradeLevelDTO gradeLevelDTO = gradeLevelMapper.toDto(gradeLevel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGradeLevelMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(gradeLevelDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GradeLevel in the database
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGradeLevel() throws Exception {
        // Initialize the database
        gradeLevel.setId(UUID.randomUUID().toString());
        gradeLevelRepository.saveAndFlush(gradeLevel);

        int databaseSizeBeforeDelete = gradeLevelRepository.findAll().size();

        // Delete the gradeLevel
        restGradeLevelMockMvc
            .perform(delete(ENTITY_API_URL_ID, gradeLevel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GradeLevel> gradeLevelList = gradeLevelRepository.findAll();
        assertThat(gradeLevelList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
