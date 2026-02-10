# Design Document: Turret Subsystem

## Overview

The Turret subsystem provides a clean interface for controlling a two-servo turret mechanism on an FTC robot. The design accounts for the mechanical gear reversal where both servos spin in the same direction (clockwise) to produce counterclockwise turret rotation.

The subsystem follows FTC best practices by:
- Encapsulating hardware configuration and control logic
- Providing a simple, intuitive API for OpMode usage
- Supporting both TeleOp and Autonomous operation
- Including telemetry support for debugging

## Architecture

The turret subsystem consists of a single `Turret` class that manages two CRServo instances. The class provides:

1. **Initialization**: Hardware configuration and servo retrieval
2. **Control Interface**: Methods for rotation control (power-based and direction-based)
3. **State Management**: Tracking current power and direction
4. **Telemetry**: Methods for retrieving current state

The design is stateless except for tracking the last commanded power value, making it simple and predictable.

## Components and Interfaces

### Turret Class

**Location**: `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystems/Turret.java`

**Fields**:
```java
private CRServo leftServo;
private CRServo rightServo;
private double currentPower;
private static final String DEFAULT_LEFT_SERVO_NAME = "turretRotOne";
private static final String DEFAULT_RIGHT_SERVO_NAME = "turretRotTwo";
```

**Constructor**:
```java
public Turret(HardwareMap hardwareMap)
public Turret(HardwareMap hardwareMap, String leftServoName, String rightServoName)
```

The constructor initializes the servos from the hardware map. The overloaded version allows custom servo names for flexibility.

**Public Methods**:

1. `void setPower(double power)` - Sets rotation power for both servos
   - Clamps power to [-1.0, 1.0]
   - Positive power → counterclockwise rotation
   - Negative power → clockwise rotation
   - Updates currentPower field

2. `void rotateLeft(double speed)` - Rotates turret counterclockwise
   - Calls setPower with positive speed value
   - Convenience method for clearer code

3. `void rotateRight(double speed)` - Rotates turret clockwise
   - Calls setPower with negative speed value
   - Convenience method for clearer code

4. `void stop()` - Stops turret rotation
   - Sets both servos to zero power
   - Calls setPower(0)

5. `double getCurrentPower()` - Returns current power setting
   - Returns the currentPower field value

6. `String getDirection()` - Returns current rotation direction
   - Returns "LEFT" if currentPower > 0
   - Returns "RIGHT" if currentPower < 0
   - Returns "STOPPED" if currentPower == 0

**Private Methods**:

1. `double clampPower(double power)` - Clamps power to valid range
   - Returns Math.max(-1.0, Math.min(1.0, power))

## Data Models

### Power Value
- Type: `double`
- Range: [-1.0, 1.0]
- Semantics:
  - -1.0: Full speed clockwise
  - 0.0: Stopped
  - 1.0: Full speed counterclockwise

### Direction State
- Type: `String` (enum-like)
- Values: "LEFT", "RIGHT", "STOPPED"
- Derived from currentPower value

### Hardware Names
- Type: `String`
- Default values: "turretRotOne", "turretRotTwo"
- Configurable via constructor overload


## Correctness Properties

A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.

### Property 1: Servo Initialization
*For any* valid Hardware_Map containing servos with the specified names, initializing a Turret should successfully retrieve both CRServo instances without throwing exceptions.

**Validates: Requirements 1.1, 1.2**

### Property 2: Power Clamping
*For any* power value outside the range [-1.0, 1.0], calling setPower should result in both servos receiving a clamped value within [-1.0, 1.0], where values greater than 1.0 become 1.0 and values less than -1.0 become -1.0.

**Validates: Requirements 2.4**

### Property 3: Synchronized Servo Power
*For any* power value in the range [-1.0, 1.0], calling setPower should result in both servos receiving exactly the same power value.

**Validates: Requirements 2.1, 2.2, 2.3, 2.5**

### Property 4: Left Rotation Direction
*For any* positive speed value, calling rotateLeft should result in both servos receiving a positive power value equal to the clamped speed.

**Validates: Requirements 4.1, 4.3**

### Property 5: Right Rotation Direction
*For any* positive speed value, calling rotateRight should result in both servos receiving a negative power value equal to the negated clamped speed.

**Validates: Requirements 4.2, 4.4**

### Property 6: State Consistency
*For any* sequence of power commands (setPower, rotateLeft, rotateRight, stop), getCurrentPower should always return the most recently set power value, and getDirection should return "LEFT" for positive power, "RIGHT" for negative power, and "STOPPED" for zero power.

**Validates: Requirements 5.1, 5.2, 5.3**

### Property 7: Missing Servo Error Handling
*For any* Hardware_Map that is missing one or both required servos, attempting to initialize a Turret should throw an exception with a message indicating which servo name could not be found.

**Validates: Requirements 1.3**

## Error Handling

### Missing Hardware
When a servo cannot be found in the Hardware_Map during initialization, the constructor throws an `IllegalArgumentException` with a descriptive message:
- "Could not find servo: [servo_name]"

This allows the OpMode to fail fast with a clear error message rather than encountering null pointer exceptions later.

### Invalid Power Values
Power values outside [-1.0, 1.0] are silently clamped to the valid range. This is a defensive programming practice that prevents invalid values from reaching the hardware while maintaining predictable behavior.

### Null Hardware_Map
If a null Hardware_Map is passed to the constructor, the standard Java `NullPointerException` will be thrown when attempting to retrieve servos. This is acceptable as it indicates a programming error that should be caught during development.

## Testing Strategy

The turret subsystem will be validated using both unit tests and property-based tests to ensure comprehensive coverage.

### Unit Tests
Unit tests will verify specific examples and edge cases:
- Initialization with valid hardware configuration
- Stop command sets power to zero
- Direction string values for specific power values (0, positive, negative)
- Exception messages for missing servos

### Property-Based Tests
Property-based tests will verify universal properties across many generated inputs:
- Power clamping for random out-of-range values (Property 2)
- Synchronized servo power for random valid power values (Property 3)
- Left rotation for random positive speeds (Property 4)
- Right rotation for random positive speeds (Property 5)
- State consistency across random command sequences (Property 6)

**Test Configuration**:
- Use a property-based testing library for Java (e.g., jqwik or QuickTheories)
- Run minimum 100 iterations per property test
- Each property test must include a comment tag: **Feature: turret-subsystem, Property N: [property description]**
- Mock CRServo instances to avoid hardware dependencies during testing

**Test Structure**:
```java
// Example property test structure
@Property
// Feature: turret-subsystem, Property 3: Synchronized Servo Power
void testSynchronizedServoPower(@ForAll @DoubleRange(min = -1.0, max = 1.0) double power) {
    // Test that both servos receive the same power value
}
```

### Integration Testing
While not part of the automated test suite, the turret should be tested on actual hardware to verify:
- Physical rotation direction matches expected behavior
- Servo synchronization produces smooth turret rotation
- Power levels produce appropriate rotation speeds
- Stop command holds position effectively
