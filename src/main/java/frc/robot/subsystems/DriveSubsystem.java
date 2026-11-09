package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DriveSubsystem extends SubsystemBase {

    // CHANGE THESE CAN IDs TO YOUR ACTUAL ROBOT'S!
    private final SparkMax leftMaster   = new SparkMax(1, MotorType.kBrushless);
    private final SparkMax leftFollower  = new SparkMax(2, MotorType.kBrushless);
    private final SparkMax rightMaster   = new SparkMax(3, MotorType.kBrushless);
    private final SparkMax rightFollower = new SparkMax(4, MotorType.kBrushless);

    private final DifferentialDrive drive;

    public DriveSubsystem() {
        // Configs for left side (usually not inverted)
        SparkBaseConfig leftMasterConfig = new SparkMaxConfig()
            .inverted(false)
            .idleMode(IdleMode.kBrake);  // or kCoast

        SparkBaseConfig leftFollowerConfig = new SparkMaxConfig()
            .follow(leftMaster)
            .idleMode(IdleMode.kBrake);

        // Configs for right side (usually inverted for forward sync)
        SparkBaseConfig rightMasterConfig = new SparkMaxConfig()
            .inverted(true)
            .idleMode(IdleMode.kBrake);

        SparkBaseConfig rightFollowerConfig = new SparkMaxConfig()
            .follow(rightMaster)
            .idleMode(IdleMode.kBrake);

        // Apply configs (use kResetSafeParameters for testing; kNoResetSafeParameters if you want to preserve)
        leftMaster.configure(leftMasterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        leftFollower.configure(leftFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rightMaster.configure(rightMasterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rightFollower.configure(rightFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        // DifferentialDrive uses the masters directly (followers are handled by hardware)
        drive = new DifferentialDrive(leftMaster, rightMaster);
    }

    public void tankDrive(double leftSpeed, double rightSpeed) {
        drive.tankDrive(leftSpeed, rightSpeed);
    }

    /**
     * @param speed    forward is positive
     * @param rotation counterclockwise (left) is positive
     */
    public void arcadeDrive(double speed, double rotation) {
        drive.arcadeDrive(speed, rotation);
    }

    public void stop() {
        drive.stopMotor();
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Drive/Left Output", leftMaster.get());
        SmartDashboard.putNumber("Drive/Right Output", rightMaster.get());
    }
}