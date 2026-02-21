package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Complete smart shooting system with auto-aim and velocity calculation.
 * 
 * Controls:
 * - Left Stick X: Manual turret control
 * - A Button: Auto-aim and calculate shooter velocity (Red Alliance)
 * - B Button: Manual shooter at fixed speed
 * - X Button: Stop shooter
 * 
 * DECODE 2025-26 Season
 */
@TeleOp(name = "Smart Shooter - Red Alliance", group = "Competition")
public class SmartShooterOpMode extends LinearOpMode {
    
    private TurretTargeting targeting;
    private SmartShooter shooter;
    
    // Configuration - ADJUST FOR YOUR ROBOT
    private static final double SHOOTER_HEIGHT = 0.3;      // meters
    private static final double BASKET_HEIGHT = 1.2;       // meters - ADJUST FOR DECODE
    private static final double LAUNCH_ANGLE = 45.0;       // degrees
    private static final double CAMERA_HEIGHT = 0.25;      // meters
    private static final double CAMERA_ANGLE = 15.0;       // degrees
    private static final double FLYWHEEL_DIAMETER = 0.1;   // meters (100mm)
    private static final double RPM_TOLERANCE = 50.0;      // RPM
    
    @Override
    public void runOpMode() {
        // Initialize systems
        targeting = new TurretTargeting(hardwareMap);
        shooter = new SmartShooter(hardwareMap);
        
        // Configure shooter kinematics
        shooter.configureKinematics(SHOOTER_HEIGHT, BASKET_HEIGHT, LAUNCH_ANGLE);
        shooter.configureCamera(CAMERA_HEIGHT, CAMERA_ANGLE);
        shooter.setFlywheelDiameter(FLYWHEEL_DIAMETER);
        
        telemetry.addData("Status", "Initialized - Smart Shooter");
        telemetry.addData("", "");
        telemetry.addData("Controls", "");
        telemetry.addData("  Left Stick X", "Manual Turret");
        telemetry.addData("  A Button", "Auto-Aim + Smart Velocity");
        telemetry.addData("  B Button", "Manual Shooter");
        telemetry.addData("  X Button", "Stop Shooter");
        telemetry.update();
        
        waitForStart();
        
        while (opModeIsActive()) {
            // Update systems
            targeting.update();
            shooter.update();
            
            // Control logic
            if (gamepad1.a) {
                // AUTO MODE: Aim and calculate velocity
                autoShootMode();
            } else if (gamepad1.b) {
                // MANUAL SHOOTER MODE
                manualShooterMode();
            } else if (gamepad1.x) {
                // STOP SHOOTER
                shooter.stopShooter();
                telemetry.addData("Mode", "SHOOTER STOPPED");
            } else {
                // MANUAL TURRET ONLY
                manualTurretMode();
            }
            
            // Display telemetry
            displayTelemetry();
            telemetry.update();
        }
        
        // Cleanup
        targeting.stop();
        shooter.stop();
    }
    
    /**
     * Auto-aim and calculate optimal shooter velocity.
     */
    private void autoShootMode() {
        telemetry.addData("Mode", "AUTO-AIM + SMART VELOCITY");
        
        // Aim turret at target
        boolean turretOnTarget = targeting.aimAtRedBasket();
        
        // Calculate and set shooter velocity
        boolean velocitySet = shooter.shootAtRedBasket();
        
        if (velocitySet) {
            double requiredRPM = shooter.getRequiredRPM(true);
            double currentRPM = shooter.getCurrentRPM();
            boolean shooterReady = shooter.isAtTargetVelocity(requiredRPM, RPM_TOLERANCE);
            
            telemetry.addData("", "--- Shooter Status ---");
            telemetry.addData("Required RPM", "%.0f", requiredRPM);
            telemetry.addData("Current RPM", "%.0f", currentRPM);
            telemetry.addData("Shooter Ready", shooterReady ? "YES ✓" : "Spinning up...");
            
            telemetry.addData("", "--- Turret Status ---");
            telemetry.addData("Turret On Target", turretOnTarget ? "YES ✓" : "Aiming...");
            
            if (turretOnTarget && shooterReady) {
                telemetry.addData("", "");
                telemetry.addData(">>> READY TO SHOOT <<<", "");
            }
        } else {
            telemetry.addData("Target", "NOT FOUND or UNREACHABLE");
            shooter.stopShooter();
        }
    }
    
    /**
     * Manual shooter at fixed speed.
     */
    private void manualShooterMode() {
        telemetry.addData("Mode", "MANUAL SHOOTER");
        
        // Fixed RPM for testing
        double fixedRPM = 3000;  // Adjust for your robot
        shooter.setShooterRPM(fixedRPM);
        
        telemetry.addData("Target RPM", "%.0f", fixedRPM);
        telemetry.addData("Current RPM", "%.0f", shooter.getCurrentRPM());
        
        // Manual turret control
        double manualPower = -gamepad1.left_stick_x;
        targeting.getTurret().setPower(manualPower);
    }
    
    /**
     * Manual turret control only.
     */
    private void manualTurretMode() {
        telemetry.addData("Mode", "MANUAL TURRET");
        
        double manualPower = -gamepad1.left_stick_x;
        targeting.getTurret().setPower(manualPower);
        
        telemetry.addData("Turret Power", "%.2f", manualPower);
    }
    
    /**
     * Displays comprehensive telemetry.
     */
    private void displayTelemetry() {
        telemetry.addData("", "");
        telemetry.addData("--- Vision ---", "");
        telemetry.addData("Red Basket Visible", targeting.getVision().hasRedBasketTarget());
        
        if (targeting.getVision().hasRedBasketTarget()) {
            telemetry.addData("Target X Offset", "%.2f°", 
                targeting.getVision().getRedBasketX());
            telemetry.addData("Target Y Offset", "%.2f°", 
                targeting.getVision().getTargetY());
        }
        
        telemetry.addData("", "");
        telemetry.addData("--- Current State ---", "");
        telemetry.addData("Turret Power", "%.2f", 
            targeting.getTurret().getCurrentPower());
        telemetry.addData("Turret Direction", 
            targeting.getTurret().getDirection());
        telemetry.addData("Shooter RPM", "%.0f", shooter.getCurrentRPM());
    }
}
