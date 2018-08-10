package com.acme.web.rest;

import com.acme.AcmeApp;

import com.acme.domain.Answer;
import com.acme.repository.AnswerRepository;
import com.acme.repository.search.AnswerSearchRepository;
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
 * Test class for the AnswerResource REST controller.
 *
 * @see AnswerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AcmeApp.class)
public class AnswerResourceIntTest {

    private static final String DEFAULT_ANSWER_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_ASSOCIATED_QUESTION = "AAAAAAAAAA";
    private static final String UPDATED_ASSOCIATED_QUESTION = "BBBBBBBBBB";

    private static final Long DEFAULT_ASSOCIATED_QUESTION_ID = 1L;
    private static final Long UPDATED_ASSOCIATED_QUESTION_ID = 2L;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final ZonedDateTime DEFAULT_ANSWERED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ANSWERED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AnswerSearchRepository answerSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAnswerMockMvc;

    private Answer answer;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AnswerResource answerResource = new AnswerResource(answerRepository, answerSearchRepository);
        this.restAnswerMockMvc = MockMvcBuilders.standaloneSetup(answerResource)
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
    public static Answer createEntity(EntityManager em) {
        Answer answer = new Answer()
            .answerText(DEFAULT_ANSWER_TEXT)
            .associatedQuestion(DEFAULT_ASSOCIATED_QUESTION)
            .associatedQuestionID(DEFAULT_ASSOCIATED_QUESTION_ID)
            .userID(DEFAULT_USER_ID)
            .answeredDate(DEFAULT_ANSWERED_DATE);
        return answer;
    }

    @Before
    public void initTest() {
        answerSearchRepository.deleteAll();
        answer = createEntity(em);
    }

