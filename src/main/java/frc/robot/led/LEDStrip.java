package frc.robot.led;

import edu.wpi.first.wpilibj2.command.Subsystem;

public sealed interface LEDStrip extends Subsystem permits DummyLEDStrip, PhysicalLEDStrip {
    void usePattern(LEDPattern pattern);

    void update();

    @Override
    default void periodic() {
        update();
    }

    void resetToBlank();
}
