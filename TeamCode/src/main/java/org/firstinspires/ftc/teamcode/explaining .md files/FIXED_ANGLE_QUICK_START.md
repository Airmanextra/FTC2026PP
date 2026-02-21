# Fixed-Angle Shooter - Quick Start Guide

## What You Have

A shooter that:
- ✅ Shoots at a **FIXED angle** (doesn't move)
- ✅ Changes **motor speed** to hit different distances
- ✅ Uses **Limelight** to see the basket
- ✅ **Automatically calculates** the right speed

## 3-Step Setup

### Step 1: Measure (5 minutes)

Grab a ruler and protractor. Measure these:

```
┌─────────────────────────────────────────┐
│ Measurement          │ Your Value       │
├─────────────────────────────────────────┤
│ Shooter height       │ _____ inches     │
│ (ground to exit)     │                  │
├─────────────────────────────────────────┤
│ Fixed launch angle   │ _____ degrees    │
│ (use protractor!)    │                  │
├─────────────────────────────────────────┤
│ Camera height        │ _____ inches     │
│ (ground to lens)     │                  │
├─────────────────────────────────────────┤
│ Camera tilt angle    │ _____ degrees    │
│ (0° = horizontal)    │                  │
├─────────────────────────────────────────┤
│ Flywheel diameter    │ _____ inches     │
│ (measure wheel)      │                  │
└─────────────────────────────────────────┘
```

**Convert to meters:** Multiply inches by 0.0254

Example:
- 12 inches = 12 × 0.0254 = 0.3048 meters

### Step 2: Configure Code (2 minutes)

Open `SmartShooterOpMode.java` and change these lines:

```java
// YOUR MEASUREMENTS HERE (in meters!)
private static final double SHOOTER_HEIGHT = 0.30;    // Your shooter height
private static final double BASKET_HEIGHT = 1.20;     // From game manual
private static final double LAUNCH_ANGLE = 42.0;      // YOUR FIXED ANGLE!
private static final double CAMERA_HEIGHT = 0.25;     // Your camera height
private static final double CAMERA_ANGLE = 15.0;      // Your camera tilt
private static final double FLYWHEEL_DIAMETER = 0.10; // Your wheel size
```

**Important:** The `LAUNCH_ANGLE` is your fixed angle. Measure it carefully!

### Step 3: Test (3 minutes)

1. **Place robot 8 feet from basket**
2. **Run `SmartShooterOpMode`**
3. **Press A button** (auto-aim + auto-speed)
4. **Watch the telemetry:**
   ```
   Required RPM: 1,234
   Current RPM: 1,230
   Shooter Ready: YES ✓
   Turret On Target: YES ✓
   >>> READY TO SHOOT <<<
   ```
5. **Shoot!**

If it goes in → You're done! 🎉  
If it misses → Check your measurements

## How It Works (Simple)

```
1. Limelight sees basket
   ↓
2. Calculates distance (using camera angle)
   ↓
3. Calculates speed needed (using your fixed angle)
   ↓
4. Converts to RPM (using flywheel diameter)
   ↓
5. Motor spins at that RPM
   ↓
6. Ball goes in basket! 🏀
```

**You don't change the angle - only the speed changes!**

## Example: How Speed Changes

Your fixed angle: 40°

```
Close (5 feet):   Motor = 1,100 RPM  (slow)
Medium (10 feet): Motor = 1,300 RPM  (medium)
Far (15 feet):    Motor = 1,600 RPM  (fast)
```

Same angle, different speeds!

## Troubleshooting

### Problem: Shots are consistently SHORT
**Possible causes:**
1. Launch angle measured wrong (measure again!)
2. Flywheel diameter wrong (measure wheel)
3. Motor not reaching target RPM (check motor)

**Fix:** Re-measure your fixed angle with a protractor

### Problem: Shots are consistently LONG
**Possible causes:**
1. Launch angle measured wrong (measure again!)
2. Ball has too much spin (check compression)

**Fix:** Re-measure your fixed angle with a protractor

### Problem: Shots are INCONSISTENT
**Possible causes:**
1. Motor speed varies (check power supply)
2. Ball feeding inconsistent (check mechanism)
3. Flywheel slipping (check compression)

**Fix:** Check motor velocity feedback in telemetry

### Problem: "Target unreachable" error
**Possible causes:**
1. Too far away for your fixed angle
2. Basket too high for your setup
3. Wrong measurements in code

**Fix:** 
- Move closer to basket, OR
- Check if angle is really 40-45° (optimal range)

## Testing Checklist

- [ ] Measured shooter height (in inches, converted to meters)
- [ ] Measured fixed launch angle with protractor
- [ ] Measured camera height and tilt angle
- [ ] Measured flywheel diameter
- [ ] Put all measurements in code (in meters!)
- [ ] Tested at 5 feet - ball goes in
- [ ] Tested at 10 feet - ball goes in
- [ ] Tested at 15 feet - ball goes in
- [ ] Limelight auto-aim works
- [ ] Auto-velocity calculation works
- [ ] Ready for competition!

## Key Concepts

### What Changes:
- ✅ Motor speed (RPM) - adjusts for distance
- ✅ Turret rotation - aims left/right

### What Stays Fixed:
- 🔒 Launch angle - NEVER changes
- 🔒 Shooter height - doesn't move
- 🔒 Basket height - set by game rules

### Why This is Good:
- Simple mechanism (no angle adjustment needed)
- Fast response (just change motor speed)
- Reliable (fewer moving parts)
- Consistent (angle never drifts)

## Competition Usage

### During Match:
```java
// Press A button for full auto
if (gamepad1.a) {
    targeting.update();
    shooter.update();
    
    boolean turretReady = targeting.aimAtRedBasket();
    boolean shooterReady = shooter.shootAtRedBasket();
    
    if (turretReady && shooterReady) {
        // FIRE YOUR FEEDING MECHANISM HERE!
        // The turret is aimed and speed is perfect
    }
}
```

### What You See:
```
Mode: AUTO-AIM + SMART VELOCITY
Required RPM: 1,234
Current RPM: 1,230
Shooter Ready: YES ✓
Turret On Target: YES ✓

>>> READY TO SHOOT <<<
```

When you see "READY TO SHOOT", trigger your ball feeder!

## Files You Need

**Main OpMode:**
- `SmartShooterOpMode.java` - Use this in competition

**Testing:**
- `LimelightTestOpMode.java` - Test vision only
- `ShooterKinematicsTestOpMode.java` - Test calculations

**Documentation:**
- `SHOOTER_EXPLAINED_SIMPLE.md` - Simple explanation
- `FIXED_ANGLE_SHOOTER_EXPLAINED.md` - Detailed fixed-angle info
- `SHOOTER_QUICK_REFERENCE.md` - Formula reference

## Final Tips

1. **Measure your fixed angle carefully!**
   - Use a protractor or phone app
   - Measure multiple times
   - This is the most important measurement!

2. **Test at known distances**
   - Start at 8 feet
   - Verify the shot goes in
   - If not, re-check measurements

3. **Trust the math**
   - If measurements are right, math is right
   - Don't randomly adjust values
   - Use the test OpMode to verify

4. **Keep it simple**
   - Your fixed angle is a feature, not a limitation
   - It makes the system more reliable
   - Focus on accurate measurements

## You're Ready!

With a fixed-angle shooter:
1. ✅ Measure your angle once
2. ✅ Put it in the code
3. ✅ Let the code calculate speeds
4. ✅ Win matches!

**The code does all the hard work - you just need accurate measurements!** 🚀
