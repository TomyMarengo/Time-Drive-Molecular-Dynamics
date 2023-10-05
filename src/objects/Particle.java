package objects;

import static utils.Integrator.factorial;

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
    private double r3;
    private double r3_last;
    private double r4;
    private double r4_last;
    private double r5;
    private double r5_last;
    private final double deltaT;


    public Particle(double mass, double radius, double limitVelocity, double tau, double r_0, double r1_0) {
        this(mass, radius, limitVelocity, tau, r_0, r1_0, 0.001);
    }

    public Particle(double mass, double radius, double limitVelocity, double tau, double r_0, double r1_0, double deltaT) {
        this.mass = mass;
        this.radius = radius;
        this.limitVelocity = limitVelocity;
        this.tau = tau;
        this.r_last = r_0 - deltaT * r1_0 + deltaT * deltaT * ((limitVelocity - r1_last) / (tau * mass) + addedForces / mass) / 2;
        this.r = r_0;
        this.r1_last = r1_0 - deltaT * ((limitVelocity - r1_last) / (tau * mass) + addedForces / mass);
        this.r1 = r1_0;
        this.r2_last = (limitVelocity - r1_last) / (tau * mass) + addedForces / mass;
        this.r2 = (limitVelocity - r1_0) / (tau * mass) + addedForces / mass;
        this.r3 = 0;
        this.r3_last = 0;
        this.r4 = 0;
        this.r4_last = 0;
        this.r5 = 0;
        this.r5_last = 0;
        this.deltaT = deltaT;
    }

    public void getNextGear() {
        double[] coefficents = {3.0 / 16.0, 251.0 / 360.0, 1.0, 11.0 / 18.0, 1.0 / 6.0, 1.0 / 60.0};
        double[] rp = {0, 0, 0, 0, 0, 0};
        double[] my_r = {this.r, this.r1, this.r2, this.r3, this.r4, this.r5};
        double[] my_r_last = {this.r_last, this.r1_last, this.r2_last, this.r3_last, this.r4_last, this.r5_last};

        for (int i = 0; i <= 5; i++) {
            for (int j = i; j <= 5; j++) {
                rp[i] = rp[i] + my_r_last[j] * Math.pow(deltaT, j - i) / factorial(j - i);
            }
        }

        double deltaA = (limitVelocity - rp[1]) / (tau * mass) + addedForces / mass - rp[2];
        double deltaR2 = deltaA * deltaT * deltaT / 2.0;

        for (int i = 0; i <= 5; i++) {
            my_r[i] = rp[i] + coefficents[i] * deltaR2 * factorial(i) / Math.pow(deltaT, i);
        }

        this.r_last = this.r;
        this.r = my_r[0];
        this.r1_last = this.r1;
        this.r1 = my_r[1];
        this.r2_last = this.r2;
        this.r2 = my_r[2];
        this.r3_last = this.r3;
        this.r3 = my_r[3];
        this.r4_last = this.r4;
        this.r4 = my_r[4];
        this.r5_last = this.r5;
        this.r5 = my_r[5];

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
