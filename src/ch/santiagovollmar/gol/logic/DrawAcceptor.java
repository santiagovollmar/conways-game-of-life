package ch.santiagovollmar.gol.logic;

import java.util.Collection;

public interface DrawAcceptor {
    public void fill(Point p);
    public void clear(Point p);

    public void fill(Collection<Point> p);
    public void clear(Collection<Point> p);
}
