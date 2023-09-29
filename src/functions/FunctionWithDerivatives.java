package functions;

import functions.Derivatives;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FunctionWithDerivatives implements Derivatives {
    private final Map<Integer, Map<Integer, Double>> derivativeFunctions;

    public FunctionWithDerivatives(Map<Integer, Double> function) {
        this.derivativeFunctions = new HashMap<>();
        this.derivativeFunctions.put(0, function);
    }

    public void setDerivative(int order, Map<Integer, Double> derivativeFunction) {
        this.derivativeFunctions.put(order, derivativeFunction);
    }

    @Override
    public double calculateDerivative(int order, int step) {
        return derivativeFunctions.get(order).get(step);
    }

}
