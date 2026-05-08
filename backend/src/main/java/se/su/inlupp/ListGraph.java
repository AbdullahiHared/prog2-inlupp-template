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
    throw new UnsupportedOperationException("Unimplemented method 'remove'");
  }

  @Override
  public boolean hasNode(T node) {
    throw new UnsupportedOperationException("Unimplemented method 'hasNode'");
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {
    throw new UnsupportedOperationException("Unimplemented method 'connect'");
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

