package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Roller intake subsystem for collecting game pieces and transferring them.
 *
 * Uses a goBILDA 512 RPM Yellow Jacket motor (5202/5203 series with 13.7:1 ratio).
 * Motor encoder: 28 PPR × 4 (quadrature) × 13.7 (gear ratio) = ~1536 counts per revolution.
 * 
 * This motor drives both the intake rollers and transfer mechanism via belt.
 * Positive power intakes and transfers; negative power reverses both.
 */
public class Intake {

    private final DcMotorEx intakeMotor;

    // State tracking
    private double currentPower;

    // Default hardware name
    private static final String DEFAULT_INTAKE_MOTOR_NAME = "intakeMotor";
    
    /**
     * Encoder counts per output shaft revolution for goBILDA 512 RPM motor.
     * 28 PPR × 4 (quadrature) × 13.7 (gear ratio) ≈ 1536 counts/rev
     */
    public static final double TICKS_PER_REV = 1536.0;

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
     * Runs the intake and transfer to pull in and move game pieces.
     *
     * @param power Power in range [0, 1.0]
     */
    public void intake(double power) {
        setPower(Math.abs(power));
    }

    /**
     * Runs the intake and transfer in reverse to expel game pieces.
     *
     * @param power Power in range [0, 1.0]
     */
    public void outtake(double power) {
        setPower(-Math.abs(power));
    }

    /**
     * Alias for intake() - runs both intake and transfer.
     *
     * @param power Power in range [0, 1.0]
     */
    public void transfer(double power) {
        intake(power);
    }

    /**
     * Alias for outtake() - reverses both intake and transfer.
     *
     * @param power Power in range [0, 1.0]
     */
    public void reverse(double power) {
        outtake(power);
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
