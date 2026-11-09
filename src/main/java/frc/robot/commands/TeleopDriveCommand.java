package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;

/**
 * Default teleop drive. Tank drive normally; arcade drive (right stick) when arcade mode is on.
 * Slow mode scales output down, drive-straight averages the two tank sticks, and reversed
 * swaps which end of the robot is the front.
 */
public class TeleopDriveCommand extends Command {

  private final DriveSubsystem drive;
  private final DoubleSupplier leftY;
  private final DoubleSupplier rightY;
  private final DoubleSupplier rightX;
  private final BooleanSupplier arcadeMode;
  private final BooleanSupplier slowMode;
  private final BooleanSupplier driveStraight;
  private final BooleanSupplier reversed;

  public TeleopDriveCommand(DriveSubsystem drive,
                            DoubleSupplier leftY,
                            DoubleSupplier rightY,
                            DoubleSupplier rightX,
                            BooleanSupplier arcadeMode,
                            BooleanSupplier slowMode,
                            BooleanSupplier driveStraight,
                            BooleanSupplier reversed) {
    this.drive = drive;
    this.leftY = leftY;
    this.rightY = rightY;
    this.rightX = rightX;
    this.arcadeMode = arcadeMode;
    this.slowMode = slowMode;
    this.driveStraight = driveStraight;
    this.reversed = reversed;

    addRequirements(drive);
  }

  @Override
  public void execute() {
    double scale = slowMode.getAsBoolean() ? DriveConstants.kSlowModeScale : 1.0;

    if (arcadeMode.getAsBoolean()) {
      double speed    = MathUtil.applyDeadband(rightY.getAsDouble(), DriveConstants.kDeadband);
      double rotation = MathUtil.applyDeadband(rightX.getAsDouble(), DriveConstants.kDeadband);

      if (reversed.getAsBoolean()) {
        speed = -speed; // turning direction stays the same from the driver's view
      }

      drive.arcadeDrive(speed * scale, rotation * scale);
      return;
    }

    double left  = MathUtil.applyDeadband(leftY.getAsDouble(), DriveConstants.kDeadband);
    double right = MathUtil.applyDeadband(rightY.getAsDouble(), DriveConstants.kDeadband);

    if (driveStraight.getAsBoolean()) {
      double average = (left + right) / 2.0;
      left = average;
      right = average;
    }

    if (reversed.getAsBoolean()) {
      // Back becomes front: negate and swap sides so the sticks still map to the driver's left/right
      double oldLeft = left;
      left = -right;
      right = -oldLeft;
    }

    drive.tankDrive(left * scale, right * scale);
  }

  @Override
  public void end(boolean interrupted) {
    drive.stop();
  }

  @Override
  public boolean isFinished() {
    return false;   // runs until interrupted (default command)
  }
}
