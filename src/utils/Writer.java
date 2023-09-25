package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import objects.Particle;

public class Writer {

    public void writePos(float pos, BufferedWriter writer) {
        try {
            writer.write(pos + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeStep(float time, int step, List<Particle> particles, List<Particle> ghostParticles, BufferedWriter writer)  {
        try {
            writer.write("Time " + time + "\n");

            for (Particle particle : particles) {
                writer.write(particle.getrMap().get(step) + " " + particle.getR1Map().get(step) + "\n");
            }
            for (Particle particle : ghostParticles) {
                writer.write(particle.getrMap().get(step) + " " + particle.getR1Map().get(step) + "\n");
            }

            writer.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}