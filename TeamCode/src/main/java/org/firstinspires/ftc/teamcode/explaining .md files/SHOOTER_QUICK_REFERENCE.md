# Shooter Kinematics Quick Reference

## Essential Formula

```
Required Velocity = √(g × d² / (2 × cos²(θ) × (d × tan(θ) - h)))
```

- **g** = 9.81 m/s² (gravity)
- **d** = horizontal distance (meters)
- **θ** = launch angle (degrees)
- **h** = height difference (basket - shooter)

## Quick Setup (3 Steps)

### 1. Measure Your Robot
```java
double SHOOTER_HEIGHT = 0.30;    // 12 inches = 0.3048m
double BASKET_HEIGHT = 1.20;     // Check DECODE manual!
double LAUNCH_ANGLE = 45.0;      // Measure with protractor
double CAMERA_HEIGHT = 0.25;     // 10 inches = 0.254m
double CAMERA_ANGLE = 15.0;      // Tilt angle (+ = up)
double FLYWHEEL_DIA = 0.10;      // 4 inches = 0.1016m
```

### 2. Initialize System
```java
SmartShooter shooter = new SmartShooter(hardwareMap);
shooter.configureKinematics(SHOOTER_HEIGHT, BASKET_HEIGHT, LAUNCH_ANGLE);
shooter.configureCamera(CAMERA_HEIGHT, CAMERA_ANGLE);
shooter.setFlywheelDiameter(FLYWHEEL_DIA);
```

### 3. Use in Loop
```java
shooter.update();
if (shooter.shootAtRedBasket()) {
    // Velocity calculated and set!
    double rpm = shooter.getCurrentRPM();
}
```

## Common Conversions

| From | To | Formula |
|------|-----|---------|
| Inches | Meters | `inches × 0.0254` |
| Meters | Inches | `meters / 0.0254` |
| RPM | m/s | `(rpm / 60) × π × diameter` |
| m/s | RPM | `(velocity / (π × diameter)) × 60` |

## Typical Values

| Distance | 45° Angle | ~Required RPM* |
|----------|-----------|----------------|
| 1.0 m | h=0.9m | ~600 RPM |
| 1.5 m | h=0.9m | ~860 RPM |
| 2.0 m | h=0.9m | ~1100 RPM |
| 2.5 m | h=0.9m | ~1350 RPM |
| 3.0 m | h=0.9m | ~1600 RPM |

*Assuming 100mm flywheel, 0.3m shooter height, 1.2m basket height

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Shots short | Increase RPM or angle |
| Shots long | Decrease RPM or angle |
| Math error | Target unreachable - increase angle |
| Inconsistent | Check flywheel speed stability |

## Test Procedure

1. **Run `ShooterKinematicsTestOpMode`**
2. **Set distance to 2.0m**
3. **Place robot exactly 2.0m from basket**
4. **Note calculated RPM**
5. **Set shooter to that RPM**
6. **Shoot and observe**
7. **Adjust if needed**

## Integration Checklist

- [ ] Measured all robot dimensions
- [ ] Converted to meters
- [ ] Configured SmartShooter
- [ ] Tested at known distance
- [ ] Verified RPM calculation
- [ ] Tested with Limelight
- [ ] Tuned for consistency

## Code Templates

### Manual Distance
```java
ShooterKinematics k = new ShooterKinematics(0.3, 1.2, 45.0);
double velocity = k.calculateLaunchVelocity(2.5);  // 2.5m
double rpm = k.velocityToRpm(velocity, 0.1);
```

### Limelight Angle
```java
double velocity = k.calculateVelocityFromAngle(
    vision.getTargetY(),  // ty angle
    0.25,                 // camera height
    15.0                  // camera tilt
);
```

### Fixed Velocity
```java
double angle = k.calculateOptimalAngle(2.5, 8.0);  // 2.5m, 8m/s
// Adjust hood to this angle
```

## Files Reference

- **ShooterKinematics.java** - Core physics calculations
- **SmartShooter.java** - Integrated vision + kinematics
- **SmartShooterOpMode.java** - Complete example
- **ShooterKinematicsTestOpMode.java** - Calibration tool
- **SHOOTER_KINEMATICS_GUIDE.md** - Full documentation
