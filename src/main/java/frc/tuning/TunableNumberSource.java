package frc.tuning;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.csm.PackagePrivate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@PackagePrivate
class TunableNumberSource implements NumberSource {
    private final String name;
    private final double defaultValue;
    private double lastValue = Double.NaN;
    private final List<Consumer<Double>> listeners = new ArrayList<>();

    public TunableNumberSource(String name, double defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        if (!SmartDashboard.containsKey(name)) {
            SmartDashboard.putNumber(name, this.defaultValue);
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public double valueAsDouble() {
        return SmartDashboard.getNumber(name, defaultValue);
    }

    @Override
    public void update() {
        double value = valueAsDouble();
        if(value != lastValue) {
            lastValue = value;
            listeners.forEach(l -> l.accept(value));
        }
    }

    @Override
    public void addListener(Consumer<Double> listener) {
        listeners.add(listener);
    }
}
