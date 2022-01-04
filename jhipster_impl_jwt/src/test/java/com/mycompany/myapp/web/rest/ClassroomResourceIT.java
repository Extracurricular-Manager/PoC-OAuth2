package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Classroom;
import com.mycompany.myapp.repository.ClassroomRepository;
import com.mycompany.myapp.service.EntityManager;
import com.mycompany.myapp.service.dto.ClassroomDTO;
import com.mycompany.myapp.service.mapper.ClassroomMapper;
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
 * Integration tests for the {@link ClassroomResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class ClassroomResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PROFESSOR = "AAAAAAAAAA";
    private static final String UPDATED_PROFESSOR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/classrooms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private ClassroomMapper classroomMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Classroom classroom;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Classroom createEntity(EntityManager em) {
        Classroom classroom = new Classroom().name(DEFAULT_NAME).professor(DEFAULT_PROFESSOR);
        return classroom;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Classroom createUpdatedEntity(EntityManager em) {
        Classroom classroom = new Classroom().name(UPDATED_NAME).professor(UPDATED_PROFESSOR);
        return classroom;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Classroom.class).block();
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
        classroom = createEntity(em);
    }

    @Test
    void createClassroom() throws Exception {
        int databaseSizeBeforeCreate = classroomRepository.findAll().collectList().block().size();
        // Create the Classroom
        ClassroomDTO classroomDTO = classroomMapper.toDto(classroom);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(classroomDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeCreate + 1);
        Classroom testClassroom = classroomList.get(classroomList.size() - 1);
        assertThat(testClassroom.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClassroom.getProfessor()).isEqualTo(DEFAULT_PROFESSOR);
    }

    @Test
    void createClassroomWithExistingId() throws Exception {
        // Create the Classroom with an existing ID
        classroom.setId(1L);
        ClassroomDTO classroomDTO = classroomMapper.toDto(classroom);

        int databaseSizeBeforeCreate = classroomRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(classroomDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllClassroomsAsStream() {
        // Initialize the database
        classroomRepository.save(classroom).block();

        List<Classroom> classroomList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ClassroomDTO.class)
            .getResponseBody()
            .map(classroomMapper::toEntity)
            .filter(classroom::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(classroomList).isNotNull();
        assertThat(classroomList).hasSize(1);
        Classroom testClassroom = classroomList.get(0);
        assertThat(testClassroom.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClassroom.getProfessor()).isEqualTo(DEFAULT_PROFESSOR);
    }

    @Test
    void getAllClassrooms() {
        // Initialize the database
        classroomRepository.save(classroom).block();

        // Get all the classroomList
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
            .value(hasItem(classroom.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].professor")
            .value(hasItem(DEFAULT_PROFESSOR));
    }

    @Test
    void getClassroom() {
        // Initialize the database
        classroomRepository.save(classroom).block();

        // Get the classroom
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, classroom.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(classroom.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.professor")
            .value(is(DEFAULT_PROFESSOR));
    }

    @Test
    void getNonExistingClassroom() {
        // Get the classroom
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewClassroom() throws Exception {
        // Initialize the database
        classroomRepository.save(classroom).block();

        int databaseSizeBeforeUpdate = classroomRepository.findAll().collectList().block().size();

        // Update the classroom
        Classroom updatedClassroom = classroomRepository.findById(classroom.getId()).block();
        updatedClassroom.name(UPDATED_NAME).professor(UPDATED_PROFESSOR);
        ClassroomDTO classroomDTO = classroomMapper.toDto(updatedClassroom);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, classroomDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(classroomDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);
        Classroom testClassroom = classroomList.get(classroomList.size() - 1);
        assertThat(testClassroom.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClassroom.getProfessor()).isEqualTo(UPDATED_PROFESSOR);
    }

    @Test
    void putNonExistingClassroom() throws Exception {
        int databaseSizeBeforeUpdate = classroomRepository.findAll().collectList().block().size();
        classroom.setId(count.incrementAndGet());

        // Create the Classroom
        ClassroomDTO classroomDTO = classroomMapper.toDto(classroom);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, classroomDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(classroomDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClassroom() throws Exception {
        int databaseSizeBeforeUpdate = classroomRepository.findAll().collectList().block().size();
        classroom.setId(count.incrementAndGet());

        // Create the Classroom
        ClassroomDTO classroomDTO = classroomMapper.toDto(classroom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(classroomDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClassroom() throws Exception {
        int databaseSizeBeforeUpdate = classroomRepository.findAll().collectList().block().size();
        classroom.setId(count.incrementAndGet());

        // Create the Classroom
        ClassroomDTO classroomDTO = classroomMapper.toDto(classroom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(classroomDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateClassroomWithPatch() throws Exception {
        // Initialize the database
        classroomRepository.save(classroom).block();

        int databaseSizeBeforeUpdate = classroomRepository.findAll().collectList().block().size();

        // Update the classroom using partial update
        Classroom partialUpdatedClassroom = new Classroom();
        partialUpdatedClassroom.setId(classroom.getId());

        partialUpdatedClassroom.name(UPDATED_NAME).professor(UPDATED_PROFESSOR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClassroom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedClassroom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);
        Classroom testClassroom = classroomList.get(classroomList.size() - 1);
        assertThat(testClassroom.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClassroom.getProfessor()).isEqualTo(UPDATED_PROFESSOR);
    }

    @Test
    void fullUpdateClassroomWithPatch() throws Exception {
        // Initialize the database
        classroomRepository.save(classroom).block();

        int databaseSizeBeforeUpdate = classroomRepository.findAll().collectList().block().size();

        // Update the classroom using partial update
        Classroom partialUpdatedClassroom = new Classroom();
        partialUpdatedClassroom.setId(classroom.getId());

        partialUpdatedClassroom.name(UPDATED_NAME).professor(UPDATED_PROFESSOR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClassroom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedClassroom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);
        Classroom testClassroom = classroomList.get(classroomList.size() - 1);
        assertThat(testClassroom.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClassroom.getProfessor()).isEqualTo(UPDATED_PROFESSOR);
    }

    @Test
    void patchNonExistingClassroom() throws Exception {
        int databaseSizeBeforeUpdate = classroomRepository.findAll().collectList().block().size();
        classroom.setId(count.incrementAndGet());

        // Create the Classroom
        ClassroomDTO classroomDTO = classroomMapper.toDto(classroom);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, classroomDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(classroomDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClassroom() throws Exception {
        int databaseSizeBeforeUpdate = classroomRepository.findAll().collectList().block().size();
        classroom.setId(count.incrementAndGet());

        // Create the Classroom
        ClassroomDTO classroomDTO = classroomMapper.toDto(classroom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(classroomDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClassroom() throws Exception {
        int databaseSizeBeforeUpdate = classroomRepository.findAll().collectList().block().size();
        classroom.setId(count.incrementAndGet());

        // Create the Classroom
        ClassroomDTO classroomDTO = classroomMapper.toDto(classroom);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(classroomDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClassroom() {
        // Initialize the database
        classroomRepository.save(classroom).block();

        int databaseSizeBeforeDelete = classroomRepository.findAll().collectList().block().size();

        // Delete the classroom
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, classroom.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Classroom> classroomList = classroomRepository.findAll().collectList().block();
        assertThat(classroomList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
