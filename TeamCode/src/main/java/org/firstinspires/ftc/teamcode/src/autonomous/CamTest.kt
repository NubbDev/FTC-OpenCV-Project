package org.firstinspires.ftc.teamcode.src.autonomous

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.src.models.abot.instances.AutoInstance
import org.firstinspires.ftc.teamcode.src.pipelines.HandtrackBlue


@Autonomous(name="Cam Test", group="test")
class CamTest: LinearOpMode() {

    override fun runOpMode() {

        val instance = AutoInstance(this)
        val dash = FtcDashboard.getInstance()
//        val dashTelemetry = dash.telemetry
        telemetry = MultipleTelemetry(telemetry, dash.telemetry)
        instance.runCam(true)
        instance.cam!!.setPipeline(HandtrackBlue())
        dash.startCameraStream(instance.cam, 0.0)

        while (opModeInInit()) {
            if (instance.cam != null) {
                telemetry.addData("Cam", "Connected")
                telemetry.update()
            } else {
                telemetry.addData("Cam", "Not Connected")
                telemetry.update()
            }
//            dash.startCameraStream(instance.cam, 0.0)
        }
        waitForStart()

        while (opModeIsActive()){
            telemetry.addData("Frame Count", instance.cam!!.frameCount)
            telemetry.addData("FPS", instance.cam!!.fps.toInt())
            telemetry.addData("Total frame time ms", instance.cam!!.totalFrameTimeMs)
            telemetry.addData("Pipeline time ms", instance.cam!!.pipelineTimeMs)
            telemetry.addData("Overhead time ms", instance.cam!!.overheadTimeMs)
            telemetry.addData("Theoretical max FPS", instance.cam!!.currentPipelineMaxFps)

            telemetry.update()
            sleep(100)
        }
//            instance.closeCam()
    }
}