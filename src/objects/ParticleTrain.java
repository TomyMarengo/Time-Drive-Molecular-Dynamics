package objects;

import utils.Integrator;
import utils.Writer;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class ParticleTrain {
    private final List<Particle> particles;
    private final double deltaT;
    private final double tf;
    private final double L;
    private final double k = 2.5; //  [kg/(s^2)]
    private final int maxStep;
    private int step = 0;


    public ParticleTrain(List<Particle> particles, double deltaT, double tf, double L) {
        this.deltaT = deltaT;
        this.tf = tf;
        this.L = L;
        this.maxStep = (int) (tf / deltaT);

        this.particles = new ArrayList<>();
        for (Particle particle : particles) {
            this.particles.add(new Particle(particle.getMass(), particle.getRadius(), particle.getLimitVelocity(),
                    particle.getTau(), particle.getrMap().get(step), particle.getLimitVelocity(), deltaT));
        }
    }

    private void writeOutputStep() {
        try {
            DecimalFormat decimalFormat = new DecimalFormat("#.######");
            // Decimal format with locale US
            decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            String formattedDeltaT = decimalFormat.format(deltaT);
            BufferedWriter writer = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/particle_train_"
                    + particles.size() + "_" + formattedDeltaT + ".txt", true));

            writer.write("Time " + step * deltaT + "\n");
            for (Particle particle : particles) {
                writer.write(particle.getrMap().get(step) + " " + particle.getR1Map().get(step) + "\n");
            }
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start() {

        writeOutputStep();

        for (int j = 1; j <= maxStep; j++) {
            this.step = j;

            for (Particle particle : particles) {
                if (particle.getrMap().get(step - 1) > L) {
                    particle.getrMap().put(step - 1, particle.getrMap().get(step - 1) - L);
                }

                double[] rBeeman = Integrator.beeman(particle, step, deltaT);

                particle.getrMap().put(step , rBeeman[0]);
                particle.getR1Map().put(step , rBeeman[1]);
                particle.getR2Map().put(step, particle.getForceFunction().apply(rBeeman[0], rBeeman[1]) / particle.getMass());

                particle.removeForces();

                particle.cleanMaps(step);
            }

            calculateCollisions();



            if (deltaT >= 0.1 || (step % ((int) (1 / deltaT)) == 0)) {
                writeOutputStep();
            }
        }
    }


    private void calculateCollisions() {
        // Watch if two particles collide, if they do, add a force, same magnitude, opposite direction to both particles
        // a particle near L could collide with a particle near 0, so we need to check both sides, periodic boundary conditions
        for (int i = 0; i < particles.size(); i++) {
            Particle particle1 = particles.get(i);
            for (int j = i + 1; j < particles.size(); j++) {
                Particle particle2 = particles.get(j);

                if (collides(particle1, particle2)) {
                    double force = k * (Math.abs(particle2.getrMap().get(step - 1) - particle1.getrMap().get(step - 1))
                            - (particle1.getRadius() + particle2.getRadius())) * Math.signum(particle2.getrMap().get(step - 1) - particle1.getrMap().get(step - 1));
                    particle1.addForce(force);
                }

            }
        }
    }

    private boolean collides(Particle particle1, Particle particle2) {
        return Math.abs(particle1.getrMap().get(step - 1) - particle2.getrMap().get(step - 1)) <= (particle1.getRadius() + particle2.getRadius());
    }

    public static void main(String[] args) {
        // Constants
        double r = 0.0225; // [m]
        double m = 0.025; // [kg]
        double L = 1.35; // [m]
        double tau = 1; // [s]

        Random random = new Random();

        double[] deltaTs = {0.001};
        int[] Ns = {10};
        int tf = 180; // [s]

        for (int n : Ns) {
            List<Particle> particles = new ArrayList<>();

            // Initilize particles
            double[] xs = new double[n];
            for (int i = 0; i < n; i++) {
                xs[i] = i * L / n;
            }
            double[] taken = new double[n];

            for (int i = 0; i < n; i++) {
                // random x between xs[0] and xs[N-1], if it's already taken, try again
                int index = random.nextInt(n);
                while (taken[index] == 1) {
                    index = random.nextInt(n);
                }
                taken[index] = 1;
                double x = xs[index];
                double limitVelocity = random.nextDouble() * 0.03 + 0.09; // [9-12] [cm/s]

                particles.add(new Particle(m, r, limitVelocity, tau, x, limitVelocity));
            }

            for (double deltaT : deltaTs) {
                ParticleTrain particleTrain = new ParticleTrain(particles, deltaT, tf, L);
                particleTrain.start();
            }
        }
    }

    /*
    // Orderer ASC
    public static void main(String[] args) {
        // Constants
        double r = 0.0225; // [m]
        double m = 0.025; // [kg]
        double L = 1.35; // [m]
        double tau = 1; // [s]

        Random random = new Random();

        double[] deltaTs = {0.001};
        int[] Ns = {5, 10, 15, 20, 25, 30};
        int tf = 180; // [s]

        for (int n : Ns) {
            List<Particle> particles = new ArrayList<>();

            // Initilize particles
            double[] xs = new double[n];
            double[] limitVelocities = new double[n];
            for (int i = 0; i < n; i++) {
                xs[i] = i * L / n;
                limitVelocities[i] = random.nextDouble() * 0.03 + 0.09; // [9-12] [cm/s]
            }
            // sort limitVelocities ASC
            Arrays.sort(limitVelocities);

            for (int i = 0; i < n; i++) {
                particles.add(new Particle(m, r, limitVelocities[i], tau, xs[i], limitVelocities[i]));
            }

            for (double deltaT : deltaTs) {
                ParticleTrain particleTrain = new ParticleTrain(particles, deltaT, tf, L);
                particleTrain.start();
            }
        }
    }*/
}
