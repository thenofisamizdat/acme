package com.acme.web.rest;

import com.acme.AcmeApp;

import com.acme.domain.Question;
import com.acme.repository.AnswerMetaDataRepository;
import com.acme.repository.QuestionRepository;
import com.acme.repository.search.AnswerMetaDataSearchRepository;
import com.acme.repository.search.QuestionSearchRepository;
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
 * Test class for the QuestionResource REST controller.
 *
 * @see QuestionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AcmeApp.class)
public class QuestionResourceIntTest {

    private static final String DEFAULT_QUESTION = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_MANDATORY = false;
    private static final Boolean UPDATED_MANDATORY = true;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionSearchRepository questionSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private AnswerMetaDataRepository answerMetaDataRepository;

    @Autowired
    private AnswerMetaDataSearchRepository answerMetaDataSearchRepository;

    @Autowired
    private EntityManager em;

    private MockMvc restQuestionMockMvc;

    private Question question;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final QuestionResource questionResource = new QuestionResource(questionRepository, questionSearchRepository, answerMetaDataRepository, answerMetaDataSearchRepository);
        this.restQuestionMockMvc = MockMvcBuilders.standaloneSetup(questionResource)
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
    public static Question createEntity(EntityManager em) {
        Question question = new Question()
            .question(DEFAULT_QUESTION)
            .mandatory(DEFAULT_MANDATORY);
        return question;
    }

    @Before
    public void initTest() {
        questionSearchRepository.deleteAll();
        question = createEntity(em);
    }

    @Test
    @Transactional
    public void createQuestion() throws Exception {
        int databaseSizeBeforeCreate = questionRepository.findAll().size();

        // Create the Question
        restQuestionMockMvc.perform(post("/api/questions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isCreated());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeCreate + 1);
        Question testQuestion = questionList.get(questionList.size() - 1);
        assertThat(testQuestion.getQuestion()).isEqualTo(DEFAULT_QUESTION);
        assertThat(testQuestion.isMandatory()).isEqualTo(DEFAULT_MANDATORY);

        // Validate the Question in Elasticsearch
        Question questionEs = questionSearchRepository.findOne(testQuestion.getId());
        assertThat(questionEs).isEqualToIgnoringGivenFields(testQuestion);
    }

    @Test
    @Transactional
    public void createQuestionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = questionRepository.findAll().size();

        // Create the Question with an existing ID
        question.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionMockMvc.perform(post("/api/questions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllQuestions() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);

        // Get all the questionList
        restQuestionMockMvc.perform(get("/api/questions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question.getId().intValue())))
            .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION.toString())))
            .andExpect(jsonPath("$.[*].mandatory").value(hasItem(DEFAULT_MANDATORY.booleanValue())));
    }

    @Test
    @Transactional
    public void getQuestion() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);

        // Get the question
        restQuestionMockMvc.perform(get("/api/questions/{id}", question.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(question.getId().intValue()))
            .andExpect(jsonPath("$.question").value(DEFAULT_QUESTION.toString()))
            .andExpect(jsonPath("$.mandatory").value(DEFAULT_MANDATORY.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingQuestion() throws Exception {
        // Get the question
        restQuestionMockMvc.perform(get("/api/questions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuestion() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);
        questionSearchRepository.save(question);
        int databaseSizeBeforeUpdate = questionRepository.findAll().size();

        // Update the question
        Question updatedQuestion = questionRepository.findOne(question.getId());
        // Disconnect from session so that the updates on updatedQuestion are not directly saved in db
        em.detach(updatedQuestion);
        updatedQuestion
            .question(UPDATED_QUESTION)
            .mandatory(UPDATED_MANDATORY);

        restQuestionMockMvc.perform(put("/api/questions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedQuestion)))
            .andExpect(status().isOk());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        Question testQuestion = questionList.get(questionList.size() - 1);
        assertThat(testQuestion.getQuestion()).isEqualTo(UPDATED_QUESTION);
        assertThat(testQuestion.isMandatory()).isEqualTo(UPDATED_MANDATORY);

        // Validate the Question in Elasticsearch
        Question questionEs = questionSearchRepository.findOne(testQuestion.getId());
        assertThat(questionEs).isEqualToIgnoringGivenFields(testQuestion);
    }

    @Test
    @Transactional
    public void updateNonExistingQuestion() throws Exception {
        int databaseSizeBeforeUpdate = questionRepository.findAll().size();

        // Create the Question

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restQuestionMockMvc.perform(put("/api/questions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isCreated());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteQuestion() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);
        questionSearchRepository.save(question);
        int databaseSizeBeforeDelete = questionRepository.findAll().size();

        // Get the question
        restQuestionMockMvc.perform(delete("/api/questions/{id}", question.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean questionExistsInEs = questionSearchRepository.exists(question.getId());
        assertThat(questionExistsInEs).isFalse();

        // Validate the database is empty
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchQuestion() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);
        questionSearchRepository.save(question);

        // Search the question
        restQuestionMockMvc.perform(get("/api/_search/questions?query=id:" + question.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question.getId().intValue())))
            .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION.toString())))
            .andExpect(jsonPath("$.[*].mandatory").value(hasItem(DEFAULT_MANDATORY.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Question.class);
        Question question1 = new Question();
        question1.setId(1L);
        Question question2 = new Question();
        question2.setId(question1.getId());
        assertThat(question1).isEqualTo(question2);
        question2.setId(2L);
        assertThat(question1).isNotEqualTo(question2);
        question1.setId(null);
        assertThat(question1).isNotEqualTo(question2);
    }
}
