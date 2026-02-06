package org.firstinspires.ftc.teamcode.subsystems;

/**
 * Calculates shooter velocity and angle for scoring in baskets.
 * Uses projectile motion physics to determine optimal launch parameters.
 * 
 * DECODE 2025-26 Season - Basket Scoring
 */
public class ShooterKinematics {
    
    // Physical constants
    private static final double GRAVITY = 9.81; // m/s^2 (or 386.4 in/s^2 if using inches)
    
    // Robot-specific measurements (CONFIGURE THESE FOR YOUR ROBOT)
    private double shooterHeight = 0.3;  // Height of shooter above ground (meters)
    private double basketHeight = 1.2;   // Height of basket rim (meters) - ADJUST FOR DECODE
    private double launchAngle = 45.0;   // Launch angle in degrees
    
    /**
     * Constructs ShooterKinematics with default values.
     * You should configure these for your specific robot.
     */
    public ShooterKinematics() {
        // Use defaults
    }
    
    /**
     * Constructs ShooterKinematics with custom robot measurements.
     * 
     * @param shooterHeight Height of shooter above ground (meters)
     * @param basketHeight Height of basket rim (meters)
     * @param launchAngle Launch angle in degrees
     */
    public ShooterKinematics(double shooterHeight, double basketHeight, double launchAngle) {
        this.shooterHeight = shooterHeight;
        this.basketHeight = basketHeight;
        this.launchAngle = launchAngle;
    }
    
    /**
     * Calculates required launch velocity for a given horizontal distance.
     * Uses the projectile motion equation.
     * 
     * @param horizontalDistance Distance to target in meters
     * @return Required launch velocity in m/s
     */
    public double calculateLaunchVelocity(double horizontalDistance) {
        double angleRad = Math.toRadians(launchAngle);
        double heightDiff = basketHeight - shooterHeight;
        
        // Projectile motion equation solved for initial velocity:
        // v = sqrt(g * d^2 / (2 * cos^2(θ) * (d * tan(θ) - h)))
        // where: g = gravity, d = horizontal distance, θ = angle, h = height difference
        
        double cosAngle = Math.cos(angleRad);
        double tanAngle = Math.tan(angleRad);
        
        double numerator = GRAVITY * horizontalDistance * horizontalDistance;
        double denominator = 2 * cosAngle * cosAngle * (horizontalDistance * tanAngle - heightDiff);
        
        if (denominator <= 0) {
            // Target is unreachable at this angle
            return -1;
        }
        
        return Math.sqrt(numerator / denominator);
    }
    
    /**
     * Calculates required launch velocity using Limelight distance estimation.
     * Uses the target area to estimate distance.
     * 
     * @param targetArea Target area from Limelight (0-100)
     * @param knownTagSize Known size of AprilTag in meters (typically 0.165m for 6.5")
     * @return Required launch velocity in m/s, or -1 if unreachable
     */
    public double calculateVelocityFromArea(double targetArea, double knownTagSize) {
        // Estimate distance from target area
        // This is an approximation - calibrate for your camera
        double distance = estimateDistanceFromArea(targetArea, knownTagSize);
        return calculateLaunchVelocity(distance);
    }
    
    /**
     * Calculates required launch velocity using Limelight vertical angle.
     * More accurate than area-based estimation.
     * 
     * @param verticalAngle Vertical angle to target in degrees (ty from Limelight)
     * @param cameraHeight Height of camera above ground (meters)
     * @param cameraMountAngle Angle of camera mount in degrees (positive = tilted up)
     * @return Required launch velocity in m/s, or -1 if unreachable
     */
    public double calculateVelocityFromAngle(double verticalAngle, double cameraHeight, 
                                             double cameraMountAngle) {
        // Calculate horizontal distance using trigonometry
        double totalAngle = cameraMountAngle + verticalAngle;
        double heightDiff = basketHeight - cameraHeight;
        
        if (Math.abs(totalAngle) >= 90) {
            return -1; // Invalid angle
        }
        
        double distance = heightDiff / Math.tan(Math.toRadians(totalAngle));
        
        if (distance <= 0) {
            return -1; // Target is behind or at same height
        }
        
        return calculateLaunchVelocity(distance);
    }
    
