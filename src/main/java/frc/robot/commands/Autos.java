package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

public final class Autos {
    private Autos() {
        throw new UnsupportedOperationException("This is a utility class!");
    }

    public static Command none() {
        return new Command() {};  // empty command
    }
}