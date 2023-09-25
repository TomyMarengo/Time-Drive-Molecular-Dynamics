package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import objects.Particle;

public class Writer {

    public void writePos(float pos, BufferedWriter writer) throws IOException {
        writer.write(pos + "\n");
    }

    public void writeStep(int step, List<Particle> particles, BufferedWriter writer) throws IOException {
        writer.write("Step " + step + "\n");

        for (Particle particle : particles) {
            writer.write(particle.getX() + " " + particle.getY() + " " +
                    particle.getVelocityX() + " " + particle.getVelocityY() + "\n");
        }

        writer.write("\n");
    }
}