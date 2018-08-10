package com.acme.repository;

import com.acme.domain.QuestionType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the QuestionType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionTypeRepository extends JpaRepository<QuestionType, Long> {

}
