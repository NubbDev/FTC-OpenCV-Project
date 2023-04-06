package org.firstinspires.ftc.teamcode.src.autonomous

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.src.models.abot.instances.AutoInstance
import kotlin.math.abs

@Autonomous(name = "Pivot Test", group = "test")
class PivotTest: LinearOpMode() {

    override fun runOpMode() {
        val instance = AutoInstance(this)
        val dash = FtcDashboard.getInstance()
        telemetry = MultipleTelemetry(telemetry, dash.telemetry)
        while (opModeInInit()) {
            telemetry.addData("Robot Heading", instance.bot.absoluteHeading)
            telemetry.update()
        }
        waitForStart()
        instance.bot.resetEncoders()

        if (opModeIsActive()) {
//            instance.pivot(45.0, telemetry)
            instance.holdHeading(telemetry, 60000)
//            instance.pivot(315.0, telemetry)
//            instance.holdHeading(telemetry, 5000)
        }
    }

}