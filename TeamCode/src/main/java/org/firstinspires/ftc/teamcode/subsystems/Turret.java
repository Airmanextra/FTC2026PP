package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Turret subsystem for controlling a two-servo turret mechanism.
 * 
 * The turret uses two continuous rotation servos that spin in the same direction
 * to rotate the turret mechanism. The servos are mechanically coupled through gears
 * that reverse the direction, so when the servos spin clockwise, the turret rotates
 * counterclockwise.
 */
public class Turret {
    // Servo instances
    private CRServo leftServo;
    private CRServo rightServo;
    
    // State tracking
    private double currentPower;
    
    // Default hardware names
    private static final String DEFAULT_LEFT_SERVO_NAME = "turretRotOne";
    private static final String DEFAULT_RIGHT_SERVO_NAME = "turretRotTwo";
    
    /**
     * Constructs a Turret with default servo names.
     * 
     * @param hardwareMap The FTC hardware map for retrieving configured devices
     * @throws IllegalArgumentException if a servo cannot be found in the hardware map
     */
    public Turret(HardwareMap hardwareMap) {
        this(hardwareMap, DEFAULT_LEFT_SERVO_NAME, DEFAULT_RIGHT_SERVO_NAME);
    }
    
    /**
     * Constructs a Turret with custom servo names.
     * 
     * @param hardwareMap The FTC hardware map for retrieving configured devices
     * @param leftServoName The hardware name for the left servo
     * @param rightServoName The hardware name for the right servo
     * @throws IllegalArgumentException if a servo cannot be found in the hardware map
     */
    public Turret(HardwareMap hardwareMap, String leftServoName, String rightServoName) {
        // Initialize current power to zero (stopped)
        this.currentPower = 0.0;
        
        // Retrieve left servo with error handling
        try {
            this.leftServo = hardwareMap.get(CRServo.class, leftServoName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find servo: " + leftServoName);
        }
        
        // Retrieve right servo with error handling
        try {
            this.rightServo = hardwareMap.get(CRServo.class, rightServoName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find servo: " + rightServoName);
        }
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
     * Sets the rotation power for both servos.
     * 
     * Positive power rotates the turret counterclockwise (accounting for gear reversal).
     * Negative power rotates the turret clockwise.
     * Power values outside [-1.0, 1.0] are clamped to the valid range.
     * 
     * @param power The desired power value in the range [-1.0, 1.0]
     */
    public void setPower(double power) {
        // Clamp the power value to valid range
        double clampedPower = clampPower(power);
        
        // Set both servos to the same power value
        leftServo.setPower(clampedPower);
        rightServo.setPower(clampedPower);
        
        // Update the current power state
        currentPower = clampedPower;
    }
    
    /**
     * Rotates the turret counterclockwise (left) at the specified speed.
     * 
     * This is a convenience method that calls setPower with a positive speed value.
     * The speed will be clamped to the valid range [0, 1.0].
     * 
     * @param speed The desired rotation speed (positive value)
     */
    public void rotateLeft(double speed) {
        setPower(speed);
    }
    
    /**
     * Rotates the turret clockwise (right) at the specified speed.
     * 
     * This is a convenience method that calls setPower with a negative speed value.
     * The speed will be clamped to the valid range [0, 1.0].
     * 
     * @param speed The desired rotation speed (positive value)
     */
    public void rotateRight(double speed) {
        setPower(-speed);
    }
    
    /**
     * Stops the turret rotation by setting both servos to zero power.
     * 
     * The turret will maintain its current position until a new rotation command is issued.
     */
    public void stop() {
        setPower(0);
    }
    
    /**
     * Clamps a power value to the valid range [-1.0, 1.0].
     * 
     * @param power The power value to clamp
     * @return The clamped power value
     */
    private double clampPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }
}
