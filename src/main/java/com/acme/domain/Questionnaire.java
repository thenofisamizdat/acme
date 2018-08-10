package com.acme.domain;


import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Questionnaire.
 */
@Entity
@Table(name = "questionnaire")
@Document(indexName = "questionnaire")
public class Questionnaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created")
    private ZonedDateTime created;

    @ManyToMany
    @JoinTable(name = "questionnaire_id",
               joinColumns = @JoinColumn(name="questionnaires_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="ids_id", referencedColumnName="id"))
    private Set<Question> ids = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Questionnaire title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public Questionnaire createdBy(Long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public Questionnaire created(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public Set<Question> getIds() {
        return ids;
    }

    public Questionnaire ids(Set<Question> questions) {
        this.ids = questions;
        return this;
    }

    public Questionnaire addId(Question question) {
        this.ids.add(question);
        question.getQuestionnaires().add(this);
        return this;
    }

    public Questionnaire removeId(Question question) {
        this.ids.remove(question);
        question.getQuestionnaires().remove(this);
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
        Questionnaire questionnaire = (Questionnaire) o;
        if (questionnaire.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), questionnaire.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Questionnaire{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", created='" + getCreated() + "'" +
            "}";
    }
}
