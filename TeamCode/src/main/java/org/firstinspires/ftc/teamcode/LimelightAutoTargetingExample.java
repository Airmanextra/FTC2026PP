package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.subsystems.TurretTargeting;

/**
 * Example Autonomous OpMode demonstrating Limelight-based targeting.
 * 
 * This example shows how to:
 * 1. Initialize the targeting system
 * 2. Wait for a target to be acquired
 * 3. Automatically aim at the target
 * 4. Perform an action when on target (e.g., shoot)
 * 
 * DECODE 2025-26 Season
 */
@Autonomous(name = "Limelight Auto Targeting Example", group = "Examples")
@Disabled  // Remove this line when ready to use
public class LimelightAutoTargetingExample extends LinearOpMode {
    
    private TurretTargeting targeting;
    
    // Alliance selection - change this based on your alliance
    private static final boolean IS_RED_ALLIANCE = true;
    
    @Override
    public void runOpMode() {
        // Initialize targeting system
        targeting = new TurretTargeting(hardwareMap);
        
        telemetry.addData("Status", "Initialized");
        telemetry.addData("Alliance", IS_RED_ALLIANCE ? "RED" : "BLUE");
        telemetry.update();
        
        waitForStart();
        
        if (opModeIsActive()) {
            // Example autonomous sequence
            autonomousTargetAndShoot();
        }
        
        // Clean up
        targeting.stop();
    }
    
    /**
     * Example autonomous sequence: acquire target, aim, and shoot.
     */
    private void autonomousTargetAndShoot() {
        // Step 1: Wait for target acquisition
        telemetry.addData("Step", "1. Acquiring Target...");
        telemetry.update();
        
        if (!waitForTarget(3.0)) {
            telemetry.addData("Error", "Target not found!");
            telemetry.update();
            sleep(2000);
            return;
        }
        
        // Step 2: Aim at target
        telemetry.addData("Step", "2. Aiming at Target...");
        telemetry.update();
        
        if (!aimAtTarget(5.0)) {
            telemetry.addData("Error", "Could not aim at target!");
            telemetry.update();
            sleep(2000);
            return;
        }
        
        // Step 3: On target - ready to shoot
        telemetry.addData("Step", "3. ON TARGET!");
        telemetry.addData("Status", "Ready to shoot");
        telemetry.update();
        
        // TODO: Add your shooting mechanism code here
        // Example: shooter.fire();
        
        sleep(1000);
        
        // Step 4: Complete
        telemetry.addData("Step", "4. Complete");
        telemetry.update();
    }
    
    /**
     * Waits for a target to be detected.
     * 
     * @param timeoutSeconds Maximum time to wait in seconds
     * @return true if target found, false if timeout
     */
    private boolean waitForTarget(double timeoutSeconds) {
        double startTime = getRuntime();
        
        while (opModeIsActive() && (getRuntime() - startTime) < timeoutSeconds) {
            targeting.update();
            
            boolean hasTarget = IS_RED_ALLIANCE ? 
                targeting.getVision().hasRedBasketTarget() :
                targeting.getVision().hasBlueBasketTarget();
            
            if (hasTarget) {
                telemetry.addData("Target", "FOUND");
                telemetry.update();
                return true;
            }
            
            telemetry.addData("Target", "Searching...");
            telemetry.addData("Time Remaining", "%.1f s", 
                timeoutSeconds - (getRuntime() - startTime));
            telemetry.update();
            
            sleep(50);
        }
        
        return false;
    }
    
    /**
     * Aims at the target until on target or timeout.
     * 
     * @param timeoutSeconds Maximum time to aim in seconds
     * @return true if on target, false if timeout
     */
    private boolean aimAtTarget(double timeoutSeconds) {
        double startTime = getRuntime();
        
        while (opModeIsActive() && (getRuntime() - startTime) < timeoutSeconds) {
            targeting.update();
            
            // Aim at the appropriate alliance target
            boolean onTarget = IS_RED_ALLIANCE ?
                targeting.aimAtRedBasket() :
                targeting.aimAtBlueBasket();
            
            if (onTarget) {
                telemetry.addData("Aiming", "ON TARGET");
                telemetry.update();
                return true;
            }
            
            // Display aiming status
            double targetX = IS_RED_ALLIANCE ?
                targeting.getVision().getRedBasketX() :
                targeting.getVision().getBlueBasketX();
            
            telemetry.addData("Aiming", "Adjusting...");
            telemetry.addData("Target Offset", "%.2f°", targetX);
            telemetry.addData("Turret Power", "%.2f", 
                targeting.getTurret().getCurrentPower());
            telemetry.addData("Time Remaining", "%.1f s", 
                timeoutSeconds - (getRuntime() - startTime));
            telemetry.update();
            
            sleep(20);
        }
        
        return false;
    }
}
