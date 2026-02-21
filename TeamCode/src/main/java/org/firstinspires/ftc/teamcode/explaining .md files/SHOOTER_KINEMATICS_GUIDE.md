# Shooter Kinematics Guide - DECODE 2025-26

## Overview

This guide explains how to calculate the optimal shooter velocity for scoring in baskets using projectile motion physics and Limelight vision.

## The Physics

### Projectile Motion Equation

When shooting at an angle θ with initial velocity v, the projectile follows a parabolic path:

```
v = √(g × d² / (2 × cos²(θ) × (d × tan(θ) - h)))
```

Where:
- **v** = Initial launch velocity (m/s)
- **g** = Gravity (9.81 m/s²)
- **d** = Horizontal distance to target (m)
- **θ** = Launch angle (degrees)
- **h** = Height difference (basket height - shooter height)

### Key Concepts

1. **Launch Angle**: Fixed angle at which the ball leaves the shooter (typically 30-50°)
2. **Launch Velocity**: Speed needed to reach the target (what we calculate)
3. **Height Difference**: Vertical distance between shooter and basket rim
4. **Horizontal Distance**: Ground distance to the target

## Using the System

### Method 1: Distance-Based (Simple)

If you know the distance to the target:

```java
ShooterKinematics kinematics = new ShooterKinematics(
    0.3,   // Shooter height (m)
    1.2,   // Basket height (m)
    45.0   // Launch angle (degrees)
);

double distance = 2.5;  // meters
double velocity = kinematics.calculateLaunchVelocity(distance);
double rpm = kinematics.velocityToRpm(velocity, 0.1);  // 0.1m = flywheel diameter
```

### Method 2: Limelight Angle-Based (Recommended)

Using Limelight's vertical angle (most accurate):

```java
double verticalAngle = vision.getTargetY();  // ty from Limelight
double cameraHeight = 0.25;  // meters
double cameraMountAngle = 15.0;  // degrees (positive = tilted up)

double velocity = kinematics.calculateVelocityFromAngle(
    verticalAngle, 
    cameraHeight, 
    cameraMountAngle
);

double rpm = kinematics.velocityToRpm(velocity, 0.1);
```

### Method 3: Integrated Smart Shooter

Fully automated with vision:

```java
SmartShooter shooter = new SmartShooter(hardwareMap);

// Configure your robot
shooter.configureKinematics(0.3, 1.2, 45.0);
shooter.configureCamera(0.25, 15.0);
shooter.setFlywheelDiameter(0.1);

// Auto-calculate and set velocity
shooter.update();
boolean success = shooter.shootAtRedBasket();
```

## Measuring Your Robot

### Critical Measurements

You need to measure these values accurately:

#### 1. Shooter Height
- Measure from ground to center of shooter exit
- Use meters (1 inch = 0.0254 meters)
- Example: 12 inches = 0.3048 meters

#### 2. Basket Height
- Measure from ground to basket rim
- **IMPORTANT**: Get exact height from DECODE game manual
- Typical FTC basket: ~1.2 meters (verify for DECODE!)

#### 3. Launch Angle
- Angle at which ball leaves shooter
- Measure with protractor or calculate from shooter geometry
- Common range: 30-50 degrees
- 45° is optimal for maximum range

#### 4. Camera Height
- Measure from ground to camera lens center
- Example: 10 inches = 0.254 meters

#### 5. Camera Mount Angle
- Angle camera is tilted (0° = horizontal)
- Positive = tilted up, Negative = tilted down
- Measure with phone level app or protractor
- Example: 15° up

#### 6. Flywheel Diameter
- Measure diameter of shooter wheel
- Use meters (1 inch = 0.0254 meters)
- Example: 4 inches = 0.1016 meters

### Measurement Tips

1. **Use consistent units**: Convert everything to meters
2. **Measure multiple times**: Average 3-5 measurements
3. **Account for compression**: Measure with ball in shooter
4. **Document everything**: Write down all measurements

## Calibration Process

### Step 1: Measure Robot
Record all measurements above in a configuration file or constants class.

### Step 2: Test at Known Distance
1. Place robot at known distance (e.g., 2 meters)
2. Calculate required velocity
3. Shoot and observe
4. Adjust if needed

### Step 3: Tune Launch Angle
If shots are consistently:
- **Too short**: Increase launch angle or velocity
- **Too long**: Decrease launch angle or velocity
- **Correct distance but wrong arc**: Adjust launch angle only

### Step 4: Verify with Limelight
1. Use Limelight angle-based calculation
2. Test at multiple distances
3. Compare calculated vs actual results
4. Fine-tune camera mount angle if needed

