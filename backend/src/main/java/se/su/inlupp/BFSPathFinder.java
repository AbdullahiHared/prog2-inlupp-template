package se.su.inlupp;

import java.util.*;

public class BFSPathFinder<T> implements PathFinder<T> {

    public BFSPathFinder() {

    }


    @Override
    public Path<T> findPath(Graph<T> graph, T from, T to) {
        if (graph == null || from == null || to == null) {
            return null;
        }

        if (!graph.hasNode(from) || !graph.hasNode(to)) {
            return null;
        }

        if (from.equals(to)) {
            return new GraphPath<>(from, new ArrayList<>());
        }

        Queue<T> queue = new LinkedList<>();
        Set<T> visited = new HashSet<>();
        Map<T, T> previousNode = new HashMap<>();
        Map<T, Edge<T>> previousEdge = new HashMap<>();

        queue.add(from);
        visited.add(from);

        while (!queue.isEmpty()) {
            T current = queue.poll();

            for (Edge<T> edge : graph.getEdgesFrom(current)) {
                T neighbour = edge.getDestination();

                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    previousNode.put(neighbour, current);
                    previousEdge.put(neighbour, edge);
                    queue.add(neighbour);

                    if (neighbour.equals(to)) {
                        return buildPath(from, to, previousNode, previousEdge);
                    }
                }
            }
        }
        return null;
    }

    private Path<T> buildPath(T from, T to, Map<T, T> previousNode, Map<T, Edge<T>> previousEdge) {
        List<Edge<T>> edges = new ArrayList<>();

        T current = to;

        while (!current.equals(from)) {
            Edge<T> edge = previousEdge.get(current);

            if (edge == null) {
                return null;
            }

            edges.add(edge);
            current = previousNode.get(current);
        }

        Collections.reverse(edges);
        return new GraphPath<>(from, edges);
    }

}

