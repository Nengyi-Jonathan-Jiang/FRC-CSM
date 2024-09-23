package frc.robot.oi;

import edu.wpi.first.wpilibj2.command.button.Trigger;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public class OI {
    private final Controller driverController;
    private final Controller operatorController;

    private OI() {
        driverController = new Controller(0);
        operatorController = new Controller(1);
    }

    public Trigger on(BooleanSupplier condition) {
        return new Trigger(condition);
    }

    public Controller operatorController() {
        return operatorController;
    }

    public Controller driverController() {
        return driverController;
    }

    public enum Axes {
        LEFT_STICK_X(0),
        LEFT_STICK_Y(1, true),
        LEFT_TRIGGER(2),
        RIGHT_TRIGGER(3),
        RIGHT_STICK_X(4),
        RIGHT_STICK_Y(5, true);

        private final int id;
        private final boolean shouldInvert;

        Axes(int id) {
            this(id, false);
        }

        Axes(int id, boolean shouldInvert) {
            this.id = id;
            this.shouldInvert = shouldInvert;
        }

        public int getID() {
            return id;
        }

        public boolean shouldInvert() {
            return shouldInvert;
        }
    }

    public enum Buttons {
        A_BUTTON(1),
        B_BUTTON(2),
        X_BUTTON(3),
        Y_BUTTON(4),
        LEFT_BUMPER(5),
        RIGHT_BUMPER(6),
        LEFT_STICK(8),
        RIGHT_STICK(9),
        D_PAD_UP(10),
        D_PAD_RIGHT(11),
        D_PAD_DOWN(12),
        D_PAD_LEFT(13),
        LEFT_TRIGGER(14),
        RIGHT_TRIGGER(15);

        private final int id;

        Buttons(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }

    // Only one instance of OI can exist at a time, and it should be accessible from
    // anywhere using getInstance()
    private static OI singletonInstance = null;

    public static OI getInstance() {
        return singletonInstance = Objects.requireNonNullElseGet(singletonInstance, OI::new);
    }
}
