package se.su.inlupp;
import java.util.*;

public class DFSPathFinder<T> implements PathFinder<T> {


  //Empty constructor
  public DFSPathFinder() {

  };

  //Från Interface
  @Override
  public Path<T> findPath(Graph<T> graph, T from, T to) {

    //Security checks like in BFS.
    if (graph == null || from == null || to == null) {
      return null;
    }

    if (!graph.hasNode(from) || !graph.hasNode(to)) {
      return null;
    }

    if (from.equals(to)) {
      return new GraphPath(from, new ArrayList<>());
    }

    //Connections mapp, call method connect.
    Map<T, T> connections = new HashMap<>();
    connect(graph, from, null, connections);

    //If to does not exist there is no way
    if (!connections.containsKey(to)) {
      return null;
    }

    //Retrace the way back
    LinkedList<Edge<T>> edges = new LinkedList<>();
    T current = to;
    while (current != null && !current.equals(from)) {
      T previous = connections.get(current);
      Edge<T> edge = graph.getEdgeBetween(previous, current);
      edges.addFirst(edge);
      current = previous;
    }

    return new GraphPath(from, edges);

  }

  private void connect(Graph<T> graph, T to, T from, Map<T, T> connections) {
    connections.put(to, from);

    for (Edge<T> edge : graph.getEdgesFrom(to)) {
      T destination = edge.getDestination();
      if (!connections.containsKey(destination)) {
        connect(graph, destination, to, connections);
      }
    }
  }

  private class GraphPath implements Path<T> {
    private final T start;
    private final List<Edge<T>> edges;

    private GraphPath(T start, List<Edge<T>> edges) {
      this.start = start;
      this.edges = new ArrayList<>(edges);
    }


    //Helper methods
    public T getStart() {
      return start;
    }

    public T getEnd() {
      if (edges.isEmpty()) {
        return start;
      }
      return edges.get(edges.size() - 1).getDestination();
    }

    public int getTotalWeight() {
      int totalWeight = 0;
      for (Edge<T> edge : edges) {
        totalWeight += edge.getWeight();
      }
      return totalWeight;
    }

    public List<Edge<T>> getEdges() {
      return new ArrayList<>(edges);
    }

    public List<T> getNodes() {
      List<T> nodes = new ArrayList<>();
      nodes.add(start);
      for (Edge<T> edge : edges) {
        nodes.add(edge.getDestination());
      }
      return nodes;
    }

    public Iterator<Edge<T>> iterator() {
      return edges.iterator();
    }

    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Path from ")
              .append(getStart())
              .append(" to ")
              .append(getEnd())
              .append("\n");

      for (Edge<T> edge : edges) {
        builder.append(edge).append("\n");
      }

      builder.append("Total weight: ").append(getTotalWeight());
      return builder.toString();
    }

  }
}







