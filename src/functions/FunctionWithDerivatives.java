package functions;

import functions.Derivatives;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FunctionWithDerivatives implements Derivatives {
    private final Map<Integer, Map<Integer, Float>> derivativeFunctions;

    public FunctionWithDerivatives(Map<Integer, Float> function) {
        this.derivativeFunctions = new HashMap<>();
        this.derivativeFunctions.put(0, function);
    }

    public void setDerivative(int order, Map<Integer, Float> derivativeFunction) {
        this.derivativeFunctions.put(order, derivativeFunction);
    }

    @Override
    public float calculateDerivative(int order, int step) {
        return derivativeFunctions.get(order).get(step);
    }

}
