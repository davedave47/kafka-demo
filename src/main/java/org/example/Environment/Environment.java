package org.example.Environment;

public class Environment {
    private Air air;
    private Water water;
    private Earth earth;
    public Environment(Air air, Water water, Earth earth) {
        this.air = air;
        this.water = water;
        this.earth = earth;
    }
    public void setEarth(Earth earth) {
        this.earth = earth;
    }

    public Air getAir() {
        return air;
    }

    public Water getWater() {
        return water;
    }

    public Earth getEarth() {
        return earth;
    }

    @Override
    public String toString() {
        return "Environment{" +
                "air=" + air.toString() +
                ", water=" + water.toString() +
                ", earth=" + earth.toString() +
                '}';
    }
}
