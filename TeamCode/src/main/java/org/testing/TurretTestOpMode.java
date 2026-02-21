package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

/**
 * TeleOp OpMode for testing the Turret subsystem.
 * 
 * Controls:
 * - Left Bumper: Rotate turret left (counterclockwise) at full speed
 * - Right Bumper: Rotate turret right (clockwise) at full speed
 * - Left Trigger: Rotate turret left (counterclockwise) at variable speed
 * - Right Trigger: Rotate turret right (clockwise) at variable speed
 * 
 * Telemetry displays:
 * - Current power setting
 * - Current rotation direction
 * - Control instructions
 */
@TeleOp(name = "Turret Test", group = "Test")
public class TurretTestOpMode extends OpMode {
    
    private Turret turret;
    
    /**
     * Initializes the turret subsystem.
     * Called once when the driver presses INIT.
     */
    @Override
    public void init() {
        try {
            // Initialize the turret with default servo names
            turret = new Turret(hardwareMap);
            
            // Display initialization success
            telemetry.addData("Status", "Turret initialized successfully");
            telemetry.addData("Info", "Use bumpers or triggers to control turret");
            telemetry.update();
        } catch (IllegalArgumentException e) {
            // Display initialization error
            telemetry.addData("Error", "Failed to initialize turret");
            telemetry.addData("Details", e.getMessage());
            telemetry.update();
        }
    }
    
    /**
     * Runs repeatedly while the OpMode is active.
     * Handles gamepad input and updates telemetry.
     */
    @Override
    public void loop() {
        // Check if turret was initialized successfully
        if (turret == null) {
            telemetry.addData("Error", "Turret not initialized");
            telemetry.update();
            return;
        }
        
        // Handle gamepad input for turret control
        // Priority: Bumpers > Triggers > Stop
        
        if (gamepad1.left_bumper) {
            // Left bumper: rotate left at full speed
            turret.rotateLeft(1.0);
        } else if (gamepad1.right_bumper) {
            // Right bumper: rotate right at full speed
            turret.rotateRight(1.0);
        } else if (gamepad1.left_trigger > 0.1) {
            // Left trigger: rotate left at variable speed
            turret.rotateLeft(gamepad1.left_trigger);
        } else if (gamepad1.right_trigger > 0.1) {
            // Right trigger: rotate right at variable speed
            turret.rotateRight(gamepad1.right_trigger);
        } else {
            // No input: stop the turret
            turret.stop();
        }
        
        // Display telemetry
        telemetry.addData("Status", "Running");
        telemetry.addData("", ""); // Blank line for readability
        
        // Display current turret state
        telemetry.addData("Power", "%.2f", turret.getCurrentPower());
        telemetry.addData("Direction", turret.getDirection());
        telemetry.addData("", ""); // Blank line for readability
        
        // Display control instructions
        telemetry.addData("Controls", "");
        telemetry.addData("  Left Bumper", "Rotate Left (Full Speed)");
        telemetry.addData("  Right Bumper", "Rotate Right (Full Speed)");
        telemetry.addData("  Left Trigger", "Rotate Left (Variable)");
        telemetry.addData("  Right Trigger", "Rotate Right (Variable)");
        
        telemetry.update();
    }
    
    /**
     * Called when the OpMode is stopped.
     * Ensures the turret is stopped when the OpMode ends.
     */
    @Override
    public void stop() {
        if (turret != null) {
            turret.stop();
        }
    }
}
