package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Shooter Test", group = "Test")
public class ShooterTest extends LinearOpMode {

    private DcMotor sl;
    private DcMotor sr;

    @Override
    public void runOpMode() {
        sl = hardwareMap.get(DcMotor.class, "sl");
        sr = hardwareMap.get(DcMotor.class, "sr");
        
        sl.setDirection(DcMotorSimple.Direction.FORWARD);
        sr.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Instructions", "Right trigger = shoot, Left trigger = reverse");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double rightTrigger = gamepad1.right_trigger;
            double leftTrigger = gamepad1.left_trigger;

            if (rightTrigger > 0) {
                sl.setPower(rightTrigger);
                sr.setPower(rightTrigger);
            } else if (leftTrigger > 0) {
                sl.setPower(-leftTrigger);
                sr.setPower(-leftTrigger);
            } else {
                sl.setPower(0);
                sr.setPower(0);
            }

            telemetry.addData("Right Trigger", "%.2f", rightTrigger);
            telemetry.addData("Left Trigger", "%.2f", leftTrigger);
            telemetry.addData("Left Motor Power", "%.2f", sl.getPower());
            telemetry.addData("Right Motor Power", "%.2f", sr.getPower());
            telemetry.update();
        }
    }
}
