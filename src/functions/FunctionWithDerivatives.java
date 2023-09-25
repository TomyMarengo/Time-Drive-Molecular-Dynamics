package functions;

import functions.Derivatives;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FunctionWithDerivatives implements Derivatives {
    private final Function<Float, Float> function;
    private final Map<Integer, Function<Float, Float>> derivativeFunctions;

    public FunctionWithDerivatives(Function<Float, Float> function) {
        this.function = function;
        this.derivativeFunctions = new HashMap<>();
    }

    public void setDerivative(int order, Function<Float, Float> derivativeFunction) {
        derivativeFunctions.put(order, derivativeFunction);
    }

    @Override
    public float calculate(float t) {
        return function.apply(t);
    }

    public float calculateDerivative(int order, float t) {
        return derivativeFunctions.getOrDefault(order, x -> 0f).apply(t);
    }
}
