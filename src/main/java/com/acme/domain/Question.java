package com.acme.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

/**
 * A Question.
 */
@Entity
@Table(name = "question")
@Document(indexName = "question")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question")
    private String question;

    @Column(name = "mandatory")
    private Boolean mandatory;

    @Transient
    @JsonProperty
    private List<String> answers;

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    @ManyToOne
    private QuestionType questionType;

    @ManyToMany(mappedBy = "ids")
    @JsonIgnore
    private Set<Questionnaire> questionnaires = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public Question question(String question) {
        this.question = question;
        return this;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean isMandatory() {
        return mandatory;
    }

    public Question mandatory(Boolean mandatory) {
        this.mandatory = mandatory;
        return this;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public Question questionType(QuestionType questionType) {
        this.questionType = questionType;
        return this;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Set<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }

    public Question questionnaires(Set<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
        return this;
    }

    public Question addQuestionnaire(Questionnaire questionnaire) {
        this.questionnaires.add(questionnaire);
        questionnaire.getIds().add(this);
        return this;
    }

    public Question removeQuestionnaire(Questionnaire questionnaire) {
        this.questionnaires.remove(questionnaire);
        questionnaire.getIds().remove(this);
        return this;
    }

    public void setQuestionnaires(Set<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
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
        Question question = (Question) o;
        if (question.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), question.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", question='" + getQuestion() + "'" +
            ", mandatory='" + isMandatory() + "'" +
            "}";
    }
}
