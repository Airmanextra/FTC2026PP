package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Shooter subsystem for controlling dual flywheel shooter motors.
 *
 * Uses two goBILDA 6000 RPM Yellow Jacket motors (5202/5203 series, 1:1 ratio).
 * Left motor (sl) spins clockwise, right motor (sr) spins counterclockwise.
 * Motor encoder: 28 PPR × 4 (quadrature) × 1 (no gearing) = 112 counts per revolution.
 *
 * The key configuration detail for closed-loop velocity control is the
 * encoder resolution in ticks per output shaft revolution.
 */
public class Shooter {

    // Default hardware names
    private static final String DEFAULT_LEFT_MOTOR_NAME = "sl";
    private static final String DEFAULT_RIGHT_MOTOR_NAME = "sr";

    /**
     * Encoder counts per output shaft revolution for the
     * goBILDA 6000 RPM Yellow Jacket motor (1:1 ratio).
     *
     * 28 PPR × 4 (quadrature) = 112 counts per revolution.
     *
     * You can fine-tune this value based on your own measurements if needed.
     */
    public static final double TICKS_PER_REV = 112.0;

    private final DcMotorEx sl;
    private final DcMotorEx sr;

    // Tracks the last commanded RPM for convenience / telemetry
    private double targetRPM = 0.0;

    /**
     * Constructs a Shooter using the default hardware names (sl and sr).
     *
     * @param hardwareMap FTC hardware map
     * @throws IllegalArgumentException if the motors cannot be found
     */
    public Shooter(HardwareMap hardwareMap) {
        this(hardwareMap, DEFAULT_LEFT_MOTOR_NAME, DEFAULT_RIGHT_MOTOR_NAME);
    }

    /**
     * Constructs a Shooter with custom motor hardware names.
     *
     * @param hardwareMap FTC hardware map
     * @param leftMotorName   configured hardware name of the left shooter motor (sl)
     * @param rightMotorName  configured hardware name of the right shooter motor (sr)
     * @throws IllegalArgumentException if the motors cannot be found
     */
    public Shooter(HardwareMap hardwareMap, String leftMotorName, String rightMotorName) {
        try {
            this.sl = hardwareMap.get(DcMotorEx.class, leftMotorName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find left shooter motor: " + leftMotorName);
        }

        try {
            this.sr = hardwareMap.get(DcMotorEx.class, rightMotorName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find right shooter motor: " + rightMotorName);
        }

        // Configure left motor (clockwise)
        sl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        sl.setDirection(DcMotorSimple.Direction.FORWARD);

        // Configure right motor (counterclockwise - reversed)
        sr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        sr.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    /**
     * Sets both shooter motors to a specific RPM using closed-loop
     * velocity control.
     *
     * @param rpm desired motor RPM
     */
    public void setRPM(double rpm) {
        sl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        double ticksPerSecond = rpmToTicksPerSecond(rpm);
        sl.setVelocity(ticksPerSecond);
        sr.setVelocity(ticksPerSecond);
        
        targetRPM = rpm;
    }

    /**
     * Sets both shooter motors to an open-loop power value.
     *
     * @param power motor power in the range [-1.0, 1.0]
     */
    public void setPower(double power) {
        sl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        sl.setPower(power);
        sr.setPower(power);
        
        // When in power mode targetRPM is no longer meaningful
        targetRPM = 0.0;
    }

    /**
     * Stops both shooter motors.
     */
    public void stop() {
        sl.setPower(0.0);
        sr.setPower(0.0);
    }

    /**
     * @return current shooter velocity in RPM (average of both motors)
     */
    public double getCurrentRPM() {
        double leftTicksPerSecond = sl.getVelocity();
        double rightTicksPerSecond = sr.getVelocity();
        
        double leftRPM = ticksPerSecondToRPM(leftTicksPerSecond);
        double rightRPM = ticksPerSecondToRPM(rightTicksPerSecond);
        
        // Return average RPM
        return (leftRPM + rightRPM) / 2.0;
    }

    /**
     * @return current left motor velocity in RPM
     */
    public double getLeftRPM() {
        return ticksPerSecondToRPM(sl.getVelocity());
    }

    /**
     * @return current right motor velocity in RPM
     */
    public double getRightRPM() {
        return ticksPerSecondToRPM(sr.getVelocity());
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
     * Gives direct access to the left motor if needed
     * for advanced control or tuning.
     *
     * @return DcMotorEx left shooter motor
     */
    public DcMotorEx getLeftMotor() {
        return sl;
    }

    /**
     * Gives direct access to the right motor if needed
     * for advanced control or tuning.
     *
     * @return DcMotorEx right shooter motor
     */
    public DcMotorEx getRightMotor() {
        return sr;
    }

    /**
     * Gets the left motor for backward compatibility.
     * @deprecated Use getLeftMotor() instead
     */
    @Deprecated
    public DcMotorEx getMotor() {
        return sl;
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

