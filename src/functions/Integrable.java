package functions;

import java.util.function.BiFunction;

public interface Integrable {

    BiFunction<Double, Double, Double>  getForceFunction();
    FunctionWithDerivatives getrFunction();
    double getMass();
}
