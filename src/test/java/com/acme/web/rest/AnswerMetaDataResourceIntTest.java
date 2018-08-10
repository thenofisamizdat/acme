package com.acme.web.rest;

import com.acme.AcmeApp;

import com.acme.domain.AnswerMetaData;
import com.acme.repository.AnswerMetaDataRepository;
import com.acme.repository.search.AnswerMetaDataSearchRepository;
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
import java.util.List;

import static com.acme.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AnswerMetaDataResource REST controller.
 *
 * @see AnswerMetaDataResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AcmeApp.class)
public class AnswerMetaDataResourceIntTest {

    private static final String DEFAULT_ANSWER = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER = "BBBBBBBBBB";

    private static final String DEFAULT_ASSOCIATED_QUESTION = "AAAAAAAAAA";
    private static final String UPDATED_ASSOCIATED_QUESTION = "BBBBBBBBBB";

    private static final Long DEFAULT_ASSOCIATED_QUESTION_ID = 1L;
    private static final Long UPDATED_ASSOCIATED_QUESTION_ID = 2L;

    @Autowired
    private AnswerMetaDataRepository answerMetaDataRepository;

    @Autowired
    private AnswerMetaDataSearchRepository answerMetaDataSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAnswerMetaDataMockMvc;

    private AnswerMetaData answerMetaData;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AnswerMetaDataResource answerMetaDataResource = new AnswerMetaDataResource(answerMetaDataRepository, answerMetaDataSearchRepository);
        this.restAnswerMetaDataMockMvc = MockMvcBuilders.standaloneSetup(answerMetaDataResource)
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
    public static AnswerMetaData createEntity(EntityManager em) {
        AnswerMetaData answerMetaData = new AnswerMetaData()
            .answer(DEFAULT_ANSWER)
            .associatedQuestion(DEFAULT_ASSOCIATED_QUESTION)
            .associatedQuestionID(DEFAULT_ASSOCIATED_QUESTION_ID);
        return answerMetaData;
    }

    @Before
    public void initTest() {
        answerMetaDataSearchRepository.deleteAll();
        answerMetaData = createEntity(em);
    }

