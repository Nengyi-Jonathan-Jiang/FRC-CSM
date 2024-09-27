package frc.robot.subsystems.shooter;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import frc.csm.PackagePrivate;
import frc.robot.Constants;

@PackagePrivate
class ConcreteShooterSubsystem extends ShooterSubsystem {
    private final TalonFX shooterMotor;

    private final BangBangController shooterBangBangController;
    private final SimpleMotorFeedforward shooterFeedForwardController;

    private double desiredShooterSpeed;

    @PackagePrivate
    ConcreteShooterSubsystem() {
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

    @Override
    protected void setDesiredSpeed(double speed) {
        desiredShooterSpeed = speed;
    }

    @Override
    protected void stopShooter() {
        desiredShooterSpeed = 0;
    }

    @Override
    public boolean isShooterAtDesiredSpeed() {
        return Math.abs(getShooterSpeed() - desiredShooterSpeed) <= 0.5;
    }

    protected double calculateMotorOutput() {
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

    private double getShooterSpeed() {
        StatusSignal<Double> velocitySignal = shooterMotor.getVelocity();
        velocitySignal.refresh();
        return velocitySignal.getValue();
    }

    private void setShooterMotor(double output) {
        shooterMotor.set(output);
    }

    @Override
    public void periodic() {
        setShooterMotor(calculateMotorOutput());
    }
}
