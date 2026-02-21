package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Smart shooter system that automatically calculates required velocity
 * based on distance to target using Limelight vision.
 *
 * Combines LimelightVision with ShooterKinematics and the Shooter
 * motor subsystem for intelligent shooting.
 */
public class SmartShooter {

    private LimelightVision vision;
    private ShooterKinematics kinematics;
    private Shooter shooter;

    // Robot-specific configuration (ADJUST FOR YOUR ROBOT)
    private double flywheelDiameter = 0.1;  // Flywheel diameter in meters (100mm = 0.1m)
    private double cameraHeight = 0.25;     // Camera height in meters
    private double cameraMountAngle = 15.0; // Camera tilt angle in degrees

    // Default hardware names
    private static final String DEFAULT_SHOOTER_MOTOR = "shooterMotor";

    /**
     * Constructs a SmartShooter with default hardware names.
     *
     * @param hardwareMap The FTC hardware map
     */
    public SmartShooter(HardwareMap hardwareMap) {
        this(hardwareMap, DEFAULT_SHOOTER_MOTOR);
    }

    /**
     * Constructs a SmartShooter with custom hardware names.
     *
     * @param hardwareMap      The FTC hardware map
     * @param shooterMotorName Name of the shooter motor
     */
    public SmartShooter(HardwareMap hardwareMap, String shooterMotorName) {
        // Initialize vision
        this.vision = new LimelightVision(hardwareMap);

        // Initialize kinematics with default values
        // ADJUST THESE VALUES FOR YOUR ROBOT
        this.kinematics = new ShooterKinematics(
                0.3,   // Shooter height (meters)
                1.2,   // Basket height (meters) - ADJUST FOR DECODE
                45.0   // Launch angle (degrees)
        );

        // Initialize shooter subsystem (configured for goBILDA 5203-2402-0001)
        this.shooter = new Shooter(hardwareMap, shooterMotorName);
    }

    /**
     * Updates the vision system. Call this in your OpMode loop.
     */
    public void update() {
        vision.update();
    }

    /**
     * Calculates and sets the optimal shooter velocity for red alliance basket.
     *
     * @return true if velocity was set, false if no target or unreachable
     */
    public boolean shootAtRedBasket() {
        if (!vision.hasRedBasketTarget()) {
            return false;
        }

        double verticalAngle = vision.getTargetY();
        return setVelocityForTarget(verticalAngle);
    }

    /**
     * Calculates and sets the optimal shooter velocity for blue alliance basket.
     *
     * @return true if velocity was set, false if no target or unreachable
     */
    public boolean shootAtBlueBasket() {
        if (!vision.hasBlueBasketTarget()) {
            return false;
        }

        double verticalAngle = vision.getTargetY();
        return setVelocityForTarget(verticalAngle);
    }

    /**
     * Sets shooter velocity based on vertical angle to target.
     *
     * @param verticalAngle Vertical angle from Limelight (ty)
     * @return true if velocity was set, false if unreachable
     */
    private boolean setVelocityForTarget(double verticalAngle) {
        // Calculate required launch velocity
        double velocity = kinematics.calculateVelocityFromAngle(
                verticalAngle,
                cameraHeight,
                cameraMountAngle
        );

        if (velocity < 0) {
            // Target unreachable
            stopShooter();
            return false;
        }

        // Convert velocity to RPM
        double rpm = kinematics.velocityToRpm(velocity, flywheelDiameter);

        // Set motor velocity using Shooter subsystem (configured for goBILDA 5203‑2402‑0001)
        shooter.setRPM(rpm);

        return true;
    }

    /**
     * Sets shooter to a fixed RPM.
     *
     * @param rpm Desired motor RPM
     */
    public void setShooterRPM(double rpm) {
        shooter.setRPM(rpm);
    }

    /**
     * Sets shooter to a fixed power (0-1).
     *
     * @param power Motor power (0-1)
     */
    public void setShooterPower(double power) {
        shooter.setPower(power);
    }

    /**
     * Gets the current shooter motor velocity in RPM.
     *
     * @return Current RPM
     */
    public double getCurrentRPM() {
        return shooter.getCurrentRPM();
    }

    /**
     * Gets the current shooter motor power (0-1).
     * Note: Only meaningful when using setShooterPower; when using setShooterRPM this may be 0.
     *
     * @return Current motor power
     */
    public double getShooterPower() {
        return shooter.getMotor().getPower();
    }

    /**
     * Gets the required RPM for the current target.
     *
     * @param isRedAlliance true for red alliance, false for blue
     * @return Required RPM, or -1 if no target or unreachable
     */
    public double getRequiredRPM(boolean isRedAlliance) {
        if (!vision.hasTarget()) {
            return -1;
        }

        double verticalAngle = isRedAlliance ?
                vision.getRedBasketX() : vision.getBlueBasketX();

        double velocity = kinematics.calculateVelocityFromAngle(
                verticalAngle,
                cameraHeight,
                cameraMountAngle
        );

        if (velocity < 0) {
            return -1;
        }

        return kinematics.velocityToRpm(velocity, flywheelDiameter);
    }

    /**
     * Checks if shooter is at target velocity (within tolerance).
     *
     * @param targetRPM Target RPM
     * @param tolerance Acceptable RPM difference
     * @return true if at target velocity
     */
    public boolean isAtTargetVelocity(double targetRPM, double tolerance) {
        return shooter.isAtTargetVelocity(targetRPM, tolerance);
    }

    /**
     * Stops the shooter motor.
     */
    public void stopShooter() {
        shooter.stop();
    }

    /**
     * Configures robot-specific measurements.
     *
     * @param shooterHeight Height of shooter above ground (meters)
     * @param basketHeight  Height of basket rim (meters)
     * @param launchAngle   Launch angle in degrees
     */
    public void configureKinematics(double shooterHeight, double basketHeight, double launchAngle) {
        kinematics.setShooterHeight(shooterHeight);
        kinematics.setBasketHeight(basketHeight);
        kinematics.setLaunchAngle(launchAngle);
    }

    /**
     * Configures camera measurements.
     *
     * @param cameraHeight     Height of camera above ground (meters)
     * @param cameraMountAngle Camera tilt angle in degrees
     */
    public void configureCamera(double cameraHeight, double cameraMountAngle) {
        this.cameraHeight = cameraHeight;
        this.cameraMountAngle = cameraMountAngle;
    }

    /**
     * Configures flywheel diameter.
     *
     * @param diameter Flywheel diameter in meters
     */
    public void setFlywheelDiameter(double diameter) {
        this.flywheelDiameter = diameter;
    }

    /**
     * Gets the vision subsystem for direct access.
     *
     * @return LimelightVision instance
     */
    public LimelightVision getVision() {
        return vision;
    }

    /**
     * Gets the kinematics calculator for direct access.
     *
     * @return ShooterKinematics instance
     */
    public ShooterKinematics getKinematics() {
        return kinematics;
    }

    /**
     * Gets the shooter subsystem for direct access.
     *
     * @return Shooter instance
     */
    public Shooter getShooter() {
        return shooter;
    }

    /**
     * Stops all systems.
     */
    public void stop() {
        stopShooter();
        vision.stop();
    }
}

