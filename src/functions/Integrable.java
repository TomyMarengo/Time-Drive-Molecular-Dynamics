package functions;

import java.util.function.BiFunction;

public interface Integrable {

    BiFunction<Float, Float, Float>  getForceFunction();
    FunctionWithDerivatives getrFunction();
    float getMass();
}
