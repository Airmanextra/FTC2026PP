package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.subsystems.LimelightVision;

import java.util.List;

/**
 * Test OpMode for Limelight AprilTag detection.
 * Displays all detected AprilTags and their properties.
 * 
 * DECODE 2025-26 Season AprilTag IDs:
 * Red Alliance Baskets: 11, 12, 13
 * Blue Alliance Baskets: 14, 15, 16
 */
@TeleOp(name = "Limelight Test", group = "Testing")
public class LimelightTestOpMode extends LinearOpMode {
    
    private LimelightVision vision;
    
    @Override
    public void runOpMode() {
        // Initialize Limelight
        vision = new LimelightVision(hardwareMap);
        
        telemetry.addData("Status", "Initialized");
        telemetry.addData("", "Limelight AprilTag Test");
        telemetry.update();
        
        waitForStart();
        
        while (opModeIsActive()) {
            // Update vision
            vision.update();
            
            telemetry.addData("Status", "Running");
            telemetry.addData("", "");
            
            // Display basic target info
            telemetry.addData("Has Target", vision.hasTarget());
            
            if (vision.hasTarget()) {
                telemetry.addData("Target X", "%.2f°", vision.getTargetX());
                telemetry.addData("Target Y", "%.2f°", vision.getTargetY());
                telemetry.addData("Target Area", "%.2f%%", vision.getTargetArea());
                telemetry.addData("", "");
                
                // Display alliance-specific targets
                telemetry.addData("--- Alliance Targets ---", "");
                telemetry.addData("Red Basket Visible", vision.hasRedBasketTarget());
                if (vision.hasRedBasketTarget()) {
                    telemetry.addData("Red Basket X", "%.2f°", vision.getRedBasketX());
                }
                
                telemetry.addData("Blue Basket Visible", vision.hasBlueBasketTarget());
                if (vision.hasBlueBasketTarget()) {
                    telemetry.addData("Blue Basket X", "%.2f°", vision.getBlueBasketX());
                }
                telemetry.addData("", "");
                
                // Display all detected AprilTags
                List<LLResultTypes.FiducialResult> tags = vision.getAprilTags();
                telemetry.addData("--- Detected Tags ---", "");
                telemetry.addData("Count", tags.size());
                
                for (LLResultTypes.FiducialResult tag : tags) {
                    String alliance = getAllianceForTag(tag.getFiducialId());
                    telemetry.addData(String.format("Tag %d (%s)", 
                        tag.getFiducialId(), alliance),
                        String.format("X: %.1f° Area: %.1f%%", 
                        tag.getTx(), tag.getTargetArea()));
                }
                
                // Display robot pose if available
                Pose3D robotPose = vision.getRobotPose();
                if (robotPose != null) {
                    telemetry.addData("", "");
                    telemetry.addData("--- Robot Pose ---", "");
                    telemetry.addData("X", "%.2f", robotPose.getPosition().x);
                    telemetry.addData("Y", "%.2f", robotPose.getPosition().y);
                    telemetry.addData("Z", "%.2f", robotPose.getPosition().z);
                    telemetry.addData("Yaw", "%.2f°", Math.toDegrees(robotPose.getOrientation().getYaw()));
                }
            } else {
                telemetry.addData("", "No AprilTags detected");
            }
            
            telemetry.update();
        }
        
        // Stop Limelight
        vision.stop();
    }
    
    /**
     * Gets the alliance color for a given AprilTag ID.
     */
    private String getAllianceForTag(int tagId) {
        for (int redTag : LimelightVision.RED_BASKET_TAGS) {
            if (tagId == redTag) return "RED";
        }
        for (int blueTag : LimelightVision.BLUE_BASKET_TAGS) {
            if (tagId == blueTag) return "BLUE";
        }
        return "OTHER";
    }
}
