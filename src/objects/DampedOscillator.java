package objects;

import functions.FunctionWithDerivatives;
import functions.Integrable;
import utils.Writer;
import utils.Integrator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.BiFunction;

public class DampedOscillator implements Integrable {
    private final float mass; // [kg]
    private final float k; // [N/m]
    private final float gamma; // [kg/s]
    private final float A = 1; // [m]
    private float r; // [m]
    private float v; // [m/s]

    private final FunctionWithDerivatives rFunction;
    private final BiFunction<Float, Float, Float> forceFunction;


    public DampedOscillator(float mass, float k, float gamma, float r0, float v0) {
        this.mass = mass;
        this.k = k;
        this.gamma = gamma;
        this.r = r0;
        this.v = v0;

        rFunction = new FunctionWithDerivatives(this::r);

        rFunction.setDerivative(0, this::r);
        rFunction.setDerivative(1, this::r1);
        rFunction.setDerivative(2, this::r2);
        rFunction.setDerivative(3, this::r3);
        rFunction.setDerivative(4, this::r4);
        rFunction.setDerivative(5, this::r5);

        forceFunction = (r, r1) -> -k * r - gamma * r1;
    }

    public BiFunction<Float, Float, Float> getForceFunction() {
        return forceFunction;
    }

    public FunctionWithDerivatives getrFunction() {
        return rFunction;
    }

    public float getMass() {
        return mass;
    }

    private float r(float t) {
        return A * (float) Math.exp(-gamma / (2 * mass) * t) *
                (float) Math.cos(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t);
    }

    private float r1(float t) {
        return -gamma / (2 * mass) * A * (float) Math.exp(-gamma / (2 * mass) * t) *
                (float) Math.cos(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) - (float) Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) *
                (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t);
    }

    private float r2(float t) {
        return gamma * gamma / (4 * mass * mass) * A * (float) Math.exp(-gamma / (2 * mass) * t) *
                (float) Math.cos(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) - gamma / (2 * mass) * A * (float) Math.exp(-gamma / (2 * mass) * t) *
                (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) + k / mass * A * (float) Math.exp(-gamma / (2 * mass) * t) *
                (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t);

    }

    private float r3(float t) {
        return -gamma * gamma * gamma / (8 * mass * mass * mass) * A * (float) Math.exp(-gamma / (2 * mass) * t) *
                (float) Math.cos(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) + gamma * gamma / (4 * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) + gamma * k / (2 * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) + gamma / (2 * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) *
                (float) Math.cos(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) - k * k / (mass * mass) * A * (float) Math.exp(-gamma / (2 * mass) * t) *
                (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t);

    }

    private float r4(float t) {
        return gamma * gamma * gamma * gamma / (16 * mass * mass * mass * mass) * A * (float) Math.exp(-gamma / (2 * mass) * t) *
                (float) Math.cos(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) - gamma * gamma * gamma / (8 * mass * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) + gamma * gamma * k / (4 * mass * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) + gamma * gamma / (4 * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) *
                (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) - gamma * k * k / (2 * mass * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.cos(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) + k * k * k / (mass * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t);
    }

    private float r5(float t) {
        return -gamma * gamma * gamma * gamma * gamma / (32 * mass * mass * mass * mass * mass) * A * (float) Math.exp(-gamma / (2 * mass) * t) *
                (float) Math.cos(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) + gamma * gamma * gamma * gamma / (16 * mass * mass * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) - gamma * gamma * gamma * k / (8 * mass * mass * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) + gamma * gamma * k * k / (4 * mass * mass * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) - gamma * k / (4 * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) *
                (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) + k * k * k / (2 * mass * mass * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.cos(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t) - k * k * k * k / (mass * mass * mass * mass) * A *
                (float) Math.exp(-gamma / (2 * mass) * t) * (float) Math.sin(Math.sqrt(k / mass - gamma * gamma / (4 * mass * mass)) * t);
    }


    public static void main(String[] args) throws IOException {
        float mass = 70; // [kg]
        float k = 10000; // [N/m]
        float gamma = 100; // [kg/s]
        float amplitude = 1; // [m]
        float r0 = 1; // [m]
        float v0 = -1 * amplitude * gamma / (2 * mass); // [m/s]

        DampedOscillator dampedOscillator = new DampedOscillator(mass, k, gamma,r0, v0);

        float deltaT = 0.01f; // [s]
        float tf = 5; // [s]

        //Define all three integrators, for each define same oscillator and calculate r and v from 0 to 5s
        Writer writer = new Writer();
        //Beeman

        float t = 0; // [s]
        float[] rBeeman = new float[2];
        rBeeman[0] = r0;
        rBeeman[1] = v0;

        BufferedWriter bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/oscilator_beeman.txt", true));
        writer.writePos(rBeeman[0], bw);

        while (t < tf) {
            rBeeman = Integrator.beeman(dampedOscillator, t, deltaT);
            t += deltaT;
            writer.writePos(rBeeman[0], bw);
        }
        bw.close();

        //Original Verlet
        t = 0; // [s]
        float[] rVerlet = new float[2];
        rVerlet[0] = r0;
        rVerlet[1] = v0;

        bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/oscilator_verlet.txt", true));
        writer.writePos(rVerlet[0], bw);

        while (t < tf) {
            rVerlet = Integrator.originalVerlet(dampedOscillator, t, deltaT);
            t += deltaT;
            writer.writePos(rVerlet[0], bw);
        }
        bw.close();

        //Gear Predictor-Corrector
        t = 0; // [s]
        float[] rGear = new float[2];
        rGear[0] = r0;
        rGear[1] = v0;

        bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/oscilator_gear.txt", true));
        writer.writePos(rGear[0], bw);

        while (t < tf) {
            rGear = Integrator.gearPredictorCorrector(5, dampedOscillator, t, deltaT);
            t += deltaT;
            writer.writePos(rGear[0], bw);
        }
        bw.close();

    }
}
