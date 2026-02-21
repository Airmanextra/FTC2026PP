package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import net.jqwik.api.*;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.StringLength;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Property-based and unit tests for the Turret subsystem (motor-based).
 */
class TurretTest {

    private static final String DEFAULT_MOTOR_NAME = "turretMotor";

    /**
     * Property: For any valid Hardware_Map containing a motor with the specified name,
     * initializing a Turret should successfully retrieve the DcMotorEx without throwing.
     */
    @Property
    void testMotorInitialization(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String motorName) {

        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);

        when(hardwareMap.get(DcMotorEx.class, motorName)).thenReturn(mockMotor);

        assertDoesNotThrow(() -> {
            Turret turret = new Turret(hardwareMap, motorName);
            verify(hardwareMap).get(DcMotorEx.class, motorName);
            assertEquals(0.0, turret.getCurrentPower(), 0.001);
        });
    }

    @Test
    void testDefaultConstructorInitialization() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);

        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);

        verify(hardwareMap).get(DcMotorEx.class, DEFAULT_MOTOR_NAME);
        assertEquals(0.0, turret.getCurrentPower(), 0.001);
    }

    /**
     * Property: Missing motor should throw with descriptive message.
     */
    @Property
    void testMissingMotorErrorHandling(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String motorName) {

        HardwareMap hardwareMap = mock(HardwareMap.class);
        when(hardwareMap.get(eq(DcMotorEx.class), eq(motorName)))
                .thenThrow(new IllegalArgumentException("Could not find " + motorName));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Turret(hardwareMap, motorName);
        });

        assertTrue(exception.getMessage().contains(motorName));
        assertTrue(exception.getMessage().contains("Could not find motor:"));
    }

    @Test
    void testMissingMotorThrowsException() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME))
                .thenThrow(new IllegalArgumentException("Could not find " + DEFAULT_MOTOR_NAME));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Turret(hardwareMap);
        });

        assertEquals("Could not find motor: " + DEFAULT_MOTOR_NAME, exception.getMessage());
    }

    @Test
    void testSetPowerWithValidValue() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.setPower(0.5);

        verify(mockMotor).setPower(-0.5);  // gear inverts: motor CW → turret CCW
        assertEquals(0.5, turret.getCurrentPower(), 0.001);
    }

    @Test
    void testSetPowerClampsHighValue() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.setPower(1.5);

        verify(mockMotor).setPower(-1.0);  // gear inverts
        assertEquals(1.0, turret.getCurrentPower(), 0.001);
    }

    @Test
    void testSetPowerClampsLowValue() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.setPower(-1.5);

        verify(mockMotor).setPower(1.0);  // gear inverts
        assertEquals(-1.0, turret.getCurrentPower(), 0.001);
    }

    @Test
    void testSetPowerWithZero() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.setPower(0.0);

        verify(mockMotor).setPower(-0.0);  // gear inverts (0 -> -0)
        assertEquals(0.0, turret.getCurrentPower(), 0.001);
    }

    @Test
    void testStopMethod() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.setPower(0.7);
        turret.stop();

        verify(mockMotor).setPower(-0.0);  // gear inverts (0 -> -0)
        assertEquals(0.0, turret.getCurrentPower(), 0.001);
    }

    @Property
    void testPowerClamping(@ForAll double power) {
        Assume.that(power < -1.0 || power > 1.0);

        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.setPower(power);

        double expected = power > 1.0 ? 1.0 : -1.0;
        verify(mockMotor).setPower(-expected);  // gear inverts
        assertEquals(expected, turret.getCurrentPower(), 0.001);
    }

    @Property
    void testSynchronizedPower(@ForAll @DoubleRange(min = -1.0, max = 1.0) double power) {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.setPower(power);

        verify(mockMotor).setPower(-power);  // gear inverts
        assertEquals(power, turret.getCurrentPower(), 0.001);
    }

    @Test
    void testRotateLeftWithPositiveSpeed() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.rotateLeft(0.75);

        verify(mockMotor).setPower(-0.75);  // gear inverts
        assertEquals(0.75, turret.getCurrentPower(), 0.001);
    }

    @Test
    void testRotateLeftClampsSpeed() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.rotateLeft(1.5);

        verify(mockMotor).setPower(-1.0);  // gear inverts
        assertEquals(1.0, turret.getCurrentPower(), 0.001);
    }

    @Property
    void testLeftRotationDirection(@ForAll double speed) {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.rotateLeft(speed);

        double expected = Math.max(-1.0, Math.min(1.0, speed));
        verify(mockMotor).setPower(-expected);  // gear inverts
        assertEquals(expected, turret.getCurrentPower(), 0.001);
    }

    @Test
    void testRotateRightWithPositiveSpeed() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.rotateRight(0.75);

        verify(mockMotor).setPower(0.75);  // gear inverts (-(-0.75))
        assertEquals(-0.75, turret.getCurrentPower(), 0.001);
    }

    @Test
    void testRotateRightClampsSpeed() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.rotateRight(1.5);

        verify(mockMotor).setPower(1.0);  // gear inverts
        assertEquals(-1.0, turret.getCurrentPower(), 0.001);
    }

    @Property
    void testRightRotationDirection(@ForAll double speed) {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.rotateRight(speed);

        double expected = Math.max(-1.0, Math.min(1.0, -speed));
        verify(mockMotor).setPower(-expected);  // gear inverts
        assertEquals(expected, turret.getCurrentPower(), 0.001);
    }

    @Test
    void testGetDirectionStopped() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.setPower(0.0);

        assertEquals("STOPPED", turret.getDirection());
    }

    @Test
    void testGetDirectionLeft() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.setPower(0.5);

        assertEquals("LEFT", turret.getDirection());
    }

    @Test
    void testGetDirectionRight() {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);
        turret.setPower(-0.5);

        assertEquals("RIGHT", turret.getDirection());
    }

    @Property
    void testStateConsistency(
            @ForAll @DoubleRange(min = -2.0, max = 2.0) double power1,
            @ForAll @DoubleRange(min = -2.0, max = 2.0) double power2,
            @ForAll @DoubleRange(min = -2.0, max = 2.0) double speed1,
            @ForAll @DoubleRange(min = -2.0, max = 2.0) double speed2) {

        HardwareMap hardwareMap = mock(HardwareMap.class);
        DcMotorEx mockMotor = mock(DcMotorEx.class);
        when(hardwareMap.get(DcMotorEx.class, DEFAULT_MOTOR_NAME)).thenReturn(mockMotor);

        Turret turret = new Turret(hardwareMap);

        turret.setPower(power1);
        double expected1 = Math.max(-1.0, Math.min(1.0, power1));
        assertEquals(expected1, turret.getCurrentPower(), 0.001);
        assertDirectionMatchesPower(turret.getDirection(), expected1);

        turret.setPower(power2);
        double expected2 = Math.max(-1.0, Math.min(1.0, power2));
        assertEquals(expected2, turret.getCurrentPower(), 0.001);
        assertDirectionMatchesPower(turret.getDirection(), expected2);

        turret.rotateLeft(speed1);
        double expectedLeft = Math.max(-1.0, Math.min(1.0, speed1));
        assertEquals(expectedLeft, turret.getCurrentPower(), 0.001);
        assertDirectionMatchesPower(turret.getDirection(), expectedLeft);

        turret.rotateRight(speed2);
        double expectedRight = Math.max(-1.0, Math.min(1.0, -speed2));
        assertEquals(expectedRight, turret.getCurrentPower(), 0.001);
        assertDirectionMatchesPower(turret.getDirection(), expectedRight);

        turret.stop();
        assertEquals(0.0, turret.getCurrentPower(), 0.001);
        assertEquals("STOPPED", turret.getDirection());
    }

    private void assertDirectionMatchesPower(String direction, double power) {
        if (power > 0) {
            assertEquals("LEFT", direction);
        } else if (power < 0) {
            assertEquals("RIGHT", direction);
        } else {
            assertEquals("STOPPED", direction);
        }
    }
}
