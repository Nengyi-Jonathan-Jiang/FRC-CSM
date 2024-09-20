package frc.csm;

import java.util.function.Supplier;


/**
 * A class representing a state transition.
 */
@PackagePrivate
record StateTransition(Supplier<Boolean> condition, State nextState, Runnable action) {
}
