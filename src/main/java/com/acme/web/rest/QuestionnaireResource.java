package com.acme.web.rest;

import com.acme.domain.Answer;
import com.acme.domain.AnsweredQuestionnaire;
import com.acme.domain.Question;
import com.acme.repository.*;
import com.acme.repository.search.AnswerSearchRepository;
import com.codahale.metrics.annotation.Timed;
import com.acme.domain.Questionnaire;

import com.acme.repository.search.QuestionnaireSearchRepository;
import com.acme.web.rest.errors.BadRequestAlertException;
import com.acme.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Questionnaire.
 */
@RestController
@RequestMapping("/api")
public class QuestionnaireResource {

    private final Logger log = LoggerFactory.getLogger(QuestionnaireResource.class);

    private static final String ENTITY_NAME = "questionnaire";

    private final QuestionnaireRepository questionnaireRepository;

    private final QuestionnaireSearchRepository questionnaireSearchRepository;

    private final AnsweredQuestionnaireRepository answeredQuestionnaireRepository;

    private final AnswerMetaDataRepository answerMetaDataRepository;

    private final UserRepository userRepository;

    private final AnswerRepository answerRepository;

    private final AnswerSearchRepository answerSearchRepository;

    public QuestionnaireResource(QuestionnaireRepository questionnaireRepository, QuestionnaireSearchRepository questionnaireSearchRepository, AnsweredQuestionnaireRepository answeredQuestionnaireRepository, AnswerMetaDataRepository answerMetaDataRepository, UserRepository userRepository, AnswerRepository answerRepository, AnswerSearchRepository answerSearchRepository) {
        this.questionnaireRepository = questionnaireRepository;
        this.questionnaireSearchRepository = questionnaireSearchRepository;
        this.answeredQuestionnaireRepository = answeredQuestionnaireRepository;
        this.answerMetaDataRepository = answerMetaDataRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.answerSearchRepository = answerSearchRepository;
    }

    /**
     * POST  /questionnaires : Create a new questionnaire.
     *
     * @param questionnaire the questionnaire to create
     * @return the ResponseEntity with status 201 (Created) and with body the new questionnaire, or with status 400 (Bad Request) if the questionnaire has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/questionnaires")
    @Timed
    public ResponseEntity<Questionnaire> createQuestionnaire(@RequestBody Questionnaire questionnaire) throws URISyntaxException {
        log.debug("REST request to save Questionnaire : {}", questionnaire);
        if (questionnaire.getId() != null) {
            throw new BadRequestAlertException("A new questionnaire cannot already have an ID", ENTITY_NAME, "idexists");
        }

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        questionnaire.setCreated(ZonedDateTime.now());
        questionnaire.setCreatedBy(userRepository.findOneByLogin(user).get().getId());
        Questionnaire result = questionnaireRepository.save(questionnaire);
        questionnaireSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/questionnaires/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /questionnaires : Updates an existing questionnaire.
     *
     * @param questionnaire the questionnaire to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated questionnaire,
     * or with status 400 (Bad Request) if the questionnaire is not valid,
     * or with status 500 (Internal Server Error) if the questionnaire couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/questionnaires")
    @Timed
    public ResponseEntity<Questionnaire> updateQuestionnaire(@RequestBody Questionnaire questionnaire) throws URISyntaxException {
        log.debug("REST request to update Questionnaire : {}", questionnaire);
        if (questionnaire.getId() == null) {
            return createQuestionnaire(questionnaire);
        }

        try {
            String user = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!user.equals("admin")) {
                for (Question question : questionnaire.getIds()) {
                    Answer answer = new Answer();
                    answer.setAnswerText(question.getAnswers().get(0));
                    answer.setAssociatedQuestion(question.getQuestion());
                    answer.setAssociatedQuestionID(question.getId());
                    answer.setAnsweredDate(ZonedDateTime.now());
                    answerRepository.save(answer);
                    answerSearchRepository.save(answer);

                }
            }
        }
        catch(Exception e){}

        Questionnaire result = questionnaireRepository.save(questionnaire);
        questionnaireSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, questionnaire.getId().toString()))
            .body(result);
    }

    /**
     * GET  /questionnaires : get all the questionnaires.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of questionnaires in body
     */
    @GetMapping("/questionnaires")
    @Timed
    public List<Questionnaire> getAllQuestionnaires() {
        log.debug("REST request to get all Questionnaires");
        List<Questionnaire> questionnaireList = questionnaireRepository.findAllWithEagerRelationships();
        for (Questionnaire questionnaire : questionnaireList){
            try {
                questionnaire.setUser(userRepository.findOne(questionnaire.getCreatedBy()));
                questionnaire.setAnsweredQuestionnaires(answeredQuestionnaireRepository.findAllByQuestionnaireID(questionnaire.getId()));
                for (Question question : questionnaire.getIds()){
                    try {
                        question.setAnswerOptions(answerMetaDataRepository.findAllAnswersByQuestionID(question.getId()));
                    }
                    catch(Exception e){}
                }
            }
            catch(Exception e){}
        }
        return questionnaireList;
    }

    /**
     * GET  /questionnaires/:id : get the "id" questionnaire.
     *
     * @param id the id of the questionnaire to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the questionnaire, or with status 404 (Not Found)
     */
    @GetMapping("/questionnaires/{id}")
    @Timed
    public ResponseEntity<Questionnaire> getQuestionnaire(@PathVariable Long id) {
        log.debug("REST request to get Questionnaire : {}", id);
        Questionnaire questionnaire = questionnaireRepository.findOneWithEagerRelationships(id);

        try {
            questionnaire.setUser(userRepository.findOne(questionnaire.getCreatedBy()));
            questionnaire.setAnsweredQuestionnaires(answeredQuestionnaireRepository.findAllByQuestionnaireID(questionnaire.getId()));
        }
        catch(Exception e){}

        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(questionnaire));
    }

    /**
     * DELETE  /questionnaires/:id : delete the "id" questionnaire.
     *
     * @param id the id of the questionnaire to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/questionnaires/{id}")
    @Timed
    public ResponseEntity<Void> deleteQuestionnaire(@PathVariable Long id) {
        log.debug("REST request to delete Questionnaire : {}", id);
        questionnaireRepository.delete(id);
        questionnaireSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/questionnaires?query=:query : search for the questionnaire corresponding
     * to the query.
     *
     * @param query the query of the questionnaire search
     * @return the result of the search
     */
    @GetMapping("/_search/questionnaires")
    @Timed
    public List<Questionnaire> searchQuestionnaires(@RequestParam String query) {
        log.debug("REST request to search Questionnaires for query {}", query);
        return StreamSupport
            .stream(questionnaireSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
