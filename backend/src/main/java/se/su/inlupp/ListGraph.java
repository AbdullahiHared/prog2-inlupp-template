package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {
  private Map<T, List<Edge<T>>> edgeNodes = new HashMap<>();
  @Override
  public void add(T node) {
    edgeNodes.computeIfAbsent(node, e -> new ArrayList<Edge<T>>()); // save node if absent.
  }

  @Override
  public void remove(T node) {
    if(edgeNodes.containsKey(node)) {
      for(T n: edgeNodes.keySet()) {
        List<Edge<T>> edgeList = edgeNodes.get(n); // get edges connecting to n
        List<Edge<T>> edgesToRemove = new ArrayList<>();
        for(Edge<T> edge : edgeList) {
          if(edge.getDestination().equals(node)) {
            edgesToRemove.add(edge); //
          }
        }

        // Remove edges connected to the given node
        for(Edge<T> edge : edgesToRemove) {
          edgeList.remove(edge);
        }
      }
      edgeNodes.remove(node);
    } else {
      throw new NoSuchElementException("given node was not found");
    }
  }

  @Override
  public boolean hasNode(T node) {
    return edgeNodes.containsKey(node);
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {
    if(!hasNode(node1) || !hasNode(node2)) throw new NoSuchElementException("Node is missing");
    if(weight < 0) throw new IllegalArgumentException("Weight cannot be negative");
    Edge<T> edgesBetween = getEdgeBetween(node1, node2);
    if(edgesBetween != null) {
      // create new Edges
      EdgeBase<T> firstEdge = new EdgeBase(weight, name, node2);
      EdgeBase<T> secondEdge = new EdgeBase(weight, name, node1);
      // get Edges
      List<Edge<T>> node1Edges = edgeNodes.get(node1);
      List<Edge<T>> node2Edges = edgeNodes.get(node2);

      // update nodes edges
      node1Edges.add(firstEdge);
      node2Edges.add(secondEdge);
    } else {
      throw new IllegalStateException();
    }

  }

  @Override
  public void disconnect(T node1, T node2) {
    throw new UnsupportedOperationException("Unimplemented method 'disconnect'");
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    throw new UnsupportedOperationException("Unimplemented method 'setConnectionWeight'");
  }

  @Override
  public Set<T> getNodes() {
    throw new UnsupportedOperationException("Unimplemented method 'getNodes'");
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    throw new UnsupportedOperationException("Unimplemented method 'getEdgesFrom'");
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    throw new UnsupportedOperationException("Unimplemented method 'getEdgeBetween'");
  }

  @Override
  public Iterator<T> iterator() {
    throw new UnsupportedOperationException("Unimplemented method 'iterator'");
  }
}



