package frc.robot.subsystems.feeder;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import java.util.Objects;

public class FeederSubsystem extends SubsystemBase {
    private static FeederSubsystem singletonInstance;

    public static FeederSubsystem getInstance() {
        return singletonInstance = Objects.requireNonNullElseGet(singletonInstance, FeederSubsystem::new);
    }

    private final WPI_VictorSPX feederMotor;
    private final DigitalInput feederSensor;

    private FeederSubsystem() {
        feederMotor = new WPI_VictorSPX(Constants.MotorIDs.FEEDER);

        feederMotor.configFactoryDefault();
        feederMotor.setInverted(true);

        feederSensor = new DigitalInput(Constants.DIOPorts.FEEDER_SENSOR);
    }

    private void runFeeder(double speed) {
        feederMotor.set(speed);
    }

    private void stopFeeder() {
        runFeeder(0);
    }

    public boolean hasBall() {
        return !this.feederSensor.get();
    }

    public Command feedCommand() {
        return startEnd(() -> runFeeder(0.5), this::stopFeeder);
    }
}
