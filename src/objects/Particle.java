package objects;

public class Particle {
    private final double mass; // Mass of the particle
    private final double radius; // Radius of the particle
    private final double limitVelocity;
    private final double tau;
    private double addedForces = 0;
    private double r;
    private double r_last;
    private double r1;
    private double r1_last;
    private double r2;
    private double r2_last;
    private final double deltaT;


    public Particle(double mass, double radius, double limitVelocity, double tau, double r_0, double r1_0) {
        this(mass, radius, limitVelocity, tau, r_0, r1_0, 0.001);
    }

    public Particle(double mass, double radius, double limitVelocity, double tau, double r_0, double r1_0, double deltaT) {
        this.mass = mass;
        this.radius = radius;
        this.limitVelocity = limitVelocity;
        this.tau = tau;
        this.r_last = r_0;
        this.r = r_0;
        this.r1_last = r1_0;
        this.r1 = r1_0;
        this.r2_last = (limitVelocity - r1_last) / (tau * mass) + addedForces / mass;
        this.r2 = (limitVelocity - r1_0) / (tau * mass) + addedForces / mass;
        this.deltaT = deltaT;
    }

    public void getNextBeeman() {
        r_last = r;
        r = r + r1 * deltaT + (2.0 / 3.0) * r2 * deltaT * deltaT - (1.0 / 6.0) * r2_last * deltaT * deltaT;
        r1_last = r1;
        double r1_predicted = r1 + (3.0 / 2.0) * r2 * deltaT - (1.0 / 2.0) * r2_last * deltaT;

        double r2_next = (limitVelocity - r1_predicted) / (tau * mass) + addedForces / mass;
        r1 = r1 + (1.0 / 3.0) * r2_next * deltaT + (5.0 / 6.0) * r2 * deltaT - (1.0 / 6.0) * r2_last * deltaT;
        r2_last = r2;
        r2 = r2_next;

        addedForces = 0;
    }

    public void addForce(double force) {
        addedForces += force;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public double getLimitVelocity() {
        return limitVelocity;
    }

    public double getTau() {
        return tau;
    }

    public double getAddedForces() {
        return addedForces;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getR_last() {
        return r_last;
    }

    public double getR1() {
        return r1;
    }

    public double getR1_last() {
        return r1_last;
    }

    public double getR2() {
        return r2;
    }

    public double getR2_last() {
        return r2_last;
    }
}
