package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.csm.CommandStateMachine;
import frc.csm.State;
import frc.robot.subsystems.feeder.FeederSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;

public class SpinUpThenShootCommand {
    public static Command create(ShooterSubsystem shooterSubsystem, FeederSubsystem feederSubsystem) {
        CommandStateMachine csm = new CommandStateMachine();
        State spinUpState = csm.addState(
            shooterSubsystem::shootCommand
        );
        State shootState = csm.addState(
            shooterSubsystem::shootCommand,
            feederSubsystem::feedCommand
        );

        spinUpState.on(shooterSubsystem::isShooterAtDesiredSpeed, shootState);
        shootState.whenTimeElapsed(1.0, csm.finishedState);

        csm.finalizeSetup();
        return csm;
    }
}
