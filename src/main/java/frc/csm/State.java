package frc.csm;

import edu.wpi.first.wpilibj2.command.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class State {
    private final List<Command> commands;

    public State(Collection<Command> commands) {
        this.commands = new ArrayList<>(commands);
    }
}
