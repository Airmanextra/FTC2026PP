# Limelight Quick Start Guide - DECODE 2025-26

## 5-Minute Setup

### 1. Hardware Configuration (Driver Station)
```
Configure Robot → Add Device:
- Limelight 3A → Name: "limelight"
- CRServo → Name: "turretRotOne"
- CRServo → Name: "turretRotTwo"
```

### 2. Test Limelight Detection
Run `LimelightTestOpMode` to verify:
- ✓ Limelight is connected
- ✓ AprilTags are detected
- ✓ Correct tag IDs appear (11-13 for Red, 14-16 for Blue)

### 3. Run Your Alliance OpMode
- **Red Alliance:** `LimelightRedAllianceOpMode`
- **Blue Alliance:** `LimelightBlueAllianceOpMode`

**Controls:**
- Left Stick X = Manual turret control
- A Button = Auto-aim at basket

## AprilTag IDs - DECODE 2025-26

| Alliance | Basket Tags |
|----------|-------------|
| Red      | 11, 12, 13  |
| Blue     | 14, 15, 16  |

## Code Examples

### Vision Only (No Turret)
```java
LimelightVision vision = new LimelightVision(hardwareMap);

while (opModeIsActive()) {
    vision.update();
    
    if (vision.hasRedBasketTarget()) {
        double offset = vision.getRedBasketX();
        // Use offset for aiming
    }
}

vision.stop();
```

### With Turret Auto-Aim
```java
TurretTargeting targeting = new TurretTargeting(hardwareMap);

while (opModeIsActive()) {
    targeting.update();
    
    if (gamepad1.a) {
        boolean onTarget = targeting.aimAtRedBasket();
        // onTarget is true when aimed correctly
    }
}

targeting.stop();
```

## Common Issues

| Problem | Solution |
|---------|----------|
| "Could not find Limelight" | Check USB connection and hardware config name |
| No tags detected | Verify Limelight pipeline 0 is AprilTag mode |
| Turret oscillates | Decrease proportional gain: `targeting.setProportionalGain(0.015)` |
| Turret too slow | Increase proportional gain: `targeting.setProportionalGain(0.025)` |
| Wrong alliance targets | Use correct OpMode (Red vs Blue) |

## Tuning Parameters

```java
// Adjust targeting behavior
targeting.setProportionalGain(0.02);    // Speed of response (default: 0.02)
targeting.setMinimumPower(0.1);         // Overcome friction (default: 0.1)
targeting.setTargetTolerance(2.0);      // Acceptable error (default: 2.0°)
```

## Next Steps

1. ✓ Test basic detection with `LimelightTestOpMode`
2. ✓ Test manual + auto-aim with alliance OpMode
3. ✓ Tune targeting parameters for your robot
4. ✓ Integrate with your shooting mechanism
5. ✓ Test in autonomous with `LimelightAutoTargetingExample`

## Full Documentation

See `LIMELIGHT_README.md` for complete documentation, troubleshooting, and advanced usage.
