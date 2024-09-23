package frc.robot.subsystems.shooter;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import java.util.Objects;

public class ShooterSubsystem extends SubsystemBase {
    private static ShooterSubsystem singletonInstance;

    public static ShooterSubsystem getInstance() {
        return singletonInstance = Objects.requireNonNullElseGet(singletonInstance, ShooterSubsystem::new);
    }

    private final TalonFX shooterMotor;

    private final BangBangController shooterBangBangController;
    private final SimpleMotorFeedforward shooterFeedForwardController;
    private double desiredShooterSpeed;

    private ShooterSubsystem() {
        shooterMotor = new TalonFX(Constants.MotorIDs.SHOOTER);

        shooterMotor.setNeutralMode(NeutralModeValue.Coast);
        shooterMotor.getConfigurator().apply(new CurrentLimitsConfigs().withStatorCurrentLimit(20));
        shooterMotor.setInverted(true);

        // TODO: refactor the magic numbers into constants
        shooterBangBangController = new BangBangController(5);
        shooterFeedForwardController = new SimpleMotorFeedforward(
            0.64665,
            0.10772,
            0.037027
        );

        desiredShooterSpeed = 0;
    }

    private double calculateMotorOutput() {
        if (desiredShooterSpeed <= 1.0) {
            return 0;
        }
        return shooterBangBangController.calculate(
            getShooterSpeed(),
            desiredShooterSpeed
        ) + 0.0006 * shooterFeedForwardController.calculate(
            desiredShooterSpeed
        );
    }

    private void setDesiredSpeed(double speed) {
        desiredShooterSpeed = Math.max(speed, 0.0);
    }

    private void stopShooter() {
        desiredShooterSpeed = 0;
    }

    public double getShooterSpeed() {
        StatusSignal<Double> velocitySignal = shooterMotor.getVelocity();
        velocitySignal.refresh();
        return velocitySignal.getValue();
    }

    public boolean isShooterAtDesiredSpeed() {
        return Math.abs(getShooterSpeed() - desiredShooterSpeed) <= 0.5;
    }

    @Override
    public void periodic() {
        shooterMotor.set(calculateMotorOutput());
    }

    public Command idleShooterCommand() {
        return runOnce(() -> this.setDesiredSpeed(5));
    }

    public Command shootCommand() {
        return startEnd(() -> this.setDesiredSpeed(20), this::stopShooter);
    }
}