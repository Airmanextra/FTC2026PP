package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import net.jqwik.api.*;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.StringLength;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Property-based and unit tests for the Turret subsystem.
 */
class TurretTest {
    
    /**
     * Feature: turret-subsystem, Property 1: Servo Initialization
     * 
     * For any valid Hardware_Map containing servos with the specified names,
     * initializing a Turret should successfully retrieve both CRServo instances
     * without throwing exceptions.
     * 
     * Validates: Requirements 1.1, 1.2
     */
    @Property
    void testServoInitialization(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String leftServoName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String rightServoName) {
        
        // Arrange: Create mock hardware map and servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        // Configure hardware map to return mock servos
        when(hardwareMap.get(CRServo.class, leftServoName)).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, rightServoName)).thenReturn(mockRightServo);
        
        // Act & Assert: Turret should initialize without throwing exceptions
        assertDoesNotThrow(() -> {
            Turret turret = new Turret(hardwareMap, leftServoName, rightServoName);
            
            // Verify that the hardware map was queried for both servos
            // Use atLeastOnce() to handle cases where both names are the same
            verify(hardwareMap, atLeastOnce()).get(CRServo.class, leftServoName);
            verify(hardwareMap, atLeastOnce()).get(CRServo.class, rightServoName);
            
            // Verify initial state
            assertEquals(0.0, turret.getCurrentPower(), 0.001, 
                "Turret should initialize with zero power");
        });
    }
    
    /**
     * Unit test for default constructor servo initialization.
     * 
     * Validates: Requirements 1.1, 1.2
     */
    @Test
    void testDefaultConstructorInitialization() {
        // Arrange: Create mock hardware map and servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        // Configure hardware map to return mock servos for default names
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        // Act: Create turret with default constructor
        Turret turret = new Turret(hardwareMap);
        
        // Assert: Verify that the hardware map was queried for both servos with default names
        verify(hardwareMap).get(CRServo.class, "turretLeft");
        verify(hardwareMap).get(CRServo.class, "turretRight");
        
        // Verify initial state
        assertEquals(0.0, turret.getCurrentPower(), 0.001, 
            "Turret should initialize with zero power");
    }
    
    /**
     * Feature: turret-subsystem, Property 7: Missing Servo Error Handling
     * 
     * For any Hardware_Map that is missing one or both required servos,
     * attempting to initialize a Turret should throw an exception with a
     * message indicating which servo name could not be found.
     * 
     * Validates: Requirements 1.3
     */
    @Property
    void testMissingServoErrorHandling(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String leftServoName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String rightServoName,
            @ForAll boolean missingLeft,
            @ForAll boolean missingRight) {
        
        // Skip test if both servos are present (not testing error case)
        Assume.that(missingLeft || missingRight);
        
        // If both servo names are the same, they must have the same missing status
        // (can't have one missing and one present with the same name)
        if (leftServoName.equals(rightServoName)) {
            Assume.that(missingLeft == missingRight);
        }
        
        // Arrange: Create mock hardware map
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        // Configure hardware map to throw exception for missing servos
        if (missingLeft) {
            when(hardwareMap.get(eq(CRServo.class), eq(leftServoName)))
                .thenThrow(new IllegalArgumentException("Could not find " + leftServoName));
        } else {
            when(hardwareMap.get(eq(CRServo.class), eq(leftServoName))).thenReturn(mockLeftServo);
        }
        
        // Only configure right servo if it has a different name than left
        if (!leftServoName.equals(rightServoName)) {
            if (missingRight) {
                when(hardwareMap.get(eq(CRServo.class), eq(rightServoName)))
                    .thenThrow(new IllegalArgumentException("Could not find " + rightServoName));
            } else {
                when(hardwareMap.get(eq(CRServo.class), eq(rightServoName))).thenReturn(mockRightServo);
            }
        }
        
        // Act & Assert: Turret initialization should throw exception with descriptive message
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Turret(hardwareMap, leftServoName, rightServoName);
        }, "Turret initialization should throw IllegalArgumentException when servo is missing");
        
        // Verify the exception message contains the missing servo name
        String expectedServoName = missingLeft ? leftServoName : rightServoName;
        assertTrue(exception.getMessage().contains(expectedServoName),
            "Exception message should contain the missing servo name: " + expectedServoName 
            + ", but was: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Could not find servo:"),
            "Exception message should indicate servo not found, but was: " + exception.getMessage());
    }
    
    /**
     * Unit test for missing left servo error handling.
     * 
     * Validates: Requirements 1.3
     */
    @Test
    void testMissingLeftServoThrowsException() {
        // Arrange: Create mock hardware map that throws for left servo
        HardwareMap hardwareMap = mock(HardwareMap.class);
        when(hardwareMap.get(CRServo.class, "turretLeft"))
            .thenThrow(new IllegalArgumentException("Could not find turretLeft"));
        
        // Act & Assert: Should throw exception with descriptive message
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Turret(hardwareMap);
        });
        
        assertEquals("Could not find servo: turretLeft", exception.getMessage());
    }
    
    /**
     * Unit test for missing right servo error handling.
     * 
     * Validates: Requirements 1.3
     */
    @Test
    void testMissingRightServoThrowsException() {
        // Arrange: Create mock hardware map with left servo but not right
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight"))
            .thenThrow(new IllegalArgumentException("Could not find turretRight"));
        
        // Act & Assert: Should throw exception with descriptive message
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Turret(hardwareMap);
        });
        
        assertEquals("Could not find servo: turretRight", exception.getMessage());
    }
    
    /**
     * Unit test for setPower method with valid power value.
     * 
     * Validates: Requirements 2.1, 2.2, 2.3, 2.5
     */
    @Test
    void testSetPowerWithValidValue() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Set power to 0.5
        turret.setPower(0.5);
        
        // Assert: Both servos should receive the same power value
        verify(mockLeftServo).setPower(0.5);
        verify(mockRightServo).setPower(0.5);
        
        // Assert: Current power should be updated
        assertEquals(0.5, turret.getCurrentPower(), 0.001);
    }
    
    /**
     * Unit test for setPower method with power value above maximum.
     * 
     * Validates: Requirements 2.4
     */
    @Test
    void testSetPowerClampsHighValue() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Set power to 1.5 (above maximum)
        turret.setPower(1.5);
        
        // Assert: Both servos should receive clamped value of 1.0
        verify(mockLeftServo).setPower(1.0);
        verify(mockRightServo).setPower(1.0);
        
        // Assert: Current power should be clamped to 1.0
        assertEquals(1.0, turret.getCurrentPower(), 0.001);
    }
    
    /**
     * Unit test for setPower method with power value below minimum.
     * 
     * Validates: Requirements 2.4
     */
    @Test
    void testSetPowerClampsLowValue() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Set power to -1.5 (below minimum)
        turret.setPower(-1.5);
        
        // Assert: Both servos should receive clamped value of -1.0
        verify(mockLeftServo).setPower(-1.0);
        verify(mockRightServo).setPower(-1.0);
        
        // Assert: Current power should be clamped to -1.0
        assertEquals(-1.0, turret.getCurrentPower(), 0.001);
    }
    
    /**
     * Unit test for setPower method with zero power.
     * 
     * Validates: Requirements 2.1, 2.5
     */
    @Test
    void testSetPowerWithZero() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Set power to 0
        turret.setPower(0.0);
        
        // Assert: Both servos should receive zero power
        verify(mockLeftServo).setPower(0.0);
        verify(mockRightServo).setPower(0.0);
        
        // Assert: Current power should be zero
        assertEquals(0.0, turret.getCurrentPower(), 0.001);
    }
    
    /**
     * Unit test for stop method.
     * 
     * Validates: Requirements 3.1
     */
    @Test
    void testStopMethod() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Set turret to a non-zero power first
        turret.setPower(0.7);
        
        // Act: Call stop method
        turret.stop();
        
        // Assert: Both servos should receive zero power
        verify(mockLeftServo).setPower(0.0);
        verify(mockRightServo).setPower(0.0);
        
        // Assert: Current power should be zero
        assertEquals(0.0, turret.getCurrentPower(), 0.001);
    }
    
    /**
     * Feature: turret-subsystem, Property 2: Power Clamping
     * 
     * For any power value outside the range [-1.0, 1.0], calling setPower
     * should result in both servos receiving a clamped value within [-1.0, 1.0],
     * where values greater than 1.0 become 1.0 and values less than -1.0 become -1.0.
     * 
     * Validates: Requirements 2.4
     */
    @Property
    void testPowerClamping(@ForAll double power) {
        // Filter to only test values outside the valid range
        Assume.that(power < -1.0 || power > 1.0);
        
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Set power to out-of-range value
        turret.setPower(power);
        
        // Assert: Determine expected clamped value
        double expectedClampedPower;
        if (power > 1.0) {
            expectedClampedPower = 1.0;
        } else { // power < -1.0
            expectedClampedPower = -1.0;
        }
        
        // Assert: Both servos should receive the clamped value
        verify(mockLeftServo).setPower(expectedClampedPower);
        verify(mockRightServo).setPower(expectedClampedPower);
        
        // Assert: Current power should be the clamped value
        assertEquals(expectedClampedPower, turret.getCurrentPower(), 0.001,
            "Power should be clamped to valid range [-1.0, 1.0]");
        
        // Assert: Clamped value is within valid range
        assertTrue(turret.getCurrentPower() >= -1.0 && turret.getCurrentPower() <= 1.0,
            "Clamped power must be within [-1.0, 1.0]");
    }
    
    /**
     * Feature: turret-subsystem, Property 3: Synchronized Servo Power
     * 
     * For any power value in the range [-1.0, 1.0], calling setPower should
     * result in both servos receiving exactly the same power value.
     * 
     * Validates: Requirements 2.1, 2.2, 2.3, 2.5
     */
    @Property
    void testSynchronizedServoPower(@ForAll @DoubleRange(min = -1.0, max = 1.0) double power) {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Set power to the generated value
        turret.setPower(power);
        
        // Assert: Both servos should receive exactly the same power value
        verify(mockLeftServo).setPower(power);
        verify(mockRightServo).setPower(power);
        
        // Assert: Current power should match the set value
        assertEquals(power, turret.getCurrentPower(), 0.001,
            "Current power should match the set power value");
    }
    
    /**
     * Unit test for rotateLeft method with positive speed.
     * 
     * Validates: Requirements 4.1, 4.3
     */
    @Test
    void testRotateLeftWithPositiveSpeed() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Rotate left at speed 0.75
        turret.rotateLeft(0.75);
        
        // Assert: Both servos should receive positive power value
        verify(mockLeftServo).setPower(0.75);
        verify(mockRightServo).setPower(0.75);
        
        // Assert: Current power should be positive
        assertEquals(0.75, turret.getCurrentPower(), 0.001);
        assertTrue(turret.getCurrentPower() > 0, "rotateLeft should result in positive power");
    }
    
    /**
     * Unit test for rotateLeft method with speed that needs clamping.
     * 
     * Validates: Requirements 4.1, 4.3
     */
    @Test
    void testRotateLeftClampsSpeed() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Rotate left at speed 1.5 (above maximum)
        turret.rotateLeft(1.5);
        
        // Assert: Both servos should receive clamped value of 1.0
        verify(mockLeftServo).setPower(1.0);
        verify(mockRightServo).setPower(1.0);
        
        // Assert: Current power should be clamped to 1.0
        assertEquals(1.0, turret.getCurrentPower(), 0.001);
    }
    
    /**
     * Feature: turret-subsystem, Property 4: Left Rotation Direction
     * 
     * For any positive speed value, calling rotateLeft should result in both
     * servos receiving a positive power value equal to the clamped speed.
     * 
     * Validates: Requirements 4.1, 4.3
     */
    @Property
    void testLeftRotationDirection(@ForAll double speed) {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Rotate left at the generated speed
        turret.rotateLeft(speed);
        
        // Assert: Determine expected clamped speed
        double expectedClampedSpeed = Math.max(-1.0, Math.min(1.0, speed));
        
        // Assert: Both servos should receive the clamped speed value
        verify(mockLeftServo).setPower(expectedClampedSpeed);
        verify(mockRightServo).setPower(expectedClampedSpeed);
        
        // Assert: Current power should match the clamped speed
        assertEquals(expectedClampedSpeed, turret.getCurrentPower(), 0.001,
            "Current power should match the clamped speed value");
        
        // Assert: For positive input speeds, the power should be positive (or zero if speed was zero)
        if (speed > 0) {
            assertTrue(turret.getCurrentPower() > 0,
                "rotateLeft with positive speed should result in positive power");
        }
    }
    
    /**
     * Unit test for rotateRight method with positive speed.
     * 
     * Validates: Requirements 4.2, 4.4
     */
    @Test
    void testRotateRightWithPositiveSpeed() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Rotate right at speed 0.75
        turret.rotateRight(0.75);
        
        // Assert: Both servos should receive negative power value
        verify(mockLeftServo).setPower(-0.75);
        verify(mockRightServo).setPower(-0.75);
        
        // Assert: Current power should be negative
        assertEquals(-0.75, turret.getCurrentPower(), 0.001);
        assertTrue(turret.getCurrentPower() < 0, "rotateRight should result in negative power");
    }
    
    /**
     * Unit test for rotateRight method with speed that needs clamping.
     * 
     * Validates: Requirements 4.2, 4.4
     */
    @Test
    void testRotateRightClampsSpeed() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Rotate right at speed 1.5 (above maximum)
        turret.rotateRight(1.5);
        
        // Assert: Both servos should receive clamped value of -1.0
        verify(mockLeftServo).setPower(-1.0);
        verify(mockRightServo).setPower(-1.0);
        
        // Assert: Current power should be clamped to -1.0
        assertEquals(-1.0, turret.getCurrentPower(), 0.001);
    }
    
    /**
     * Feature: turret-subsystem, Property 5: Right Rotation Direction
     * 
     * For any positive speed value, calling rotateRight should result in both
     * servos receiving a negative power value equal to the negated clamped speed.
     * 
     * Validates: Requirements 4.2, 4.4
     */
    @Property
    void testRightRotationDirection(@ForAll double speed) {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Rotate right at the generated speed
        turret.rotateRight(speed);
        
        // Assert: Determine expected clamped power (negated speed)
        double expectedClampedPower = Math.max(-1.0, Math.min(1.0, -speed));
        
        // Assert: Both servos should receive the negated clamped speed value
        verify(mockLeftServo).setPower(expectedClampedPower);
        verify(mockRightServo).setPower(expectedClampedPower);
        
        // Assert: Current power should match the negated clamped speed
        assertEquals(expectedClampedPower, turret.getCurrentPower(), 0.001,
            "Current power should match the negated clamped speed value");
        
        // Assert: For positive input speeds, the power should be negative
        if (speed > 0) {
            assertTrue(turret.getCurrentPower() < 0,
                "rotateRight with positive speed should result in negative power");
        }
        
        // Assert: For negative input speeds, the power should be positive (or zero if speed was zero)
        if (speed < 0) {
            assertTrue(turret.getCurrentPower() > 0,
                "rotateRight with negative speed should result in positive power");
        }
    }
    
    /**
     * Unit test for getDirection method with zero power.
     * 
     * Validates: Requirements 5.2
     */
    @Test
    void testGetDirectionStopped() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Set power to zero
        turret.setPower(0.0);
        
        // Assert: Direction should be "STOPPED"
        assertEquals("STOPPED", turret.getDirection(),
            "Direction should be STOPPED when power is zero");
    }
    
    /**
     * Unit test for getDirection method with positive power.
     * 
     * Validates: Requirements 5.2
     */
    @Test
    void testGetDirectionLeft() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Set power to positive value
        turret.setPower(0.5);
        
        // Assert: Direction should be "LEFT"
        assertEquals("LEFT", turret.getDirection(),
            "Direction should be LEFT when power is positive");
    }
    
    /**
     * Unit test for getDirection method with negative power.
     * 
     * Validates: Requirements 5.2
     */
    @Test
    void testGetDirectionRight() {
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Act: Set power to negative value
        turret.setPower(-0.5);
        
        // Assert: Direction should be "RIGHT"
        assertEquals("RIGHT", turret.getDirection(),
            "Direction should be RIGHT when power is negative");
    }
    
    /**
     * Feature: turret-subsystem, Property 6: State Consistency
     * 
     * For any sequence of power commands (setPower, rotateLeft, rotateRight, stop),
     * getCurrentPower should always return the most recently set power value, and
     * getDirection should return "LEFT" for positive power, "RIGHT" for negative
     * power, and "STOPPED" for zero power.
     * 
     * Validates: Requirements 5.1, 5.2, 5.3
     */
    @Property
    void testStateConsistency(
            @ForAll @DoubleRange(min = -2.0, max = 2.0) double power1,
            @ForAll @DoubleRange(min = -2.0, max = 2.0) double power2,
            @ForAll @DoubleRange(min = -2.0, max = 2.0) double speed1,
            @ForAll @DoubleRange(min = -2.0, max = 2.0) double speed2) {
        
        // Arrange: Create turret with mock servos
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        when(hardwareMap.get(CRServo.class, "turretLeft")).thenReturn(mockLeftServo);
        when(hardwareMap.get(CRServo.class, "turretRight")).thenReturn(mockRightServo);
        
        Turret turret = new Turret(hardwareMap);
        
        // Test 1: setPower command
        turret.setPower(power1);
        double expectedPower1 = Math.max(-1.0, Math.min(1.0, power1));
        assertEquals(expectedPower1, turret.getCurrentPower(), 0.001,
            "getCurrentPower should return the most recently set power value");
        assertDirectionMatchesPower(turret.getDirection(), expectedPower1);
        
        // Test 2: Another setPower command
        turret.setPower(power2);
        double expectedPower2 = Math.max(-1.0, Math.min(1.0, power2));
        assertEquals(expectedPower2, turret.getCurrentPower(), 0.001,
            "getCurrentPower should return the most recently set power value");
        assertDirectionMatchesPower(turret.getDirection(), expectedPower2);
        
        // Test 3: rotateLeft command
        turret.rotateLeft(speed1);
        double expectedPowerLeft = Math.max(-1.0, Math.min(1.0, speed1));
        assertEquals(expectedPowerLeft, turret.getCurrentPower(), 0.001,
            "getCurrentPower should return the power from rotateLeft");
        assertDirectionMatchesPower(turret.getDirection(), expectedPowerLeft);
        
        // Test 4: rotateRight command
        turret.rotateRight(speed2);
        double expectedPowerRight = Math.max(-1.0, Math.min(1.0, -speed2));
        assertEquals(expectedPowerRight, turret.getCurrentPower(), 0.001,
            "getCurrentPower should return the power from rotateRight");
        assertDirectionMatchesPower(turret.getDirection(), expectedPowerRight);
        
        // Test 5: stop command
        turret.stop();
        assertEquals(0.0, turret.getCurrentPower(), 0.001,
            "getCurrentPower should return 0.0 after stop");
        assertEquals("STOPPED", turret.getDirection(),
            "getDirection should return STOPPED after stop");
    }
    
    /**
     * Helper method to assert that direction string matches the power value.
     * 
     * @param direction The direction string from getDirection()
     * @param power The expected power value
     */
    private void assertDirectionMatchesPower(String direction, double power) {
        if (power > 0) {
            assertEquals("LEFT", direction,
                "Direction should be LEFT for positive power (" + power + ")");
        } else if (power < 0) {
            assertEquals("RIGHT", direction,
                "Direction should be RIGHT for negative power (" + power + ")");
        } else {
            assertEquals("STOPPED", direction,
                "Direction should be STOPPED for zero power");
        }
    }
}
