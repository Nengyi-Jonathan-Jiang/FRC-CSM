package frc.csm;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public sealed class State permits FinishedState {
    private final List<CommandSupplier> commandSuppliers = new ArrayList<>();
    private final CommandStateMachine csm;
    private final List<StateTransition> transitions = new ArrayList<>();

    private final List<Runnable> entryActions = new ArrayList<>();
    private final List<Runnable> executionActions = new ArrayList<>();
    private final List<Runnable> exitActions = new ArrayList<>();
    private final List<Subsystem> otherRequirements = new ArrayList<>();

    @PackagePrivate
    State(CommandStateMachine csm) {
        this.csm = csm;
    }

    @PackagePrivate
    final List<StateTransition> getTransitions() {
        return transitions;
    }

    @PackagePrivate
    final Collection<Subsystem> getRequirements() {
        return Stream.concat(
            commandSuppliers.stream()
                .map(Supplier::get)
                .map(Command::getRequirements)
                .flatMap(Collection::stream),
            otherRequirements.stream()
        ).collect(Collectors.toSet());
    }

    /**
     * Adds an extra subsystem requirement to this state that is not present
     * in any of the commands in the state.
     */
    public final void addRequirement(Subsystem requirement) {
        otherRequirements.add(requirement);
    }

    /**
     * Adds a new command to the state and returns the passed command
     */
    public final Command addCommand(Command command) {
        addCommand(() -> command);
        return command;
    }

    /**
     * Adds a new command to the state. Creation of the command is deferred until
     * the state is entered, which allows parameterized commands' parameters to be
     * evaluated at the time the state is entered rather than at the time the state
     * machine is created, e.g.:
     * <pre> {@code
     *      state.addCommand(() -> new DriveCommand(getDistanceToDrive()));
     * }</pre>
     */
    public void addCommand(CommandSupplier command) {
        csm.assertSetupNotComplete();
        commandSuppliers.add(command);
    }

    /**
     * Adds arbitrary code to be executed periodically while in the state
     */
    public void addExecutionCode(Runnable action) {
        csm.assertSetupNotComplete();
        executionActions.add(action);
    }

    /**
     * Adds arbitrary code to be executed once when the state is entered
     */
    public void addEntryCode(Runnable action) {
        csm.assertSetupNotComplete();
        entryActions.add(action);
    }

    /**
     * Adds arbitrary code to be executed once when the state is exited
     */
    public void addExitCode(Runnable action) {
        csm.assertSetupNotComplete();
        exitActions.add(action);
    }

    /**
     * Configures a state to transition to another state when a condition is
     * true. The condition is implemented as a function that will be called
     * during execution of the state.
     */
    public final void on(Supplier<Boolean> condition, State nextState) {
        on(condition, nextState, () -> {
        });
    }

    /**
     * Configures a state to transition to another state when a condition is
     * true. The condition is implemented as a function that will be called
     * during execution of the state. In addition, the given action will be
     * run after this state's exit code is executed and before the next
     * state's entry code is executed when this transition is taken.
     */
    public void on(Supplier<Boolean> condition, State nextState, Runnable action) {
        csm.assertSetupNotComplete();
        csm.assertHasState(nextState);
        transitions.add(new StateTransition(condition, nextState, action));
    }

    /**
     * Configures a state to transition to another state when all commands in
     * the state have finished.
     */
    public final void whenAllFinished(State nextState) {
        whenAllFinished(nextState, () -> {
        });
    }

    /**
     * Configures a state to transition to another state when all commands in
     * the state have finished. In addition, the given action will be run
     * after this state's exit code is executed and before the next state's
     * entry code is executed when this transition is taken.
     */
    public final void whenAllFinished(State nextState, Runnable action) {
        on(this::didAllCommandsFinish, nextState, action);
    }


    /**
     * Configures a state to transition to another state when any command in
     * the state has finished.
     */
    public final void whenAnyFinished(State nextState) {
        whenAnyFinished(nextState, () -> {
        });
    }

    /**
     * Configures a state to transition to another state when any command in
     * the state has finished. In addition, the given action will be run after
     * this state's exit code is executed and before the next state's entry code
     * is executed when this transition is taken.
     */
    public final void whenAnyFinished(State nextState, Runnable action) {
        on(this::didAnyCommandFinish, nextState, action);
    }

    /**
     * Configures a state to transition to another state when a given amount of
     * time has elapsed since the state was last entered.
     */
    public final void whenTimeElapsed(double elapsedTime, State nextState) {
        whenTimeElapsed(elapsedTime, nextState, () -> {
        });
    }

    /**
     * Configures a state to transition to another state when a given amount of
     * time has elapsed since the state was last entered. In addition, the given
     * action will be run after this state's exit code is executed and before the
     * next state's entry code is executed when this transition is taken.
     */
    public final void whenTimeElapsed(double elapsedTime, State nextState, Runnable action) {
        on(() -> this.elapsedTime() >= elapsedTime, nextState, action);
    }

    /**
     * Configures a state to transition to another state when a given amount of
     * time has elapsed since the state machine began execution.
     */
    public final void whenTotalTimeElapsed(double elapsedTime, State nextState) {
        whenTotalTimeElapsed(elapsedTime, nextState, () -> {
        });
    }

    /**
     * Configures a state to transition to another state when a given amount of
     * time has elapsed since the state machine began execution. In addition,
     * the given action will be run after this state's exit code is executed and
     * before the next state's entry code is executed when this transition is
     * taken.
     */
    public final void whenTotalTimeElapsed(double elapsedTime, State nextState, Runnable action) {
        on(() -> csm.elapsedTime() >= elapsedTime, nextState, action);
    }

    // State execution variables and functions

    private boolean isExecuting = false;
    private double executionStartTimeSeconds = 0;
    private final List<Command> currentlyExecutingCommands = new ArrayList<>();
    private boolean didAnyCommandFinish = false;

    private void assertIsExecuting() {
        csm.assertIsExecuting();
        if (!isExecuting) {
            throw new RuntimeException("Cannot call state execution methods when state is not executing");
        }
    }

    @PackagePrivate
    void enterState() {
        if (isExecuting) {
            throw new RuntimeException("Entered state while state is already executing");
        }
        // Reset the currently executing commands;
        currentlyExecutingCommands.clear();
        commandSuppliers.stream().map(Supplier::get).forEach(currentlyExecutingCommands::add);
        // Reset state execution variables
        isExecuting = true;
        executionStartTimeSeconds = Timer.getFPGATimestamp();
        didAnyCommandFinish = false;

        currentlyExecutingCommands.forEach(Command::initialize);
        entryActions.forEach(Runnable::run);
    }

    @PackagePrivate
    void executeState() {
        assertIsExecuting();

        ListIterator<Command> commandListIterator = currentlyExecutingCommands.listIterator();
        while (commandListIterator.hasNext()) {
            Command command = commandListIterator.next();
            command.execute();
            if (command.isFinished()) {
                commandListIterator.remove();
                command.end(false);
                didAnyCommandFinish = true;
            }
        }
        executionActions.forEach(Runnable::run);
    }

    @PackagePrivate
    void exitState() {
        if (!isExecuting) {
            throw new RuntimeException("Exited state while state is not executing");
        }
        isExecuting = false;

        // Interrupt any commands that are still executing and remove them from
        // the list of currently executing commands
        currentlyExecutingCommands.forEach(command -> command.end(true));
        currentlyExecutingCommands.clear();
        // Run the exit actions
        exitActions.forEach(Runnable::run);
    }

    /**
     * Returns whether all command have finished executing since the state was last entered
     */
    public final boolean didAllCommandsFinish() {
        assertIsExecuting();
        return currentlyExecutingCommands.isEmpty() || currentlyExecutingCommands.stream().allMatch(Command::isFinished);
    }

    /**
     * Returns whether any command has finished executing since the state was last entered
     */
    public final boolean didAnyCommandFinish() {
        assertIsExecuting();
        return currentlyExecutingCommands.isEmpty() || didAnyCommandFinish;
    }

    /**
     * Returns the elapsed time since the state was last entered, in seconds
     */
    public final double elapsedTime() {
        assertIsExecuting();
        return Timer.getFPGATimestamp() - executionStartTimeSeconds;
    }
}
