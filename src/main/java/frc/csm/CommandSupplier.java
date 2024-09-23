package frc.csm;

import edu.wpi.first.wpilibj2.command.Command;

public interface CommandSupplier {
    Command createCommand();
}
