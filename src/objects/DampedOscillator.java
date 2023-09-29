package objects;

import functions.FunctionWithDerivatives;
import functions.Integrable;
import utils.Writer;
import utils.Integrator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DampedOscillator implements Integrable {
    private final float mass; // [kg]
    private final float k; // [N/m]
    private final float gamma; // [kg/s]
    private final float A = 1; // [m]
    private Map<Integer, Float> rMap = new HashMap<>();
    private Map<Integer, Float> r1Map = new HashMap<>();
    private Map<Integer, Float> r2Map = new HashMap<>();
    private Map<Integer, Float> r3Map = new HashMap<>();
    private Map<Integer, Float> r4Map = new HashMap<>();
    private Map<Integer, Float> r5Map = new HashMap<>();

    private final FunctionWithDerivatives rFunction;
    private final BiFunction<Float, Float, Float> forceFunction;

    public DampedOscillator(float mass, float k, float gamma, float r_0, float r1_0) {
        this.mass = mass;
        this.k = k;
        this.gamma = gamma;

        rFunction = new FunctionWithDerivatives(getrMap());

        rFunction.setDerivative(1, getR1Map());
        rFunction.setDerivative(2, getR2Map());
        rFunction.setDerivative(3, getR3Map());
        rFunction.setDerivative(4, getR4Map());
        rFunction.setDerivative(5, getR5Map());

        forceFunction = (r, r1) -> -k * r - gamma * r1;

        rMap.put(-1, r_0);
        rMap.put(0, r_0);

        r1Map.put(-1, r1_0);
        r1Map.put(0, r1_0);

        r2Map.put(-1, forceFunction.apply(r_0, r1_0) / mass);
        r2Map.put(0, forceFunction.apply(r_0, r1_0) / mass);

        r3Map.put(-1, 0f);
        r3Map.put(0, 0f);

        r4Map.put(-1, 0f);
        r4Map.put(0, 0f);

        r5Map.put(-1, 0f);
        r5Map.put(0, 0f);
    }

    public BiFunction<Float, Float, Float> getForceFunction() {
        return forceFunction;
    }

    public FunctionWithDerivatives getrFunction() {
        return rFunction;
    }

    public Map<Integer, Float> getrMap() {
        return rMap;
    }

    public Map<Integer, Float> getR1Map() {
        return r1Map;
    }

    public Map<Integer, Float> getR2Map() {
        return r2Map;
    }

    public Map<Integer, Float> getR3Map() {
        return r3Map;
    }

    public Map<Integer, Float> getR4Map() {
        return r4Map;
    }

    public Map<Integer, Float> getR5Map() {
        return r5Map;
    }

    public float getMass() {
        return mass;
    }


    public static void main(String[] args) throws IOException {
        float mass = 70; // [kg]
        float k = 10000; // [N/m]
        float gamma = 100; // [kg/s]
        float amplitude = 1; // [m]
        float r0 = 1; // [m]
        float v0 = -1 * amplitude * gamma / (2 * mass); // [m/s]

        Writer writer = new Writer();

        float[] deltaTs = {0.1f, 0.01f, 0.001f, 0.0001f};
        int tf = 5; // [s]
        int[] maxSteps = new int[deltaTs.length];
        for (int i = 0; i < deltaTs.length; i++) {
            maxSteps[i] = (int) (tf / deltaTs[i]);
        }

        for (int i = 0; i < deltaTs.length; i++) {
            //Beeman
            DecimalFormat decimalFormat = new DecimalFormat("#.####", );
            String formattedDeltaT = decimalFormat.format(deltaTs[i]);
            System.out.println(formattedDeltaT);

            BufferedWriter bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/oscilator_beeman_" + formattedDeltaT + ".txt", true));
            DampedOscillator dampedOscillator = new DampedOscillator(mass, k, gamma, r0, v0);
            writer.writePos(dampedOscillator.getrMap().get(0), bw);
            for (int j = 1; j <= maxSteps[i]; j++) {
                float[] rBeeman = Integrator.beeman(dampedOscillator, j, deltaTs[i]);

                dampedOscillator.getrMap().put(j , rBeeman[0]);
                dampedOscillator.getR1Map().put(j , rBeeman[1]);
                dampedOscillator.getR2Map().put(j, dampedOscillator.getForceFunction().apply(rBeeman[0], rBeeman[1]) / mass);

                writer.writePos(rBeeman[0], bw);
            }
            bw.close();

            //Gear Predictor-Corrector
            bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/oscilator_gear_" + formattedDeltaT + ".txt", true));
            dampedOscillator = new DampedOscillator(mass, k, gamma, r0, v0);
            writer.writePos(dampedOscillator.getrMap().get(0), bw);
            for (int j = 1; j <= maxSteps[i]; j++) {
                float[] rGear = Integrator.gearPredictorCorrector(5, dampedOscillator, j, deltaTs[i]);

                dampedOscillator.getrMap().put(j, rGear[0]);
                dampedOscillator.getR1Map().put(j, rGear[1]);
                dampedOscillator.getR2Map().put(j, rGear[2]);
                dampedOscillator.getR3Map().put(j, rGear[3]);
                dampedOscillator.getR4Map().put(j, rGear[4]);
                dampedOscillator.getR5Map().put(j, rGear[5]);

                writer.writePos(rGear[0], bw);
            }
            bw.close();

            //Original Verlet
            bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/oscilator_verlet_" + formattedDeltaT + ".txt", true));
            dampedOscillator = new DampedOscillator(mass, k, gamma, r0, v0);
            writer.writePos(dampedOscillator.getrMap().get(0), bw);
            for (int j = 1; j <= maxSteps[i]; j++) {
                float[] rVerlet = Integrator.originalVerlet(dampedOscillator, j, deltaTs[i]);

                dampedOscillator.getrMap().put(j, rVerlet[0]);
                dampedOscillator.getR1Map().put(j, rVerlet[1]);
                dampedOscillator.getR2Map().put(j, dampedOscillator.getForceFunction().apply(rVerlet[0], rVerlet[1]) / mass);

                writer.writePos(rVerlet[0], bw);
            }
            bw.close();
        }

    }
}
