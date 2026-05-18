package se.su.inlupp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphPath<T> implements Path<T> {
    private T start;
    private List<Edge<T>> edges = new ArrayList<>();

    public GraphPath(T start, List<Edge<T>> edges) {
        this.edges = edges;
        this.start = start;
    }
    @Override
    public T getStart() {
        return start;
    }

    @Override
    public T getEnd() {
        T lastNode = edges.getLast().getDestination();
        if(lastNode == null) return start;
        return lastNode;
    }

    @Override
    public int getTotalWeight() {
        int totalWeight = 0;
        for(Edge<T> edge : edges) {
            totalWeight+= edge.getWeight();
        }

        return totalWeight;
    }

    @Override
    public List<Edge<T>> getEdges() {
        List<Edge<T>> edgesCopy = new ArrayList<>(edges);
        return edgesCopy;
    }

    @Override
    public List<T> getNodes() {
        List<T> nodes = new ArrayList<>();
        nodes.add(start);
        for (Edge<T> edge : edges) {
            nodes.add(edge.getDestination());
        }
        return nodes;
    }

    @Override
    public Iterator<Edge<T>> iterator() {
        return edges.iterator();
    }

    @Override
    public String toString() {
        return start + " " + getEnd() + getTotalWeight();
    }
}
