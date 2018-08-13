package com.acme.repository;

import com.acme.domain.AnsweredQuestionnaire;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the AnsweredQuestionnaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnsweredQuestionnaireRepository extends JpaRepository<AnsweredQuestionnaire, Long> {
    @Query("select distinct answered_questionnaire from AnsweredQuestionnaire answered_questionnaire left join fetch answered_questionnaire.ids")
    List<AnsweredQuestionnaire> findAllWithEagerRelationships();

    @Query("select answered_questionnaire from AnsweredQuestionnaire answered_questionnaire left join fetch answered_questionnaire.ids where answered_questionnaire.id =:id")
    AnsweredQuestionnaire findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select distinct answered_questionnaire from AnsweredQuestionnaire answered_questionnaire where answered_questionnaire.questionnaireID =:questionnaire_id")
    List<AnsweredQuestionnaire> findAllByQuestionnaireID(@Param("questionnaire_id") Long questionnaire_id);
}
