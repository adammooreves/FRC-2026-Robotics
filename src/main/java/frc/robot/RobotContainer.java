package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.TeleopDriveCommand;
import frc.robot.subsystems.DriveSubsystem;

public class RobotContainer {

  private final DriveSubsystem driveSubsystem = new DriveSubsystem();

  private final CommandXboxController controller =
    new CommandXboxController(OperatorConstants.kDriverControllerPort);

  // Toggled by buttons; read every loop by the drive command
  private boolean arcadeMode = false;
  private boolean reversed = false;

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {
    // Default command → runs in teleop automatically
    driveSubsystem.setDefaultCommand(
      new TeleopDriveCommand(
        driveSubsystem,
        () -> -controller.getLeftY(),   // negative = forward when stick pushed forward
        () -> -controller.getRightY(),
        () -> -controller.getRightX(),  // negative: stick right = turn clockwise
        () -> arcadeMode,
        controller.leftBumper(),
        controller.rightBumper(),
        () -> reversed
      )
    );

    // Toggle which end of the robot is the front
    controller.y()
      .onTrue(Commands.runOnce(() -> reversed = !reversed));

    // Toggle between tank (two sticks) and arcade (right stick only)
    controller.a()
      .onTrue(Commands.runOnce(() -> arcadeMode = !arcadeMode));

    // Turn in place while either turn button is held (teleop only). One command handles both
    // buttons so switching between them is smooth; holding both cancels out.
    Trigger turnLeft  = controller.x();
    Trigger turnRight = controller.b();
    turnLeft.or(turnRight).and(DriverStation::isTeleopEnabled)
      .whileTrue(driveSubsystem.runEnd(
        () -> {
          double direction = (turnLeft.getAsBoolean() ? 1 : 0) - (turnRight.getAsBoolean() ? 1 : 0);
          driveSubsystem.arcadeDrive(0, direction * DriveConstants.kTurnInPlaceSpeed);
        },
        driveSubsystem::stop));
  }

  /** Called every loop from Robot.robotPeriodic() to show drive modes on the dashboard. */
  public void updateDashboard() {
    SmartDashboard.putBoolean("Drive/Arcade Mode", arcadeMode);
    SmartDashboard.putBoolean("Drive/Reversed", reversed);
    SmartDashboard.putBoolean("Drive/Slow Mode", controller.leftBumper().getAsBoolean());
  }

  public Command getAutonomousCommand() {
    return Autos.none(); // no auto yet – add later
  }
}
