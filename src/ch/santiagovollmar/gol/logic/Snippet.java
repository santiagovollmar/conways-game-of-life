package ch.santiagovollmar.gol.logic;

import java.util.HashSet;

public class Snippet {
  private HashSet<Point> scene = new HashSet<>();
  private String name;
  private String description;
  
  public Snippet(HashSet<Point> scene, String name, String description) {
    super();
    this.scene = scene;
    this.name = name.trim();
    this.description = description.trim();
  }
  
  public Snippet(String name, String description) {
    this(null, name, description);
  }
  
  public Snippet(String name) {
    this(name, null);
  }

  public HashSet<Point> getScene() {
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
