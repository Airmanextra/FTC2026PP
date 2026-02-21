package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Turret subsystem for controlling a motorized turret mechanism.
 *
 * Uses a goBILDA 512 RPM Yellow Jacket motor (5202/5203 series with 13.7:1 ratio).
 * Motor encoder: 28 PPR × 4 (quadrature) × 13.7 (gear ratio) = ~1536 counts per revolution.
 * 
 * The API reflects turret direction (positive = left/CCW, negative = right/CW).
 */
public class Turret {

    private final DcMotorEx turretMotor;

    // State tracking
    private double currentPower;

    // Default hardware name
    private static final String DEFAULT_TURRET_MOTOR_NAME = "turretMotor";
    
    /**
     * Encoder counts per output shaft revolution for goBILDA 512 RPM motor.
     * 28 PPR × 4 (quadrature) × 13.7 (gear ratio) ≈ 1536 counts/rev
     */
    public static final double TICKS_PER_REV = 1536.0;

    /**
     * Constructs a Turret with the default motor name.
     *
     * @param hardwareMap The FTC hardware map for retrieving configured devices
     * @throws IllegalArgumentException if the motor cannot be found in the hardware map
     */
    public Turret(HardwareMap hardwareMap) {
        this(hardwareMap, DEFAULT_TURRET_MOTOR_NAME);
    }

    /**
     * Constructs a Turret with a custom motor name.
     *
     * @param hardwareMap The FTC hardware map for retrieving configured devices
     * @param motorName   The hardware name for the turret motor
     * @throws IllegalArgumentException if the motor cannot be found in the hardware map
     */
    public Turret(HardwareMap hardwareMap, String motorName) {
        this.currentPower = 0.0;

        try {
            this.turretMotor = hardwareMap.get(DcMotorEx.class, motorName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find motor: " + motorName);
        }

        // Configure motor for open-loop power control
        turretMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Gets the current power setting of the turret.
     *
     * @return The current power value in the range [-1.0, 1.0]
     */
    public double getCurrentPower() {
        return currentPower;
    }

    /**
     * Gets the current rotation direction of the turret.
     *
     * @return "LEFT" for counterclockwise rotation (positive power),
     *         "RIGHT" for clockwise rotation (negative power),
     *         "STOPPED" for zero power
     */
    public String getDirection() {
        if (currentPower > 0) {
            return "LEFT";
        } else if (currentPower < 0) {
            return "RIGHT";
        } else {
            return "STOPPED";
        }
    }

    /**
     * Sets the rotation power for the turret motor.
     *
     * Positive power rotates the turret counterclockwise (left).
     * Negative power rotates clockwise (right).
     * Power values outside [-1.0, 1.0] are clamped to the valid range.
     *
     * @param power The desired power value in the range [-1.0, 1.0]
     */
    public void setPower(double power) {
        double clampedPower = clampPower(power);
        // Invert power: gear reverses direction (motor CW → turret CCW)
        turretMotor.setPower(-clampedPower);
        currentPower = clampedPower;
    }

    /**
     * Rotates the turret counterclockwise (left) at the specified speed.
     *
     * @param speed The desired rotation speed (positive value, clamped to [0, 1.0])
     */
    public void rotateLeft(double speed) {
        setPower(speed);
    }

    /**
     * Rotates the turret clockwise (right) at the specified speed.
     *
     * @param speed The desired rotation speed (positive value, clamped to [0, 1.0])
     */
    public void rotateRight(double speed) {
        setPower(-speed);
    }

    /**
     * Stops the turret rotation.
     */
    public void stop() {
        setPower(0);
    }

    /**
     * Gets the underlying motor for advanced control or tuning.
     *
     * @return The turret DcMotorEx instance
     */
    public DcMotorEx getMotor() {
        return turretMotor;
    }

    private double clampPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }
}
