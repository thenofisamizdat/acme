package com.acme.repository.search;

import com.acme.domain.AnsweredQuestionnaire;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the AnsweredQuestionnaire entity.
 */
public interface AnsweredQuestionnaireSearchRepository extends ElasticsearchRepository<AnsweredQuestionnaire, Long> {
}
