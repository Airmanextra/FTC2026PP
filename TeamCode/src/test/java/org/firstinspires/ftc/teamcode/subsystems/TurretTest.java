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
}
