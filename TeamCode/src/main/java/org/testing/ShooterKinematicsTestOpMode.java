package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.ShooterKinematics;

/**
 * Test OpMode for calibrating shooter kinematics.
 * Use this to verify calculations at known distances.
 * 
 * Controls:
 * - D-Pad Up/Down: Adjust test distance
 * - D-Pad Left/Right: Adjust launch angle
 * - A: Increase shooter height
 * - B: Decrease shooter height
 * 
 * Place robot at the displayed distance and verify the calculated RPM works.
 */
@TeleOp(name = "Shooter Kinematics Test", group = "Testing")
public class ShooterKinematicsTestOpMode extends LinearOpMode {
    
    private ShooterKinematics kinematics;
    
    // Adjustable parameters
    private double testDistance = 2.0;      // meters
    private double shooterHeight = 0.3;     // meters
    private double basketHeight = 1.2;      // meters
    private double launchAngle = 45.0;      // degrees
    private double flywheelDiameter = 0.1;  // meters
    
    // Adjustment increments
    private static final double DISTANCE_INCREMENT = 0.1;  // 10cm
    private static final double HEIGHT_INCREMENT = 0.01;   // 1cm
    private static final double ANGLE_INCREMENT = 1.0;     // 1 degree
    
    @Override
    public void runOpMode() {
        // Initialize kinematics
        kinematics = new ShooterKinematics(shooterHeight, basketHeight, launchAngle);
        
        telemetry.addData("Status", "Initialized");
        telemetry.addData("", "Shooter Kinematics Test");
        telemetry.addData("", "");
        telemetry.addData("Controls", "");
        telemetry.addData("  D-Pad Up/Down", "Distance ±10cm");
        telemetry.addData("  D-Pad Left/Right", "Angle ±1°");
        telemetry.addData("  A/B", "Shooter Height ±1cm");
        telemetry.update();
        
        waitForStart();
        
        while (opModeIsActive()) {
            // Handle input
            handleInput();
            
            // Update kinematics with current parameters
            kinematics.setShooterHeight(shooterHeight);
            kinematics.setBasketHeight(basketHeight);
            kinematics.setLaunchAngle(launchAngle);
            
            // Calculate required velocity
            double velocity = kinematics.calculateLaunchVelocity(testDistance);
            double rpm = kinematics.velocityToRpm(velocity, flywheelDiameter);
            double timeOfFlight = kinematics.calculateTimeOfFlight(testDistance, velocity);
            
            // Display configuration
            telemetry.addData("=== Configuration ===", "");
            telemetry.addData("Test Distance", "%.2f m (%.1f in)", 
                testDistance, testDistance / 0.0254);
            telemetry.addData("Shooter Height", "%.3f m (%.1f in)", 
                shooterHeight, shooterHeight / 0.0254);
            telemetry.addData("Basket Height", "%.3f m (%.1f in)", 
                basketHeight, basketHeight / 0.0254);
            telemetry.addData("Launch Angle", "%.1f°", launchAngle);
            telemetry.addData("Flywheel Diameter", "%.3f m (%.1f in)", 
                flywheelDiameter, flywheelDiameter / 0.0254);
            
            telemetry.addData("", "");
            
            // Display calculations
            if (velocity > 0) {
                telemetry.addData("=== Calculations ===", "");
                telemetry.addData("Required Velocity", "%.2f m/s", velocity);
                telemetry.addData("Required RPM", "%.0f", rpm);
                telemetry.addData("Time of Flight", "%.2f seconds", timeOfFlight);
                telemetry.addData("", "");
                telemetry.addData("Status", "✓ Target Reachable");
            } else {
                telemetry.addData("=== Calculations ===", "");
                telemetry.addData("Status", "✗ TARGET UNREACHABLE");
                telemetry.addData("", "Try:");
                telemetry.addData("  - Increase launch angle", "");
                telemetry.addData("  - Decrease distance", "");
                telemetry.addData("  - Increase shooter height", "");
            }
            
            telemetry.addData("", "");
            telemetry.addData("=== Instructions ===", "");
            telemetry.addData("1.", "Place robot at test distance");
            telemetry.addData("2.", "Set shooter to calculated RPM");
            telemetry.addData("3.", "Shoot and observe");
            telemetry.addData("4.", "Adjust parameters if needed");
            
            telemetry.update();
            
            sleep(100);  // Prevent button spam
        }
    }
    
    /**
     * Handles gamepad input for adjusting parameters.
     */
    private void handleInput() {
        // Adjust distance
        if (gamepad1.dpad_up) {
            testDistance += DISTANCE_INCREMENT;
        } else if (gamepad1.dpad_down) {
            testDistance = Math.max(0.1, testDistance - DISTANCE_INCREMENT);
        }
        
        // Adjust launch angle
        if (gamepad1.dpad_right) {
            launchAngle = Math.min(89.0, launchAngle + ANGLE_INCREMENT);
        } else if (gamepad1.dpad_left) {
            launchAngle = Math.max(1.0, launchAngle - ANGLE_INCREMENT);
        }
        
        // Adjust shooter height
        if (gamepad1.a) {
            shooterHeight += HEIGHT_INCREMENT;
        } else if (gamepad1.b) {
            shooterHeight = Math.max(0.01, shooterHeight - HEIGHT_INCREMENT);
        }
        
        // Adjust basket height (Y/X buttons)
        if (gamepad1.y) {
            basketHeight += HEIGHT_INCREMENT;
        } else if (gamepad1.x) {
            basketHeight = Math.max(0.01, basketHeight - HEIGHT_INCREMENT);
        }
    }
}
