package frc.robot.subsystems;

import java.util.Objects;
import java.util.function.Supplier;

public class SingletonInstance<T> {
    private final Supplier<T> instanceSupplier;
    private T instance;

    public SingletonInstance(Supplier<T> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    public T get() {
        return instance = Objects.requireNonNullElseGet(instance, instanceSupplier);
    }
}
