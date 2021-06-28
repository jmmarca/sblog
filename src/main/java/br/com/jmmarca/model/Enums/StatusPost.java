package br.com.jmmarca.model.Enums;

public enum StatusPost {
    PENDING("PENDING"), PUBLISHED("PUBLISHED");

    private String name;

    StatusPost(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return name();
    }

    @Override
    public String toString() {
        return getName();
    }
}
