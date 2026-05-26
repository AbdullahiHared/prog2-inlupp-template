package se.su.inlupp;

import java.util.Collection;
import java.util.Set;

public class RouteModel {
    private final Graph<Place> graph = new ListGraph<>();
    private PathFinder<Place> pathFinder = new BFSPathFinder<>();
    private String imagePath = null;
    private boolean changed = false;

    public void addPlace(Place place) {
        graph.add(place);
        changed = true;
    }

    public void removePlace(Place place) {
        graph.remove(place);
        changed = true;
    }

    public Set<Place> getPlaces() {
        return graph.getNodes();
    }

    public boolean hasUnsavedChanges() {
        return changed;
    }

    public void resetChanges() {
        changed = false;
    }

    public void clear() {
        clearPlaces();
        changed = false;
        imagePath = null;
    }

    private void clearPlaces() {
        graph.getNodes().forEach(p -> graph.remove(p));
    }

    public void connect(Place a, Place b, String name, int weight) {
        graph.connect(a, b, name, weight);
        changed = true;
    }

    public Edge<Place> getEdgeBetween(Place a, Place b) {
        return graph.getEdgeBetween(a, b);
    }

    public Collection<Edge<Place>> getEdgesFrom(Place place) {
        return graph.getEdgesFrom(place);
    }

    public void setPathFinder(PathFinder<Place> newFinder) {
        this.pathFinder = newFinder;
    }

    public Path<Place> findPath(Place from, Place to) {
        return pathFinder.findPath(graph, from, to);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}
