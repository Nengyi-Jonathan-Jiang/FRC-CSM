package frc.robot.subsystems.intake;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import java.util.Objects;

public class IntakeSubsystem extends SubsystemBase {
    private static IntakeSubsystem singletonInstance;

    public static IntakeSubsystem getInstance() {
        return singletonInstance = Objects.requireNonNullElseGet(singletonInstance, IntakeSubsystem::new);
    }

    private final WPI_VictorSPX intakeMotor, hopperMotor;
    private final Solenoid intakePiston;

    private IntakeSubsystem() {
        intakeMotor = new WPI_VictorSPX(Constants.MotorIDs.INTAKE);
        hopperMotor = new WPI_VictorSPX(Constants.MotorIDs.HOPPER);
        intakePiston = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.Pneumatics.INTAKE_PISTON);

        intakeMotor.configFactoryDefault();
        hopperMotor.configFactoryDefault();

        intakeMotor.setNeutralMode(NeutralMode.Brake);
        hopperMotor.setNeutralMode(NeutralMode.Brake);

        intakeMotor.setInverted(true);
        hopperMotor.setInverted(true);
    }

    private void extendIntake() {
        intakePiston.set(true);
    }

    private void retractIntake() {
        intakePiston.set(false);
    }

    private void runIntake(double speed) {
        extendIntake();
        intakeMotor.set(speed);
        hopperMotor.set(speed);
    }

    private void stopIntake() {
        retractIntake();
        intakeMotor.stopMotor();
        hopperMotor.stopMotor();
    }

    public Command intakeCommand() {
        return startEnd(() -> runIntake(0.6), this::stopIntake);
    }
}