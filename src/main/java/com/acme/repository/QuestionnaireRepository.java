package com.acme.repository;

import com.acme.domain.Questionnaire;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the Questionnaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {
    @Query("select distinct questionnaire from Questionnaire questionnaire left join fetch questionnaire.ids")
    List<Questionnaire> findAllWithEagerRelationships();

    @Query("select questionnaire from Questionnaire questionnaire left join fetch questionnaire.ids where questionnaire.id =:id")
    Questionnaire findOneWithEagerRelationships(@Param("id") Long id);

}
