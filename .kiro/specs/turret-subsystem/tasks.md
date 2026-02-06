# Implementation Plan: Turret Subsystem

## Overview

This implementation plan breaks down the turret subsystem into discrete coding tasks. The approach follows FTC best practices and ensures incremental validation through testing. The turret will be implemented as a standalone subsystem class that can be used in any OpMode.

## Tasks

- [x] 1. Create Turret class structure and initialization
  - Create `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystems/Turret.java`
  - Define class fields for two CRServo instances and currentPower tracking
  - Implement both constructors (default names and custom names)
  - Implement servo retrieval from Hardware_Map with error handling
  - _Requirements: 1.1, 1.2, 1.3_

- [x] 1.1 Write property test for servo initialization
  - **Property 1: Servo Initialization**
  - **Validates: Requirements 1.1, 1.2**

- [x] 1.2 Write property test for missing servo error handling
  - **Property 7: Missing Servo Error Handling**
  - **Validates: Requirements 1.3**

- [x] 2. Implement core power control methods
  - [x] 2.1 Implement clampPower private method
    - Clamp power values to [-1.0, 1.0] range
    - _Requirements: 2.4_
  
  - [x] 2.2 Implement setPower method
    - Clamp input power value
    - Set both servos to the same power
    - Update currentPower field
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_
  
  - [x] 2.3 Implement stop method
    - Call setPower(0) to stop both servos
    - _Requirements: 3.1_

- [x] 2.4 Write property test for power clamping
  - **Property 2: Power Clamping**
  - **Validates: Requirements 2.4**

- [x] 2.5 Write property test for synchronized servo power
  - **Property 3: Synchronized Servo Power**
  - **Validates: Requirements 2.1, 2.2, 2.3, 2.5**

- [x] 2.6 Write unit test for stop method
  - Test that stop() sets power to zero
  - _Requirements: 3.1_

- [x] 3. Implement convenience rotation methods
  - [x] 3.1 Implement rotateLeft method
    - Accept speed parameter
    - Call setPower with positive speed value
    - _Requirements: 4.1, 4.3_
  
  - [x] 3.2 Implement rotateRight method
    - Accept speed parameter
    - Call setPower with negative speed value
    - _Requirements: 4.2, 4.4_

- [x] 3.3 Write property test for left rotation direction
  - **Property 4: Left Rotation Direction**
  - **Validates: Requirements 4.1, 4.3**

- [x] 3.4 Write property test for right rotation direction
  - **Property 5: Right Rotation Direction**
  - **Validates: Requirements 4.2, 4.4**

- [x] 4. Implement telemetry and state methods
  - [x] 4.1 Implement getCurrentPower method
    - Return currentPower field value
    - _Requirements: 5.1_
  
  - [x] 4.2 Implement getDirection method
    - Return "LEFT" for positive power
    - Return "RIGHT" for negative power
    - Return "STOPPED" for zero power
    - _Requirements: 5.2_

- [x] 4.3 Write property test for state consistency
  - **Property 6: State Consistency**
  - **Validates: Requirements 5.1, 5.2, 5.3**

- [x] 4.4 Write unit tests for getDirection edge cases
  - Test direction strings for specific power values (0, 0.5, -0.5)
  - _Requirements: 5.2_

- [x] 5. Create example OpMode for testing
  - Create `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/TurretTestOpMode.java`
  - Implement TeleOp OpMode that demonstrates turret usage
  - Map gamepad controls to turret rotation (left/right triggers or bumpers)
  - Display telemetry showing current power and direction
  - _Requirements: 6.1, 6.3_

- [x] 6. Final checkpoint - Ensure all tests pass
  - Run all unit tests and property tests
  - Verify test coverage for all requirements
  - Ensure all tests pass, ask the user if questions arise

## Notes

- The Turret class uses mocking for CRServo instances during testing to avoid hardware dependencies
- Property tests should use jqwik or QuickTheories library for Java
- Each property test should run minimum 100 iterations
- The example OpMode (task 5) provides a practical way to test on hardware but is not required for core functionality
- Integration testing on actual hardware should verify physical rotation direction and servo synchronization
