package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import net.jqwik.api.*;
import net.jqwik.api.constraints.AlphaChars;
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
        
        // Arrange: Create mock hardware map
        HardwareMap hardwareMap = mock(HardwareMap.class);
        CRServo mockLeftServo = mock(CRServo.class);
        CRServo mockRightServo = mock(CRServo.class);
        
        // Configure hardware map to throw exception for missing servos
        if (missingLeft) {
            when(hardwareMap.get(CRServo.class, leftServoName))
                .thenThrow(new IllegalArgumentException("Could not find " + leftServoName));
        } else {
            when(hardwareMap.get(CRServo.class, leftServoName)).thenReturn(mockLeftServo);
        }
        
        if (missingRight) {
            when(hardwareMap.get(CRServo.class, rightServoName))
                .thenThrow(new IllegalArgumentException("Could not find " + rightServoName));
        } else {
            when(hardwareMap.get(CRServo.class, rightServoName)).thenReturn(mockRightServo);
        }
        
        // Act & Assert: Turret initialization should throw exception with descriptive message
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Turret(hardwareMap, leftServoName, rightServoName);
        });
        
        // Verify the exception message contains the missing servo name
        String expectedServoName = missingLeft ? leftServoName : rightServoName;
        assertTrue(exception.getMessage().contains(expectedServoName),
            "Exception message should contain the missing servo name: " + expectedServoName);
        assertTrue(exception.getMessage().contains("Could not find servo:"),
            "Exception message should indicate servo not found");
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
}
