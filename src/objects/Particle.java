package objects;

import functions.FunctionWithDerivatives;
import functions.Integrable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class Particle implements Comparable<Particle>, Integrable {
    private float x; // X-coordinate of the particle
    private float velocityX; // X-component of the velocity
    private final float mass; // Mass of the particle
    private final float radius; // Radius of the particle
    private final float limitVelocity;
    private final float tau;
    private final List<Float> addedForces;
    private final FunctionWithDerivatives rFunction;


    public Particle(float x, float velocityX, float mass, float radius, float limitVelocity, float tau) {
        this.x = x;
        this.velocityX = velocityX;
        this.mass = mass;
        this.radius = radius;
        this.limitVelocity = limitVelocity;
        this.tau = tau;

        this.addedForces = new ArrayList<>();
        rFunction = new FunctionWithDerivatives(this::r);

    }

    @Override
    public BiFunction<Float, Float, Float> getForceFunction() {
        float force = 0;
        for (float addedForce : addedForces) {
            force += addedForce;
        }
        float finalForce = force;
        return (r, v) -> (limitVelocity - v) / tau + finalForce;
    }

    @Override
    public FunctionWithDerivatives getrFunction() {
        return rFunction;
    }

    public int addForce(float force) {
        //add force to addedForces array and return the index of the added force
        int index = addedForces.size();
        addedForces.add(force);
        return index;
    }

    public void deleteForce(int index) {
        addedForces.remove(index);
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
        return Double.compare(this.x, otherParticle.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Particle particle = (Particle) obj;
        return Objects.equals(x, particle.x);
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

}
