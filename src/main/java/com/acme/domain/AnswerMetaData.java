package com.acme.domain;


import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A AnswerMetaData.
 */
@Entity
@Table(name = "answer_meta_data")
@Document(indexName = "answermetadata")
public class AnswerMetaData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "answer")
    private String answer;

    @Column(name = "associated_question")
    private String associatedQuestion;

    @Column(name = "associated_question_id")
    private Long associatedQuestionID;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public AnswerMetaData answer(String answer) {
        this.answer = answer;
        return this;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAssociatedQuestion() {
        return associatedQuestion;
    }

    public AnswerMetaData associatedQuestion(String associatedQuestion) {
        this.associatedQuestion = associatedQuestion;
        return this;
    }

    public void setAssociatedQuestion(String associatedQuestion) {
        this.associatedQuestion = associatedQuestion;
    }

    public Long getAssociatedQuestionID() {
        return associatedQuestionID;
    }

    public AnswerMetaData associatedQuestionID(Long associatedQuestionID) {
        this.associatedQuestionID = associatedQuestionID;
        return this;
    }

    public void setAssociatedQuestionID(Long associatedQuestionID) {
        this.associatedQuestionID = associatedQuestionID;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnswerMetaData answerMetaData = (AnswerMetaData) o;
        if (answerMetaData.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), answerMetaData.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AnswerMetaData{" +
            "id=" + getId() +
            ", answer='" + getAnswer() + "'" +
            ", associatedQuestion='" + getAssociatedQuestion() + "'" +
            ", associatedQuestionID=" + getAssociatedQuestionID() +
            "}";
    }
}
