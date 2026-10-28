package client.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Punto {
    @JsonProperty
    private double x;
    @JsonProperty
    private double y;

    public Punto() {
    }

    public Punto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
