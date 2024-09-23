package frc.tuning;

import java.util.function.Consumer;

public interface NumberSource extends ValueSource {
    default int valueAsInt() {
        return (int) valueAsDouble();
    }
    double valueAsDouble();
    void addListener(Consumer<Double> listener);
}

