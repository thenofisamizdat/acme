package com.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.acme.domain.AnswerMetaData;

import com.acme.repository.AnswerMetaDataRepository;
import com.acme.repository.search.AnswerMetaDataSearchRepository;
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
 * REST controller for managing AnswerMetaData.
 */
@RestController
@RequestMapping("/api")
public class AnswerMetaDataResource {

    private final Logger log = LoggerFactory.getLogger(AnswerMetaDataResource.class);

    private static final String ENTITY_NAME = "answerMetaData";

    private final AnswerMetaDataRepository answerMetaDataRepository;

    private final AnswerMetaDataSearchRepository answerMetaDataSearchRepository;

    public AnswerMetaDataResource(AnswerMetaDataRepository answerMetaDataRepository, AnswerMetaDataSearchRepository answerMetaDataSearchRepository) {
        this.answerMetaDataRepository = answerMetaDataRepository;
        this.answerMetaDataSearchRepository = answerMetaDataSearchRepository;
    }

    /**
     * POST  /answer-meta-data : Create a new answerMetaData.
     *
     * @param answerMetaData the answerMetaData to create
     * @return the ResponseEntity with status 201 (Created) and with body the new answerMetaData, or with status 400 (Bad Request) if the answerMetaData has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/answer-meta-data")
    @Timed
    public ResponseEntity<AnswerMetaData> createAnswerMetaData(@RequestBody AnswerMetaData answerMetaData) throws URISyntaxException {
        log.debug("REST request to save AnswerMetaData : {}", answerMetaData);
        if (answerMetaData.getId() != null) {
            throw new BadRequestAlertException("A new answerMetaData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AnswerMetaData result = answerMetaDataRepository.save(answerMetaData);
        answerMetaDataSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/answer-meta-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /answer-meta-data : Updates an existing answerMetaData.
     *
     * @param answerMetaData the answerMetaData to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated answerMetaData,
     * or with status 400 (Bad Request) if the answerMetaData is not valid,
     * or with status 500 (Internal Server Error) if the answerMetaData couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/answer-meta-data")
    @Timed
    public ResponseEntity<AnswerMetaData> updateAnswerMetaData(@RequestBody AnswerMetaData answerMetaData) throws URISyntaxException {
        log.debug("REST request to update AnswerMetaData : {}", answerMetaData);
        if (answerMetaData.getId() == null) {
            return createAnswerMetaData(answerMetaData);
        }
        AnswerMetaData result = answerMetaDataRepository.save(answerMetaData);
        answerMetaDataSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, answerMetaData.getId().toString()))
            .body(result);
    }

    /**
     * GET  /answer-meta-data : get all the answerMetaData.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of answerMetaData in body
     */
    @GetMapping("/answer-meta-data")
    @Timed
    public List<AnswerMetaData> getAllAnswerMetaData() {
        log.debug("REST request to get all AnswerMetaData");
        return answerMetaDataRepository.findAll();
        }

    /**
     * GET  /answer-meta-data/:id : get the "id" answerMetaData.
     *
     * @param id the id of the answerMetaData to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the answerMetaData, or with status 404 (Not Found)
     */
    @GetMapping("/answer-meta-data/{id}")
    @Timed
    public ResponseEntity<AnswerMetaData> getAnswerMetaData(@PathVariable Long id) {
        log.debug("REST request to get AnswerMetaData : {}", id);
        AnswerMetaData answerMetaData = answerMetaDataRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(answerMetaData));
    }

    /**
     * DELETE  /answer-meta-data/:id : delete the "id" answerMetaData.
     *
     * @param id the id of the answerMetaData to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/answer-meta-data/{id}")
    @Timed
    public ResponseEntity<Void> deleteAnswerMetaData(@PathVariable Long id) {
        log.debug("REST request to delete AnswerMetaData : {}", id);
        answerMetaDataRepository.delete(id);
        answerMetaDataSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/answer-meta-data?query=:query : search for the answerMetaData corresponding
     * to the query.
     *
     * @param query the query of the answerMetaData search
     * @return the result of the search
     */
    @GetMapping("/_search/answer-meta-data")
    @Timed
    public List<AnswerMetaData> searchAnswerMetaData(@RequestParam String query) {
        log.debug("REST request to search AnswerMetaData for query {}", query);
        return StreamSupport
            .stream(answerMetaDataSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
