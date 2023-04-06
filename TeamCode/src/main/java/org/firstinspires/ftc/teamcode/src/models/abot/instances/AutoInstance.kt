package org.firstinspires.ftc.teamcode.src.models.abot.instances

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.src.models.abot.utils.Cam
import org.firstinspires.ftc.teamcode.src.models.abot.utils.RobotClass
import org.firstinspires.ftc.teamcode.src.models.abot.utils.convertAngle
import org.firstinspires.ftc.teamcode.src.models.abot.utils.inchToTick
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvWebcam
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

enum class Position {
    Left, Right, Middle, None, Top, Bottom
}

class AutoInstance(Instance: LinearOpMode) {
    @Config
    object Bot {
        @JvmField var power = 0.95
    }
    private val instance = Instance
    val bot = RobotClass(instance)
    var cam: OpenCvWebcam? = null
    var heading = 0.0
    var difference = 0.0
    var pow = 0.0
    var diff= 0.0
    var value = 0.0
    private var cameraMonitorViewId = instance.hardwareMap.appContext.resources.getIdentifier(
        "cameraMonitorViewId", "id",
        instance.hardwareMap.appContext.packageName
    )

    //Initialize class upon called
    init {
        /*
        TODO: Add Servo Positions and anything else specific to Autonomous
        */
    }

    /**
     * Forward bot function
     * @param distance in inches (+/- for forward or backwards)
     * @param power number range from 0 to 1 (No negatives pls)
     * @param heading direction of where the bot is pointing (has to be the same
     */
    fun forward(distance: Double, power: Double) {
//        val pid = PIDController(3.0, 0.0, 0.5) // Changes these constants as needed.

        val targetFL = bot.fl.currentPosition + inchToTick(distance)
        val targetFR = bot.fl.currentPosition + inchToTick(distance)
        val targetBL = bot.fl.currentPosition + inchToTick(distance)
        val targetBR = bot.fl.currentPosition + inchToTick(distance)

        bot.fl.targetPosition = targetFL.toInt()
        bot.fr.targetPosition = targetFR.toInt()
        bot.bl.targetPosition = targetBL.toInt()
        bot.br.targetPosition = targetBR.toInt()

        bot.setMode(DcMotor.RunMode.RUN_TO_POSITION)

        bot.move(power, power, power, power)

        while (instance.opModeIsActive() &&
            bot.fl.isBusy &&
            bot.fr.isBusy &&
            bot.bl.isBusy &&
            bot.br.isBusy
        ) {
//            val pow = pid.angleUpdate(bot.rawHeading, heading)
//            bot.adjustBot(pow, pow)
        }
        bot.move(0.0, 0.0, 0.0, 0.0)
        bot.setMode(DcMotor.RunMode.RUN_USING_ENCODER)
        instance.sleep(100)
    }

    /**
     * Pivot Function
     * @param angle in degrees
     */
    fun pivot(angle: Double, telemetry: Telemetry) {
        heading = angle
        diff = convertAngle(heading) - convertAngle(bot.rawHeading)
        while (instance.opModeIsActive() && abs(diff) > 1) {
            diff = convertAngle(heading) - convertAngle(bot.absoluteHeading)
            pow = maxOf(Bot.power * ((abs(diff) / 100)), 0.1)
            value = if (abs(diff) > 180) -sign(diff)  else sign(diff)

            if (value > 0) bot.move(-pow, pow, -pow, pow) else bot.move(pow, -pow, pow, -pow)

            telemetry.addData("Target Heading", heading)
            telemetry.addData("Current Heading", bot.absoluteHeading)
            telemetry.addData("", "")
            telemetry.addData("Difference", diff)
            telemetry.addData("Value", value)
            telemetry.addData("Power", pow)
            telemetry.update()
        }
        bot.move(0.0, 0.0, 0.0, 0.0)
    }

    fun holdHeading(telemetry: Telemetry, timeout: Int) {
        val timer = ElapsedTime()
        timer.reset()
        while(instance.opModeIsActive() && timer.milliseconds() < timeout) {
            diff = convertAngle(heading) - convertAngle(bot.rawHeading)
            while (abs(diff) > 1) {
                diff = convertAngle(heading) - convertAngle(bot.rawHeading)
                pow = maxOf(Bot.power * ((abs(diff) / 100)), 0.1)
                value = if (abs(diff) > 180) -sign(diff)  else sign(diff)

                if (value > 0) bot.move(-pow, pow, -pow, pow) else bot.move(pow, -pow, pow, -pow)

                telemetry.addData("Target Heading", heading)
                telemetry.addData("Current Heading", bot.absoluteHeading)
                telemetry.addData("", "")
                telemetry.addData("Difference", diff)
                telemetry.addData("Value", value)
                telemetry.addData("Power", pow)
                telemetry.update()
            }
            bot.move(0.0, 0.0, 0.0, 0.0)
        }
    }

    fun runCam(streamed: Boolean = false) {
        val camInstance = Cam()
        cam = if (streamed) camInstance.getCamInstance(bot.cam, cameraMonitorViewId) as OpenCvWebcam else camInstance.getCamInstance(bot.cam) as OpenCvWebcam
        instance.telemetry.addData("Camera", "Initialized")
        instance.telemetry.update()
        cam!!.openCameraDeviceAsync(object : OpenCvCamera.AsyncCameraOpenListener {
            override fun onOpened() {
                cam!!.setViewportRenderer(OpenCvCamera.ViewportRenderer.GPU_ACCELERATED)
                cam!!.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT)
                instance.telemetry.addData("Camera", "Streaming")
                instance.telemetry.update()
            }

            override fun onError(errorCode: Int) {
                /*
                * This will be called if the camera could not be opened
                */
            }
        }
        )
    }

    fun closeCam() {
        if (cam == null) return
        cam!!.closeCameraDevice()
    }

    //Q: I am receiving an error that says 15 issues were found when checking AAR metadata. What does this mean?
    //A: This means that the AAR file is not being generated correctly. This is usually caused by a missing dependency. Check the build.gradle file for the module that is missing the dependency.


}