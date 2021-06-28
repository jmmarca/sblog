package br.com.jmmarca.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Classe Abstrata para herança e economia de código.
 *
 */
@MappedSuperclass
public abstract class AbstractModel implements Comparable<AbstractModel>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private ZonedDateTime dhCriacao;

    @Column(nullable = false)
    private ZonedDateTime dhModificacao;

    @PrePersist
    public void prePersist() {
        dhCriacao = dhModificacao = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        dhModificacao = ZonedDateTime.now();
    }

    @Override
    public int compareTo(AbstractModel o) {
        return this.getId().compareTo(o.getId());
    }

    public boolean equals(Object ojb) {
        if (ojb == null || ojb.getClass() != this.getClass()) {
            return false;
        }

        return this.getId().equals(((AbstractModel) ojb).getId());
    }

    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedAt() {
        return dhCriacao;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.dhCriacao = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return dhModificacao;
    }

    public void setUpdatedAt(ZonedDateTime dhModificacao) {
        this.dhModificacao = dhModificacao;
    }
}