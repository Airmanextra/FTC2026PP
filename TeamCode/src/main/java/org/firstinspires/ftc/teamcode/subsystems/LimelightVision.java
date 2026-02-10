package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

/**
 * Limelight Vision subsystem for AprilTag detection and targeting.
 * Supports DECODE 2025-26 season with red and blue alliance targeting.
 * 
 * AprilTag IDs for DECODE 2025-26:
 * Red Alliance Baskets: 11, 12, 13
 * Blue Alliance Baskets: 14, 15, 16
 */
public class LimelightVision {
    private Limelight3A limelight;
    private LLResult latestResult;
    
    // DECODE 2025-26 AprilTag IDs
    public static final int[] RED_BASKET_TAGS = {11, 12, 13};
    public static final int[] BLUE_BASKET_TAGS = {14, 15, 16};
    
    // Default hardware name
    private static final String DEFAULT_LIMELIGHT_NAME = "limelight";
    
    // Pipeline indices
    private static final int APRILTAG_PIPELINE = 0;
    
    /**
     * Constructs a LimelightVision with default hardware name.
     * 
     * @param hardwareMap The FTC hardware map for retrieving configured devices
     * @throws IllegalArgumentException if Limelight cannot be found in the hardware map
     */
    public LimelightVision(HardwareMap hardwareMap) {
        this(hardwareMap, DEFAULT_LIMELIGHT_NAME);
    }
    
    /**
     * Constructs a LimelightVision with custom hardware name.
     * 
     * @param hardwareMap The FTC hardware map for retrieving configured devices
     * @param limelightName The hardware name for the Limelight
     * @throws IllegalArgumentException if Limelight cannot be found in the hardware map
     */
    public LimelightVision(HardwareMap hardwareMap, String limelightName) {
        try {
            this.limelight = hardwareMap.get(Limelight3A.class, limelightName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find Limelight: " + limelightName);
        }
        
        // Set to AprilTag detection pipeline
        limelight.pipelineSwitch(APRILTAG_PIPELINE);
        
        // Start the Limelight
        limelight.start();
    }
    
    /**
     * Updates the latest result from the Limelight.
     * Call this periodically in your OpMode loop.
     */
    public void update() {
        latestResult = limelight.getLatestResult();
    }
    
    /**
     * Checks if any AprilTag is currently detected.
     * 
     * @return true if at least one AprilTag is visible
     */
    public boolean hasTarget() {
        return latestResult != null && latestResult.isValid();
    }
    
    /**
     * Gets the horizontal offset to the target in degrees.
     * Positive values mean the target is to the right.
     * 
     * @return Horizontal offset in degrees, or 0 if no target
     */
    public double getTargetX() {
        if (!hasTarget()) return 0.0;
        return latestResult.getTx();
    }
    
    /**
     * Gets the vertical offset to the target in degrees.
     * Positive values mean the target is above the crosshair.
     * 
     * @return Vertical offset in degrees, or 0 if no target
     */
    public double getTargetY() {
        if (!hasTarget()) return 0.0;
        return latestResult.getTy();
    }
    
    /**
     * Gets the area of the target as a percentage of the image.
     * 
     * @return Target area (0-100), or 0 if no target
     */
    public double getTargetArea() {
        if (!hasTarget()) return 0.0;
        return latestResult.getTa();
    }
    
    /**
     * Gets all detected AprilTag fiducials.
     * 
     * @return List of detected AprilTag fiducials, or empty list if none
     */
    public List<LLResultTypes.FiducialResult> getAprilTags() {
        if (!hasTarget()) return List.of();
        return latestResult.getFiducialResults();
    }
    
    /**
     * Finds a specific AprilTag by ID.
     * 
     * @param tagId The AprilTag ID to search for
     * @return The fiducial result if found, null otherwise
     */
    public LLResultTypes.FiducialResult getAprilTagById(int tagId) {
        List<LLResultTypes.FiducialResult> tags = getAprilTags();
        for (LLResultTypes.FiducialResult tag : tags) {
            if (tag.getFiducialId() == tagId) {
                return tag;
            }
        }
        return null;
    }
    
    /**
     * Checks if a specific AprilTag is visible.
     * 
     * @param tagId The AprilTag ID to check
     * @return true if the tag is currently visible
     */
    public boolean isTagVisible(int tagId) {
        return getAprilTagById(tagId) != null;
    }
    
    /**
     * Finds the closest red alliance basket tag.
     * 
     * @return The closest red basket tag, or null if none visible
     */
    public LLResultTypes.FiducialResult getClosestRedBasket() {
        return getClosestTag(RED_BASKET_TAGS);
    }
    
    /**
     * Checks if any red alliance basket tag is visible.
     * 
     * @return true if at least one red basket tag is visible
     */
    public boolean hasRedBasketTarget() {
        return getClosestRedBasket() != null;
    }
    
    /**
     * Finds the closest blue alliance basket tag.
     * 
     * @return The closest blue basket tag, or null if none visible
     */
    public LLResultTypes.FiducialResult getClosestBlueBasket() {
        return getClosestTag(BLUE_BASKET_TAGS);
    }
    
    /**
     * Checks if any blue alliance basket tag is visible.
     * 
     * @return true if at least one blue basket tag is visible
     */
    public boolean hasBlueBasketTarget() {
        return getClosestBlueBasket() != null;
    }
    
    /**
     * Gets the horizontal offset to the closest red basket.
     * 
     * @return Horizontal offset in degrees, or 0 if no red basket visible
     */
    public double getRedBasketX() {
        LLResultTypes.FiducialResult tag = getClosestRedBasket();
        return tag != null ? tag.getTargetXDegrees() : 0.0;
    }
    
    /**
     * Gets the horizontal offset to the closest blue basket.
     * 
     * @return Horizontal offset in degrees, or 0 if no blue basket visible
     */
    public double getBlueBasketX() {
        LLResultTypes.FiducialResult tag = getClosestBlueBasket();
        return tag != null ? tag.getTargetXDegrees() : 0.0;
    }

    /**
     * Gets the robot's pose from AprilTag localization.
     * 
     * @return Robot pose in 3D space, or null if not available
     */
    public Pose3D getRobotPose() {
        if (!hasTarget()) return null;
        return latestResult.getBotpose();
    }
    
    /**
     * Finds the closest tag from a list of tag IDs.
     * 
     * @param tagIds Array of tag IDs to search for
     * @return The closest tag from the list, or null if none visible
     */
    private LLResultTypes.FiducialResult getClosestTag(int[] tagIds) {
        List<LLResultTypes.FiducialResult> allTags = getAprilTags();
        LLResultTypes.FiducialResult closest = null;
        double minArea = 0;
        
        for (LLResultTypes.FiducialResult tag : allTags) {
            for (int targetId : tagIds) {
                if (tag.getFiducialId() == targetId) {
                    double area = tag.getTargetArea();
                    if (closest == null || area > minArea) {
                        closest = tag;
                        minArea = area;
                    }
                }
            }
        }
        
        return closest;
    }
    
    /**
     * Stops the Limelight.
     * Call this when the OpMode stops.
     */
    public void stop() {
        if (limelight != null) {
            limelight.stop();
        }
    }
    
    /**
     * Gets the raw Limelight device for advanced usage.
     * 
     * @return The Limelight3A device
     */
    public Limelight3A getLimelight() {
        return limelight;
    }
}
