# Complete Smart Shooting System - Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    COMPLETE SHOOTING SYSTEM                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ├─────────────────────────────────┐
                              │                                 │
                    ┌─────────▼─────────┐          ┌───────────▼──────────┐
                    │  VISION SYSTEM    │          │   TURRET SYSTEM      │
                    │  (Limelight)      │          │   (Auto-Aim)         │
                    └─────────┬─────────┘          └───────────┬──────────┘
                              │                                 │
                    ┌─────────▼─────────┐          ┌───────────▼──────────┐
                    │ LimelightVision   │          │ TurretTargeting      │
                    │ - Detect tags     │          │ - Aim turret         │
                    │ - Get angles      │          │ - Track target       │
                    │ - Filter alliance │          │ - PID control        │
                    └─────────┬─────────┘          └───────────┬──────────┘
                              │                                 │
                              └──────────┬──────────────────────┘
                                         │
                              ┌──────────▼──────────┐
                              │  SHOOTER SYSTEM     │
                              │  (Smart Velocity)   │
                              └──────────┬──────────┘
                                         │
                    ┌────────────────────┼────────────────────┐
                    │                    │                    │
         ┌──────────▼──────────┐  ┌─────▼──────┐  ┌─────────▼─────────┐
         │ ShooterKinematics   │  │ SmartShooter│  │ Shooter Motor     │
         │ - Physics calc      │  │ - Integration│  │ - Velocity ctrl   │
         │ - Velocity needed   │  │ - Auto config│  │ - RPM feedback    │
         │ - RPM conversion    │  │ - Vision link│  │                   │
         └─────────────────────┘  └──────────────┘  └───────────────────┘
```

## Component Breakdown

### 1. Vision Layer (Limelight)

**LimelightVision.java**
- Detects AprilTags on field
- Filters by alliance (Red: 11-13, Blue: 14-16)
- Provides horizontal offset (tx) for aiming
- Provides vertical angle (ty) for distance
- Estimates robot pose

**Key Methods:**
```java
vision.update();                    // Update detection
vision.hasRedBasketTarget();        // Check for red basket
vision.getRedBasketX();             // Get horizontal offset
vision.getTargetY();                // Get vertical angle
```

### 2. Turret Layer (Auto-Aim)

**Turret.java**
- Controls two CRServos for rotation
- Synchronized servo control
- Power clamping and safety

**TurretTargeting.java**
- Combines Turret + LimelightVision
- Proportional control for smooth aiming
- Auto-aims at alliance-specific targets

**Key Methods:**
```java
targeting.update();                 // Update vision
targeting.aimAtRedBasket();         // Auto-aim red
targeting.aimAtBlueBasket();        // Auto-aim blue
```

### 3. Shooter Layer (Kinematics)

**ShooterKinematics.java**
- Projectile motion physics
- Calculates required velocity from distance
- Converts velocity ↔ RPM
- Optimal angle calculations

**Key Methods:**
```java
kinematics.calculateLaunchVelocity(distance);
kinematics.calculateVelocityFromAngle(ty, camHeight, camAngle);
kinematics.velocityToRpm(velocity, wheelDiameter);
```

**SmartShooter.java**
- Integrates vision + kinematics
- Auto-calculates shooter velocity
- Controls shooter motor
- Velocity feedback and monitoring

**Key Methods:**
```java
shooter.update();                   // Update vision
shooter.shootAtRedBasket();         // Auto-calc velocity
shooter.getCurrentRPM();            // Get current speed
shooter.isAtTargetVelocity();       // Check if ready
```

## Data Flow

```
1. DETECTION
   Limelight → AprilTag detected → Filter by alliance

2. AIMING
   Target offset (tx) → Turret PID → Servo power → Turret rotates

3. DISTANCE
   Vertical angle (ty) → Kinematics → Required velocity

4. VELOCITY
   Required velocity → RPM conversion → Motor control

5. FEEDBACK
   Motor encoder → Current RPM → Compare to target → Ready signal
```

## Usage Patterns

### Pattern 1: Full Auto (Recommended)
```java
TurretTargeting targeting = new TurretTargeting(hardwareMap);
SmartShooter shooter = new SmartShooter(hardwareMap);

// Configure once
shooter.configureKinematics(0.3, 1.2, 45.0);
shooter.configureCamera(0.25, 15.0);

// In loop
targeting.update();
shooter.update();

if (gamepad1.a) {
    boolean turretReady = targeting.aimAtRedBasket();
    boolean shooterReady = shooter.shootAtRedBasket();
    
    if (turretReady && shooterReady) {
        // FIRE!
    }
}
```

### Pattern 2: Vision Only
```java
LimelightVision vision = new LimelightVision(hardwareMap);