    @Test
    @Transactional
    public void createAnswerMetaData() throws Exception {
        int databaseSizeBeforeCreate = answerMetaDataRepository.findAll().size();

        // Create the AnswerMetaData
        restAnswerMetaDataMockMvc.perform(post("/api/answer-meta-data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(answerMetaData)))
            .andExpect(status().isCreated());

        // Validate the AnswerMetaData in the database
        List<AnswerMetaData> answerMetaDataList = answerMetaDataRepository.findAll();
        assertThat(answerMetaDataList).hasSize(databaseSizeBeforeCreate + 1);
        AnswerMetaData testAnswerMetaData = answerMetaDataList.get(answerMetaDataList.size() - 1);
        assertThat(testAnswerMetaData.getAnswer()).isEqualTo(DEFAULT_ANSWER);
        assertThat(testAnswerMetaData.getAssociatedQuestion()).isEqualTo(DEFAULT_ASSOCIATED_QUESTION);
        assertThat(testAnswerMetaData.getAssociatedQuestionID()).isEqualTo(DEFAULT_ASSOCIATED_QUESTION_ID);

        // Validate the AnswerMetaData in Elasticsearch
        AnswerMetaData answerMetaDataEs = answerMetaDataSearchRepository.findOne(testAnswerMetaData.getId());
        assertThat(answerMetaDataEs).isEqualToIgnoringGivenFields(testAnswerMetaData);
    }

    @Test
    @Transactional
    public void createAnswerMetaDataWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = answerMetaDataRepository.findAll().size();

        // Create the AnswerMetaData with an existing ID
        answerMetaData.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnswerMetaDataMockMvc.perform(post("/api/answer-meta-data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(answerMetaData)))
            .andExpect(status().isBadRequest());

        // Validate the AnswerMetaData in the database
        List<AnswerMetaData> answerMetaDataList = answerMetaDataRepository.findAll();
        assertThat(answerMetaDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllAnswerMetaData() throws Exception {
        // Initialize the database
        answerMetaDataRepository.saveAndFlush(answerMetaData);

        // Get all the answerMetaDataList
        restAnswerMetaDataMockMvc.perform(get("/api/answer-meta-data?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answerMetaData.getId().intValue())))
            .andExpect(jsonPath("$.[*].answer").value(hasItem(DEFAULT_ANSWER.toString())))
            .andExpect(jsonPath("$.[*].associatedQuestion").value(hasItem(DEFAULT_ASSOCIATED_QUESTION.toString())))
            .andExpect(jsonPath("$.[*].associatedQuestionID").value(hasItem(DEFAULT_ASSOCIATED_QUESTION_ID.intValue())));
    }

    @Test
    @Transactional
    public void getAnswerMetaData() throws Exception {
        // Initialize the database
        answerMetaDataRepository.saveAndFlush(answerMetaData);

        // Get the answerMetaData
        restAnswerMetaDataMockMvc.perform(get("/api/answer-meta-data/{id}", answerMetaData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(answerMetaData.getId().intValue()))
            .andExpect(jsonPath("$.answer").value(DEFAULT_ANSWER.toString()))
            .andExpect(jsonPath("$.associatedQuestion").value(DEFAULT_ASSOCIATED_QUESTION.toString()))
            .andExpect(jsonPath("$.associatedQuestionID").value(DEFAULT_ASSOCIATED_QUESTION_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingAnswerMetaData() throws Exception {
        // Get the answerMetaData
        restAnswerMetaDataMockMvc.perform(get("/api/answer-meta-data/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAnswerMetaData() throws Exception {
        // Initialize the database
        answerMetaDataRepository.saveAndFlush(answerMetaData);
        answerMetaDataSearchRepository.save(answerMetaData);
        int databaseSizeBeforeUpdate = answerMetaDataRepository.findAll().size();

        // Update the answerMetaData
        AnswerMetaData updatedAnswerMetaData = answerMetaDataRepository.findOne(answerMetaData.getId());
        // Disconnect from session so that the updates on updatedAnswerMetaData are not directly saved in db
        em.detach(updatedAnswerMetaData);
        updatedAnswerMetaData
            .answer(UPDATED_ANSWER)
            .associatedQuestion(UPDATED_ASSOCIATED_QUESTION)
            .associatedQuestionID(UPDATED_ASSOCIATED_QUESTION_ID);

        restAnswerMetaDataMockMvc.perform(put("/api/answer-meta-data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAnswerMetaData)))
            .andExpect(status().isOk());

        // Validate the AnswerMetaData in the database
        List<AnswerMetaData> answerMetaDataList = answerMetaDataRepository.findAll();
        assertThat(answerMetaDataList).hasSize(databaseSizeBeforeUpdate);
        AnswerMetaData testAnswerMetaData = answerMetaDataList.get(answerMetaDataList.size() - 1);
        assertThat(testAnswerMetaData.getAnswer()).isEqualTo(UPDATED_ANSWER);
        assertThat(testAnswerMetaData.getAssociatedQuestion()).isEqualTo(UPDATED_ASSOCIATED_QUESTION);
        assertThat(testAnswerMetaData.getAssociatedQuestionID()).isEqualTo(UPDATED_ASSOCIATED_QUESTION_ID);

        // Validate the AnswerMetaData in Elasticsearch
        AnswerMetaData answerMetaDataEs = answerMetaDataSearchRepository.findOne(testAnswerMetaData.getId());
        assertThat(answerMetaDataEs).isEqualToIgnoringGivenFields(testAnswerMetaData);
    }

    @Test
    @Transactional
    public void updateNonExistingAnswerMetaData() throws Exception {
        int databaseSizeBeforeUpdate = answerMetaDataRepository.findAll().size();

        // Create the AnswerMetaData

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAnswerMetaDataMockMvc.perform(put("/api/answer-meta-data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(answerMetaData)))
            .andExpect(status().isCreated());

        // Validate the AnswerMetaData in the database
        List<AnswerMetaData> answerMetaDataList = answerMetaDataRepository.findAll();
        assertThat(answerMetaDataList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAnswerMetaData() throws Exception {
        // Initialize the database
        answerMetaDataRepository.saveAndFlush(answerMetaData);
        answerMetaDataSearchRepository.save(answerMetaData);
        int databaseSizeBeforeDelete = answerMetaDataRepository.findAll().size();

        // Get the answerMetaData
        restAnswerMetaDataMockMvc.perform(delete("/api/answer-meta-data/{id}", answerMetaData.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean answerMetaDataExistsInEs = answerMetaDataSearchRepository.exists(answerMetaData.getId());
        assertThat(answerMetaDataExistsInEs).isFalse();

        // Validate the database is empty
        List<AnswerMetaData> answerMetaDataList = answerMetaDataRepository.findAll();
        assertThat(answerMetaDataList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAnswerMetaData() throws Exception {
        // Initialize the database
        answerMetaDataRepository.saveAndFlush(answerMetaData);
        answerMetaDataSearchRepository.save(answerMetaData);

        // Search the answerMetaData
        restAnswerMetaDataMockMvc.perform(get("/api/_search/answer-meta-data?query=id:" + answerMetaData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answerMetaData.getId().intValue())))
            .andExpect(jsonPath("$.[*].answer").value(hasItem(DEFAULT_ANSWER.toString())))
            .andExpect(jsonPath("$.[*].associatedQuestion").value(hasItem(DEFAULT_ASSOCIATED_QUESTION.toString())))
            .andExpect(jsonPath("$.[*].associatedQuestionID").value(hasItem(DEFAULT_ASSOCIATED_QUESTION_ID.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnswerMetaData.class);
        AnswerMetaData answerMetaData1 = new AnswerMetaData();
        answerMetaData1.setId(1L);
        AnswerMetaData answerMetaData2 = new AnswerMetaData();
        answerMetaData2.setId(answerMetaData1.getId());
        assertThat(answerMetaData1).isEqualTo(answerMetaData2);
        answerMetaData2.setId(2L);
        assertThat(answerMetaData1).isNotEqualTo(answerMetaData2);
        answerMetaData1.setId(null);
        assertThat(answerMetaData1).isNotEqualTo(answerMetaData2);
    }
}
