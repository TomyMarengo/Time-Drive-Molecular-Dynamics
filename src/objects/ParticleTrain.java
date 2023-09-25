package objects;

import utils.Integrator;
import utils.Writer;

import java.io.*;
import java.util.*;

public class ParticleTrain {
    private final Writer writer = new Writer();
    private final BufferedWriter bw;
    private final List<Particle> particles;
    private final List<Particle> ghostParticles;
    private final float deltaT;
    private final float tf;
    private final float L;
    private final float k = 2.5f; //  [kg/(s^2)]


    public ParticleTrain(List<Particle> particles, float deltaT, float tf, float L) {
        this.particles = particles;
        this.deltaT = deltaT;
        this.tf = tf;
        this.L = L;

        try {
            bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/particle_train_"
                    + particles.size() + "_" + deltaT + ".txt", true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.ghostParticles = new ArrayList<>();
        for (Particle particle : particles) {
            this.ghostParticles.add(
                    new Particle(
                            particle.getId(), particle.getX() - L, particle.getLimitVelocity(),
                            particle.getMass(), particle.getRadius(), particle.getLimitVelocity(), particle.getTau()
                    )
            );
        }

    }

    public void start() {
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).removeForces();
            ghostParticles.get(i).removeForces();
        }

        for (float t = 0; t <= tf; t += deltaT) {
            calculateCollisions();

            for (int i = 0; i < particles.size(); i++) {
                Particle particle = particles.get(i);
                Particle ghostParticle = ghostParticles.get(i);
                float[] r = Integrator.beeman(particle, t, deltaT);
                particle.setX(t, deltaT, r[0]);
                particle.setVelocityX(t, deltaT, r[1]);
                ghostParticle.setX(t, deltaT,r[0] - L);
                ghostParticle.setVelocityX(t, deltaT, r[1]);

                if (particle.getX() > L + particle.getRadius()) {
                    particle.setX(t, deltaT,particle.getX() - L);
                    ghostParticle.setX(t, deltaT,ghostParticle.getX() - L);
                }

            }

            writer.writeStep((int) (t / deltaT), particles, ghostParticles, bw);
        }
    }


    private void calculateCollisions() {
        // Watch if two particles collide, if they do, add a force, same magnitude, opposite direction to both particles
        // a particle near L could collide with a particle near 0, so we need to check both sides, periodic boundary conditions
        for (int i = 0; i < particles.size(); i++) {
            Particle particle1 = particles.get(i);
            Particle ghostParticle1 = ghostParticles.get(i);

            for (int j = i + 1; j < particles.size(); j++) {
                Particle particle2 = particles.get(j);
                Particle ghostParticle2 = ghostParticles.get(j);

                float force = 0;
                if (collides(particle1, particle2)) {
                    force = k * (Math.abs(particle2.getX() - particle1.getX()) - (particle1.getRadius() + particle2.getRadius())) * Math.signum(particle2.getX() - particle1.getX());
                }
                else if(collides(particle1, ghostParticle2)) {
                    force = k * (Math.abs(ghostParticle2.getX() - particle1.getX()) - (particle1.getRadius() + ghostParticle2.getRadius())) * Math.signum(ghostParticle2.getX() - particle1.getX());
                }
                else if(collides(ghostParticle1, particle2)) {
                    force = k * (Math.abs(particle2.getX() - ghostParticle1.getX()) - (ghostParticle1.getRadius() + particle2.getRadius())) * Math.signum(particle2.getX() - ghostParticle1.getX());
                }
                else if(collides(ghostParticle1, ghostParticle2)) {
                    force = k * (Math.abs(ghostParticle2.getX() - ghostParticle1.getX()) - (ghostParticle1.getRadius() + ghostParticle2.getRadius())) * Math.signum(ghostParticle2.getX() - ghostParticle1.getX());
                }

                if (force != 0) {
                    particle1.addForce(force);
                    particle2.addForce(-force);
                    ghostParticle1.addForce(force);
                    ghostParticle2.addForce(-force);
                }
            }
        }
    }

    private boolean collides(Particle particle1, Particle particle2) {
        return Math.abs(particle1.getX() - particle2.getX()) <= particle1.getRadius() + particle2.getRadius();
    }

    public static void main(String[] args) {

        /* Constants */
        float r = 0.0225f; // [m]
        float m = 0.025f; // [kg]
        float L = 1.35f; // [m]
        float tau = 1; // [s]
        float N = 25; // Number of particles

        float deltaT = 0.01f; // [s]
        float tf = 5; // [s]

        List<Particle> particles = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < N; i++) {
            // random x between 0 and L, random limitVelocity between 9 and 12
            float x = random.nextFloat() * L;
            float limitVelocity = random.nextFloat() * 0.03f + 0.09f; // [9-12] [cm/s]

            particles.add(new Particle(i, x, limitVelocity, m, r, limitVelocity, tau));
        }

        ParticleTrain particleTrain = new ParticleTrain(particles, deltaT, tf, L);
        particleTrain.start();

    }
}
