package org.firstinspires.ftc.teamcode.src.autonomous

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.src.models.abot.instances.AutoInstance
import org.firstinspires.ftc.teamcode.src.pipelines.HandtrackRed
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

@Config
object Bot {
    @JvmField var power = 0.95
}

enum class BotState {
Forward, Pivot
}

@Autonomous(name="Red Obj Track", group="test")
class RedObjectTracking: LinearOpMode() {

    override fun runOpMode() {

        val instance = AutoInstance(this)
        val dash = FtcDashboard.getInstance()
        telemetry = MultipleTelemetry(telemetry, dash.telemetry)
        val tracker = HandtrackRed()

//        val dashTelemetry = dash.telemetry
        instance.runCam(true)
        instance.cam!!.setPipeline(tracker)
        dash.startCameraStream(instance.cam, 0.0)



        while (opModeInInit()) {
            if (instance.cam != null) {
                telemetry.addData("Cam", "Connected")
                telemetry.update()
            } else {
                telemetry.addData("Cam", "Not Connected")
                telemetry.update()
            }
            telemetry.addData("Object X", tracker.getCenterOfObj().x)
            telemetry.addData("Object Y", tracker.getCenterOfObj().y)
            telemetry.addData("Position X", tracker.PosX)
            telemetry.addData("Position Y", tracker.PosY)
            telemetry.addData("Difference X", tracker.differenceX)
            telemetry.addData("Difference Y", tracker.differenceY)
            telemetry.update()
        }
        waitForStart()

        while (opModeIsActive()){
            val denominator = max(abs(tracker.differenceY) + abs(tracker.differenceX), 1.0)
            telemetry.addData("Object X", tracker.getCenterOfObj().x)
            telemetry.addData("Object Y", tracker.getCenterOfObj().y)
            telemetry.addData("Position X", tracker.PosX)
            telemetry.addData("Position Y", tracker.PosY)
            telemetry.addData("Difference X", tracker.differenceX)
            telemetry.addData("Difference Y", tracker.differenceY)
            telemetry.addData("", "")
            telemetry.addData("Power FL", instance.bot.fl.power)
            telemetry.addData("Power FR", instance.bot.fr.power)
            telemetry.addData("Power BL", instance.bot.bl.power)
            telemetry.addData("Power BR", instance.bot.br.power)
            telemetry.addData("", "")
            telemetry.addData("Denominator", denominator)

            telemetry.update()

            instance.bot.move(
                ((tracker.differenceY - tracker.differenceX).pow(1))/10.0 * Bot.power,
                ((tracker.differenceY + tracker.differenceX).pow(1))/10.0 * Bot.power,
                ((tracker.differenceY - tracker.differenceX).pow(1))/10.0 * Bot.power,
                ((tracker.differenceY + tracker.differenceX).pow(1))/10.0 * Bot.power
            )
        }
    }
}