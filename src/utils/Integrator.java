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

    public static float[] gearPredictorCorrector(int order, Integrable model, int step, float deltaT) {
        //r_function
        FunctionWithDerivatives r_function = model.getrFunction();
        //force_function
        BiFunction<Float, Float, Float> force_function = model.getForceFunction();
        //mass
        float mass = model.getMass();

        float[][] coefficents = {
                {},
                {},
                {0f, 1f, 1f},
                {1f / 6f, 5f / 6f, 1f, 1f / 3f},
                {19f / 90f, 3f / 4f, 1f, 1f / 2f, 1f / 12f},
                {3f / 16f, 251f / 360f, 1f, 11f / 18f, 1f / 6f, 1f / 60f}
        };

        // 1) Predict
        float[] rp = new float[order + 1];
        for (int i = 0; i <= order; i++) {
            for (int j = i; j <= order; j++) {
                rp[i] += r_function.calculateDerivative(j, step - 1) * (float) Math.pow(deltaT, j) / factorial(j);
            }
        }

        // 2) Evaluate

        // deltaA = a(t+deltaT) - ap(t+deltaT)
        float deltaA = force_function.apply(rp[0], rp[1]) / mass - rp[2];
        float deltaR2 = deltaA * deltaT * deltaT / 2.0f;

        // 3) Correct
        float[] r = new float[order + 1];

        for (int i = 0; i <= order; i++) { //Only r and v
            r[i] = rp[i] + coefficents[order][i] * deltaR2 * factorial(i) / (float) Math.pow(deltaT, i);
        }

        return r;
    }

    public static float[] beeman(Integrable model, int step, float deltaT) {
        //r_function
        FunctionWithDerivatives r_function = model.getrFunction();
        //force_function
        BiFunction<Float, Float, Float> force_function = model.getForceFunction();
        //mass
        float mass = model.getMass();

        float[] r = new float[2]; // r and v

        r[0] = r_function.calculateDerivative(0, step - 1)
                + r_function.calculateDerivative(1, step - 1) * deltaT
                + 2f / 3f * r_function.calculateDerivative(2, step - 1) * deltaT * deltaT
                - 1f / 6f * r_function.calculateDerivative(2, step - 2) * deltaT * deltaT;

        float vp = r_function.calculateDerivative(1, step - 1)
                + 3f / 2f * r_function.calculateDerivative(2, step - 1) * deltaT
                - 1f / 2f * r_function.calculateDerivative(2, step - 2) * deltaT;

        r[1] = r_function.calculateDerivative(1, step - 1)
                + 5f / 12f * force_function.apply(r[0], vp) / mass * deltaT
                + 2f / 3f * r_function.calculateDerivative(2, step - 1) * deltaT
                - 1f / 12f * r_function.calculateDerivative(2, step - 2) * deltaT;

        return r;
    }

    public static float[] originalVerlet(Integrable model, int step, float deltaT) {
        //r_function
        FunctionWithDerivatives r_function = model.getrFunction();
        //force_function
        BiFunction<Float, Float, Float> force_function = model.getForceFunction();
        //mass
        float mass = model.getMass();

        float[] r = new float[2]; // r and v

        r[0] = r_function.calculateDerivative(0, step - 1)
                + r_function.calculateDerivative(1, step - 1) * deltaT
                + 1f / 2f * r_function.calculateDerivative(2, step - 1) * deltaT * deltaT
                + 1f / 6f * r_function.calculateDerivative(3, step - 1) * deltaT * deltaT * deltaT;

        //r(t-deltaT)
        float r_t_minus_deltaT = r_function.calculateDerivative(0, step - 1)
                - r_function.calculateDerivative(1, step - 1) * deltaT
                + 1f / 2f * r_function.calculateDerivative(2, step - 1) * deltaT * deltaT
                - 1f / 6f * r_function.calculateDerivative(3, step - 1) * deltaT * deltaT * deltaT;

        r[1] = (r[0] - r_t_minus_deltaT) / (2 * deltaT);

        return r;
    }
}