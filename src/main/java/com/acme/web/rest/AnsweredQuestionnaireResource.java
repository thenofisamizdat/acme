package com.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.acme.domain.AnsweredQuestionnaire;

import com.acme.repository.AnsweredQuestionnaireRepository;
import com.acme.repository.search.AnsweredQuestionnaireSearchRepository;
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
 * REST controller for managing AnsweredQuestionnaire.
 */
@RestController
@RequestMapping("/api")
public class AnsweredQuestionnaireResource {

    private final Logger log = LoggerFactory.getLogger(AnsweredQuestionnaireResource.class);

    private static final String ENTITY_NAME = "answeredQuestionnaire";

    private final AnsweredQuestionnaireRepository answeredQuestionnaireRepository;

    private final AnsweredQuestionnaireSearchRepository answeredQuestionnaireSearchRepository;

    public AnsweredQuestionnaireResource(AnsweredQuestionnaireRepository answeredQuestionnaireRepository, AnsweredQuestionnaireSearchRepository answeredQuestionnaireSearchRepository) {
        this.answeredQuestionnaireRepository = answeredQuestionnaireRepository;
        this.answeredQuestionnaireSearchRepository = answeredQuestionnaireSearchRepository;
    }

    /**
     * POST  /answered-questionnaires : Create a new answeredQuestionnaire.
     *
     * @param answeredQuestionnaire the answeredQuestionnaire to create
     * @return the ResponseEntity with status 201 (Created) and with body the new answeredQuestionnaire, or with status 400 (Bad Request) if the answeredQuestionnaire has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/answered-questionnaires")
    @Timed
    public ResponseEntity<AnsweredQuestionnaire> createAnsweredQuestionnaire(@RequestBody AnsweredQuestionnaire answeredQuestionnaire) throws URISyntaxException {
        log.debug("REST request to save AnsweredQuestionnaire : {}", answeredQuestionnaire);
        if (answeredQuestionnaire.getId() != null) {
            throw new BadRequestAlertException("A new answeredQuestionnaire cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AnsweredQuestionnaire result = answeredQuestionnaireRepository.save(answeredQuestionnaire);
        answeredQuestionnaireSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/answered-questionnaires/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /answered-questionnaires : Updates an existing answeredQuestionnaire.
     *
     * @param answeredQuestionnaire the answeredQuestionnaire to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated answeredQuestionnaire,
     * or with status 400 (Bad Request) if the answeredQuestionnaire is not valid,
     * or with status 500 (Internal Server Error) if the answeredQuestionnaire couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/answered-questionnaires")
    @Timed
    public ResponseEntity<AnsweredQuestionnaire> updateAnsweredQuestionnaire(@RequestBody AnsweredQuestionnaire answeredQuestionnaire) throws URISyntaxException {
        log.debug("REST request to update AnsweredQuestionnaire : {}", answeredQuestionnaire);
        if (answeredQuestionnaire.getId() == null) {
            return createAnsweredQuestionnaire(answeredQuestionnaire);
        }
        AnsweredQuestionnaire result = answeredQuestionnaireRepository.save(answeredQuestionnaire);
        answeredQuestionnaireSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, answeredQuestionnaire.getId().toString()))
            .body(result);
    }

    /**
     * GET  /answered-questionnaires : get all the answeredQuestionnaires.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of answeredQuestionnaires in body
     */
    @GetMapping("/answered-questionnaires")
    @Timed
    public List<AnsweredQuestionnaire> getAllAnsweredQuestionnaires() {
        log.debug("REST request to get all AnsweredQuestionnaires");
        return answeredQuestionnaireRepository.findAllWithEagerRelationships();
        }

    /**
     * GET  /answered-questionnaires/:id : get the "id" answeredQuestionnaire.
     *
     * @param id the id of the answeredQuestionnaire to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the answeredQuestionnaire, or with status 404 (Not Found)
     */
    @GetMapping("/answered-questionnaires/{id}")
    @Timed
    public ResponseEntity<AnsweredQuestionnaire> getAnsweredQuestionnaire(@PathVariable Long id) {
        log.debug("REST request to get AnsweredQuestionnaire : {}", id);
        AnsweredQuestionnaire answeredQuestionnaire = answeredQuestionnaireRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(answeredQuestionnaire));
    }

    /**
     * DELETE  /answered-questionnaires/:id : delete the "id" answeredQuestionnaire.
     *
     * @param id the id of the answeredQuestionnaire to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/answered-questionnaires/{id}")
    @Timed
    public ResponseEntity<Void> deleteAnsweredQuestionnaire(@PathVariable Long id) {
        log.debug("REST request to delete AnsweredQuestionnaire : {}", id);
        answeredQuestionnaireRepository.delete(id);
        answeredQuestionnaireSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/answered-questionnaires?query=:query : search for the answeredQuestionnaire corresponding
     * to the query.
     *
     * @param query the query of the answeredQuestionnaire search
     * @return the result of the search
     */
    @GetMapping("/_search/answered-questionnaires")
    @Timed
    public List<AnsweredQuestionnaire> searchAnsweredQuestionnaires(@RequestParam String query) {
        log.debug("REST request to search AnsweredQuestionnaires for query {}", query);
        return StreamSupport
            .stream(answeredQuestionnaireSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