    /**
     * Calculates the optimal launch angle for a given distance and velocity.
     * Useful if you have a fixed shooter speed.
     * 
     * @param horizontalDistance Distance to target in meters
     * @param launchVelocity Available launch velocity in m/s
     * @return Optimal launch angle in degrees, or -1 if unreachable
     */
    public double calculateOptimalAngle(double horizontalDistance, double launchVelocity) {
        double heightDiff = basketHeight - shooterHeight;
        double v2 = launchVelocity * launchVelocity;
        double v4 = v2 * v2;
        double gd = GRAVITY * horizontalDistance;
        
        // Quadratic formula solution for launch angle
        double discriminant = v4 - GRAVITY * (GRAVITY * horizontalDistance * horizontalDistance + 
                                               2 * heightDiff * v2);
        
        if (discriminant < 0) {
            return -1; // Unreachable with this velocity
        }
        
        // Two possible angles - use the lower one for efficiency
        double angle1 = Math.atan((v2 - Math.sqrt(discriminant)) / gd);
        double angle2 = Math.atan((v2 + Math.sqrt(discriminant)) / gd);
        
        // Return the lower angle (more efficient trajectory)
        return Math.toDegrees(Math.min(angle1, angle2));
    }
    
    /**
     * Converts shooter motor RPM to launch velocity.
     * 
     * @param rpm Motor RPM
     * @param wheelDiameter Flywheel diameter in meters
     * @return Launch velocity in m/s
     */
    public double rpmToVelocity(double rpm, double wheelDiameter) {
        // Circumference = π * diameter
        // Velocity = (RPM / 60) * circumference
        double circumference = Math.PI * wheelDiameter;
        return (rpm / 60.0) * circumference;
    }
    
    /**
     * Converts desired launch velocity to required motor RPM.
     * 
     * @param velocity Desired launch velocity in m/s
     * @param wheelDiameter Flywheel diameter in meters
     * @return Required motor RPM
     */
    public double velocityToRpm(double velocity, double wheelDiameter) {
        // RPM = (velocity / circumference) * 60
        double circumference = Math.PI * wheelDiameter;
        return (velocity / circumference) * 60.0;
    }
    
    /**
     * Estimates distance from target area (rough approximation).
     * Should be calibrated for your specific camera and tag size.
     * 
     * @param targetArea Target area percentage (0-100)
     * @param knownTagSize Known AprilTag size in meters
     * @return Estimated distance in meters
     */
    private double estimateDistanceFromArea(double targetArea, double knownTagSize) {
        // This is a rough approximation: distance ≈ k / sqrt(area)
        // Calibration constant - adjust based on testing
        double k = 50.0;
        
        if (targetArea <= 0) {
            return Double.MAX_VALUE;
        }
        
        return k / Math.sqrt(targetArea);
    }
    
    /**
     * Calculates time of flight for the projectile.
     * 
     * @param horizontalDistance Distance to target in meters
     * @param launchVelocity Launch velocity in m/s
     * @return Time of flight in seconds
     */
    public double calculateTimeOfFlight(double horizontalDistance, double launchVelocity) {
        double angleRad = Math.toRadians(launchAngle);
        return horizontalDistance / (launchVelocity * Math.cos(angleRad));
    }
    
    /**
     * Checks if a target is reachable with the given velocity.
     * 
     * @param horizontalDistance Distance to target in meters
     * @param launchVelocity Available launch velocity in m/s
     * @return true if target is reachable
     */
    public boolean isTargetReachable(double horizontalDistance, double launchVelocity) {
        return calculateOptimalAngle(horizontalDistance, launchVelocity) >= 0;
    }
    
    // Getters and setters
    
    public void setShooterHeight(double shooterHeight) {
        this.shooterHeight = shooterHeight;
    }
    
    public void setBasketHeight(double basketHeight) {
        this.basketHeight = basketHeight;
    }
    
    public void setLaunchAngle(double launchAngle) {
        this.launchAngle = launchAngle;
    }
    
    public double getShooterHeight() {
        return shooterHeight;
    }
    
    public double getBasketHeight() {
        return basketHeight;
    }
    
    public double getLaunchAngle() {
        return launchAngle;
    }
}
