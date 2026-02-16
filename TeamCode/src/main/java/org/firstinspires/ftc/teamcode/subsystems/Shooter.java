package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Shooter subsystem for controlling a single flywheel shooter motor.
 *
 * This implementation is configured for a goBILDA 5203-2402-0001
 * Yellow Jacket planetary gear motor (5.2:1 ratio, ~1150 RPM no-load).
 *
 * The key configuration detail for closed-loop velocity control is the
 * encoder resolution in ticks per output shaft revolution.
 */
public class Shooter {

    // Default hardware name (matches HARDWARE_CONFIG.md)
    private static final String DEFAULT_SHOOTER_MOTOR_NAME = "shooterMotor";

    /**
     * Encoder ticks per output shaft revolution for the
     * goBILDA 5203‑2402‑0001 Yellow Jacket motor.
     *
     * According to goBILDA documentation this motor provides
     * approximately 145.1 counts per output revolution.
     *
     * You can fine‑tune this value based on your own measurements
     * if needed.
     */
    public static final double TICKS_PER_REV = 145.1;

    private final DcMotorEx shooterMotor;

    // Tracks the last commanded RPM for convenience / telemetry
    private double targetRPM = 0.0;

    /**
     * Constructs a Shooter using the default hardware name
     * defined in {@link #DEFAULT_SHOOTER_MOTOR_NAME}.
     *
     * @param hardwareMap FTC hardware map
     * @throws IllegalArgumentException if the motor cannot be found
     */
    public Shooter(HardwareMap hardwareMap) {
        this(hardwareMap, DEFAULT_SHOOTER_MOTOR_NAME);
    }

    /**
     * Constructs a Shooter with a custom motor hardware name.
     *
     * @param hardwareMap FTC hardware map
     * @param motorName   configured hardware name of the shooter motor
     * @throws IllegalArgumentException if the motor cannot be found
     */
    public Shooter(HardwareMap hardwareMap, String motorName) {
        try {
            this.shooterMotor = hardwareMap.get(DcMotorEx.class, motorName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find shooter motor: " + motorName);
        }

        // Default configuration for a flywheel shooter
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    /**
     * Sets the shooter motor to a specific RPM using closed-loop
     * velocity control.
     *
     * @param rpm desired motor RPM
     */
    public void setRPM(double rpm) {
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterMotor.setVelocity(rpmToTicksPerSecond(rpm));
        targetRPM = rpm;
    }

    /**
     * Sets the shooter motor to an open-loop power value.
     *
     * @param power motor power in the range [-1.0, 1.0]
     */
    public void setPower(double power) {
        shooterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooterMotor.setPower(power);
        // When in power mode targetRPM is no longer meaningful
        targetRPM = 0.0;
    }

    /**
     * Stops the shooter motor.
     */
    public void stop() {
        shooterMotor.setPower(0.0);
    }

    /**
     * @return current shooter velocity in RPM
     */
    public double getCurrentRPM() {
        double ticksPerSecond = shooterMotor.getVelocity();
        return ticksPerSecondToRPM(ticksPerSecond);
    }

    /**
     * @return last commanded target RPM (0 if in power mode or not set)
     */
    public double getTargetRPM() {
        return targetRPM;
    }

    /**
     * Checks if the shooter is at the given target RPM within a tolerance.
     *
     * @param targetRPM desired RPM
     * @param tolerance allowable deviation in RPM
     * @return true if current RPM is within tolerance of target
     */
    public boolean isAtTargetVelocity(double targetRPM, double tolerance) {
        double currentRPM = getCurrentRPM();
        return Math.abs(currentRPM - targetRPM) <= tolerance;
    }

    /**
     * Gives direct access to the underlying motor if needed
     * for advanced control or tuning.
     *
     * @return DcMotorEx shooter motor
     */
    public DcMotorEx getMotor() {
        return shooterMotor;
    }

    /**
     * Utility: converts RPM to encoder ticks per second.
     */
    private double rpmToTicksPerSecond(double rpm) {
        // ticks/second = RPM * (ticks/rev) / 60
        return rpm * TICKS_PER_REV / 60.0;
    }

    /**
     * Utility: converts encoder ticks per second to RPM.
     */
    private double ticksPerSecondToRPM(double ticksPerSecond) {
        // RPM = ticks/second * 60 / (ticks/rev)
        return ticksPerSecond * 60.0 / TICKS_PER_REV;
    }
}

