package ch.santiagovollmar.gol.logic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Snippet implements Serializable {
    private Set<Point> scene;
    private String name;
    private String description;

    public Snippet(Set<Point> scene, String name, String description) {
        super();
        this.scene = scene;
        this.name = name.trim();
        this.description = description.trim();
    }

    public Snippet(Set<Point> scene) {
        this.scene = scene;
    }

    public Snippet(String name, String description) {
        this(null, name, description);
    }

    public Snippet(String name) {
        this(name, null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((scene == null) ? 0 : scene.hashCode());

        if (scene != null) {
            Point wrapper = new Point(0, 0);
            scene.forEach(point -> {
                wrapper.x ^= point.hashCode();
            });
            result = prime * result + wrapper.x;
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Snippet)) return false;
        Snippet other = (Snippet) obj;
        if (description == null) {
            if (other.description != null) return false;
        } else if (!description.equals(other.description)) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (scene == null) {
            if (other.scene != null) return false;
        } else if (!scene.equals(other.scene)) return false;
        return true;
    }

    public Set<Point> getScene() {
        return scene;
    }

    public void setScene(HashSet<Point> scene) {
        this.scene = scene;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
