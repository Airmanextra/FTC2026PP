package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Roller intake subsystem for collecting game pieces.
 *
 * Uses a goBILDA 512 RPM Yellow Jacket motor (or compatible DcMotorEx).
 * Positive power intakes (pulls in); negative power outtakes (expels).
 */
public class Intake {

    private final DcMotorEx intakeMotor;

    // State tracking
    private double currentPower;

    // Default hardware name
    private static final String DEFAULT_INTAKE_MOTOR_NAME = "intakeMotor";

    /**
     * Constructs an Intake with the default motor name.
     *
     * @param hardwareMap The FTC hardware map
     * @throws IllegalArgumentException if the motor cannot be found
     */
    public Intake(HardwareMap hardwareMap) {
        this(hardwareMap, DEFAULT_INTAKE_MOTOR_NAME);
    }

    /**
     * Constructs an Intake with a custom motor name.
     *
     * @param hardwareMap The FTC hardware map
     * @param motorName   The hardware name for the intake motor
     * @throws IllegalArgumentException if the motor cannot be found
     */
    public Intake(HardwareMap hardwareMap, String motorName) {
        this.currentPower = 0.0;

        try {
            this.intakeMotor = hardwareMap.get(DcMotorEx.class, motorName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find intake motor: " + motorName);
        }

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Sets the intake power. Positive = intake (pull in), negative = outtake (expel).
     *
     * @param power Power in range [-1.0, 1.0]
     */
    public void setPower(double power) {
        double clamped = clampPower(power);
        intakeMotor.setPower(clamped);
        currentPower = clamped;
    }

    /**
     * Runs the intake to pull in game pieces.
     *
     * @param power Power in range [0, 1.0]
     */
    public void intake(double power) {
        setPower(Math.abs(power));
    }

    /**
     * Runs the intake in reverse to expel game pieces.
     *
     * @param power Power in range [0, 1.0]
     */
    public void outtake(double power) {
        setPower(-Math.abs(power));
    }

    /**
     * Stops the intake motor.
     */
    public void stop() {
        setPower(0);
    }

    /**
     * @return Current commanded power
     */
    public double getCurrentPower() {
        return currentPower;
    }

    /**
     * @return The underlying motor for advanced control
     */
    public DcMotorEx getMotor() {
        return intakeMotor;
    }

    private double clampPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }
}
