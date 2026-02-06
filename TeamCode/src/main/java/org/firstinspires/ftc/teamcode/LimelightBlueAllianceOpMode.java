package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.TurretTargeting;

/**
 * TeleOp mode for Blue Alliance with Limelight AprilTag targeting.
 * 
 * Controls:
 * - Left Stick X: Manual turret control
 * - A Button: Auto-aim at blue alliance basket
 * 
 * DECODE 2025-26 Season
 * Blue Alliance Basket Tags: 14, 15, 16
 */
@TeleOp(name = "Limelight Blue Alliance", group = "Competition")
public class LimelightBlueAllianceOpMode extends LinearOpMode {
    
    private TurretTargeting targeting;
    
    @Override
    public void runOpMode() {
        // Initialize the turret targeting system
        targeting = new TurretTargeting(hardwareMap);
        
        telemetry.addData("Status", "Initialized - Blue Alliance");
        telemetry.addData("Controls", "Left Stick X = Manual Turret");
        telemetry.addData("", "A Button = Auto-Aim Blue Basket");
        telemetry.update();
        
        waitForStart();
        
        while (opModeIsActive()) {
            // Update vision system
            targeting.update();
            
            // Check for auto-aim button
            if (gamepad1.a) {
                // Auto-aim at blue basket
                boolean onTarget = targeting.aimAtBlueBasket();
                
                telemetry.addData("Mode", "AUTO-AIM BLUE BASKET");
                telemetry.addData("On Target", onTarget ? "YES" : "NO");
                
                if (targeting.getVision().hasBlueBasketTarget()) {
                    telemetry.addData("Target X", "%.2f°", targeting.getVision().getBlueBasketX());
                } else {
                    telemetry.addData("Target", "NOT FOUND");
                }
            } else {
                // Manual control with left stick
                double manualPower = -gamepad1.left_stick_x;
                targeting.getTurret().setPower(manualPower);
                
                telemetry.addData("Mode", "MANUAL");
                telemetry.addData("Turret Power", "%.2f", manualPower);
            }
            
            // Display vision info
            telemetry.addData("", "--- Vision Status ---");
            telemetry.addData("Blue Basket Visible", targeting.getVision().hasBlueBasketTarget());
            telemetry.addData("AprilTags Detected", targeting.getVision().getAprilTags().size());
            
            // Display turret info
            telemetry.addData("", "--- Turret Status ---");
            telemetry.addData("Current Power", "%.2f", targeting.getTurret().getCurrentPower());
            telemetry.addData("Direction", targeting.getTurret().getDirection());
            
            telemetry.update();
        }
        
        // Stop everything when OpMode ends
        targeting.stop();
    }
}
