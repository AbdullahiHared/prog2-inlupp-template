package se.su.inlupp;

public class EdgeBase<T> implements Edge<T> {
    private int edgeWeight;
    private String edgeName;
    private T destination;
    public EdgeBase(int weight, String edgeName,T destination) {
        this.edgeWeight = weight;
        this.edgeName = edgeName;
        this.destination = destination;
    }
    @Override
    public int getWeight() {
        return this.edgeWeight;
    }

    @Override
    public void setWeight(int weight) {
        if(weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negativ");
        } else {
            this.edgeWeight = weight;
        }
    }

    @Override
    public T getDestination() {
        return this.destination;
    }

    @Override
    public String getName() {
        return this.edgeName;
    }

    @Override
    public String toString() {
        return "Edge weight " + this.edgeWeight + " Name " + this.edgeName + " Destination " + this.destination;
    }
}
