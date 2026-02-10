# Hardware Configuration Guide

## Your Robot's Hardware Names

### Turret Servos
- **Left Servo:** `turretRotOne`
- **Right Servo:** `turretRotTwo`
- **Type:** Continuous Rotation Servos (CRServo)

### Vision
- **Limelight Camera:** `limelight`
- **Type:** Limelight 3A

### Shooter (if applicable)
- **Shooter Motor:** `shooterMotor`
- **Type:** DcMotorEx (with encoder)

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

### Step 3: Add Turret Servos
1. Find your servo controller (REV Hub or Expansion Hub)
2. Tap on a servo port (e.g., Servo Port 0)
3. Select "Continuous Rotation Servo"
4. Name it: `turretRotOne`
5. Repeat for second servo
6. Name it: `turretRotTwo`
7. Save

### Step 4: Add Shooter Motor (if applicable)
1. Find your motor controller (REV Hub)
2. Tap on a motor port (e.g., Motor Port 0)
3. Select "REV Robotics Core Hex Motor" (or your motor type)
4. Name it: `shooterMotor`
5. Save

### Step 5: Verify Configuration
Your configuration should show:
```
Limelight 3A: limelight
Servo Port 0: turretRotOne (CRServo)
Servo Port 1: turretRotTwo (CRServo)
Motor Port 0: shooterMotor (DcMotorEx)
```

## Code Configuration

The code is already set up with your hardware names!

### Turret.java
```java
private static final String DEFAULT_LEFT_SERVO_NAME = "turretRotOne";
private static final String DEFAULT_RIGHT_SERVO_NAME = "turretRotTwo";
```

### LimelightVision.java
```java
private static final String DEFAULT_LIMELIGHT_NAME = "limelight";
```

### SmartShooter.java
```java
private static final String DEFAULT_SHOOTER_MOTOR = "shooterMotor";
```

## Using Default Names

If you use the default hardware names above, you can initialize like this:

```java
// Uses default names automatically
Turret turret = new Turret(hardwareMap);
LimelightVision vision = new LimelightVision(hardwareMap);
SmartShooter shooter = new SmartShooter(hardwareMap);
TurretTargeting targeting = new TurretTargeting(hardwareMap);
```

## Using Custom Names

If you want different names, you can specify them:

```java
// Custom names
Turret turret = new Turret(hardwareMap, "myLeftServo", "myRightServo");
LimelightVision vision = new LimelightVision(hardwareMap, "myLimelight");
SmartShooter shooter = new SmartShooter(hardwareMap, "myShooterMotor");
```

## Troubleshooting

### Error: "Could not find servo: turretRotOne"
**Problem:** Servo not configured or wrong name  
**Solution:**
1. Check Driver Station configuration
2. Verify servo is named exactly `turretRotOne`
3. Check servo is plugged into correct port
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

### Servos Don't Move
**Problem:** Servos configured but not responding  
**Solution:**
1. Check servo power (REV Hub powered?)
2. Verify servo type is "Continuous Rotation Servo"
3. Test servos individually with a simple OpMode
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

**Servos:**
- Port 0: `turretRotOne`
- Port 1: `turretRotTwo`

**Motors:**
- Port 0: `shooterMotor`
- Ports 1-3: Drive motors (if applicable)

**USB:**
- USB Port: `limelight`

**Why this layout:**
- Servos on adjacent ports for easier wiring
- Shooter motor on Port 0 (easy to remember)
- Limelight on USB (only option)

## Configuration Checklist

- [ ] Limelight 3A added as `limelight`
- [ ] Servo 1 added as `turretRotOne` (CRServo)
- [ ] Servo 2 added as `turretRotTwo` (CRServo)
- [ ] Motor added as `shooterMotor` (DcMotorEx)
- [ ] Configuration saved
- [ ] Robot Controller restarted
- [ ] Test OpMode runs without errors
- [ ] Servos respond to commands
- [ ] Limelight detects AprilTags
- [ ] Shooter motor spins

## Ready to Go!

Once your hardware is configured with these names:
- `turretRotOne`
- `turretRotTwo`
- `limelight`
- `shooterMotor`

All the OpModes will work automatically! Just run `SmartShooterOpMode` and press A to test the complete system.
