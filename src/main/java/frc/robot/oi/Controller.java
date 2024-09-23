package frc.robot.oi;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import java.util.function.BooleanSupplier;

public class Controller {
    private static final double DEADZONE_VALUE = 0.01;

    private static final int NUM_BUTTONS = 10;
    private static final int NUM_POV_BUTTONS = 4;
    private static final int NUM_SPECIAL_BUTTONS = 2;

    private final Joystick stick;
    private final Trigger[] buttons;

    Controller(int joystickID) {
        stick = new Joystick(joystickID);

        buttons = new Trigger[NUM_BUTTONS + NUM_POV_BUTTONS + NUM_SPECIAL_BUTTONS];

        for (int i = 0; i < NUM_BUTTONS; i++) {
            buttons[i] = new JoystickButton(stick, i);
        }

        for (int i = 0; i < NUM_POV_BUTTONS; i++) {
            buttons[i + NUM_BUTTONS] = new POVButton(stick, i * 90);
        }

        BooleanSupplier[] special_buttons = {
            () -> axis(OI.Axes.LEFT_TRIGGER) > 0.5,
            () -> axis(OI.Axes.RIGHT_TRIGGER) > 0.5,
        };
        for (int i = 0; i < NUM_SPECIAL_BUTTONS; i++) {
            buttons[i + NUM_BUTTONS + NUM_POV_BUTTONS] = new Trigger(special_buttons[i]);
        }
    }

    /**
     * Creates a dead zone for joysticks; the controllers sticks are a little loose
     * and so there is a margin of error for where they should be considered
     * neutral
     */
    private static double applyDeadzone(double value) {
        // When the axis is LESS than the magic number, the
        // joystick is in the loose position so return zero - as if the
        // joystick was not moved
        if (Math.abs(value) < DEADZONE_VALUE) {
            return 0;
        }

        // When the joystick is MORE than the magic number,
        // scale the value so that the point right after the
        // deadzone is 0 so the robot does not jerk forward
        // when it passes the deadzone.
        return Math.signum(value) * ((Math.abs(value) - DEADZONE_VALUE) / (1 - DEADZONE_VALUE));
    }

    public double axis(OI.Axes axis) {
        double rawAxisValue = stick.getRawAxis(axis.getID());
        double multiplier = axis.shouldInvert() ? -1 : 1;
        return applyDeadzone(multiplier * rawAxisValue);
    }

    public Trigger button(OI.Buttons button) {
        return buttons[button.getID()];
    }

    public void setRumble(double strength) {
        stick.setRumble(GenericHID.RumbleType.kBothRumble, strength);
    }

    public void stopRumble() {
        stick.setRumble(GenericHID.RumbleType.kBothRumble, 0);
    }
}
