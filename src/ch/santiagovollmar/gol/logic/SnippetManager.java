package ch.santiagovollmar.gol.logic;

import ch.santiagovollmar.gol.util.PropertyManager;

import java.io.*;
import java.util.ArrayList;

public class SnippetManager {
    private static ArrayList<Snippet> snippets;

    public static void load() throws IOException {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(new File(PropertyManager.get("jcgol.home") + "snippets")));
            try {
                snippets = (ArrayList<Snippet>) in.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("File contained corrupt data");
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static void store() throws IOException {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(PropertyManager.get("jcgol.home") + "snippets")));
            out.writeObject(snippets);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static void add(Snippet snippet) {
        snippets.add(snippet);
    }

    public static Iterable<Snippet> get() {
        return snippets;
    }
}
