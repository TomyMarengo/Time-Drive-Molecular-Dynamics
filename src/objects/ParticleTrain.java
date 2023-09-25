package objects;

import utils.Writer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleTrain {
    private final Writer writer = new Writer();
    private final List<Particle> particles;
    private final float deltaT;
    private final float tf;

    public ParticleTrain(List<Particle> particles, float deltaT, float tf) {
        this.particles = particles;
        this.deltaT = deltaT;
        this.tf = tf;
    }

    public void start() {
    }

    public static void main(String[] args) {

        /* Constants */
        float r = 0.0225f; // [m]
        float m = 0.025f; // [kg]
        float L = 1.35f; // [m]
        float tau = 1; // [s]
        float N = 25; // Number of particles

        List<Particle> particles = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < N; i++) {
            // random x between 0 and L, random limitVelocity between 9 and 12
            float x = random.nextFloat() * L;
            float limitVelocity = random.nextFloat() * 0.03f + 0.09f; // [9-12] [cm/s]

            particles.add(new Particle(x, limitVelocity, m, r, limitVelocity, tau));
        }



    }
}
