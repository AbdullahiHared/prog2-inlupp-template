package se.su.inlupp;

public class Place {
    private final String name;
    private double x;
    private double y;

    // konstruktör
    public Place(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    // getters and setters
    public void setPosition(double x, double y) {
        this.y = y;
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
