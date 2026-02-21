package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Transfer subsystem for moving game pieces from intake to shooter.
 *
 * Uses a goBILDA 512 RPM Yellow Jacket motor (5202/5203 series with 13.7:1 ratio).
 * Motor encoder: 28 PPR × 4 (quadrature) × 13.7 (gear ratio) = ~1536 counts per revolution.
 * 
 * Positive power transfers pieces toward shooter; negative power reverses.
 */
public class Transfer {

    private final DcMotorEx transferMotor;

    // State tracking
    private double currentPower;

    // Default hardware name
    private static final String DEFAULT_TRANSFER_MOTOR_NAME = "transferMotor";
    
    /**
     * Encoder counts per output shaft revolution for goBILDA 512 RPM motor.
     * 28 PPR × 4 (quadrature) × 13.7 (gear ratio) ≈ 1536 counts/rev
     */
    public static final double TICKS_PER_REV = 1536.0;

    /**
     * Constructs a Transfer with the default motor name.
     *
     * @param hardwareMap The FTC hardware map
     * @throws IllegalArgumentException if the motor cannot be found
     */
    public Transfer(HardwareMap hardwareMap) {
        this(hardwareMap, DEFAULT_TRANSFER_MOTOR_NAME);
    }

    /**
     * Constructs a Transfer with a custom motor name.
     *
     * @param hardwareMap The FTC hardware map
     * @param motorName   The hardware name for the transfer motor
     * @throws IllegalArgumentException if the motor cannot be found
     */
    public Transfer(HardwareMap hardwareMap, String motorName) {
        this.currentPower = 0.0;

        try {
            this.transferMotor = hardwareMap.get(DcMotorEx.class, motorName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find transfer motor: " + motorName);
        }

        transferMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        transferMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Sets the transfer power. Positive = toward shooter, negative = reverse.
     *
     * @param power Power in range [-1.0, 1.0]
     */
    public void setPower(double power) {
        double clamped = clampPower(power);
        transferMotor.setPower(clamped);
        currentPower = clamped;
    }

    /**
     * Runs the transfer to move pieces toward the shooter.
     *
     * @param power Power in range [0, 1.0]
     */
    public void transfer(double power) {
        setPower(Math.abs(power));
    }

    /**
     * Runs the transfer in reverse.
     *
     * @param power Power in range [0, 1.0]
     */
    public void reverse(double power) {
        setPower(-Math.abs(power));
    }

    /**
     * Stops the transfer motor.
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
        return transferMotor;
    }

    private double clampPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }
}
