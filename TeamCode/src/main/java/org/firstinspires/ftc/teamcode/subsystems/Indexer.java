package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Indexer servo that acts as a gate to stop balls from transferring into the intake.
 *
 * When closed, the indexer blocks balls from entering. When open, balls can flow through.
 */
public class Indexer {

    private final Servo indexerServo;

    // Servo positions (adjust for your mechanism)
    private double positionClosed = 0.0;  // Blocks balls
    private double positionOpen = 1.0;    // Allows balls through

    // Default hardware name
    private static final String DEFAULT_INDEXER_SERVO_NAME = "indexerServo";

    private boolean isOpen;

    /**
     * Constructs an Indexer with the default servo name.
     *
     * @param hardwareMap The FTC hardware map
     * @throws IllegalArgumentException if the servo cannot be found
     */
    public Indexer(HardwareMap hardwareMap) {
        this(hardwareMap, DEFAULT_INDEXER_SERVO_NAME);
    }

    /**
     * Constructs an Indexer with a custom servo name.
     *
     * @param hardwareMap The FTC hardware map
     * @param servoName   The hardware name for the indexer servo
     * @throws IllegalArgumentException if the servo cannot be found
     */
    public Indexer(HardwareMap hardwareMap, String servoName) {
        try {
            this.indexerServo = hardwareMap.get(Servo.class, servoName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find indexer servo: " + servoName);
        }

        // Start closed (blocks balls) for safety
        close();
    }

    /**
     * Opens the indexer so balls can flow into the intake.
     */
    public void open() {
        indexerServo.setPosition(positionOpen);
        isOpen = true;
    }

    /**
     * Closes the indexer to block balls from entering the intake.
     */
    public void close() {
        indexerServo.setPosition(positionClosed);
        isOpen = false;
    }

    /**
     * Sets the indexer to a custom position (0.0 = closed, 1.0 = open).
     *
     * @param position Position in range [0.0, 1.0]
     */
    public void setPosition(double position) {
        double clamped = Math.max(0.0, Math.min(1.0, position));
        indexerServo.setPosition(clamped);
        isOpen = clamped > 0.5;  // Approximate
    }

    /**
     * @return true if the indexer is open, false if closed
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * @return The underlying servo for advanced control
     */
    public Servo getServo() {
        return indexerServo;
    }

    /**
     * Configures custom open/closed positions if your mechanism differs.
     *
     * @param closedPosition Servo position when closed (blocks balls)
     * @param openPosition   Servo position when open (allows balls)
     */
    public void configurePositions(double closedPosition, double openPosition) {
        this.positionClosed = Math.max(0.0, Math.min(1.0, closedPosition));
        this.positionOpen = Math.max(0.0, Math.min(1.0, openPosition));
    }
}
