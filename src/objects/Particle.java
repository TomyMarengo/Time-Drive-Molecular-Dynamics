package objects;

import functions.FunctionWithDerivatives;
import functions.Integrable;

import java.util.*;
import java.util.function.BiFunction;

public class Particle implements Integrable {
    private final float mass; // Mass of the particle
    private final float radius; // Radius of the particle
    private final float limitVelocity;
    private final float tau;
    private Map<Integer, Float> rMap = new HashMap<>();
    private Map<Integer, Float> r1Map = new HashMap<>();
    private Map<Integer, Float> r2Map = new HashMap<>();
    private Map<Integer, Float> r3Map = new HashMap<>();
    private Map<Integer, Float> r4Map = new HashMap<>();
    private Map<Integer, Float> r5Map = new HashMap<>();
    private float addedForces = 0;
    private final FunctionWithDerivatives rFunction;
    private final BiFunction<Float, Float, Float> forceFunction;


    public Particle(float mass, float radius, float limitVelocity, float tau, float r_0, float r1_0) {
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

        rMap.put(-1, r_0);
        rMap.put(0, r_0);

        r1Map.put(-1, r1_0);
        r1Map.put(0, r1_0);

        float r2_0 = getForceFunction().apply(r_0, r1_0) / mass;
        r2Map.put(-1, r2_0);
        r2Map.put(0, r2_0);

        float r3_0 = 0;
        r3Map.put(-1, r3_0);
        r3Map.put(0, r3_0);

        float r4_0 = 0;
        r4Map.put(-1, r4_0);
        r4Map.put(0, r4_0);

        float r5_0 = 0;
        r5Map.put(-1, r5_0);
        r5Map.put(0, r5_0);
    }

    @Override
    public BiFunction<Float, Float, Float> getForceFunction() {
        return forceFunction;
    }

    @Override
    public FunctionWithDerivatives getrFunction() {
        return rFunction;
    }

    public void addForce(float force) {
        addedForces += force;
    }

    public void removeForces() {
        addedForces = 0;
    }

    public Map<Integer, Float> getrMap() {
        return rMap;
    }

    public Map<Integer, Float> getR1Map() {
        return r1Map;
    }

    public Map<Integer, Float> getR2Map() {
        return r2Map;
    }

    public Map<Integer, Float> getR3Map() {
        return r3Map;
    }

    public Map<Integer, Float> getR4Map() {
        return r4Map;
    }

    public Map<Integer, Float> getR5Map() {
        return r5Map;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public float getMass() {
        return mass;
    }

    public float getLimitVelocity() {
        return limitVelocity;
    }

    public float getTau() {
        return tau;
    }
}
