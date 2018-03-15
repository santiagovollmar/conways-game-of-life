package ch.santiagovollmar.gol.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyManager {
  private static final Properties defaultProperties;
  public static final Properties properties;
  
  static {
    defaultProperties = new Properties();
    properties = new Properties(defaultProperties);
  }
  
  public static void readProperties() {
    try {
      defaultProperties.load(PropertyManager.class.getResourceAsStream("../configuration/default.properties"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }
}
