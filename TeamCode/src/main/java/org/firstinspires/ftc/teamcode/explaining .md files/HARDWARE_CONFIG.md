# Hardware Configuration Guide

## Your Robot's Hardware Names

### Turret Motor
- **Turret Motor:** `turretMotor`
- **Type:** goBILDA 312 RPM Yellow Jacket motor (DcMotorEx)

### Vision
- **Limelight Camera:** `limelight`
- **Type:** Limelight 3A

### Shooter (if applicable)
- **Shooter Motor:** `shooterMotor`
- **Type:** DcMotorEx (with encoder)

### Intake
- **Intake Motor:** `intakeMotor`
- **Type:** goBILDA 512 RPM Yellow Jacket motor (DcMotorEx)

### Indexer (ball gate)
- **Indexer Servo:** `indexerServo`
- **Type:** Standard Servo (blocks balls when closed)

## Driver Station Configuration

### Step 1: Open Configuration
1. Connect Driver Station to Robot Controller
2. Tap the three dots (⋮) menu
3. Select "Configure Robot"

### Step 2: Add Limelight
1. Tap "Scan" to detect USB devices
2. Find "Limelight 3A"
3. Tap to add it
4. Name it: `limelight`
5. Save

### Step 3: Add Turret Motor
1. Find your motor controller (REV Hub)
2. Tap on a motor port (e.g., Motor Port 1)
3. Select "goBILDA 312 RPM" or your turret motor type
4. Name it: `turretMotor`
5. Save

### Step 4: Add Shooter Motor (if applicable)
1. Find your motor controller (REV Hub)
2. Tap on a motor port (e.g., Motor Port 0)
3. Select "REV Robotics Core Hex Motor" (or your motor type)
4. Name it: `shooterMotor`
5. Save

### Step 5: Add Intake Motor
1. Tap on a motor port (e.g., Motor Port 2)
2. Select "goBILDA 512 RPM" or your intake motor type
3. Name it: `intakeMotor`
4. Save

### Step 6: Add Indexer Servo
1. Tap on a servo port (e.g., Servo Port 0)
2. Select "Servo"
3. Name it: `indexerServo`
4. Save

### Step 7: Verify Configuration
Your configuration should show:
```
Limelight 3A: limelight
Motor Port 0: shooterMotor (DcMotorEx)
Motor Port 1: turretMotor (DcMotorEx)
Motor Port 2: intakeMotor (DcMotorEx)
Servo Port 0: indexerServo (Servo)
```

## Code Configuration

The code is already set up with your hardware names!

### Turret.java
```java
private static final String DEFAULT_TURRET_MOTOR_NAME = "turretMotor";
```

### LimelightVision.java
```java
private static final String DEFAULT_LIMELIGHT_NAME = "limelight";
```

### SmartShooter.java
```java
private static final String DEFAULT_SHOOTER_MOTOR = "shooterMotor";
```

### Intake.java
```java
private static final String DEFAULT_INTAKE_MOTOR_NAME = "intakeMotor";
```

### Indexer.java
```java
private static final String DEFAULT_INDEXER_SERVO_NAME = "indexerServo";
```

## Using Default Names

If you use the default hardware names above, you can initialize like this:

```java
// Uses default names automatically
Turret turret = new Turret(hardwareMap);
LimelightVision vision = new LimelightVision(hardwareMap);
SmartShooter shooter = new SmartShooter(hardwareMap);
TurretTargeting targeting = new TurretTargeting(hardwareMap);
Intake intake = new Intake(hardwareMap);
Indexer indexer = new Indexer(hardwareMap);
```

## Using Custom Names

If you want different names, you can specify them:

```java
// Custom names
Turret turret = new Turret(hardwareMap, "myTurretMotor");
LimelightVision vision = new LimelightVision(hardwareMap, "myLimelight");
SmartShooter shooter = new SmartShooter(hardwareMap, "myShooterMotor");
Intake intake = new Intake(hardwareMap, "myIntakeMotor");
Indexer indexer = new Indexer(hardwareMap, "myIndexerServo");
```

## Troubleshooting

### Error: "Could not find motor: turretMotor"
**Problem:** Turret motor not configured or wrong name  
**Solution:**
1. Check Driver Station configuration
2. Verify motor is named exactly `turretMotor`
3. Check motor is plugged into correct port
4. Restart Robot Controller app

### Error: "Could not find Limelight: limelight"
**Problem:** Limelight not detected or wrong name  
**Solution:**
1. Check USB connection
2. Verify Limelight is powered on (green LED)
3. Re-scan for USB devices in configuration
4. Verify name is exactly `limelight` (lowercase)
5. Restart Robot Controller app

### Error: "Could not find shooter motor: shooterMotor"
**Problem:** Motor not configured or wrong name  
**Solution:**
1. Check Driver Station configuration
2. Verify motor is named exactly `shooterMotor`
3. Check motor is plugged into correct port
4. Verify motor type matches your hardware

### Turret Motor Doesn't Move
**Problem:** Turret motor configured but not responding  
**Solution:**
1. Check motor power (REV Hub powered?)
2. Verify motor type is correct (DcMotorEx)
3. Test motor with a simple OpMode
4. Check for loose connections

### Limelight Not Detecting Tags
**Problem:** Limelight connected but no detections  
**Solution:**
1. Access Limelight web interface (http://limelight.local:5801)
2. Verify pipeline 0 is set to AprilTag mode
3. Check camera view shows tags
4. Adjust exposure/gain settings
5. Ensure tags are well-lit

## Quick Test OpModes

### Test Turret Only
```java
Turret turret = new Turret(hardwareMap);
turret.setPower(0.5);  // Should rotate
sleep(1000);
turret.stop();
```

### Test Limelight Only
```java
LimelightVision vision = new LimelightVision(hardwareMap);
vision.update();
telemetry.addData("Has Target", vision.hasTarget());
telemetry.update();
```

### Test Complete System
Run `SmartShooterOpMode` - it tests everything together!

## Port Recommendations

### REV Control Hub / Expansion Hub

**Motors:**
- Port 0: `shooterMotor`
- Port 1: `turretMotor`
- Port 2: `intakeMotor`
- Ports 3-5: Drive motors (if applicable)

**Servos:**
- Port 0: `indexerServo`

**USB:**
- USB Port: `limelight`

**Why this layout:**
- Shooter motor on Port 0 (easy to remember)
- Turret motor on Port 1
- Limelight on USB (only option)

## Configuration Checklist

- [ ] Limelight 3A added as `limelight`
- [ ] Motor added as `shooterMotor` (DcMotorEx)
- [ ] Motor added as `turretMotor` (DcMotorEx)
- [ ] Motor added as `intakeMotor` (DcMotorEx)
- [ ] Servo added as `indexerServo` (Servo)
- [ ] Configuration saved
- [ ] Robot Controller restarted
- [ ] Test OpMode runs without errors
- [ ] Turret motor responds to commands
- [ ] Limelight detects AprilTags
- [ ] Shooter motor spins

## Ready to Go!

Once your hardware is configured with these names:
- `turretMotor`
- `intakeMotor`
- `indexerServo`
- `limelight`
- `shooterMotor`

All the OpModes will work automatically! Just run `SmartShooterOpMode` and press A to test the complete system.
