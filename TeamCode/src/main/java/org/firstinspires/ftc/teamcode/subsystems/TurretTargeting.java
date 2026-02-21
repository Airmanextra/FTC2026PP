package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Integrated turret targeting system using Limelight vision.
 * Combines the Turret subsystem with LimelightVision for automatic targeting.
 */
public class TurretTargeting {
    private Turret turret;
    private LimelightVision vision;
    
    // PID-like control constants for targeting
    private double kP = 0.02;  // Proportional gain
    private double minPower = 0.1;  // Minimum power to overcome friction
    private double targetTolerance = 2.0;  // Degrees of acceptable error
    
    /**
     * Constructs a TurretTargeting system with default hardware names.
     * 
     * @param hardwareMap The FTC hardware map
     */
    public TurretTargeting(HardwareMap hardwareMap) {
        this.turret = new Turret(hardwareMap);
        this.vision = new LimelightVision(hardwareMap);
    }
    
    /**
     * Constructs a TurretTargeting system with custom hardware names.
     *
     * @param hardwareMap    The FTC hardware map
     * @param turretMotorName Turret motor name
     * @param limelightName  Limelight device name
     */
    public TurretTargeting(HardwareMap hardwareMap, String turretMotorName, String limelightName) {
        this.turret = new Turret(hardwareMap, turretMotorName);
        this.vision = new LimelightVision(hardwareMap, limelightName);
    }
    
    /**
     * Updates the vision system. Call this in your OpMode loop.
     */
    public void update() {
        vision.update();
    }
    
    /**
     * Automatically aims the turret at the red alliance basket.
     * 
     * @return true if on target, false if still adjusting or no target
     */
    public boolean aimAtRedBasket() {
        if (!vision.hasRedBasketTarget()) {
            turret.stop();
            return false;
        }
        
        double targetX = vision.getRedBasketX();
        return aimAtOffset(targetX);
    }
    
    /**
     * Automatically aims the turret at the blue alliance basket.
     * 
     * @return true if on target, false if still adjusting or no target
     */
    public boolean aimAtBlueBasket() {
        if (!vision.hasBlueBasketTarget()) {
            turret.stop();
            return false;
        }
        
        double targetX = vision.getBlueBasketX();
        return aimAtOffset(targetX);
    }
    
    /**
     * Aims the turret at a specific horizontal offset.
     * 
     * @param targetX Horizontal offset in degrees (positive = right)
     * @return true if on target within tolerance
     */
    private boolean aimAtOffset(double targetX) {
        // Check if we're within tolerance
        if (Math.abs(targetX) < targetTolerance) {
            turret.stop();
            return true;
        }
        
        // Calculate proportional control
        double power = targetX * kP;
        
        // Apply minimum power to overcome friction
        if (Math.abs(power) < minPower) {
            power = Math.signum(power) * minPower;
        }
        
        // Negative because positive tx means target is right, so we rotate right (negative power)
        turret.setPower(-power);
        
        return false;
    }
    
    /**
     * Sets the proportional gain for targeting.
     * Higher values = faster response but more oscillation.
     * 
     * @param kP Proportional gain (default: 0.02)
     */
    public void setProportionalGain(double kP) {
        this.kP = kP;
    }
    
    /**
     * Sets the minimum power to apply when targeting.
     * This helps overcome servo friction.
     * 
     * @param minPower Minimum power (default: 0.1)
     */
    public void setMinimumPower(double minPower) {
        this.minPower = minPower;
    }
    
    /**
     * Sets the targeting tolerance in degrees.
     * 
     * @param tolerance Acceptable error in degrees (default: 2.0)
     */
    public void setTargetTolerance(double tolerance) {
        this.targetTolerance = tolerance;
    }
    
    /**
     * Gets the turret subsystem for manual control.
     * 
     * @return The Turret instance
     */
    public Turret getTurret() {
        return turret;
    }
    
    /**
     * Gets the vision subsystem for direct access.
     * 
     * @return The LimelightVision instance
     */
    public LimelightVision getVision() {
        return vision;
    }
    
    /**
     * Stops both the turret and vision system.
     */
    public void stop() {
        turret.stop();
        vision.stop();
    }
}
