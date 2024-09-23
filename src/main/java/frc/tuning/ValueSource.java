package frc.tuning;

public interface ValueSource {
    String name();
    default void update() {}
}
