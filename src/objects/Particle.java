package objects;

import functions.FunctionWithDerivatives;
import functions.Integrable;

import java.util.*;
import java.util.function.BiFunction;

public class Particle implements Comparable<Particle>, Integrable {
    private final float id;
    private float x; // X-coordinate of the particle
    private Map<Float, Float> positionMap = new HashMap<>();
    private float velocityX; // X-component of the velocity
    private Map<Float, Float> velocityMap = new HashMap<>();
    private final float mass; // Mass of the particle
    private final float radius; // Radius of the particle
    private final float limitVelocity;
    private final float tau;
    private float addedForces = 0;
    private final FunctionWithDerivatives rFunction;


    public Particle(float id, float x, float velocityX, float mass, float radius, float limitVelocity, float tau) {
        this.id = id;
        this.x = x;
        this.velocityX = velocityX;
        this.mass = mass;
        this.radius = radius;
        this.limitVelocity = limitVelocity;
        this.tau = tau;
        rFunction = new FunctionWithDerivatives(this::r);
        rFunction.setDerivative(0, this::r);
        rFunction.setDerivative(1, this::r1);
        rFunction.setDerivative(2, this::r2);

    }

    @Override
    public BiFunction<Float, Float, Float> getForceFunction() {
        return (r, v) -> (limitVelocity - v) / tau + addedForces;
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

    private float r(float t) {
        return positionMap.getOrDefault(t, x);
    }

    private float r1(float t) {
        return velocityMap.getOrDefault(t, velocityX);
    }

    private float r2(float t) {
        return getForceFunction().apply(rFunction.calculateDerivative(0, t), rFunction.calculateDerivative(1, t)) / mass;
    }

    @Override
    public String toString() {
        return "{ " + x + ", " + velocityX + " }";
    }

    @Override
    public int compareTo(Particle otherParticle) {
        return Double.compare(this.id, otherParticle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Particle particle = (Particle) obj;
        return Objects.equals(id, particle.id);
    }

    public float getX() {
        return x;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public float getMass() {
        return mass;
    }


    public void setX(float t, float deltaT, float x) {
        this.x = x;
        positionMap.put(t, x);
        positionMap.entrySet().removeIf(entry -> entry.getKey() < t - 2*deltaT);
    }

    public void setVelocityX(float t, float deltaT, float velocityX) {
        this.velocityX = velocityX;
        velocityMap.put(t, velocityX);
        velocityMap.entrySet().removeIf(entry -> entry.getKey() < t - 2*deltaT);
    }

    public float getId() {
        return id;
    }

    public float getLimitVelocity() {
        return limitVelocity;
    }

    public float getTau() {
        return tau;
    }
}