vision.update();
if (vision.hasRedBasketTarget()) {
    double offset = vision.getRedBasketX();
    // Use offset for manual aiming
}
```

### Pattern 3: Kinematics Only
```java
ShooterKinematics kinematics = new ShooterKinematics(0.3, 1.2, 45.0);

double distance = 2.5;  // meters
double velocity = kinematics.calculateLaunchVelocity(distance);
double rpm = kinematics.velocityToRpm(velocity, 0.1);
// Set shooter to this RPM
```

## Configuration Requirements

### Hardware Config (Driver Station)
```
Limelight 3A → "limelight"
CRServo → "turretLeft"
CRServo → "turretRight"
DcMotorEx → "shooterMotor"
```

### Robot Measurements
```java
SHOOTER_HEIGHT = 0.30;      // meters (measure!)
BASKET_HEIGHT = 1.20;       // meters (from game manual)
LAUNCH_ANGLE = 45.0;        // degrees (measure!)
CAMERA_HEIGHT = 0.25;       // meters (measure!)
CAMERA_ANGLE = 15.0;        // degrees (measure!)
FLYWHEEL_DIAMETER = 0.10;   // meters (measure!)
```

## OpModes Provided

| OpMode | Purpose | Alliance |
|--------|---------|----------|
| **LimelightTestOpMode** | Test vision detection | Both |
| **LimelightRedAllianceOpMode** | TeleOp with auto-aim | Red |
| **LimelightBlueAllianceOpMode** | TeleOp with auto-aim | Blue |
| **LimelightAutoTargetingExample** | Autonomous example | Both |
| **SmartShooterOpMode** | Full system demo | Red |
| **ShooterKinematicsTestOpMode** | Calibration tool | N/A |

## Testing Workflow

```
1. Test Vision
   └─> Run LimelightTestOpMode
       └─> Verify tags detected
           └─> Check correct IDs (11-13 or 14-16)

2. Test Turret
   └─> Run LimelightRedAllianceOpMode
       └─> Press A to auto-aim
           └─> Verify turret tracks target

3. Calibrate Kinematics
   └─> Run ShooterKinematicsTestOpMode
       └─> Place at known distance
           └─> Verify calculated RPM
               └─> Adjust measurements if needed

4. Test Complete System
   └─> Run SmartShooterOpMode
       └─> Press A for full auto
           └─> Verify aim + velocity
               └─> Test shooting accuracy

5. Competition Ready!
   └─> Use in your main OpMode
```

## Tuning Parameters

### Turret Aiming
```java
targeting.setProportionalGain(0.02);    // Response speed
targeting.setMinimumPower(0.1);         // Overcome friction
targeting.setTargetTolerance(2.0);      // Acceptable error (degrees)
```

### Shooter Velocity
```java
// Adjust these if shots are consistently off
shooter.configureKinematics(
    shooterHeight,   // ± 1cm if shots short/long
    basketHeight,    // From game manual (don't change)
    launchAngle      // ± 2° if arc is wrong
);
```

## File Reference

### Core Subsystems
- `subsystems/LimelightVision.java` - Vision detection
- `subsystems/Turret.java` - Turret control
- `subsystems/TurretTargeting.java` - Vision + turret
- `subsystems/ShooterKinematics.java` - Physics calculations
- `subsystems/SmartShooter.java` - Vision + kinematics + motor

### OpModes
- `LimelightTestOpMode.java` - Vision testing
- `LimelightRedAllianceOpMode.java` - Red TeleOp
- `LimelightBlueAllianceOpMode.java` - Blue TeleOp
- `LimelightAutoTargetingExample.java` - Auto example
- `SmartShooterOpMode.java` - Complete system
- `ShooterKinematicsTestOpMode.java` - Calibration

### Documentation
- `LIMELIGHT_README.md` - Vision system guide
- `LIMELIGHT_QUICK_START.md` - 5-minute setup
- `SHOOTER_KINEMATICS_GUIDE.md` - Physics explained
- `SHOOTER_QUICK_REFERENCE.md` - Formula cheat sheet
- `COMPLETE_SYSTEM_OVERVIEW.md` - This file

## Quick Start (5 Minutes)

1. **Configure hardware** (Driver Station)
2. **Run `LimelightTestOpMode`** (verify detection)
3. **Measure your robot** (all dimensions in meters)
4. **Update constants** in `SmartShooterOpMode`
5. **Run `SmartShooterOpMode`** (test complete system)

Done! You now have auto-aim and auto-velocity calculation.