    @Test
    @Transactional
    public void createAnswer() throws Exception {
        int databaseSizeBeforeCreate = answerRepository.findAll().size();

        // Create the Answer
        restAnswerMockMvc.perform(post("/api/answers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isCreated());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeCreate + 1);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getAnswerText()).isEqualTo(DEFAULT_ANSWER_TEXT);
        assertThat(testAnswer.getAssociatedQuestion()).isEqualTo(DEFAULT_ASSOCIATED_QUESTION);
        assertThat(testAnswer.getAssociatedQuestionID()).isEqualTo(DEFAULT_ASSOCIATED_QUESTION_ID);
        assertThat(testAnswer.getUserID()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testAnswer.getAnsweredDate()).isEqualTo(DEFAULT_ANSWERED_DATE);

        // Validate the Answer in Elasticsearch
        Answer answerEs = answerSearchRepository.findOne(testAnswer.getId());
        assertThat(testAnswer.getAnsweredDate()).isEqualTo(testAnswer.getAnsweredDate());
        assertThat(answerEs).isEqualToIgnoringGivenFields(testAnswer, "answeredDate");
    }

    @Test
    @Transactional
    public void createAnswerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = answerRepository.findAll().size();

        // Create the Answer with an existing ID
        answer.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnswerMockMvc.perform(post("/api/answers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkAnswerTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        // set the field null
        answer.setAnswerText(null);

        // Create the Answer, which fails.

        restAnswerMockMvc.perform(post("/api/answers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isBadRequest());

        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAnswers() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList
        restAnswerMockMvc.perform(get("/api/answers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answer.getId().intValue())))
            .andExpect(jsonPath("$.[*].answerText").value(hasItem(DEFAULT_ANSWER_TEXT.toString())))
            .andExpect(jsonPath("$.[*].associatedQuestion").value(hasItem(DEFAULT_ASSOCIATED_QUESTION.toString())))
            .andExpect(jsonPath("$.[*].associatedQuestionID").value(hasItem(DEFAULT_ASSOCIATED_QUESTION_ID.intValue())))
            .andExpect(jsonPath("$.[*].userID").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].answeredDate").value(hasItem(sameInstant(DEFAULT_ANSWERED_DATE))));
    }

    @Test
    @Transactional
    public void getAnswer() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get the answer
        restAnswerMockMvc.perform(get("/api/answers/{id}", answer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(answer.getId().intValue()))
            .andExpect(jsonPath("$.answerText").value(DEFAULT_ANSWER_TEXT.toString()))
            .andExpect(jsonPath("$.associatedQuestion").value(DEFAULT_ASSOCIATED_QUESTION.toString()))
            .andExpect(jsonPath("$.associatedQuestionID").value(DEFAULT_ASSOCIATED_QUESTION_ID.intValue()))
            .andExpect(jsonPath("$.userID").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.answeredDate").value(sameInstant(DEFAULT_ANSWERED_DATE)));
    }

    @Test
    @Transactional
    public void getNonExistingAnswer() throws Exception {
        // Get the answer
        restAnswerMockMvc.perform(get("/api/answers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAnswer() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);
        answerSearchRepository.save(answer);
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();

        // Update the answer
        Answer updatedAnswer = answerRepository.findOne(answer.getId());
        // Disconnect from session so that the updates on updatedAnswer are not directly saved in db
        em.detach(updatedAnswer);
        updatedAnswer
            .answerText(UPDATED_ANSWER_TEXT)
            .associatedQuestion(UPDATED_ASSOCIATED_QUESTION)
            .associatedQuestionID(UPDATED_ASSOCIATED_QUESTION_ID)
            .userID(UPDATED_USER_ID)
            .answeredDate(UPDATED_ANSWERED_DATE);

        restAnswerMockMvc.perform(put("/api/answers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAnswer)))
            .andExpect(status().isOk());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getAnswerText()).isEqualTo(UPDATED_ANSWER_TEXT);
        assertThat(testAnswer.getAssociatedQuestion()).isEqualTo(UPDATED_ASSOCIATED_QUESTION);
        assertThat(testAnswer.getAssociatedQuestionID()).isEqualTo(UPDATED_ASSOCIATED_QUESTION_ID);
        assertThat(testAnswer.getUserID()).isEqualTo(UPDATED_USER_ID);
        assertThat(testAnswer.getAnsweredDate()).isEqualTo(UPDATED_ANSWERED_DATE);

        // Validate the Answer in Elasticsearch
        Answer answerEs = answerSearchRepository.findOne(testAnswer.getId());
        assertThat(testAnswer.getAnsweredDate()).isEqualTo(testAnswer.getAnsweredDate());
        assertThat(answerEs).isEqualToIgnoringGivenFields(testAnswer, "answeredDate");
    }

    @Test
    @Transactional
    public void updateNonExistingAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();

        // Create the Answer

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAnswerMockMvc.perform(put("/api/answers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isCreated());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAnswer() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);
        answerSearchRepository.save(answer);
        int databaseSizeBeforeDelete = answerRepository.findAll().size();

        // Get the answer
        restAnswerMockMvc.perform(delete("/api/answers/{id}", answer.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean answerExistsInEs = answerSearchRepository.exists(answer.getId());
        assertThat(answerExistsInEs).isFalse();

        // Validate the database is empty
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAnswer() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);
        answerSearchRepository.save(answer);

        // Search the answer
        restAnswerMockMvc.perform(get("/api/_search/answers?query=id:" + answer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answer.getId().intValue())))
            .andExpect(jsonPath("$.[*].answerText").value(hasItem(DEFAULT_ANSWER_TEXT.toString())))
            .andExpect(jsonPath("$.[*].associatedQuestion").value(hasItem(DEFAULT_ASSOCIATED_QUESTION.toString())))
            .andExpect(jsonPath("$.[*].associatedQuestionID").value(hasItem(DEFAULT_ASSOCIATED_QUESTION_ID.intValue())))
            .andExpect(jsonPath("$.[*].userID").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].answeredDate").value(hasItem(sameInstant(DEFAULT_ANSWERED_DATE))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Answer.class);
        Answer answer1 = new Answer();
        answer1.setId(1L);
        Answer answer2 = new Answer();
        answer2.setId(answer1.getId());
        assertThat(answer1).isEqualTo(answer2);
        answer2.setId(2L);
        assertThat(answer1).isNotEqualTo(answer2);
        answer1.setId(null);
        assertThat(answer1).isNotEqualTo(answer2);
    }
}
