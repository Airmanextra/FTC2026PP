# Fixed-Angle Shooter - How It Works

## Your Shooter Setup

```
         ╔═══════╗
         ║ MOTOR ║ ← Spins at different speeds (RPM)
         ╚═══╤═══╝
             │
         ┌───▼───┐
         │ Wheel │ ← Flywheel (fixed to motor)
         └───┬───┘
             │
            ⚽ ← Ball exits at FIXED ANGLE
           ╱
          ╱ 42° (example - YOUR angle doesn't change!)
         ╱
        ╱
    ───┴─────────── Ground
```

**Key Point:** The angle (42° in this example) NEVER changes. Only the motor speed changes!

## How Different Speeds Work

### Close Shot (5 feet away)
```
Basket 🏀
   ↑  ╱
   │ ╱  Small arc
   │╱   (slow speed)
   ╱
  ╱ 42° (fixed)
 ╱
🤖 Robot
Motor: 800 RPM (SLOW)
```

### Medium Shot (10 feet away)
```
        Basket 🏀
           ↗  ╱
          ╱  ╱  Medium arc
         ╱  ╱   (medium speed)
        ╱  ╱
       ╱  ╱
      ╱  ╱ 42° (same angle!)
     ╱  ╱
    ╱  ╱
🤖 Robot
Motor: 1,200 RPM (MEDIUM)
```

### Far Shot (15 feet away)
```
                    Basket 🏀
                      ↗   ╱
                    ╱   ╱  Large arc
                  ╱   ╱    (fast speed)
                ╱   ╱
              ╱   ╱
            ╱   ╱
          ╱   ╱ 42° (same angle!)
        ╱   ╱
      ╱   ╱
🤖 Robot
Motor: 1,800 RPM (FAST)
```

## The Math (Simple Version)

Since your angle is **fixed**, the formula simplifies:

```
Speed needed = √(gravity × distance² ÷ constant)
```

Where `constant` depends on your fixed angle:
- For 30°: constant = different number
- For 40°: constant = different number  
- For 45°: constant = different number

**You calculate the constant ONCE, then just plug in distance!**

## Real Example with Fixed 40° Angle

Let's say your shooter is fixed at 40°.

### The Setup:
- Shooter height: 1 foot (0.3 meters)
- Basket height: 4 feet (1.2 meters)
- Height difference: 3 feet (0.9 meters)
- **Fixed angle: 40°**

### Calculate for Different Distances:

**At 5 feet (1.5 meters):**
```
Speed = √(9.8 × 1.5² ÷ (2 × cos²(40°) × (1.5 × tan(40°) - 0.9)))
Speed = √(22.05 ÷ 0.47)
Speed = √46.9
Speed = 6.85 m/s
RPM = 1,305 RPM
```

**At 10 feet (3 meters):**
```
Speed = √(9.8 × 3² ÷ (2 × cos²(40°) × (3 × tan(40°) - 0.9)))
Speed = √(88.2 ÷ 1.86)
Speed = √47.4
Speed = 6.88 m/s
RPM = 1,311 RPM
```

**At 15 feet (4.5 meters):**
```
Speed = √(9.8 × 4.5² ÷ (2 × cos²(40°) × (4.5 × tan(40°) - 0.9)))
Speed = √(198.45 ÷ 3.86)
Speed = √51.4
Speed = 7.17 m/s
RPM = 1,366 RPM
```

### The Pattern:
```
Distance  →  Speed  →  RPM
5 feet    →  6.85   →  1,305 RPM
10 feet   →  6.88   →  1,311 RPM
15 feet   →  7.17   →  1,366 RPM
```

Notice: As distance increases, speed increases (but not linearly!)

## Why 40-45° is Best

Different angles have different "sweet spots":

### 30° Angle (Too Flat)
- ✓ Good for close shots
- ✗ Ball hits ground on far shots
- ✗ Limited range

