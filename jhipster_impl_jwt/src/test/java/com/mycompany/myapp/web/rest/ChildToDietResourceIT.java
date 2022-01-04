package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ChildToDiet;
import com.mycompany.myapp.repository.ChildToDietRepository;
import com.mycompany.myapp.service.ChildToDietService;
import com.mycompany.myapp.service.EntityManager;
import com.mycompany.myapp.service.dto.ChildToDietDTO;
import com.mycompany.myapp.service.mapper.ChildToDietMapper;
import java.time.Duration;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link ChildToDietResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class ChildToDietResourceIT {

    private static final Long DEFAULT_ID_CHILD = 1L;
    private static final Long UPDATED_ID_CHILD = 2L;

    private static final Long DEFAULT_ID_DIET = 1L;
    private static final Long UPDATED_ID_DIET = 2L;

    private static final String ENTITY_API_URL = "/api/child-to-diets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ChildToDietRepository childToDietRepository;

    @Mock
    private ChildToDietRepository childToDietRepositoryMock;

    @Autowired
    private ChildToDietMapper childToDietMapper;

    @Mock
    private ChildToDietService childToDietServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ChildToDiet childToDiet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChildToDiet createEntity(EntityManager em) {
        ChildToDiet childToDiet = new ChildToDiet().idChild(DEFAULT_ID_CHILD).idDiet(DEFAULT_ID_DIET);
        return childToDiet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChildToDiet createUpdatedEntity(EntityManager em) {
        ChildToDiet childToDiet = new ChildToDiet().idChild(UPDATED_ID_CHILD).idDiet(UPDATED_ID_DIET);
        return childToDiet;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_child_to_diet__id_child").block();
            em.deleteAll("rel_child_to_diet__id_diet").block();
            em.deleteAll(ChildToDiet.class).block();
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
        childToDiet = createEntity(em);
    }

    @Test
    void createChildToDiet() throws Exception {
        int databaseSizeBeforeCreate = childToDietRepository.findAll().collectList().block().size();
        // Create the ChildToDiet
        ChildToDietDTO childToDietDTO = childToDietMapper.toDto(childToDiet);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childToDietDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeCreate + 1);
        ChildToDiet testChildToDiet = childToDietList.get(childToDietList.size() - 1);
        assertThat(testChildToDiet.getIdChild()).isEqualTo(DEFAULT_ID_CHILD);
        assertThat(testChildToDiet.getIdDiet()).isEqualTo(DEFAULT_ID_DIET);
    }

    @Test
    void createChildToDietWithExistingId() throws Exception {
        // Create the ChildToDiet with an existing ID
        childToDiet.setId(1L);
        ChildToDietDTO childToDietDTO = childToDietMapper.toDto(childToDiet);

        int databaseSizeBeforeCreate = childToDietRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childToDietDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllChildToDietsAsStream() {
        // Initialize the database
        childToDietRepository.save(childToDiet).block();

        List<ChildToDiet> childToDietList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ChildToDietDTO.class)
            .getResponseBody()
            .map(childToDietMapper::toEntity)
            .filter(childToDiet::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(childToDietList).isNotNull();
        assertThat(childToDietList).hasSize(1);
        ChildToDiet testChildToDiet = childToDietList.get(0);
        assertThat(testChildToDiet.getIdChild()).isEqualTo(DEFAULT_ID_CHILD);
        assertThat(testChildToDiet.getIdDiet()).isEqualTo(DEFAULT_ID_DIET);
    }

    @Test
    void getAllChildToDiets() {
        // Initialize the database
        childToDietRepository.save(childToDiet).block();

        // Get all the childToDietList
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
            .value(hasItem(childToDiet.getId().intValue()))
            .jsonPath("$.[*].idChild")
            .value(hasItem(DEFAULT_ID_CHILD.intValue()))
            .jsonPath("$.[*].idDiet")
            .value(hasItem(DEFAULT_ID_DIET.intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChildToDietsWithEagerRelationshipsIsEnabled() {
        when(childToDietServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(childToDietServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChildToDietsWithEagerRelationshipsIsNotEnabled() {
        when(childToDietServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(childToDietServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getChildToDiet() {
        // Initialize the database
        childToDietRepository.save(childToDiet).block();

        // Get the childToDiet
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, childToDiet.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(childToDiet.getId().intValue()))
            .jsonPath("$.idChild")
            .value(is(DEFAULT_ID_CHILD.intValue()))
            .jsonPath("$.idDiet")
            .value(is(DEFAULT_ID_DIET.intValue()));
    }

    @Test
    void getNonExistingChildToDiet() {
        // Get the childToDiet
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewChildToDiet() throws Exception {
        // Initialize the database
        childToDietRepository.save(childToDiet).block();

        int databaseSizeBeforeUpdate = childToDietRepository.findAll().collectList().block().size();

        // Update the childToDiet
        ChildToDiet updatedChildToDiet = childToDietRepository.findById(childToDiet.getId()).block();
        updatedChildToDiet.idChild(UPDATED_ID_CHILD).idDiet(UPDATED_ID_DIET);
        ChildToDietDTO childToDietDTO = childToDietMapper.toDto(updatedChildToDiet);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, childToDietDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childToDietDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeUpdate);
        ChildToDiet testChildToDiet = childToDietList.get(childToDietList.size() - 1);
        assertThat(testChildToDiet.getIdChild()).isEqualTo(UPDATED_ID_CHILD);
        assertThat(testChildToDiet.getIdDiet()).isEqualTo(UPDATED_ID_DIET);
    }

    @Test
    void putNonExistingChildToDiet() throws Exception {
        int databaseSizeBeforeUpdate = childToDietRepository.findAll().collectList().block().size();
        childToDiet.setId(count.incrementAndGet());

        // Create the ChildToDiet
        ChildToDietDTO childToDietDTO = childToDietMapper.toDto(childToDiet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, childToDietDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childToDietDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchChildToDiet() throws Exception {
        int databaseSizeBeforeUpdate = childToDietRepository.findAll().collectList().block().size();
        childToDiet.setId(count.incrementAndGet());

        // Create the ChildToDiet
        ChildToDietDTO childToDietDTO = childToDietMapper.toDto(childToDiet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childToDietDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamChildToDiet() throws Exception {
        int databaseSizeBeforeUpdate = childToDietRepository.findAll().collectList().block().size();
        childToDiet.setId(count.incrementAndGet());

        // Create the ChildToDiet
        ChildToDietDTO childToDietDTO = childToDietMapper.toDto(childToDiet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(childToDietDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateChildToDietWithPatch() throws Exception {
        // Initialize the database
        childToDietRepository.save(childToDiet).block();

        int databaseSizeBeforeUpdate = childToDietRepository.findAll().collectList().block().size();

        // Update the childToDiet using partial update
        ChildToDiet partialUpdatedChildToDiet = new ChildToDiet();
        partialUpdatedChildToDiet.setId(childToDiet.getId());

        partialUpdatedChildToDiet.idDiet(UPDATED_ID_DIET);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChildToDiet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedChildToDiet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeUpdate);
        ChildToDiet testChildToDiet = childToDietList.get(childToDietList.size() - 1);
        assertThat(testChildToDiet.getIdChild()).isEqualTo(DEFAULT_ID_CHILD);
        assertThat(testChildToDiet.getIdDiet()).isEqualTo(UPDATED_ID_DIET);
    }

    @Test
    void fullUpdateChildToDietWithPatch() throws Exception {
        // Initialize the database
        childToDietRepository.save(childToDiet).block();

        int databaseSizeBeforeUpdate = childToDietRepository.findAll().collectList().block().size();

        // Update the childToDiet using partial update
        ChildToDiet partialUpdatedChildToDiet = new ChildToDiet();
        partialUpdatedChildToDiet.setId(childToDiet.getId());

        partialUpdatedChildToDiet.idChild(UPDATED_ID_CHILD).idDiet(UPDATED_ID_DIET);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChildToDiet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedChildToDiet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeUpdate);
        ChildToDiet testChildToDiet = childToDietList.get(childToDietList.size() - 1);
        assertThat(testChildToDiet.getIdChild()).isEqualTo(UPDATED_ID_CHILD);
        assertThat(testChildToDiet.getIdDiet()).isEqualTo(UPDATED_ID_DIET);
    }

    @Test
    void patchNonExistingChildToDiet() throws Exception {
        int databaseSizeBeforeUpdate = childToDietRepository.findAll().collectList().block().size();
        childToDiet.setId(count.incrementAndGet());

        // Create the ChildToDiet
        ChildToDietDTO childToDietDTO = childToDietMapper.toDto(childToDiet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, childToDietDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(childToDietDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchChildToDiet() throws Exception {
        int databaseSizeBeforeUpdate = childToDietRepository.findAll().collectList().block().size();
        childToDiet.setId(count.incrementAndGet());

        // Create the ChildToDiet
        ChildToDietDTO childToDietDTO = childToDietMapper.toDto(childToDiet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(childToDietDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamChildToDiet() throws Exception {
        int databaseSizeBeforeUpdate = childToDietRepository.findAll().collectList().block().size();
        childToDiet.setId(count.incrementAndGet());

        // Create the ChildToDiet
        ChildToDietDTO childToDietDTO = childToDietMapper.toDto(childToDiet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(childToDietDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ChildToDiet in the database
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteChildToDiet() {
        // Initialize the database
        childToDietRepository.save(childToDiet).block();

        int databaseSizeBeforeDelete = childToDietRepository.findAll().collectList().block().size();

        // Delete the childToDiet
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, childToDiet.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ChildToDiet> childToDietList = childToDietRepository.findAll().collectList().block();
        assertThat(childToDietList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
