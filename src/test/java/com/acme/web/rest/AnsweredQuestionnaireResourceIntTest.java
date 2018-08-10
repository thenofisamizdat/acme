package com.acme.web.rest;

import com.acme.AcmeApp;

import com.acme.domain.AnsweredQuestionnaire;
import com.acme.repository.AnsweredQuestionnaireRepository;
import com.acme.repository.search.AnsweredQuestionnaireSearchRepository;
import com.acme.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.acme.web.rest.TestUtil.sameInstant;
import static com.acme.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AnsweredQuestionnaireResource REST controller.
 *
 * @see AnsweredQuestionnaireResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AcmeApp.class)
public class AnsweredQuestionnaireResourceIntTest {

    private static final Long DEFAULT_QUESTIONNAIRE_ID = 1L;
    private static final Long UPDATED_QUESTIONNAIRE_ID = 2L;

    private static final Long DEFAULT_CREATED_BY = 1L;
    private static final Long UPDATED_CREATED_BY = 2L;

    private static final Long DEFAULT_ANSWERED_BY = 1L;
    private static final Long UPDATED_ANSWERED_BY = 2L;

    private static final ZonedDateTime DEFAULT_ANSWERED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ANSWERED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private AnsweredQuestionnaireRepository answeredQuestionnaireRepository;

    @Autowired
    private AnsweredQuestionnaireSearchRepository answeredQuestionnaireSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAnsweredQuestionnaireMockMvc;

    private AnsweredQuestionnaire answeredQuestionnaire;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AnsweredQuestionnaireResource answeredQuestionnaireResource = new AnsweredQuestionnaireResource(answeredQuestionnaireRepository, answeredQuestionnaireSearchRepository);
        this.restAnsweredQuestionnaireMockMvc = MockMvcBuilders.standaloneSetup(answeredQuestionnaireResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AnsweredQuestionnaire createEntity(EntityManager em) {
        AnsweredQuestionnaire answeredQuestionnaire = new AnsweredQuestionnaire()
            .questionnaireID(DEFAULT_QUESTIONNAIRE_ID)
            .createdBy(DEFAULT_CREATED_BY)
            .answeredBy(DEFAULT_ANSWERED_BY)
            .answeredDate(DEFAULT_ANSWERED_DATE);
        return answeredQuestionnaire;
    }

    @Before
    public void initTest() {
        answeredQuestionnaireSearchRepository.deleteAll();
        answeredQuestionnaire = createEntity(em);
    }

    @Test
    @Transactional
    public void createAnsweredQuestionnaire() throws Exception {
        int databaseSizeBeforeCreate = answeredQuestionnaireRepository.findAll().size();

        // Create the AnsweredQuestionnaire
        restAnsweredQuestionnaireMockMvc.perform(post("/api/answered-questionnaires")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(answeredQuestionnaire)))
            .andExpect(status().isCreated());

        // Validate the AnsweredQuestionnaire in the database
        List<AnsweredQuestionnaire> answeredQuestionnaireList = answeredQuestionnaireRepository.findAll();
        assertThat(answeredQuestionnaireList).hasSize(databaseSizeBeforeCreate + 1);
        AnsweredQuestionnaire testAnsweredQuestionnaire = answeredQuestionnaireList.get(answeredQuestionnaireList.size() - 1);
        assertThat(testAnsweredQuestionnaire.getQuestionnaireID()).isEqualTo(DEFAULT_QUESTIONNAIRE_ID);
        assertThat(testAnsweredQuestionnaire.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testAnsweredQuestionnaire.getAnsweredBy()).isEqualTo(DEFAULT_ANSWERED_BY);
        assertThat(testAnsweredQuestionnaire.getAnsweredDate()).isEqualTo(DEFAULT_ANSWERED_DATE);

        // Validate the AnsweredQuestionnaire in Elasticsearch
        AnsweredQuestionnaire answeredQuestionnaireEs = answeredQuestionnaireSearchRepository.findOne(testAnsweredQuestionnaire.getId());
        assertThat(testAnsweredQuestionnaire.getAnsweredDate()).isEqualTo(testAnsweredQuestionnaire.getAnsweredDate());
        assertThat(answeredQuestionnaireEs).isEqualToIgnoringGivenFields(testAnsweredQuestionnaire, "answeredDate");
    }

