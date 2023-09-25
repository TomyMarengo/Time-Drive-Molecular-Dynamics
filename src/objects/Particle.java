package objects;

import functions.FunctionWithDerivatives;
import functions.Integrable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class Particle implements Comparable<Particle>, Integrable {
    private final float id;
    private float x; // X-coordinate of the particle
    private float velocityX; // X-component of the velocity
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

    private float r(float t) { //TODO: Preguntar por esta funcion
        return 0;
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


    public void setX(float x) {
        this.x = x;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
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
