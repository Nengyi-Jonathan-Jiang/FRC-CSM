package frc.robot.subsystems.shooter;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.SingletonInstance;

import java.util.Objects;

public abstract class ShooterSubsystem extends SubsystemBase {
    public final static SingletonInstance<ShooterSubsystem> singletonInstance = new SingletonInstance<>(ConcreteShooterSubsystem::new);

    protected abstract void setDesiredSpeed(double speed);

    protected abstract void stopShooter();

    public abstract boolean isShooterAtDesiredSpeed();

    public Command idleShooterCommand() {
        return runOnce(() -> this.setDesiredSpeed(5));
    }

    public Command shootCommand() {
        return startEnd(() -> this.setDesiredSpeed(20), this::stopShooter);
    }
}