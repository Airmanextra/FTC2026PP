# Shooter Kinematics - Simple Explanation

## The Problem

You need to throw a ball into a basket. The basket is far away and higher than your shooter. **How fast should you shoot the ball?**

If you shoot too slow → ball falls short  
If you shoot too fast → ball goes over the basket  
**We need to calculate the PERFECT speed!**

## Real Life Example

Think about throwing a basketball into a hoop:
- If the hoop is close, you throw gently
- If the hoop is far away, you throw harder
- If the hoop is high up, you throw even harder

Your robot needs to do the same thing, but **automatically calculate** how hard to throw!

## The Three Things That Matter

### 1. **Distance** (How far away is the basket?)
- Farther away = need to shoot faster
- Closer = can shoot slower

### 2. **Height Difference** (Is the basket above or below you?)
- Basket higher than shooter = need more speed
- Basket at same height = need less speed

### 3. **Angle** (What angle do you shoot at?)
- Your shooter has a **FIXED angle** (doesn't change)
- You measure it once with a protractor
- Common angles: 30°, 40°, or 45°
- **The angle stays the same - you only change the SPEED**

This is actually BETTER because:
- ✓ Simpler mechanism (no moving parts for angle)
- ✓ More reliable (one less thing to break)
- ✓ Faster (just change motor speed)
- ✓ More consistent (angle never drifts)

## How We Calculate It

### Step 1: Measure Your Robot

You need to know:
- **Shooter height**: How high off the ground is your shooter? (measure with ruler)
- **Basket height**: How high is the basket? (look in game rules)
- **Shooting angle**: What angle does your ball leave at? (measure with protractor)

Example measurements:
```
Shooter height: 12 inches (1 foot)
Basket height: 4 feet
Shooting angle: 45 degrees
```

### Step 2: Find the Distance

Your Limelight camera can see the AprilTag on the basket. It tells you the angle to look at the basket.

Using **basic trigonometry** (SOH CAH TOA from geometry):
```
         Basket (4 feet high)
            🏀
           /|
          / |
         /  | Height difference = 3 feet
        /   |
       /    |
      /angle|
Robot -------
    Distance = ?
```

If you know the angle and height difference, you can find distance:
```
Distance = Height difference ÷ tan(angle)
```

**Example:**
- Height difference = 3 feet
- Camera angle = 20°
- Distance = 3 ÷ tan(20°) = 3 ÷ 0.36 = **8.2 feet**

### Step 3: Calculate Speed Needed

This is where **physics** comes in. When you throw something, gravity pulls it down.

The formula looks scary, but it's just math:
```
Speed = √(gravity × distance² ÷ (2 × cos²(angle) × (distance × tan(angle) - height)))
```

**Don't panic!** The computer does this math. You just need to understand what it means:

- **Gravity** = 9.8 (this is constant, always the same)
- **Distance** = how far (we calculated this in Step 2)
- **Angle** = your shooting angle (you measured this)
- **Height** = basket height - shooter height

**Example calculation:**
```
Distance = 8.2 feet = 2.5 meters
Angle = 45°
Height difference = 3 feet = 0.9 meters
Gravity = 9.8

Speed needed = √(9.8 × 2.5² ÷ (2 × 0.5² × (2.5 × 1 - 0.9)))
Speed needed = √(9.8 × 6.25 ÷ (0.5 × 1.6))
Speed needed = √(61.25 ÷ 0.8)
Speed needed = √76.6
Speed needed = 8.75 meters/second
```

### Step 4: Convert to Motor Speed (RPM)

Your motor spins in circles (RPM = Rotations Per Minute). We need to convert the speed to RPM.

**Simple formula:**
```
RPM = (Speed × 60) ÷ (π × wheel diameter)
```

**Example:**
```
Speed = 8.75 m/s
Wheel diameter = 0.1 meters (4 inches)

RPM = (8.75 × 60) ÷ (3.14 × 0.1)
RPM = 525 ÷ 0.314
RPM = 1,672 RPM
```

So your motor needs to spin at **1,672 RPM** to make the shot!

## Visual Explanation

```
                    🏀 Basket (high up)
                   /  \
                  /    \
                 /      \
                /        \
               /          \
              /            \
             /              \
            /                \
           /                  \
          /                    \
         /                      \
        /                        \
       /                          \
      /                            \
     /                              \
    /                                \
   /                                  \
  /                                    \
 /                                      \
🤖 Robot                                 Ground
   Shooter

The ball follows a curved path (parabola)
- Too slow: curve drops before basket
- Too fast: curve goes over basket
- Just right: curve lands in basket!
```

## What the Code Does

### Without the code:
1. You guess a speed
2. Shoot
3. Miss
4. Adjust speed
5. Shoot again
6. Repeat until you get it right (takes forever!)

### With the code:
1. Camera sees basket
2. Computer calculates distance
3. Computer calculates perfect speed
4. Motor spins at that speed
5. **You make the shot first try!**

## The Math Broken Down

Let's use **real numbers** you can understand:

### Scenario: Shooting from 10 feet away

**Given:**
- Distance: 10 feet = 3 meters
- Shooter height: 1 foot = 0.3 meters
- Basket height: 4 feet = 1.2 meters
- Height difference: 3 feet = 0.9 meters
- Angle: 45°

**Step-by-step calculation:**

1. **Find what we need:**
   - cos(45°) = 0.707
   - tan(45°) = 1.0
   - cos²(45°) = 0.5

2. **Calculate the bottom part:**
   - distance × tan(angle) = 3 × 1.0 = 3
   - Subtract height: 3 - 0.9 = 2.1
   - Multiply by 2 × cos²: 2 × 0.5 × 2.1 = 2.1

3. **Calculate the top part:**
   - gravity × distance² = 9.8 × 3² = 9.8 × 9 = 88.2

4. **Divide and square root:**
   - 88.2 ÷ 2.1 = 42
   - √42 = 6.48 m/s

5. **Convert to RPM:**
   - (6.48 × 60) ÷ (3.14 × 0.1) = 388.8 ÷ 0.314 = **1,238 RPM**

**Answer: Spin your motor at 1,238 RPM to make a 10-foot shot!**

## Why This is Cool

1. **It's automatic** - No guessing, no trial and error
2. **It's fast** - Calculates in milliseconds
3. **It adapts** - Works from any distance
4. **It's accurate** - Uses real physics, not guessing

## Your Fixed-Angle Shooter (Important!)

Since your shooter angle **doesn't move**, here's what that means:

### What You Control:
- ✅ **Motor speed (RPM)** - This is what changes for different distances
- ✅ **Turret rotation** - Aims left/right at the basket

### What Stays Fixed:
- 🔒 **Launch angle** - Measure it once, put it in the code, done!
- 🔒 **Shooter height** - Doesn't change (unless you rebuild)
- 🔒 **Basket height** - Set by game rules

### How It Works:
```
Close shot (5 feet):
  Fixed angle: 40°
  Calculated speed: SLOW (maybe 800 RPM)
  
Medium shot (10 feet):
  Fixed angle: 40° (same!)
  Calculated speed: MEDIUM (maybe 1,200 RPM)
  
Far shot (15 feet):
  Fixed angle: 40° (same!)
  Calculated speed: FAST (maybe 1,800 RPM)
```

**The angle never changes - only the speed changes!**

### Measuring Your Fixed Angle:

1. **Look at your shooter from the side**
2. **Find where the ball leaves the shooter**
3. **Use a protractor or phone app to measure the angle**
4. **Write it down - you'll use this number in the code**

Example: If your shooter points up at 42°, that's your launch angle. Put `42.0` in the code and never change it.

### Why Fixed Angle is Great:

**Pros:**
- ✓ Simpler to build (no servo/motor for angle adjustment)
- ✓ More reliable (fewer moving parts)
- ✓ Faster response (just change motor speed)
- ✓ More consistent (angle can't drift or break)
- ✓ Easier to tune (only one variable to adjust)

**Cons:**
- ✗ Limited range (some distances might be unreachable)
- ✗ Can't optimize for different situations

**Solution to limited range:**
- Choose a good angle (40-45° is usually best)
- This gives you the maximum possible range
- You can hit most targets on the field

## Common Questions

**Q: What if I don't know the distance?**  
A: The Limelight camera figures it out by looking at the AprilTag!

**Q: What if the basket moves?**  
A: The camera keeps tracking it, and the code recalculates every loop (50 times per second!)

**Q: What if my measurements are wrong?**  
A: You'll miss consistently. Measure carefully and test at a known distance to verify.

**Q: Can I just use one speed for everything?**  
A: Only if you always shoot from the exact same spot. This code lets you shoot from anywhere!

**Q: Is the math really necessary?**  
A: Yes! Without it, you're just guessing. With it, you're using science to guarantee success.

## Try It Yourself

### Step 1: Measure Your Fixed Angle
1. **Look at your shooter from the side**
2. **Use a protractor or phone level app**
3. **Measure the angle from horizontal**
4. **Write it down** (example: 42°)

### Step 2: Put Measurements in Code
```java
// In SmartShooterOpMode.java, change these lines:
private static final double SHOOTER_HEIGHT = 0.30;    // Your measurement
private static final double BASKET_HEIGHT = 1.20;     // From game manual
private static final double LAUNCH_ANGLE = 42.0;      // YOUR FIXED ANGLE HERE!
private static final double CAMERA_HEIGHT = 0.25;     // Your measurement
private static final double CAMERA_ANGLE = 15.0;      // Your measurement
private static final double FLYWHEEL_DIA = 0.10;      // Your measurement
```

### Step 3: Test at Known Distance
1. **Place robot exactly 6 feet from basket**
2. **Run ShooterKinematicsTestOpMode**
3. **It will tell you: "Required RPM: 1,234"**
4. **Set your shooter to that RPM**
5. **Shoot and see if it goes in!**

### Step 4: Adjust if Needed
If the shot is:
- **Too short**: Your angle measurement might be wrong (measure again)
- **Too long**: Your angle measurement might be wrong (measure again)
- **Correct distance but wrong arc**: Check flywheel diameter measurement

**Remember: With a fixed angle, if your measurements are right, the math will be right!**

If it doesn't go in:
- Check your measurements (most common problem)
- Make sure your shooter angle is really what you measured
- Verify the basket height from the game manual

## Summary

**The whole system in one sentence:**

> The camera sees how far away the basket is, the computer calculates how fast to shoot using physics, and the motor spins at exactly that speed to make the shot.

**That's it!** You're using the same physics that NASA uses to land rockets, just for shooting balls into baskets. Pretty cool, right? 🚀🏀
