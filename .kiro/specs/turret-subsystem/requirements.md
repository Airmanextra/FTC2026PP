# Requirements Document: Turret Subsystem

## Introduction

This document specifies the requirements for a turret subsystem for an FTC (FIRST Tech Challenge) robot. The turret uses two continuous rotation servos that spin in the same direction to rotate the turret mechanism. The servos are mechanically coupled through gears that reverse the direction, so when the servos spin clockwise, the turret rotates counterclockwise.

## Glossary

- **Turret**: The rotating mechanism mounted on the robot that can rotate to aim or position components
- **CRServo**: Continuous Rotation Servo - an FTC SDK hardware device that can spin continuously at variable speeds in either direction
- **Hardware_Map**: The FTC SDK object that provides access to configured hardware devices
- **OpMode**: An FTC program that runs on the robot during a match or testing
- **Power**: A value from -1.0 to 1.0 that controls servo speed and direction (negative = counterclockwise, positive = clockwise, 0 = stop)
- **Gear_Reversal**: The mechanical arrangement where gears reverse the direction of rotation between servos and turret

## Requirements

### Requirement 1: Hardware Configuration

**User Story:** As a robot programmer, I want to configure the turret hardware, so that the subsystem can control the physical servos.

#### Acceptance Criteria

1. WHEN the Turret subsystem is initialized with a Hardware_Map, THE Turret SHALL retrieve two CRServo instances from the Hardware_Map
2. WHEN retrieving CRServo instances, THE Turret SHALL use configurable hardware names with default values
3. IF a CRServo cannot be found in the Hardware_Map, THEN THE Turret SHALL throw a descriptive exception indicating which servo is missing

### Requirement 2: Turret Rotation Control

**User Story:** As a robot operator, I want to control the turret's rotation direction and speed, so that I can position the turret as needed during a match.

#### Acceptance Criteria

1. WHEN a rotation command is issued with a power value, THE Turret SHALL set both servos to the same power value
2. WHEN a positive power value is provided, THE Turret SHALL rotate counterclockwise (accounting for Gear_Reversal)
3. WHEN a negative power value is provided, THE Turret SHALL rotate clockwise (accounting for Gear_Reversal)
4. WHEN a power value outside the range [-1.0, 1.0] is provided, THE Turret SHALL clamp the value to the valid range
5. THE Turret SHALL accept power values as doubles in the range [-1.0, 1.0]

### Requirement 3: Turret Stop Control

**User Story:** As a robot operator, I want to stop the turret's rotation, so that I can hold the turret at a specific position.

#### Acceptance Criteria

1. WHEN a stop command is issued, THE Turret SHALL set both servos to zero power
2. WHEN the turret is stopped, THE Turret SHALL maintain the current position until a new rotation command is issued

### Requirement 4: Convenience Methods

**User Story:** As a robot programmer, I want simple methods to rotate the turret in specific directions, so that I can write clearer and more maintainable code.

#### Acceptance Criteria

1. THE Turret SHALL provide a method to rotate left (counterclockwise) at a specified speed
2. THE Turret SHALL provide a method to rotate right (clockwise) at a specified speed
3. WHEN rotating left, THE Turret SHALL apply positive power to both servos
4. WHEN rotating right, THE Turret SHALL apply negative power to both servos

### Requirement 5: Telemetry Support

**User Story:** As a robot programmer, I want to monitor the turret's state during operation, so that I can debug issues and verify correct behavior.

#### Acceptance Criteria

1. THE Turret SHALL provide a method to retrieve the current power setting
2. THE Turret SHALL provide a method to retrieve the current rotation direction (left, right, or stopped)
3. WHEN telemetry is requested, THE Turret SHALL return accurate state information reflecting the most recent commands

### Requirement 6: Integration with FTC OpMode

**User Story:** As a robot programmer, I want the turret subsystem to integrate cleanly with FTC OpModes, so that I can use it in both TeleOp and Autonomous modes.

#### Acceptance Criteria

1. THE Turret SHALL be instantiable from any OpMode with a Hardware_Map reference
2. THE Turret SHALL not depend on specific OpMode lifecycle methods (init, start, loop, stop)
3. THE Turret SHALL be usable in both TeleOp and Autonomous OpModes without modification
4. WHEN an OpMode stops, THE Turret SHALL allow the OpMode to stop servo movement through the stop method
