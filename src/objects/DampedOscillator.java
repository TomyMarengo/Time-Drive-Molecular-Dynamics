package objects;

import functions.FunctionWithDerivatives;
import functions.Integrable;
import utils.Writer;
import utils.Integrator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

public class DampedOscillator implements Integrable {
    private final double mass; // [kg]
    private final double k; // [N/m]
    private final double gamma; // [kg/s]
    private final double A = 1; // [m]
    private Map<Integer, Double> rMap = new HashMap<>();
    private Map<Integer, Double> r1Map = new HashMap<>();
    private Map<Integer, Double> r2Map = new HashMap<>();
    private Map<Integer, Double> r3Map = new HashMap<>();
    private Map<Integer, Double> r4Map = new HashMap<>();
    private Map<Integer, Double> r5Map = new HashMap<>();

    private final FunctionWithDerivatives rFunction;
    private final BiFunction<Double, Double, Double> forceFunction;

    private final BiFunction<Double, Double, Double> r3Function;
    private final BiFunction<Double, Double, Double> r4Function;
    private final BiFunction<Double, Double, Double> r5Function;

    public DampedOscillator(double mass, double k, double gamma, double r_0, double r1_0, double deltaT) {
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

        r3Function = (r1, r2) -> (-k * r1 - gamma * r2) / mass;

        r4Function = (r2, r3) -> (-k * r2 - gamma * r3) / mass;

        r5Function = (r3, r4) -> (-k * r3 - gamma * r4) / mass;

        rMap.put(-1, r_0 - deltaT * r1_0 + deltaT * deltaT * forceFunction.apply(r_0, r1_0) / (2 * mass));
        rMap.put(0, r_0);

        r1Map.put(-1, r1_0 - deltaT * forceFunction.apply(r_0, r1_0) / mass);
        r1Map.put(0, r1_0);

        r2Map.put(-1, forceFunction.apply(rMap.get(-1), r1Map.get(-1)) / mass);
        r2Map.put(0, forceFunction.apply(rMap.get(0), r1Map.get(0)) / mass);

        r3Map.put(-1, r3Function.apply(r1Map.get(-1), r2Map.get(-1)));
        r3Map.put(0, r3Function.apply(r1Map.get(0), r2Map.get(0)));

        r4Map.put(-1, r4Function.apply(r2Map.get(-1), r3Map.get(-1)));
        r4Map.put(0, r4Function.apply(r2Map.get(0), r3Map.get(0)));

        r5Map.put(-1, r5Function.apply(r3Map.get(-1), r4Map.get(-1)));
        r5Map.put(0, r5Function.apply(r3Map.get(0), r4Map.get(0)));
    }

    public BiFunction<Double, Double, Double> getForceFunction() {
        return forceFunction;
    }

    public FunctionWithDerivatives getrFunction() {
        return rFunction;
    }

    public BiFunction<Double, Double, Double> getR3Function() {
        return r3Function;
    }

    public BiFunction<Double, Double, Double> getR4Function() {
        return r4Function;
    }

    public BiFunction<Double, Double, Double> getR5Function() {
        return r5Function;
    }

    public Map<Integer, Double> getrMap() {
        return rMap;
    }

    public Map<Integer, Double> getR1Map() {
        return r1Map;
    }

    public Map<Integer, Double> getR2Map() {
        return r2Map;
    }

    public Map<Integer, Double> getR3Map() {
        return r3Map;
    }

    public Map<Integer, Double> getR4Map() {
        return r4Map;
    }

    public Map<Integer, Double> getR5Map() {
        return r5Map;
    }

    public double getMass() {
        return mass;
    }


    public static void main(String[] args) throws IOException {
        double mass = 70; // [kg]
        double k = 10000; // [N/m]
        double gamma = 100; // [kg/s]
        double amplitude = 1; // [m]
        double r0 = 1; // [m]
        double v0 = -1 * amplitude * gamma / (2 * mass); // [m/s]

        Writer writer = new Writer();

        double[] deltaTs = {0.01, 0.001, 0.0001, 0.00001, 0.000001};
        int tf = 5; // [s]
        int[] maxSteps = new int[deltaTs.length];
        for (int i = 0; i < deltaTs.length; i++) {
            maxSteps[i] = (int) (tf / deltaTs[i]);
        }

        for (int i = 0; i < deltaTs.length; i++) {
            //Beeman
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            DecimalFormat decimalFormat = new DecimalFormat("#.######", symbols);
            String formattedDeltaT = decimalFormat.format(deltaTs[i]);

            BufferedWriter bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/oscillator_beeman_" + formattedDeltaT + ".txt", true));
            DampedOscillator dampedOscillator = new DampedOscillator(mass, k, gamma, r0, v0, deltaTs[i]);
            writer.writePos(dampedOscillator.getrMap().get(0), bw);
            for (int j = 1; j <= maxSteps[i]; j++) {
                double[] rBeeman = Integrator.beeman(dampedOscillator, j, deltaTs[i]);

                dampedOscillator.getrMap().put(j , rBeeman[0]);
                dampedOscillator.getR1Map().put(j , rBeeman[1]);
                dampedOscillator.getR2Map().put(j, dampedOscillator.getForceFunction().apply(rBeeman[0], rBeeman[1]) / mass);

                writer.writePos(rBeeman[0], bw);
            }
            bw.close();

            //Gear Predictor-Corrector
            bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/oscillator_gear_" + formattedDeltaT + ".txt", true));
            dampedOscillator = new DampedOscillator(mass, k, gamma, r0, v0, deltaTs[i]);
            writer.writePos(dampedOscillator.getrMap().get(0), bw);
            for (int j = 1; j <= maxSteps[i]; j++) {
                double[] rGear = Integrator.gearPredictorCorrector(5, dampedOscillator, j, deltaTs[i]);

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
            bw = new BufferedWriter(new FileWriter("../time-drive-molecular-dynamics-animation/outputs/oscillator_verlet_" + formattedDeltaT + ".txt", true));
            dampedOscillator = new DampedOscillator(mass, k, gamma, r0, v0, deltaTs[i]);
            writer.writePos(dampedOscillator.getrMap().get(0), bw);
            for (int j = 1; j <= maxSteps[i]; j++) {
                double[] rVerlet = Integrator.originalVerlet(dampedOscillator, j, deltaTs[i]);

                dampedOscillator.getrMap().put(j, rVerlet[0]);
                dampedOscillator.getR1Map().put(j - 1, rVerlet[1]);
                dampedOscillator.getR2Map().put(j, dampedOscillator.getForceFunction().apply(rVerlet[0], rVerlet[1]) / mass);

                writer.writePos(rVerlet[0], bw);
            }
            bw.close();
        }

    }
}
