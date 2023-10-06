package objects;

import functions.FunctionWithDerivatives;
import functions.Integrable;

import java.util.*;
import java.util.function.BiFunction;

public class Particle implements Integrable {
    private final double mass; // Mass of the particle
    private final double radius; // Radius of the particle
    private final double limitVelocity;
    private final double tau;
    private Map<Integer, Double> rMap = new HashMap<>();
    private Map<Integer, Double> r1Map = new HashMap<>();
    private Map<Integer, Double> r2Map = new HashMap<>();
    private Map<Integer, Double> r3Map = new HashMap<>();
    private Map<Integer, Double> r4Map = new HashMap<>();
    private Map<Integer, Double> r5Map = new HashMap<>();
    private double addedForces = 0;
    private final FunctionWithDerivatives rFunction;
    private final BiFunction<Double, Double, Double> forceFunction;

    public Particle(double mass, double radius, double limitVelocity, double tau, double r_0, double r1_0) {
        this(mass, radius, limitVelocity, tau, r_0, r1_0, 0.001);
    }

    public Particle(double mass, double radius, double limitVelocity, double tau, double r_0, double r1_0, double deltaT) {
        this.mass = mass;
        this.radius = radius;
        this.limitVelocity = limitVelocity;
        this.tau = tau;

        rFunction = new FunctionWithDerivatives(getrMap());


        forceFunction = (r, r1) -> (limitVelocity - r1) / tau + addedForces;

        rMap.put(-1, r_0);
        rMap.put(0, r_0);

        r1Map.put(-1, r1_0);
        r1Map.put(0, r1_0);

        r2Map.put(-1, forceFunction.apply(rMap.get(-1), r1Map.get(-1)) / mass);
        r2Map.put(0, forceFunction.apply(rMap.get(0), r1Map.get(0)) / mass);

        rFunction.setDerivative(1, getR1Map());
        rFunction.setDerivative(2, getR2Map());
    }

    @Override
    public BiFunction<Double, Double, Double> getForceFunction() {
        return forceFunction;
    }

    @Override
    public FunctionWithDerivatives getrFunction() {
        return rFunction;
    }

    public void addForce(double force) {
        addedForces += force;
    }

    public void removeForces() {
        addedForces = 0;
    }

    public Map<Integer, Double> getrMap() {
        return rMap;
    }

    public Map<Integer, Double> getR1Map() {
        return r1Map;
    }

    public Map<Integer, Double> getR2Map() {
        return r2Map;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public double getMass() {
        return mass;
    }

    public double getLimitVelocity() {
        return limitVelocity;
    }

    public double getTau() {
        return tau;
    }

    public void cleanMaps(int step) {
        rMap.remove(step - 2);
        r1Map.remove(step - 2);
        r2Map.remove(step - 2);
    }
}
