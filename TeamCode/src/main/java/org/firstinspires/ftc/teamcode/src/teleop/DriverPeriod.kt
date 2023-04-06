package org.firstinspires.ftc.teamcode.src.teleop

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.firstinspires.ftc.teamcode.src.instances.TeleInstance

@TeleOp(name="TeleOp", group = "teleop")
class DriverPeriod: LinearOpMode() {
    override fun runOpMode() {
        val instance = TeleInstance(this)
        val dash = FtcDashboard.getInstance()
        telemetry = MultipleTelemetry(telemetry, dash.telemetry)

        waitForStart()

        CoroutineScope(Main).launch {
            while (opModeIsActive()) {
                instance.gamePadOne(gamepad1)
            }
        }

        while (opModeIsActive()) {
            if (instance.lockHeading) {
                instance.holdHeading()

            }
            telemetry.addData("Heading", instance.heading)
            telemetry.addData("Current", instance.bot.absoluteHeading)
            telemetry.update()
        }
    }
}