// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.csm.CommandStateMachine;
import frc.csm.State;
import frc.robot.oi.OI;
import frc.robot.subsystems.feeder.FeederSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.tuning.RobotConfiguration;


public class RobotContainer {
    public RobotContainer() {
        loadConfigFiles();
        configureBindings();
    }

    private void loadConfigFiles() {
        RobotConfiguration.loadFile("config.rcfg");
    }

    private void configureBindings() {
        OI oi = OI.getInstance();
        IntakeSubsystem intakeSubsystem = IntakeSubsystem.getInstance();
        FeederSubsystem feederSubsystem = FeederSubsystem.getInstance();
        ShooterSubsystem shooterSubsystem = ShooterSubsystem.getInstance();

        oi.driverController().button(OI.Buttons.LEFT_TRIGGER).whileTrue(shooterSubsystem.shootCommand());

        oi.driverController().button(OI.Buttons.A_BUTTON).whileTrue(new CommandStateMachine() {{
            State intakeState = addState(intakeSubsystem::intakeCommand);
            State spinUpState = addState(shooterSubsystem::shootCommand);
            State shootState = addState(shooterSubsystem::shootCommand, feederSubsystem::feedCommand);

            setInitial(intakeState);
            intakeState.on(feederSubsystem::hasBall, spinUpState);
            spinUpState.on(shooterSubsystem::isShooterAtDesiredSpeed, shootState);
            shootState.whenTimeElapsed(
                1.0,
                intakeState
            );
            finalizeSetup();
        }});
    }

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }
}