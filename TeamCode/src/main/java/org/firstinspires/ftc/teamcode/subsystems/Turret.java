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
    private static final String DEFAULT_LEFT_SERVO_NAME = "turretLeft";
    private static final String DEFAULT_RIGHT_SERVO_NAME = "turretRight";
    
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
}
