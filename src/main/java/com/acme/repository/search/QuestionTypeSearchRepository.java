package com.acme.repository.search;

import com.acme.domain.QuestionType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the QuestionType entity.
 */
public interface QuestionTypeSearchRepository extends ElasticsearchRepository<QuestionType, Long> {
}
