# Limelight AprilTag Detection for DECODE 2025-26

This package contains Limelight vision integration for automatic AprilTag detection and turret targeting for the DECODE 2025-26 FTC season.

## AprilTag IDs for DECODE 2025-26

### Red Alliance Baskets
- Tag 11
- Tag 12
- Tag 13

### Blue Alliance Baskets
- Tag 14
- Tag 15
- Tag 16

## Components

### 1. LimelightVision.java
Core vision subsystem that interfaces with the Limelight 3A camera.

**Features:**
- AprilTag detection and tracking
- Alliance-specific target filtering (red/blue baskets)
- Robot pose estimation from AprilTags
- Horizontal/vertical offset calculations
- Target area measurements

**Key Methods:**
```java
vision.update();                    // Update vision data (call in loop)
vision.hasTarget();                 // Check if any AprilTag is visible
vision.getTargetX();                // Get horizontal offset in degrees
vision.hasRedBasketTarget();        // Check for red alliance basket
vision.hasBlueBasketTarget();       // Check for blue alliance basket
vision.getRedBasketX();             // Get offset to red basket
vision.getBlueBasketX();            // Get offset to blue basket
vision.getRobotPose();              // Get robot position from AprilTags
```

### 2. TurretTargeting.java
Integrated system combining turret control with Limelight vision for automatic aiming.

**Features:**
- Automatic turret aiming at alliance-specific baskets
- Proportional control for smooth targeting
- Configurable PID-like parameters
- Manual and automatic control modes

**Key Methods:**
```java
targeting.update();                 // Update vision (call in loop)
targeting.aimAtRedBasket();         // Auto-aim at red basket
targeting.aimAtBlueBasket();        // Auto-aim at blue basket
targeting.getTurret();              // Access turret for manual control
targeting.getVision();              // Access vision system directly
```

**Tuning Parameters:**
```java
targeting.setProportionalGain(0.02);    // Adjust response speed
targeting.setMinimumPower(0.1);         // Overcome servo friction
targeting.setTargetTolerance(2.0);      // Acceptable error in degrees
```

### 3. OpModes

#### LimelightRedAllianceOpMode.java
TeleOp mode for Red Alliance with auto-targeting.

**Controls:**
- Left Stick X: Manual turret control
- A Button: Auto-aim at red alliance basket

#### LimelightBlueAllianceOpMode.java
TeleOp mode for Blue Alliance with auto-targeting.

**Controls:**
- Left Stick X: Manual turret control
- A Button: Auto-aim at blue alliance basket

#### LimelightTestOpMode.java
Diagnostic OpMode for testing Limelight detection.

**Displays:**
- All detected AprilTag IDs
- Target offsets and areas
- Alliance-specific target status
- Robot pose estimation

## Hardware Configuration

### Required Hardware
1. **Limelight 3A Camera**
   - Hardware name: `"limelight"` (default)
   - Connected via USB to Robot Controller

2. **Turret Servos** (if using TurretTargeting)
   - Left servo: `"turretLeft"` (default)
   - Right servo: `"turretRight"` (default)

### Configuration Steps

1. **Configure Limelight in Robot Controller:**
   - Connect Limelight 3A via USB
   - In Driver Station, go to Configure Robot
   - Add Limelight 3A device with name `"limelight"`

2. **Configure Turret Servos (if applicable):**
   - Add two Continuous Rotation Servos
   - Name them `"turretLeft"` and `"turretRight"`

3. **Limelight Pipeline Setup:**
   - Pipeline 0 should be configured for AprilTag detection
   - Use the Limelight web interface to verify detection
   - Adjust exposure and gain for your lighting conditions

## Usage Examples

### Basic Vision Only
```java
LimelightVision vision = new LimelightVision(hardwareMap);

while (opModeIsActive()) {
    vision.update();
    
    if (vision.hasRedBasketTarget()) {
        double offset = vision.getRedBasketX();
        telemetry.addData("Red Basket Offset", offset);
    }
    
    telemetry.update();
}

vision.stop();
```

### Integrated Turret Targeting
```java
TurretTargeting targeting = new TurretTargeting(hardwareMap);

while (opModeIsActive()) {
    targeting.update();
    
    if (gamepad1.a) {
        // Auto-aim at red basket
        boolean onTarget = targeting.aimAtRedBasket();
        if (onTarget) {
            // Ready to shoot!
        }
    } else {
        // Manual control
        targeting.getTurret().setPower(-gamepad1.left_stick_x);
    }
}

targeting.stop();
```

### Custom Hardware Names
```java
// Custom Limelight name
LimelightVision vision = new LimelightVision(hardwareMap, "myLimelight");

// Custom turret and Limelight names
TurretTargeting targeting = new TurretTargeting(
    hardwareMap, 
    "leftTurretServo",
    "rightTurretServo", 
    "myLimelight"
);
```

## Tuning Guide

### Targeting Performance

If the turret oscillates around the target:
- **Decrease** `setProportionalGain()` (try 0.015 or 0.01)
- **Increase** `setTargetTolerance()` (try 3.0 or 4.0)

If the turret responds too slowly:
- **Increase** `setProportionalGain()` (try 0.025 or 0.03)
- **Decrease** `setMinimumPower()` (try 0.08)

If the turret doesn't move at all:
- **Increase** `setMinimumPower()` (try 0.15 or 0.2)
- Check servo connections and power

### Vision Detection

For better AprilTag detection:
1. Access Limelight web interface (http://limelight.local:5801)
2. Adjust exposure for your lighting conditions
3. Increase gain if tags are far away
4. Ensure tags are well-lit and not obscured
5. Verify pipeline 0 is set to AprilTag detection

## Troubleshooting

### "Could not find Limelight" Error
- Verify Limelight is connected via USB
- Check hardware configuration name matches code
- Restart Robot Controller app

### No AprilTags Detected
- Check Limelight pipeline is set to AprilTag mode
- Verify tags are in camera field of view
- Adjust lighting and exposure settings
- Ensure tags are printed clearly and not damaged

### Turret Not Responding
- Check servo connections and power
- Verify servo names in hardware configuration
- Test servos individually with TurretTestOpMode
- Increase minimum power setting

### Wrong Alliance Targets
- Verify you're using the correct OpMode (Red vs Blue)
- Check AprilTag IDs match DECODE 2025-26 specifications
- Ensure tags are correctly labeled on field

## Integration with Existing Code

To add Limelight targeting to your existing robot code:

1. **Add vision to your subsystems:**
```java
private LimelightVision vision;

public void init() {
    vision = new LimelightVision(hardwareMap);
    // ... other initialization
}
```

2. **Update vision in your loop:**
```java
public void loop() {
    vision.update();
    // ... rest of your code
}
```

3. **Use vision data for targeting:**
```java
if (vision.hasRedBasketTarget()) {
    double offset = vision.getRedBasketX();
    // Use offset to aim your shooter/turret
}
```

4. **Clean up when done:**
```java
public void stop() {
    vision.stop();
    // ... other cleanup
}
```

## Additional Resources

- [Limelight Documentation](https://docs.limelightvision.io/)
- [FTC AprilTag Guide](https://ftc-docs.firstinspires.org/en/latest/apriltag/vision_portal/apriltag_intro/apriltag-intro.html)
- [DECODE 2025-26 Game Manual](https://www.firstinspires.org/resource-library/ftc/game-and-season-info)

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review Limelight logs in web interface
3. Test with LimelightTestOpMode for diagnostics
4. Verify hardware configuration matches code
