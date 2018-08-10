package com.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.acme.domain.QuestionType;

import com.acme.repository.QuestionTypeRepository;
import com.acme.repository.search.QuestionTypeSearchRepository;
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
 * REST controller for managing QuestionType.
 */
@RestController
@RequestMapping("/api")
public class QuestionTypeResource {

    private final Logger log = LoggerFactory.getLogger(QuestionTypeResource.class);

    private static final String ENTITY_NAME = "questionType";

    private final QuestionTypeRepository questionTypeRepository;

    private final QuestionTypeSearchRepository questionTypeSearchRepository;

    public QuestionTypeResource(QuestionTypeRepository questionTypeRepository, QuestionTypeSearchRepository questionTypeSearchRepository) {
        this.questionTypeRepository = questionTypeRepository;
        this.questionTypeSearchRepository = questionTypeSearchRepository;
    }

    /**
     * POST  /question-types : Create a new questionType.
     *
     * @param questionType the questionType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new questionType, or with status 400 (Bad Request) if the questionType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/question-types")
    @Timed
    public ResponseEntity<QuestionType> createQuestionType(@RequestBody QuestionType questionType) throws URISyntaxException {
        log.debug("REST request to save QuestionType : {}", questionType);
        if (questionType.getId() != null) {
            throw new BadRequestAlertException("A new questionType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        QuestionType result = questionTypeRepository.save(questionType);
        questionTypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/question-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /question-types : Updates an existing questionType.
     *
     * @param questionType the questionType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated questionType,
     * or with status 400 (Bad Request) if the questionType is not valid,
     * or with status 500 (Internal Server Error) if the questionType couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/question-types")
    @Timed
    public ResponseEntity<QuestionType> updateQuestionType(@RequestBody QuestionType questionType) throws URISyntaxException {
        log.debug("REST request to update QuestionType : {}", questionType);
        if (questionType.getId() == null) {
            return createQuestionType(questionType);
        }
        QuestionType result = questionTypeRepository.save(questionType);
        questionTypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, questionType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /question-types : get all the questionTypes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of questionTypes in body
     */
    @GetMapping("/question-types")
    @Timed
    public List<QuestionType> getAllQuestionTypes() {
        log.debug("REST request to get all QuestionTypes");
        return questionTypeRepository.findAll();
        }

    /**
     * GET  /question-types/:id : get the "id" questionType.
     *
     * @param id the id of the questionType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the questionType, or with status 404 (Not Found)
     */
    @GetMapping("/question-types/{id}")
    @Timed
    public ResponseEntity<QuestionType> getQuestionType(@PathVariable Long id) {
        log.debug("REST request to get QuestionType : {}", id);
        QuestionType questionType = questionTypeRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(questionType));
    }

    /**
     * DELETE  /question-types/:id : delete the "id" questionType.
     *
     * @param id the id of the questionType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/question-types/{id}")
    @Timed
    public ResponseEntity<Void> deleteQuestionType(@PathVariable Long id) {
        log.debug("REST request to delete QuestionType : {}", id);
        questionTypeRepository.delete(id);
        questionTypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/question-types?query=:query : search for the questionType corresponding
     * to the query.
     *
     * @param query the query of the questionType search
     * @return the result of the search
     */
    @GetMapping("/_search/question-types")
    @Timed
    public List<QuestionType> searchQuestionTypes(@RequestParam String query) {
        log.debug("REST request to search QuestionTypes for query {}", query);
        return StreamSupport
            .stream(questionTypeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
