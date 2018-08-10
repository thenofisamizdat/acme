package com.acme.web.rest;

import com.acme.domain.AnswerMetaData;
import com.acme.repository.AnswerMetaDataRepository;
import com.acme.repository.search.AnswerMetaDataSearchRepository;
import com.codahale.metrics.annotation.Timed;
import com.acme.domain.Question;

import com.acme.repository.QuestionRepository;
import com.acme.repository.search.QuestionSearchRepository;
import com.acme.web.rest.errors.BadRequestAlertException;
import com.acme.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Question.
 */
@RestController
@RequestMapping("/api")
public class QuestionResource {

    private final Logger log = LoggerFactory.getLogger(QuestionResource.class);

    private static final String ENTITY_NAME = "question";

    private final QuestionRepository questionRepository;

    private final QuestionSearchRepository questionSearchRepository;

    private final AnswerMetaDataRepository answerMetaDataRepository;

    private final AnswerMetaDataSearchRepository answerMetaDataSearchRepository;

    public QuestionResource(QuestionRepository questionRepository, QuestionSearchRepository questionSearchRepository, AnswerMetaDataRepository answerMetaDataRepository, AnswerMetaDataSearchRepository answerMetaDataSearchRepository) {
        this.questionRepository = questionRepository;
        this.questionSearchRepository = questionSearchRepository;
        this.answerMetaDataRepository = answerMetaDataRepository;
        this.answerMetaDataSearchRepository = answerMetaDataSearchRepository;
    }

    /**
     * POST  /questions : Create a new question.
     *
     * @param question the question to create
     * @return the ResponseEntity with status 201 (Created) and with body the new question, or with status 400 (Bad Request) if the question has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/questions")
    @Timed
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) throws URISyntaxException {
        log.debug("REST request to save Question : {}", question);
        if (question.getId() != null) {
            throw new BadRequestAlertException("A new question cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Question result = questionRepository.save(question);
        questionSearchRepository.save(result);

        // Now save Answer Meta Data
        for (String answer : question.getAnswers()){
            AnswerMetaData answerMetaData = new AnswerMetaData();
            answerMetaData.setAnswer(answer);
            answerMetaData.associatedQuestion(question.getQuestion());
            answerMetaData.associatedQuestionID(result.getId());

            answerMetaDataRepository.save(answerMetaData);
            answerMetaDataSearchRepository.save(answerMetaData);
        }

        return ResponseEntity.created(new URI("/api/questions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    private void saveAnswerMetaData(){

    }

    /**
     * PUT  /questions : Updates an existing question.
     *
     * @param question the question to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated question,
     * or with status 400 (Bad Request) if the question is not valid,
     * or with status 500 (Internal Server Error) if the question couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/questions")
    @Timed
    public ResponseEntity<Question> updateQuestion(@RequestBody Question question) throws URISyntaxException {
        log.debug("REST request to update Question : {}", question);
        if (question.getId() == null) {
            return createQuestion(question);
        }
        Question result = questionRepository.save(question);
        questionSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, question.getId().toString()))
            .body(result);
    }

    /**
     * GET  /questions : get all the questions.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of questions in body
     */
    @GetMapping("/questions")
    @Timed
    public List<Question> getAllQuestions() {
        log.debug("REST request to get all Questions");
        List<Question> questions =  questionRepository.findAll();
            for (Question question : questions){

                List<String> answers = answerMetaDataRepository.findAllAnswersByQuestionID(question.getId());
                question.setAnswers(answers);

            }
            return questions;
        }

    /**
     * GET  /questions/:id : get the "id" question.
     *
     * @param id the id of the question to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the question, or with status 404 (Not Found)
     */
    @GetMapping("/questions/{id}")
    @Timed
    public ResponseEntity<Question> getQuestion(@PathVariable Long id) {
        log.debug("REST request to get Question : {}", id);
        Question question = questionRepository.findOne(id);
        List<String> answers = answerMetaDataRepository.findAllAnswersByQuestionID(question.getId());
        question.setAnswers(answers);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(question));
    }

    /**
     * DELETE  /questions/:id : delete the "id" question.
     *
     * @param id the id of the question to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/questions/{id}")
    @Timed
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        log.debug("REST request to delete Question : {}", id);
        questionRepository.delete(id);
        questionSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/questions?query=:query : search for the question corresponding
     * to the query.
     *
     * @param query the query of the question search
     * @return the result of the search
     */
    @GetMapping("/_search/questions")
    @Timed
    public List<Question> searchQuestions(@RequestParam String query) {
        log.debug("REST request to search Questions for query {}", query);
        return StreamSupport
            .stream(questionSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
