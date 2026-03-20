package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.TurretTargeting;
import org.firstinspires.ftc.teamcode.subsystems.SmartShooter;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

import org.firstinspires.ftc.teamcode.subsystems.Indexer;

@TeleOp
public class MecanumTeleOpRED extends LinearOpMode {
    private Turret turret;
    private TurretTargeting targeting;
    private SmartShooter shooter;
    private Intake intake;
    private Indexer indexer;
    
    private static final double INTAKE_POWER = 0.8;
    
    // Power ramping variables to reduce belt skipping
    private double prevFrontLeftPower = 0;
    private double prevBackLeftPower = 0;
    private double prevFrontRightPower = 0;
    private double prevBackRightPower = 0;
    
    // Maximum power change per loop (adjust based on testing)
    private static final double MAX_POWER_CHANGE = 0.15; // Reduced from instant changes
    private static final double MAX_DRIVE_POWER = 0.85;  // Limit max power for GoBuilda 312 RPM
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("lf");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("lb");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("rf");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("rb");

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);

        // Set motor zero power behavior to reduce stress on belts
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        shooter = new SmartShooter(hardwareMap);
        targeting = new TurretTargeting(hardwareMap);
        turret = new Turret(hardwareMap);
        intake = new Intake(hardwareMap);
        indexer = new Indexer(hardwareMap);
        
        // Configure shooter
        shooter.configureKinematics(0.3, 1.2, 45.0);
        shooter.configureCamera(0.25, 15.0);
        shooter.setFlywheelDiameter(0.1);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.options) {
                imu.resetYaw();
            }

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            // Apply power limiting for GoBuilda 312 RPM motors
            frontLeftPower = Math.max(-MAX_DRIVE_POWER, Math.min(MAX_DRIVE_POWER, frontLeftPower));
            backLeftPower = Math.max(-MAX_DRIVE_POWER, Math.min(MAX_DRIVE_POWER, backLeftPower));
            frontRightPower = Math.max(-MAX_DRIVE_POWER, Math.min(MAX_DRIVE_POWER, frontRightPower));
            backRightPower = Math.max(-MAX_DRIVE_POWER, Math.min(MAX_DRIVE_POWER, backRightPower));

            // Apply power ramping to reduce belt skipping
            frontLeftPower = rampPower(prevFrontLeftPower, frontLeftPower);
            backLeftPower = rampPower(prevBackLeftPower, backLeftPower);
            frontRightPower = rampPower(prevFrontRightPower, frontRightPower);
            backRightPower = rampPower(prevBackRightPower, backRightPower);

            // Store current powers for next iteration
            prevFrontLeftPower = frontLeftPower;
            prevBackLeftPower = backLeftPower;
            prevFrontRightPower = frontRightPower;
            prevBackRightPower = backRightPower;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            if (gamepad1.left_bumper) {
                targeting.aimAtRedBasket();
            }
            
//            // Shooting - opens indexer to allow balls through
//            if (gamepad1.right_trigger > 0.5) {
//                shooter.shootAtRedBasket();
//                indexer.open();
//                // Feed balls with intake/transfer when shooting
//                intake.transfer(INTAKE_POWER);
//            } else {
//                shooter.stopShooter();
//                indexer.close();
//            }
            
            // Intake control with square button
            if (gamepad1.square) {
                intake.intake(INTAKE_POWER);
                indexer.open();
            } else if (gamepad1.cross) {
                intake.intake(-INTAKE_POWER);
                indexer.open();
            } else {
                intake.stop();
            }

            // Update subsystems
            shooter.update();
            targeting.update();

            telemetry.addData("Shooter RPM", shooter.getCurrentRPM());  
            telemetry.addData("Shooter Power", shooter.getShooterPower());
            telemetry.addData("Shooter Target RPM", shooter.getRequiredRPM(true));
            telemetry.addData("Shooter At Target Velocity", shooter.isAtTargetVelocity(shooter.getRequiredRPM(true), 100));
            telemetry.addData("Shooter On Target", shooter.shootAtRedBasket());
            telemetry.addData("Intake Power", intake.getCurrentPower());
            telemetry.addData("Indexer Open", indexer.isOpen());
            telemetry.update();
        }
    }
    
    /**
     * Smoothly ramp motor power to reduce belt skipping
     * @param prevPower Previous motor power
     * @param targetPower Desired motor power
     * @return Ramped power value
     */
    private double rampPower(double prevPower, double targetPower) {
        double powerDiff = targetPower - prevPower;
        
        if (Math.abs(powerDiff) <= MAX_POWER_CHANGE) {
            return targetPower;
        } else {
            return prevPower + Math.signum(powerDiff) * MAX_POWER_CHANGE;
        }
    }
}