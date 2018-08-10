package com.acme.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A QuestionType.
 */
@Entity
@Table(name = "question_type")
@Document(indexName = "questiontype")
public class QuestionType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jhi_type")
    private String type;

    @OneToMany(mappedBy = "questionType")
    @JsonIgnore
    private Set<Question> ids = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public QuestionType type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Question> getIds() {
        return ids;
    }

    public QuestionType ids(Set<Question> questions) {
        this.ids = questions;
        return this;
    }

    public QuestionType addId(Question question) {
        this.ids.add(question);
        question.setQuestionType(this);
        return this;
    }

    public QuestionType removeId(Question question) {
        this.ids.remove(question);
        question.setQuestionType(null);
        return this;
    }

    public void setIds(Set<Question> questions) {
        this.ids = questions;
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
        QuestionType questionType = (QuestionType) o;
        if (questionType.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), questionType.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "QuestionType{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            "}";
    }
}
