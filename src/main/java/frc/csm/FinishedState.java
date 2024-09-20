package frc.csm;

import edu.wpi.first.wpilibj2.command.Command;

import java.util.Collections;
import java.util.function.Supplier;

@PackagePrivate
final class FinishedState extends State {
    FinishedState(CommandStateMachine csm) {
        super(csm, Collections.emptyList());
    }

    @Override
    public void addCommand(Supplier<Command> command) {
        throw new RuntimeException("Cannot add commands to the finished state");
    }

    @Override
    public void on(Supplier<Boolean> condition, State nextState, Runnable action) {
        throw new RuntimeException("Cannot add state transitions away from the finished state");
    }

    @Override
    public void addEntryCode(Runnable action) {
        throw new RuntimeException("Cannot add code to the finished state");
    }

    @Override
    public void addExitCode(Runnable action) {
        throw new RuntimeException("Cannot add code to the finished state");
    }

    @Override
    public void addExecutionCode(Runnable action) {
        throw new RuntimeException("Cannot add code to the finished state");
    }
}