    @Test
    @Transactional
    public void createAnsweredQuestionnaireWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = answeredQuestionnaireRepository.findAll().size();

        // Create the AnsweredQuestionnaire with an existing ID
        answeredQuestionnaire.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnsweredQuestionnaireMockMvc.perform(post("/api/answered-questionnaires")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(answeredQuestionnaire)))
            .andExpect(status().isBadRequest());

        // Validate the AnsweredQuestionnaire in the database
        List<AnsweredQuestionnaire> answeredQuestionnaireList = answeredQuestionnaireRepository.findAll();
        assertThat(answeredQuestionnaireList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllAnsweredQuestionnaires() throws Exception {
        // Initialize the database
        answeredQuestionnaireRepository.saveAndFlush(answeredQuestionnaire);

        // Get all the answeredQuestionnaireList
        restAnsweredQuestionnaireMockMvc.perform(get("/api/answered-questionnaires?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answeredQuestionnaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].questionnaireID").value(hasItem(DEFAULT_QUESTIONNAIRE_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.intValue())))
            .andExpect(jsonPath("$.[*].answeredBy").value(hasItem(DEFAULT_ANSWERED_BY.intValue())))
            .andExpect(jsonPath("$.[*].answeredDate").value(hasItem(sameInstant(DEFAULT_ANSWERED_DATE))));
    }

    @Test
    @Transactional
    public void getAnsweredQuestionnaire() throws Exception {
        // Initialize the database
        answeredQuestionnaireRepository.saveAndFlush(answeredQuestionnaire);

        // Get the answeredQuestionnaire
        restAnsweredQuestionnaireMockMvc.perform(get("/api/answered-questionnaires/{id}", answeredQuestionnaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(answeredQuestionnaire.getId().intValue()))
            .andExpect(jsonPath("$.questionnaireID").value(DEFAULT_QUESTIONNAIRE_ID.intValue()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.intValue()))
            .andExpect(jsonPath("$.answeredBy").value(DEFAULT_ANSWERED_BY.intValue()))
            .andExpect(jsonPath("$.answeredDate").value(sameInstant(DEFAULT_ANSWERED_DATE)));
    }

    @Test
    @Transactional
    public void getNonExistingAnsweredQuestionnaire() throws Exception {
        // Get the answeredQuestionnaire
        restAnsweredQuestionnaireMockMvc.perform(get("/api/answered-questionnaires/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAnsweredQuestionnaire() throws Exception {
        // Initialize the database
        answeredQuestionnaireRepository.saveAndFlush(answeredQuestionnaire);
        answeredQuestionnaireSearchRepository.save(answeredQuestionnaire);
        int databaseSizeBeforeUpdate = answeredQuestionnaireRepository.findAll().size();

        // Update the answeredQuestionnaire
        AnsweredQuestionnaire updatedAnsweredQuestionnaire = answeredQuestionnaireRepository.findOne(answeredQuestionnaire.getId());
        // Disconnect from session so that the updates on updatedAnsweredQuestionnaire are not directly saved in db
        em.detach(updatedAnsweredQuestionnaire);
        updatedAnsweredQuestionnaire
            .questionnaireID(UPDATED_QUESTIONNAIRE_ID)
            .createdBy(UPDATED_CREATED_BY)
            .answeredBy(UPDATED_ANSWERED_BY)
            .answeredDate(UPDATED_ANSWERED_DATE);

        restAnsweredQuestionnaireMockMvc.perform(put("/api/answered-questionnaires")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAnsweredQuestionnaire)))
            .andExpect(status().isOk());

        // Validate the AnsweredQuestionnaire in the database
        List<AnsweredQuestionnaire> answeredQuestionnaireList = answeredQuestionnaireRepository.findAll();
        assertThat(answeredQuestionnaireList).hasSize(databaseSizeBeforeUpdate);
        AnsweredQuestionnaire testAnsweredQuestionnaire = answeredQuestionnaireList.get(answeredQuestionnaireList.size() - 1);
        assertThat(testAnsweredQuestionnaire.getQuestionnaireID()).isEqualTo(UPDATED_QUESTIONNAIRE_ID);
        assertThat(testAnsweredQuestionnaire.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAnsweredQuestionnaire.getAnsweredBy()).isEqualTo(UPDATED_ANSWERED_BY);
        assertThat(testAnsweredQuestionnaire.getAnsweredDate()).isEqualTo(UPDATED_ANSWERED_DATE);

        // Validate the AnsweredQuestionnaire in Elasticsearch
        AnsweredQuestionnaire answeredQuestionnaireEs = answeredQuestionnaireSearchRepository.findOne(testAnsweredQuestionnaire.getId());
        assertThat(testAnsweredQuestionnaire.getAnsweredDate()).isEqualTo(testAnsweredQuestionnaire.getAnsweredDate());
        assertThat(answeredQuestionnaireEs).isEqualToIgnoringGivenFields(testAnsweredQuestionnaire, "answeredDate");
    }

    @Test
    @Transactional
    public void updateNonExistingAnsweredQuestionnaire() throws Exception {
        int databaseSizeBeforeUpdate = answeredQuestionnaireRepository.findAll().size();

        // Create the AnsweredQuestionnaire

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAnsweredQuestionnaireMockMvc.perform(put("/api/answered-questionnaires")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(answeredQuestionnaire)))
            .andExpect(status().isCreated());

        // Validate the AnsweredQuestionnaire in the database
        List<AnsweredQuestionnaire> answeredQuestionnaireList = answeredQuestionnaireRepository.findAll();
        assertThat(answeredQuestionnaireList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAnsweredQuestionnaire() throws Exception {
        // Initialize the database
        answeredQuestionnaireRepository.saveAndFlush(answeredQuestionnaire);
        answeredQuestionnaireSearchRepository.save(answeredQuestionnaire);
        int databaseSizeBeforeDelete = answeredQuestionnaireRepository.findAll().size();

        // Get the answeredQuestionnaire
        restAnsweredQuestionnaireMockMvc.perform(delete("/api/answered-questionnaires/{id}", answeredQuestionnaire.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean answeredQuestionnaireExistsInEs = answeredQuestionnaireSearchRepository.exists(answeredQuestionnaire.getId());
        assertThat(answeredQuestionnaireExistsInEs).isFalse();

        // Validate the database is empty
        List<AnsweredQuestionnaire> answeredQuestionnaireList = answeredQuestionnaireRepository.findAll();
        assertThat(answeredQuestionnaireList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAnsweredQuestionnaire() throws Exception {
        // Initialize the database
        answeredQuestionnaireRepository.saveAndFlush(answeredQuestionnaire);
        answeredQuestionnaireSearchRepository.save(answeredQuestionnaire);

        // Search the answeredQuestionnaire
        restAnsweredQuestionnaireMockMvc.perform(get("/api/_search/answered-questionnaires?query=id:" + answeredQuestionnaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answeredQuestionnaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].questionnaireID").value(hasItem(DEFAULT_QUESTIONNAIRE_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.intValue())))
            .andExpect(jsonPath("$.[*].answeredBy").value(hasItem(DEFAULT_ANSWERED_BY.intValue())))
            .andExpect(jsonPath("$.[*].answeredDate").value(hasItem(sameInstant(DEFAULT_ANSWERED_DATE))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnsweredQuestionnaire.class);
        AnsweredQuestionnaire answeredQuestionnaire1 = new AnsweredQuestionnaire();
        answeredQuestionnaire1.setId(1L);
        AnsweredQuestionnaire answeredQuestionnaire2 = new AnsweredQuestionnaire();
        answeredQuestionnaire2.setId(answeredQuestionnaire1.getId());
        assertThat(answeredQuestionnaire1).isEqualTo(answeredQuestionnaire2);
        answeredQuestionnaire2.setId(2L);
        assertThat(answeredQuestionnaire1).isNotEqualTo(answeredQuestionnaire2);
        answeredQuestionnaire1.setId(null);
        assertThat(answeredQuestionnaire1).isNotEqualTo(answeredQuestionnaire2);
    }
}
