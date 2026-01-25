package com.taskmanagement.model;

import java.util.Objects;
import com.taskmanagement.utils.ColorValidator;

public class Label {
    private Long id;
    private String name;
    private String color = "#007BFF";

    public Label() {}

    public Label(String name) { this.name = name; }

    public Label(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getColorOrDefault() {
        return ColorValidator.getLabelColorOrDefault(color);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Label label)) return false;
        return Objects.equals(id, label.id) || Objects.equals(name, label.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}