## Advanced: Fixed Velocity Shooter

If your shooter runs at a fixed RPM, calculate the optimal angle instead:

```java
double fixedVelocity = 8.0;  // m/s (your shooter's speed)
double distance = 2.5;  // meters

double optimalAngle = kinematics.calculateOptimalAngle(distance, fixedVelocity);

// Adjust your hood/angle mechanism to this angle
```

## Conversion Formulas

### Inches to Meters
```java
double meters = inches * 0.0254;
```

### RPM to Velocity
```java
double velocity = (rpm / 60.0) * (Math.PI * diameter);
```

### Velocity to RPM
```java
double rpm = (velocity / (Math.PI * diameter)) * 60.0;
```

## Example Calculations

### Example 1: Close Shot
```
Distance: 1.5 meters
Shooter height: 0.3 meters
Basket height: 1.2 meters
Launch angle: 45°

Height difference: 1.2 - 0.3 = 0.9 meters
Required velocity: ~4.5 m/s
Required RPM (100mm wheel): ~860 RPM
```

### Example 2: Far Shot
```
Distance: 3.0 meters
Shooter height: 0.3 meters
Basket height: 1.2 meters
Launch angle: 45°

Height difference: 0.9 meters
Required velocity: ~6.5 m/s
Required RPM (100mm wheel): ~1240 RPM
```

## Troubleshooting

### Shots Consistently Short
- Increase shooter velocity/RPM
- Check for friction losses in shooter
- Verify flywheel diameter measurement
- Check if ball is slipping on flywheel

### Shots Consistently Long
- Decrease shooter velocity/RPM
- Verify distance calculation
- Check launch angle measurement

### Inconsistent Shots
- Check flywheel speed consistency
- Verify ball feeding mechanism
- Ensure proper ball compression
- Check for mechanical wear

### Limelight Distance Wrong
- Calibrate camera mount angle
- Verify camera height measurement
- Check Limelight pipeline settings
- Test at known distances

## Code Integration Example

```java
public class MyRobot {
    private SmartShooter shooter;
    
    // Robot measurements (CONFIGURE FOR YOUR ROBOT)
    private static final double SHOOTER_HEIGHT = 0.3048;  // 12 inches
    private static final double BASKET_HEIGHT = 1.2;      // From game manual
    private static final double LAUNCH_ANGLE = 42.0;      // Measured
    private static final double CAMERA_HEIGHT = 0.254;    // 10 inches
    private static final double CAMERA_ANGLE = 15.0;      // Measured
    private static final double FLYWHEEL_DIA = 0.1016;    // 4 inches
    
    public void init() {
        shooter = new SmartShooter(hardwareMap);
        
        // Configure with your measurements
        shooter.configureKinematics(
            SHOOTER_HEIGHT, 
            BASKET_HEIGHT, 
            LAUNCH_ANGLE
        );
        shooter.configureCamera(CAMERA_HEIGHT, CAMERA_ANGLE);
        shooter.setFlywheelDiameter(FLYWHEEL_DIA);
    }
    
    public void loop() {
        shooter.update();
        
        if (gamepad1.a) {
            // Auto-calculate and shoot
            boolean ready = shooter.shootAtRedBasket();
            
            if (ready) {
                double rpm = shooter.getCurrentRPM();
                double target = shooter.getRequiredRPM(true);
                
                if (Math.abs(rpm - target) < 50) {
                    // Ready to fire!
                    // Trigger your feeding mechanism here
                }
            }
        }
    }
}
```

## Testing Checklist

- [ ] All measurements recorded in meters
- [ ] Shooter height measured accurately
- [ ] Basket height verified from game manual
- [ ] Launch angle measured with protractor
- [ ] Camera height and angle measured
- [ ] Flywheel diameter measured
- [ ] Tested at 1 meter distance
- [ ] Tested at 2 meter distance
- [ ] Tested at 3 meter distance
- [ ] Limelight angle calculation verified
- [ ] RPM conversion verified
- [ ] Shooter reaches target RPM consistently

## Additional Resources

- [Projectile Motion Physics](https://en.wikipedia.org/wiki/Projectile_motion)
- [FTC Limelight Documentation](https://docs.limelightvision.io/)
- [DECODE Game Manual](https://www.firstinspires.org/resource-library/ftc/game-and-season-info)

## Support

Common issues:
1. **Math domain error**: Target unreachable at current angle/velocity
2. **Negative velocity**: Target behind robot or calculation error
3. **Very high RPM**: Check distance calculation or measurements
4. **Shots miss left/right**: Use turret auto-aim (separate from velocity)
