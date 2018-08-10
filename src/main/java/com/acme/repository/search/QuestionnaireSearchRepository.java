package com.acme.repository.search;

import com.acme.domain.Questionnaire;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Questionnaire entity.
 */
public interface QuestionnaireSearchRepository extends ElasticsearchRepository<Questionnaire, Long> {
}
