package br.com.jmmarca.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.Type;

import br.com.jmmarca.model.Enums.StatusPost;

@Entity
@Table
@JsonIgnoreProperties({ "comments", "photos" })
public class Post extends AbstractModel {

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String title;

    @Type(type = "text")
    private String summary;

    @Type(type = "text")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPost status = StatusPost.PENDENTE;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties("comments")
    private Collection<PostComment> comments = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties("photos")
    private Collection<Photo> photos = new ArrayList<>();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public StatusPost getStatus() {
        return status;
    }

    public void setStatus(StatusPost status) {
        this.status = status;
    }

    public Collection<PostComment> getComments() {
        return comments;
    }

    public void setComments(Collection<PostComment> comments) {
        this.comments = comments;
    }

    public Collection<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Collection<Photo> photos) {
        this.photos = photos;
    }

}
