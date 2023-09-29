package utils;

import functions.FunctionWithDerivatives;
import functions.Integrable;

import java.util.function.BiFunction;

public class Integrator {
    private static long factorial(int number) {
        long result = 1;

        for (int factor = 2; factor <= number; factor++) {
            result *= factor;
        }

        return result;
    }

    public static double[] gearPredictorCorrector(int order, Integrable model, int step, double deltaT) {
        //r_function
        FunctionWithDerivatives r_function = model.getrFunction();
        //force_function
        BiFunction<Double, Double, Double> force_function = model.getForceFunction();
        //mass
        double mass = model.getMass();

        double[][] coefficents = {
                {},
                {},
                {0.0, 1.0, 1.0},
                {1.0 / 6.0, 5.0 / 6.0, 1.0, 1.0 / 3.0},
                {19.0 / 90.0, 3.0 / 4.0, 1.0, 1.0 / 2.0, 1.0 / 12.0},
                {3.0 / 16.0, 251.0 / 360.0, 1.0, 11.0 / 18.0, 1.0 / 6.0, 1.0 / 60.0}
        };

        // 1) Predict
        double[] rp = new double[order + 1];
        for (int i = 0; i <= order; i++) {
            for (int j = i; j <= order; j++) {
                rp[i] += r_function.calculateDerivative(j, step - 1) * (double) Math.pow(deltaT, j - i) / factorial(j - i);
            }
        }

        // 2) Evaluate

        // deltaA = a(t+deltaT) - ap(t+deltaT)
        double deltaA = force_function.apply(rp[0], rp[1]) / mass - rp[2];
        double deltaR2 = deltaA * deltaT * deltaT / 2.0;

        // 3) Correct
        double[] r = new double[order + 1];

        for (int i = 0; i <= order; i++) { //Only r and v
            r[i] = rp[i] + coefficents[order][i] * deltaR2 * factorial(i) / (double) Math.pow(deltaT, i);
        }

        return r;
    }

    public static double[] beeman(Integrable model, int step, double deltaT) {
        //r_function
        FunctionWithDerivatives r_function = model.getrFunction();
        //force_function
        BiFunction<Double, Double, Double> force_function = model.getForceFunction();
        //mass
        double mass = model.getMass();

        double[] r = new double[2]; // r and v

        r[0] = r_function.calculateDerivative(0, step - 1)
                + r_function.calculateDerivative(1, step - 1) * deltaT
                + 2f / 3f * r_function.calculateDerivative(2, step - 1) * deltaT * deltaT
                - 1f / 6f * r_function.calculateDerivative(2, step - 2) * deltaT * deltaT;

        double vp = r_function.calculateDerivative(1, step - 1)
                + 3f / 2f * r_function.calculateDerivative(2, step - 1) * deltaT
                - 1f / 2f * r_function.calculateDerivative(2, step - 2) * deltaT;

        r[1] = r_function.calculateDerivative(1, step - 1)
                + 5f / 12f * force_function.apply(r[0], vp) / mass * deltaT
                + 2f / 3f * r_function.calculateDerivative(2, step - 1) * deltaT
                - 1f / 12f * r_function.calculateDerivative(2, step - 2) * deltaT;

        return r;
    }

    public static double[] originalVerlet(Integrable model, int step, double deltaT) {
        //r_function
        FunctionWithDerivatives r_function = model.getrFunction();
        //force_function
        BiFunction<Double, Double, Double> force_function = model.getForceFunction();
        //mass
        double mass = model.getMass();

        double[] r = new double[2]; // r and v

        r[0] = 2*r_function.calculateDerivative(0, step - 1)
                - r_function.calculateDerivative(0, step - 2)
                + r_function.calculateDerivative(2, step - 1) * deltaT * deltaT;

        r[1] = (r[0] - r_function.calculateDerivative(0, step - 2)) / (2 * deltaT);

        return r;
    }
}