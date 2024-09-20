package frc.csm;

import edu.wpi.first.wpilibj2.command.Command;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandStateMachine extends Command {
    private final State finishedState = new State(Collections.emptyList());
    private final Set<State> allStates = new HashSet<>();
    private State initialState = null;
    private State currentState = null;

    public CommandStateMachine() {
        allStates.add(finishedState);
    }

    public State addState(Command... command) {
        return addState(List.of(command));
    }

    public State addState(List<Command> commands) {
        State result = new State(commands);
        if (initialState == null) {
            initialState = result;
        }
        allStates.add(result);
        return result;
    }

    public void setInitial(State initialState) {
        this.initialState = initialState;
    }

    @Override
    public boolean isFinished() {
        return currentState == finishedState;
    }
}
