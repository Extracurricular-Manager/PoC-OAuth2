package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Child;
import com.mycompany.myapp.repository.ChildRepository;
import com.mycompany.myapp.service.EntityManager;
import com.mycompany.myapp.service.dto.ChildDTO;
import com.mycompany.myapp.service.mapper.ChildMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link ChildResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class ChildResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_BIRTHDAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_BIRTHDAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_GRADE_LEVEL = "AAAAAAAAAA";
    private static final String UPDATED_GRADE_LEVEL = "BBBBBBBBBB";

    private static final Long DEFAULT_CLASSROOM = 1L;
    private static final Long UPDATED_CLASSROOM = 2L;

    private static final Long DEFAULT_ADELPHIE = 1L;
    private static final Long UPDATED_ADELPHIE = 2L;

    private static final String ENTITY_API_URL = "/api/children";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ChildMapper childMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Child child;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Child createEntity(EntityManager em) {
        Child child = new Child()
            .name(DEFAULT_NAME)
            .surname(DEFAULT_SURNAME)
            .birthday(DEFAULT_BIRTHDAY)
            .gradeLevel(DEFAULT_GRADE_LEVEL)
            .classroom(DEFAULT_CLASSROOM)
            .adelphie(DEFAULT_ADELPHIE);
        return child;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Child createUpdatedEntity(EntityManager em) {
        Child child = new Child()
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .birthday(UPDATED_BIRTHDAY)
            .gradeLevel(UPDATED_GRADE_LEVEL)
            .classroom(UPDATED_CLASSROOM)
            .adelphie(UPDATED_ADELPHIE);
        return child;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Child.class).block();
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
        child = createEntity(em);
    }

    @Test
    void createChild() throws Exception {
        int databaseSizeBeforeCreate = childRepository.findAll().collectList().block().size();
        // Create the Child
        ChildDTO childDTO = childMapper.toDto(child);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeCreate + 1);
        Child testChild = childList.get(childList.size() - 1);
        assertThat(testChild.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testChild.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testChild.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testChild.getGradeLevel()).isEqualTo(DEFAULT_GRADE_LEVEL);
        assertThat(testChild.getClassroom()).isEqualTo(DEFAULT_CLASSROOM);
        assertThat(testChild.getAdelphie()).isEqualTo(DEFAULT_ADELPHIE);
    }

    @Test
    void createChildWithExistingId() throws Exception {
        // Create the Child with an existing ID
        child.setId(1L);
        ChildDTO childDTO = childMapper.toDto(child);

        int databaseSizeBeforeCreate = childRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllChildrenAsStream() {
        // Initialize the database
        childRepository.save(child).block();

        List<Child> childList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ChildDTO.class)
            .getResponseBody()
            .map(childMapper::toEntity)
            .filter(child::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(childList).isNotNull();
        assertThat(childList).hasSize(1);
        Child testChild = childList.get(0);
        assertThat(testChild.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testChild.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testChild.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testChild.getGradeLevel()).isEqualTo(DEFAULT_GRADE_LEVEL);
        assertThat(testChild.getClassroom()).isEqualTo(DEFAULT_CLASSROOM);
        assertThat(testChild.getAdelphie()).isEqualTo(DEFAULT_ADELPHIE);
    }

    @Test
    void getAllChildren() {
        // Initialize the database
        childRepository.save(child).block();

        // Get all the childList
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
            .value(hasItem(child.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].surname")
            .value(hasItem(DEFAULT_SURNAME))
            .jsonPath("$.[*].birthday")
            .value(hasItem(sameInstant(DEFAULT_BIRTHDAY)))
            .jsonPath("$.[*].gradeLevel")
            .value(hasItem(DEFAULT_GRADE_LEVEL))
            .jsonPath("$.[*].classroom")
            .value(hasItem(DEFAULT_CLASSROOM.intValue()))
            .jsonPath("$.[*].adelphie")
            .value(hasItem(DEFAULT_ADELPHIE.intValue()));
    }

    @Test
    void getChild() {
        // Initialize the database
        childRepository.save(child).block();

        // Get the child
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, child.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(child.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.surname")
            .value(is(DEFAULT_SURNAME))
            .jsonPath("$.birthday")
            .value(is(sameInstant(DEFAULT_BIRTHDAY)))
            .jsonPath("$.gradeLevel")
            .value(is(DEFAULT_GRADE_LEVEL))
            .jsonPath("$.classroom")
            .value(is(DEFAULT_CLASSROOM.intValue()))
            .jsonPath("$.adelphie")
            .value(is(DEFAULT_ADELPHIE.intValue()));
    }

    @Test
    void getNonExistingChild() {
        // Get the child
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewChild() throws Exception {
        // Initialize the database
        childRepository.save(child).block();

        int databaseSizeBeforeUpdate = childRepository.findAll().collectList().block().size();

        // Update the child
        Child updatedChild = childRepository.findById(child.getId()).block();
        updatedChild
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .birthday(UPDATED_BIRTHDAY)
            .gradeLevel(UPDATED_GRADE_LEVEL)
            .classroom(UPDATED_CLASSROOM)
            .adelphie(UPDATED_ADELPHIE);
        ChildDTO childDTO = childMapper.toDto(updatedChild);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, childDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeUpdate);
        Child testChild = childList.get(childList.size() - 1);
        assertThat(testChild.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testChild.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testChild.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testChild.getGradeLevel()).isEqualTo(UPDATED_GRADE_LEVEL);
        assertThat(testChild.getClassroom()).isEqualTo(UPDATED_CLASSROOM);
        assertThat(testChild.getAdelphie()).isEqualTo(UPDATED_ADELPHIE);
    }

    @Test
    void putNonExistingChild() throws Exception {
        int databaseSizeBeforeUpdate = childRepository.findAll().collectList().block().size();
        child.setId(count.incrementAndGet());

        // Create the Child
        ChildDTO childDTO = childMapper.toDto(child);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, childDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchChild() throws Exception {
        int databaseSizeBeforeUpdate = childRepository.findAll().collectList().block().size();
        child.setId(count.incrementAndGet());

        // Create the Child
        ChildDTO childDTO = childMapper.toDto(child);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamChild() throws Exception {
        int databaseSizeBeforeUpdate = childRepository.findAll().collectList().block().size();
        child.setId(count.incrementAndGet());

        // Create the Child
        ChildDTO childDTO = childMapper.toDto(child);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateChildWithPatch() throws Exception {
        // Initialize the database
        childRepository.save(child).block();

        int databaseSizeBeforeUpdate = childRepository.findAll().collectList().block().size();

        // Update the child using partial update
        Child partialUpdatedChild = new Child();
        partialUpdatedChild.setId(child.getId());

        partialUpdatedChild.name(UPDATED_NAME).birthday(UPDATED_BIRTHDAY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChild.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedChild))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeUpdate);
        Child testChild = childList.get(childList.size() - 1);
        assertThat(testChild.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testChild.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testChild.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testChild.getGradeLevel()).isEqualTo(DEFAULT_GRADE_LEVEL);
        assertThat(testChild.getClassroom()).isEqualTo(DEFAULT_CLASSROOM);
        assertThat(testChild.getAdelphie()).isEqualTo(DEFAULT_ADELPHIE);
    }

    @Test
    void fullUpdateChildWithPatch() throws Exception {
        // Initialize the database
        childRepository.save(child).block();

        int databaseSizeBeforeUpdate = childRepository.findAll().collectList().block().size();

        // Update the child using partial update
        Child partialUpdatedChild = new Child();
        partialUpdatedChild.setId(child.getId());

        partialUpdatedChild
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .birthday(UPDATED_BIRTHDAY)
            .gradeLevel(UPDATED_GRADE_LEVEL)
            .classroom(UPDATED_CLASSROOM)
            .adelphie(UPDATED_ADELPHIE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChild.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedChild))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeUpdate);
        Child testChild = childList.get(childList.size() - 1);
        assertThat(testChild.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testChild.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testChild.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testChild.getGradeLevel()).isEqualTo(UPDATED_GRADE_LEVEL);
        assertThat(testChild.getClassroom()).isEqualTo(UPDATED_CLASSROOM);
        assertThat(testChild.getAdelphie()).isEqualTo(UPDATED_ADELPHIE);
    }

    @Test
    void patchNonExistingChild() throws Exception {
        int databaseSizeBeforeUpdate = childRepository.findAll().collectList().block().size();
        child.setId(count.incrementAndGet());

        // Create the Child
        ChildDTO childDTO = childMapper.toDto(child);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, childDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(childDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchChild() throws Exception {
        int databaseSizeBeforeUpdate = childRepository.findAll().collectList().block().size();
        child.setId(count.incrementAndGet());

        // Create the Child
        ChildDTO childDTO = childMapper.toDto(child);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(childDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamChild() throws Exception {
        int databaseSizeBeforeUpdate = childRepository.findAll().collectList().block().size();
        child.setId(count.incrementAndGet());

        // Create the Child
        ChildDTO childDTO = childMapper.toDto(child);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(childDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Child in the database
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteChild() {
        // Initialize the database
        childRepository.save(child).block();

        int databaseSizeBeforeDelete = childRepository.findAll().collectList().block().size();

        // Delete the child
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, child.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Child> childList = childRepository.findAll().collectList().block();
        assertThat(childList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
