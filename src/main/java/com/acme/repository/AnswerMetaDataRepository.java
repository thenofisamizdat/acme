package com.acme.repository;

import com.acme.domain.AnswerMetaData;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the AnswerMetaData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnswerMetaDataRepository extends JpaRepository<AnswerMetaData, Long> {

    @Query("select answer_meta_data from AnswerMetaData answer_meta_data where answer_meta_data.associatedQuestionID =:associated_question_id")
    List<AnswerMetaData> findAllByQuestionID(@Param("associated_question_id") Long id);

    @Query("select answer_meta_data.answer from AnswerMetaData answer_meta_data where answer_meta_data.associatedQuestionID =:associated_question_id")
    List<String> findAllAnswersByQuestionID(@Param("associated_question_id") Long id);
}
