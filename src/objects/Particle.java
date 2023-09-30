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
    private final BiFunction<Double, Double, Double> r3Function;
    private final BiFunction<Double, Double, Double> r4Function;
    private final BiFunction<Double, Double, Double> r5Function;

    public Particle(double mass, double radius, double limitVelocity, double tau, double r_0, double r1_0) {
        this(mass, radius, limitVelocity, tau, r_0, r1_0, 0.001);
    }

    public Particle(double mass, double radius, double limitVelocity, double tau, double r_0, double r1_0, double deltaT) {
        this.mass = mass;
        this.radius = radius;
        this.limitVelocity = limitVelocity;
        this.tau = tau;

        rFunction = new FunctionWithDerivatives(getrMap());

        rFunction.setDerivative(1, getR1Map());
        rFunction.setDerivative(2, getR2Map());
        rFunction.setDerivative(3, getR3Map());
        rFunction.setDerivative(4, getR4Map());
        rFunction.setDerivative(5, getR5Map());

        forceFunction = (r, r1) -> (limitVelocity - r1) / tau + addedForces;
        r3Function = (r1, r2) -> (-r2) / (tau * mass);
        r4Function = (r2, r3) -> (-r3) / (tau * mass);
        r5Function = (r3, r4) -> (-r4) / (tau * mass);

        rMap.put(-1, r_0 - deltaT * r1_0 + deltaT * deltaT * forceFunction.apply(r_0, r1_0) / (2 * mass));
        rMap.put(0, r_0);

        r1Map.put(-1, r1_0 - deltaT * forceFunction.apply(r_0, r1_0) / mass);
        r1Map.put(0, r1_0);

        r2Map.put(-1, forceFunction.apply(rMap.get(-1), r1Map.get(-1)) / mass);
        r2Map.put(0, forceFunction.apply(rMap.get(0), r1Map.get(0)) / mass);

        r3Map.put(-1, r3Function.apply(r1Map.get(-1), r2Map.get(-1)));
        r3Map.put(0, r3Function.apply(r1Map.get(0), r2Map.get(0)));

        r4Map.put(-1, r4Function.apply(r2Map.get(-1), r3Map.get(-1)));
        r4Map.put(0, r4Function.apply(r2Map.get(0), r3Map.get(0)));

        r5Map.put(-1, r5Function.apply(r3Map.get(-1), r4Map.get(-1)));
        r5Map.put(0, r5Function.apply(r3Map.get(0), r4Map.get(0)));
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

    public Map<Integer, Double> getR3Map() {
        return r3Map;
    }

    public Map<Integer, Double> getR4Map() {
        return r4Map;
    }

    public Map<Integer, Double> getR5Map() {
        return r5Map;
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
        r3Map.remove(step - 2);
        r4Map.remove(step - 2);
        r5Map.remove(step - 2);
    }
}
