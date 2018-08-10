package com.acme.repository.search;

import com.acme.domain.Answer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Answer entity.
 */
public interface AnswerSearchRepository extends ElasticsearchRepository<Answer, Long> {
}
