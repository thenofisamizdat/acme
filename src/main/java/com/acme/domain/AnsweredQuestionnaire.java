package com.acme.domain;


import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A AnsweredQuestionnaire.
 */
@Entity
@Table(name = "answered_questionnaire")
@Document(indexName = "answeredquestionnaire")
public class AnsweredQuestionnaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "questionnaire_id")
    private Long questionnaireID;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "answered_by")
    private Long answeredBy;

    @Column(name = "answered_date")
    private ZonedDateTime answeredDate;

    @ManyToMany
    @JoinTable(name = "answered_questionnaire_id",
               joinColumns = @JoinColumn(name="answered_questionnaires_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="ids_id", referencedColumnName="id"))
    private Set<Answer> ids = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionnaireID() {
        return questionnaireID;
    }

    public AnsweredQuestionnaire questionnaireID(Long questionnaireID) {
        this.questionnaireID = questionnaireID;
        return this;
    }

    public void setQuestionnaireID(Long questionnaireID) {
        this.questionnaireID = questionnaireID;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public AnsweredQuestionnaire createdBy(Long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getAnsweredBy() {
        return answeredBy;
    }

    public AnsweredQuestionnaire answeredBy(Long answeredBy) {
        this.answeredBy = answeredBy;
        return this;
    }

    public void setAnsweredBy(Long answeredBy) {
        this.answeredBy = answeredBy;
    }

    public ZonedDateTime getAnsweredDate() {
        return answeredDate;
    }

    public AnsweredQuestionnaire answeredDate(ZonedDateTime answeredDate) {
        this.answeredDate = answeredDate;
        return this;
    }

    public void setAnsweredDate(ZonedDateTime answeredDate) {
        this.answeredDate = answeredDate;
    }

    public Set<Answer> getIds() {
        return ids;
    }

    public AnsweredQuestionnaire ids(Set<Answer> answers) {
        this.ids = answers;
        return this;
    }

    public AnsweredQuestionnaire addId(Answer answer) {
        this.ids.add(answer);
        answer.getAnsweredQuestionnaires().add(this);
        return this;
    }

    public AnsweredQuestionnaire removeId(Answer answer) {
        this.ids.remove(answer);
        answer.getAnsweredQuestionnaires().remove(this);
        return this;
    }

    public void setIds(Set<Answer> answers) {
        this.ids = answers;
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
        AnsweredQuestionnaire answeredQuestionnaire = (AnsweredQuestionnaire) o;
        if (answeredQuestionnaire.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), answeredQuestionnaire.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AnsweredQuestionnaire{" +
            "id=" + getId() +
            ", questionnaireID=" + getQuestionnaireID() +
            ", createdBy=" + getCreatedBy() +
            ", answeredBy=" + getAnsweredBy() +
            ", answeredDate='" + getAnsweredDate() + "'" +
            "}";
    }
}
