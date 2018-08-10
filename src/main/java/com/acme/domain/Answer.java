package com.acme.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Answer.
 */
@Entity
@Table(name = "answer")
@Document(indexName = "answer")
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "answer_text", nullable = false)
    private String answerText;

    @Column(name = "associated_question")
    private String associatedQuestion;

    @Column(name = "associated_question_id")
    private Long associatedQuestionID;

    @Column(name = "user_id")
    private Long userID;

    @Column(name = "answered_date")
    private ZonedDateTime answeredDate;

    @ManyToMany(mappedBy = "ids")
    @JsonIgnore
    private Set<AnsweredQuestionnaire> answeredQuestionnaires = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public Answer answerText(String answerText) {
        this.answerText = answerText;
        return this;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getAssociatedQuestion() {
        return associatedQuestion;
    }

    public Answer associatedQuestion(String associatedQuestion) {
        this.associatedQuestion = associatedQuestion;
        return this;
    }

    public void setAssociatedQuestion(String associatedQuestion) {
        this.associatedQuestion = associatedQuestion;
    }

    public Long getAssociatedQuestionID() {
        return associatedQuestionID;
    }

    public Answer associatedQuestionID(Long associatedQuestionID) {
        this.associatedQuestionID = associatedQuestionID;
        return this;
    }

    public void setAssociatedQuestionID(Long associatedQuestionID) {
        this.associatedQuestionID = associatedQuestionID;
    }

    public Long getUserID() {
        return userID;
    }

    public Answer userID(Long userID) {
        this.userID = userID;
        return this;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public ZonedDateTime getAnsweredDate() {
        return answeredDate;
    }

    public Answer answeredDate(ZonedDateTime answeredDate) {
        this.answeredDate = answeredDate;
        return this;
    }

    public void setAnsweredDate(ZonedDateTime answeredDate) {
        this.answeredDate = answeredDate;
    }

    public Set<AnsweredQuestionnaire> getAnsweredQuestionnaires() {
        return answeredQuestionnaires;
    }

    public Answer answeredQuestionnaires(Set<AnsweredQuestionnaire> answeredQuestionnaires) {
        this.answeredQuestionnaires = answeredQuestionnaires;
        return this;
    }

    public Answer addAnsweredQuestionnaire(AnsweredQuestionnaire answeredQuestionnaire) {
        this.answeredQuestionnaires.add(answeredQuestionnaire);
        answeredQuestionnaire.getIds().add(this);
        return this;
    }

    public Answer removeAnsweredQuestionnaire(AnsweredQuestionnaire answeredQuestionnaire) {
        this.answeredQuestionnaires.remove(answeredQuestionnaire);
        answeredQuestionnaire.getIds().remove(this);
        return this;
    }

    public void setAnsweredQuestionnaires(Set<AnsweredQuestionnaire> answeredQuestionnaires) {
        this.answeredQuestionnaires = answeredQuestionnaires;
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
        Answer answer = (Answer) o;
        if (answer.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), answer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Answer{" +
            "id=" + getId() +
            ", answerText='" + getAnswerText() + "'" +
            ", associatedQuestion='" + getAssociatedQuestion() + "'" +
            ", associatedQuestionID=" + getAssociatedQuestionID() +
            ", userID=" + getUserID() +
            ", answeredDate='" + getAnsweredDate() + "'" +
            "}";
    }
}
