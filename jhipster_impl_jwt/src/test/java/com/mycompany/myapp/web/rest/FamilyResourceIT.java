package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Family;
import com.mycompany.myapp.repository.FamilyRepository;
import com.mycompany.myapp.service.EntityManager;
import com.mycompany.myapp.service.dto.FamilyDTO;
import com.mycompany.myapp.service.mapper.FamilyMapper;
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
 * Integration tests for the {@link FamilyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class FamilyResourceIT {

    private static final String DEFAULT_REFERING_PARENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_REFERING_PARENT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REFERING_PARENT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_REFERING_PARENT_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_ADRESS = "AAAAAAAAAA";
    private static final String UPDATED_POSTAL_ADRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/families";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMapper familyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Family family;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Family createEntity(EntityManager em) {
        Family family = new Family()
            .referingParentName(DEFAULT_REFERING_PARENT_NAME)
            .referingParentSurname(DEFAULT_REFERING_PARENT_SURNAME)
            .telephoneNumber(DEFAULT_TELEPHONE_NUMBER)
            .postalAdress(DEFAULT_POSTAL_ADRESS);
        return family;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Family createUpdatedEntity(EntityManager em) {
        Family family = new Family()
            .referingParentName(UPDATED_REFERING_PARENT_NAME)
            .referingParentSurname(UPDATED_REFERING_PARENT_SURNAME)
            .telephoneNumber(UPDATED_TELEPHONE_NUMBER)
            .postalAdress(UPDATED_POSTAL_ADRESS);
        return family;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Family.class).block();
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
        family = createEntity(em);
    }

    @Test
    void createFamily() throws Exception {
        int databaseSizeBeforeCreate = familyRepository.findAll().collectList().block().size();
        // Create the Family
        FamilyDTO familyDTO = familyMapper.toDto(family);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(familyDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeCreate + 1);
        Family testFamily = familyList.get(familyList.size() - 1);
        assertThat(testFamily.getReferingParentName()).isEqualTo(DEFAULT_REFERING_PARENT_NAME);
        assertThat(testFamily.getReferingParentSurname()).isEqualTo(DEFAULT_REFERING_PARENT_SURNAME);
        assertThat(testFamily.getTelephoneNumber()).isEqualTo(DEFAULT_TELEPHONE_NUMBER);
        assertThat(testFamily.getPostalAdress()).isEqualTo(DEFAULT_POSTAL_ADRESS);
    }

    @Test
    void createFamilyWithExistingId() throws Exception {
        // Create the Family with an existing ID
        family.setId(1L);
        FamilyDTO familyDTO = familyMapper.toDto(family);

        int databaseSizeBeforeCreate = familyRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(familyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllFamiliesAsStream() {
        // Initialize the database
        familyRepository.save(family).block();

        List<Family> familyList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(FamilyDTO.class)
            .getResponseBody()
            .map(familyMapper::toEntity)
            .filter(family::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(familyList).isNotNull();
        assertThat(familyList).hasSize(1);
        Family testFamily = familyList.get(0);
        assertThat(testFamily.getReferingParentName()).isEqualTo(DEFAULT_REFERING_PARENT_NAME);
        assertThat(testFamily.getReferingParentSurname()).isEqualTo(DEFAULT_REFERING_PARENT_SURNAME);
        assertThat(testFamily.getTelephoneNumber()).isEqualTo(DEFAULT_TELEPHONE_NUMBER);
        assertThat(testFamily.getPostalAdress()).isEqualTo(DEFAULT_POSTAL_ADRESS);
    }

    @Test
    void getAllFamilies() {
        // Initialize the database
        familyRepository.save(family).block();

        // Get all the familyList
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
            .value(hasItem(family.getId().intValue()))
            .jsonPath("$.[*].referingParentName")
            .value(hasItem(DEFAULT_REFERING_PARENT_NAME))
            .jsonPath("$.[*].referingParentSurname")
            .value(hasItem(DEFAULT_REFERING_PARENT_SURNAME))
            .jsonPath("$.[*].telephoneNumber")
            .value(hasItem(DEFAULT_TELEPHONE_NUMBER))
            .jsonPath("$.[*].postalAdress")
            .value(hasItem(DEFAULT_POSTAL_ADRESS));
    }

    @Test
    void getFamily() {
        // Initialize the database
        familyRepository.save(family).block();

        // Get the family
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, family.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(family.getId().intValue()))
            .jsonPath("$.referingParentName")
            .value(is(DEFAULT_REFERING_PARENT_NAME))
            .jsonPath("$.referingParentSurname")
            .value(is(DEFAULT_REFERING_PARENT_SURNAME))
            .jsonPath("$.telephoneNumber")
            .value(is(DEFAULT_TELEPHONE_NUMBER))
            .jsonPath("$.postalAdress")
            .value(is(DEFAULT_POSTAL_ADRESS));
    }

    @Test
    void getNonExistingFamily() {
        // Get the family
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFamily() throws Exception {
        // Initialize the database
        familyRepository.save(family).block();

        int databaseSizeBeforeUpdate = familyRepository.findAll().collectList().block().size();

        // Update the family
        Family updatedFamily = familyRepository.findById(family.getId()).block();
        updatedFamily
            .referingParentName(UPDATED_REFERING_PARENT_NAME)
            .referingParentSurname(UPDATED_REFERING_PARENT_SURNAME)
            .telephoneNumber(UPDATED_TELEPHONE_NUMBER)
            .postalAdress(UPDATED_POSTAL_ADRESS);
        FamilyDTO familyDTO = familyMapper.toDto(updatedFamily);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, familyDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(familyDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeUpdate);
        Family testFamily = familyList.get(familyList.size() - 1);
        assertThat(testFamily.getReferingParentName()).isEqualTo(UPDATED_REFERING_PARENT_NAME);
        assertThat(testFamily.getReferingParentSurname()).isEqualTo(UPDATED_REFERING_PARENT_SURNAME);
        assertThat(testFamily.getTelephoneNumber()).isEqualTo(UPDATED_TELEPHONE_NUMBER);
        assertThat(testFamily.getPostalAdress()).isEqualTo(UPDATED_POSTAL_ADRESS);
    }

    @Test
    void putNonExistingFamily() throws Exception {
        int databaseSizeBeforeUpdate = familyRepository.findAll().collectList().block().size();
        family.setId(count.incrementAndGet());

        // Create the Family
        FamilyDTO familyDTO = familyMapper.toDto(family);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, familyDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(familyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFamily() throws Exception {
        int databaseSizeBeforeUpdate = familyRepository.findAll().collectList().block().size();
        family.setId(count.incrementAndGet());

        // Create the Family
        FamilyDTO familyDTO = familyMapper.toDto(family);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(familyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFamily() throws Exception {
        int databaseSizeBeforeUpdate = familyRepository.findAll().collectList().block().size();
        family.setId(count.incrementAndGet());

        // Create the Family
        FamilyDTO familyDTO = familyMapper.toDto(family);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(familyDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFamilyWithPatch() throws Exception {
        // Initialize the database
        familyRepository.save(family).block();

        int databaseSizeBeforeUpdate = familyRepository.findAll().collectList().block().size();

        // Update the family using partial update
        Family partialUpdatedFamily = new Family();
        partialUpdatedFamily.setId(family.getId());

        partialUpdatedFamily.referingParentSurname(UPDATED_REFERING_PARENT_SURNAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFamily.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFamily))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeUpdate);
        Family testFamily = familyList.get(familyList.size() - 1);
        assertThat(testFamily.getReferingParentName()).isEqualTo(DEFAULT_REFERING_PARENT_NAME);
        assertThat(testFamily.getReferingParentSurname()).isEqualTo(UPDATED_REFERING_PARENT_SURNAME);
        assertThat(testFamily.getTelephoneNumber()).isEqualTo(DEFAULT_TELEPHONE_NUMBER);
        assertThat(testFamily.getPostalAdress()).isEqualTo(DEFAULT_POSTAL_ADRESS);
    }

    @Test
    void fullUpdateFamilyWithPatch() throws Exception {
        // Initialize the database
        familyRepository.save(family).block();

        int databaseSizeBeforeUpdate = familyRepository.findAll().collectList().block().size();

        // Update the family using partial update
        Family partialUpdatedFamily = new Family();
        partialUpdatedFamily.setId(family.getId());

        partialUpdatedFamily
            .referingParentName(UPDATED_REFERING_PARENT_NAME)
            .referingParentSurname(UPDATED_REFERING_PARENT_SURNAME)
            .telephoneNumber(UPDATED_TELEPHONE_NUMBER)
            .postalAdress(UPDATED_POSTAL_ADRESS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFamily.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFamily))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeUpdate);
        Family testFamily = familyList.get(familyList.size() - 1);
        assertThat(testFamily.getReferingParentName()).isEqualTo(UPDATED_REFERING_PARENT_NAME);
        assertThat(testFamily.getReferingParentSurname()).isEqualTo(UPDATED_REFERING_PARENT_SURNAME);
        assertThat(testFamily.getTelephoneNumber()).isEqualTo(UPDATED_TELEPHONE_NUMBER);
        assertThat(testFamily.getPostalAdress()).isEqualTo(UPDATED_POSTAL_ADRESS);
    }

    @Test
    void patchNonExistingFamily() throws Exception {
        int databaseSizeBeforeUpdate = familyRepository.findAll().collectList().block().size();
        family.setId(count.incrementAndGet());

        // Create the Family
        FamilyDTO familyDTO = familyMapper.toDto(family);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, familyDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(familyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFamily() throws Exception {
        int databaseSizeBeforeUpdate = familyRepository.findAll().collectList().block().size();
        family.setId(count.incrementAndGet());

        // Create the Family
        FamilyDTO familyDTO = familyMapper.toDto(family);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(familyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFamily() throws Exception {
        int databaseSizeBeforeUpdate = familyRepository.findAll().collectList().block().size();
        family.setId(count.incrementAndGet());

        // Create the Family
        FamilyDTO familyDTO = familyMapper.toDto(family);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(familyDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Family in the database
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFamily() {
        // Initialize the database
        familyRepository.save(family).block();

        int databaseSizeBeforeDelete = familyRepository.findAll().collectList().block().size();

        // Delete the family
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, family.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Family> familyList = familyRepository.findAll().collectList().block();
        assertThat(familyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