### 45° Angle (Optimal)
- ✓ Maximum range possible
- ✓ Good balance of height and distance
- ✓ Works for most field positions

### 60° Angle (Too Steep)
- ✓ Good for high baskets
- ✗ Ball goes up and down (wastes energy)
- ✗ Takes longer to reach target

**Most FTC teams use 40-45° for fixed-angle shooters.**

## Measuring Your Fixed Angle

### Method 1: Protractor
```
1. Place protractor on ground next to robot
2. Align 0° with ground (horizontal)
3. Look at where ball exits shooter
4. Read the angle
```

### Method 2: Phone App
```
1. Download a level/angle app
2. Place phone on shooter barrel
3. Read the angle from horizontal
4. That's your launch angle!
```

### Method 3: Trigonometry
```
If you can measure:
- Horizontal distance from pivot to exit: X
- Vertical distance from pivot to exit: Y

Then: Angle = arctan(Y/X)

Example:
- X = 6 inches
- Y = 6 inches
- Angle = arctan(6/6) = arctan(1) = 45°
```

## What Happens If Angle is Wrong?

### If you measure 40° but it's actually 45°:
- Your calculations will be slightly off
- Shots will be consistently short or long
- **Solution:** Test at known distance and adjust

### If angle changes during match:
- This shouldn't happen with a fixed shooter!
- If it does, your mechanism is loose
- **Solution:** Tighten bolts, add support

## Advantages of Your Fixed-Angle Design

1. **Simplicity**
   - No servo/motor for angle adjustment
   - Fewer parts = fewer things to break
   - Easier to build and maintain

2. **Speed**
   - Only need to change motor speed
   - Motor speed changes in ~0.1 seconds
   - Faster than moving a hood/angle mechanism

3. **Consistency**
   - Angle never drifts
   - No calibration needed during match
   - Same angle every shot = predictable

4. **Reliability**
   - Fewer moving parts
   - Less weight
   - Less power consumption

5. **Easier Tuning**
   - Only one variable (speed)
   - Easier to debug problems
   - Faster to optimize

## Code Configuration

In your code, you set the angle ONCE:

```java
// This is your FIXED angle - measure it carefully!
private static final double LAUNCH_ANGLE = 42.0;  // YOUR ANGLE HERE

// This NEVER changes during the match
shooter.configureKinematics(
    SHOOTER_HEIGHT,
    BASKET_HEIGHT,
    LAUNCH_ANGLE  // ← This stays constant
);
```

Then during the match:
```java
// The code automatically calculates the right speed
// based on distance and your FIXED angle
shooter.update();
shooter.shootAtRedBasket();  // Speed adjusts automatically!
```

## Testing Your Fixed-Angle Shooter

### Test 1: Verify Angle Measurement
```
1. Measure angle with protractor: 42°
2. Put 42.0 in code
3. Test at 8 feet
4. If shot is perfect → angle is correct!
5. If shot is off → re-measure angle
```

### Test 2: Test Multiple Distances
```
Distance  |  Expected  |  Actual  |  Result
----------|------------|----------|----------
5 feet    |  In basket |  In!     |  ✓
8 feet    |  In basket |  In!     |  ✓
10 feet   |  In basket |  In!     |  ✓
12 feet   |  In basket |  Short   |  ✗ Check measurements
```

### Test 3: Verify Consistency
```
Shoot 10 times from same spot:
- All in basket → Great!
- Some in, some out → Check motor speed consistency
- All short/long → Check angle measurement
```

## Summary

**Your fixed-angle shooter is actually BETTER for FTC because:**

1. ✅ Simpler mechanism
2. ✅ More reliable
3. ✅ Faster response
4. ✅ Easier to tune
5. ✅ More consistent

**All you need to do:**
1. Measure your fixed angle carefully (use protractor)
2. Put it in the code
3. Let the code calculate the speed for each distance
4. Win matches! 🏆

**The code handles everything else automatically!**
