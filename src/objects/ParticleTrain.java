package objects;

import utils.Integrator;
import utils.Writer;

import java.io.*;
import java.text.DecimalFormat;
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
    private final int maxStep;
    private int step = 0;


    public ParticleTrain(List<Particle> particles, float deltaT, float tf, float L) {
        this.particles = particles;
        this.deltaT = deltaT;
        this.tf = tf;
        this.L = L;
        this.maxStep = (int) (tf / deltaT);

        try {
            DecimalFormat decimalFormat = new DecimalFormat("#.####");
            String formattedDeltaT = decimalFormat.format(deltaT);
            bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/particle_train_"
                    + particles.size() + "_" + formattedDeltaT + ".txt", true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.ghostParticles = new ArrayList<>();
        for (Particle particle : particles) {
            this.ghostParticles.add(
                    new Particle(particle.getMass(), particle.getRadius(), particle.getLimitVelocity(),
                            particle.getTau(), particle.getrMap().get(step) - L, particle.getLimitVelocity())
            );
        }
    }


    public void start() {

        writer.writeStep(step * deltaT, step, particles, ghostParticles, bw);

        for (int j = 1; j <= maxStep; j++) {
            this.step = j;

            for (int i = 0; i < particles.size(); i++) {
                particles.get(i).removeForces();
                ghostParticles.get(i).removeForces();
            }

            calculateCollisions();

            for (int i = 0; i < particles.size(); i++) {
                Particle particle = particles.get(i);
                Particle ghostParticle = ghostParticles.get(i);

                if (particle.getrMap().get(step - 1) > L + particle.getRadius()) {
                    particle.getrMap().put(step - 1, particle.getrMap().get(step - 1) - L);
                    ghostParticle.getrMap().put(step - 1, particle.getrMap().get(step - 1) - L);
                }

                float[] rBeeman = Integrator.beeman(particle, step, deltaT);
                particle.getrMap().put(step, rBeeman[0]);
                particle.getR1Map().put(step, rBeeman[1]);
                particle.getR2Map().put(j, particle.getForceFunction().apply(rBeeman[0], rBeeman[1]) / particle.getMass());

                rBeeman = Integrator.beeman(ghostParticle, step, deltaT);
                ghostParticle.getrMap().put(step, rBeeman[0]);
                ghostParticle.getR1Map().put(step, rBeeman[1]);
                ghostParticle.getR2Map().put(j, ghostParticle.getForceFunction().apply(rBeeman[0], rBeeman[1]) / ghostParticle.getMass());

            }

            if (deltaT >= 0.1f || (step % ((int) (0.1 / deltaT)) == 0)) {
                writer.writeStep(step * deltaT, step, particles, ghostParticles, bw);
            }

        }

        try {
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                    force = k * (Math.abs(particle2.getrMap().get(step - 1) - particle1.getrMap().get(step - 1))
                            - (particle1.getRadius() + particle2.getRadius())) * Math.signum(particle2.getrMap().get(step - 1) - particle1.getrMap().get(step - 1));
                }
                else if(collides(particle1, ghostParticle2)) {
                    force = k * (Math.abs(ghostParticle2.getrMap().get(step - 1) - particle1.getrMap().get(step - 1))
                            - (particle1.getRadius() + ghostParticle2.getRadius())) * Math.signum(ghostParticle2.getrMap().get(step - 1) - particle1.getrMap().get(step - 1));
                }
                else if(collides(ghostParticle1, particle2)) {
                    force = k * (Math.abs(particle2.getrMap().get(step - 1) - ghostParticle1.getrMap().get(step - 1))
                            - (ghostParticle1.getRadius() + particle2.getRadius())) * Math.signum(particle2.getrMap().get(step - 1) - ghostParticle1.getrMap().get(step - 1));
                }
                else if(collides(ghostParticle1, ghostParticle2)) {
                    force = k * (Math.abs(ghostParticle2.getrMap().get(step - 1) - ghostParticle1.getrMap().get(step - 1))
                            - (ghostParticle1.getRadius() + ghostParticle2.getRadius())) * Math.signum(ghostParticle2.getrMap().get(step - 1) - ghostParticle1.getrMap().get(step - 1));
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
        return Math.abs(particle1.getrMap().get(step - 1) - particle2.getrMap().get(step - 1)) <= particle1.getRadius() + particle2.getRadius();
    }

    public static void main(String[] args) {

        /* Constants */
        float r = 0.0225f; // [m]
        float m = 0.025f; // [kg]
        float L = 1.35f; // [m]
        float tau = 1; // [s]

        Random random = new Random();

        float[] deltaTs = {0.1f, 0.01f, 0.001f, 0.0001f};
        int[] Ns = {10, 15, 20, 25};
        int tf = 10; // [s]

        for (int n : Ns) {
            List<Particle> particles = new ArrayList<>();

            // Initilize particles
            float[] xs = new float[n];
            for (int i = 0; i < n; i++) {
                xs[i] = i * L / n;
            }
            float[] taken = new float[n];

            for (int i = 0; i < n; i++) {
                // random x between xs[0] and xs[N-1], if it's already taken, try again
                int index = random.nextInt(n);
                while (taken[index] == 1) {
                    index = random.nextInt(n);
                }
                taken[index] = 1;
                float x = xs[index];
                float limitVelocity = random.nextFloat() * 0.03f + 0.09f; // [9-12] [cm/s]

                particles.add(new Particle(m, r, limitVelocity, tau, x, limitVelocity));
            }

            for (float deltaT : deltaTs) {
                ParticleTrain particleTrain = new ParticleTrain(particles, deltaT, tf, L);
                particleTrain.start();
            }
        }

    }
}
