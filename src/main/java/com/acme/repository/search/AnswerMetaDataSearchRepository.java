package com.acme.repository.search;

import com.acme.domain.AnswerMetaData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the AnswerMetaData entity.
 */
public interface AnswerMetaDataSearchRepository extends ElasticsearchRepository<AnswerMetaData, Long> {
}
