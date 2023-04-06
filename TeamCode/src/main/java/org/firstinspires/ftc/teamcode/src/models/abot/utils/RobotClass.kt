package org.firstinspires.ftc.teamcode.src.models.abot.utils

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import kotlin.math.abs

class RobotClass(Instance: LinearOpMode) {
    val fl: DcMotorEx = Instance.hardwareMap.get(DcMotorEx::class.java, "FL")
    val fr: DcMotorEx = Instance.hardwareMap.get(DcMotorEx::class.java, "FR")
    val br: DcMotorEx = Instance.hardwareMap.get(DcMotorEx::class.java, "BR")
    val bl: DcMotorEx = Instance.hardwareMap.get(DcMotorEx::class.java, "BL")
    val cam: WebcamName = Instance.hardwareMap.get("cam1") as WebcamName
    val imu: BNO055IMU = Instance.hardwareMap.get(BNO055IMU::class.java, "imu")

    var currentRPos = 0
    var currentLPos = 0
    var currentBackPos = 0
    var oldRPos = 0
    var oldLPos = 0
    var oldBackPos = 0

    init {
        // Set Each Wheel Direction
        fl.direction = DcMotorSimple.Direction.REVERSE
        fr.direction = DcMotorSimple.Direction.FORWARD
        bl.direction = DcMotorSimple.Direction.REVERSE
        br.direction = DcMotorSimple.Direction.FORWARD

        fl.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        fl.mode = DcMotor.RunMode.RUN_USING_ENCODER
        fr.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        fr.mode = DcMotor.RunMode.RUN_USING_ENCODER
        bl.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        bl.mode = DcMotor.RunMode.RUN_USING_ENCODER
        br.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        br.mode = DcMotor.RunMode.RUN_USING_ENCODER


        // Behaviour when Motor Power = 0
        fl.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        fr.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        bl.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        br.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        // define initialization values for IMU, and then initialize it.
        val params = BNO055IMU.Parameters()
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES
        params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        params.loggingEnabled = true
        params.loggingTag = "IMU"
        params.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()
        imu.initialize(params)
    }

    fun setMode(mode: DcMotor.RunMode?) {
        fl.mode = mode
        fr.mode = mode
        bl.mode = mode
        br.mode = mode
    }

    /**
     * Move Control Function
     * @param flPower Front Left Motor Power
     * @param frPower Front Right Motor Power
     * @param blPower Back Left Motor Power
     * @param brPower Back Right Motor Power
     */
    fun move(flPower: Double?, frPower: Double?, blPower: Double?, brPower: Double?) {
        fl.power = flPower!!
        fr.power = frPower!!
        bl.power = blPower!!
        br.power = brPower!!
    }

    val rawHeading: Double
        get() = imu.getAngularOrientation(
            AxesReference.INTRINSIC,
            AxesOrder.ZYX,
            AngleUnit.DEGREES).firstAngle.toDouble()
    val absoluteHeading: Double
        get() = if (rawHeading < 0) {
            360 - abs(rawHeading)
        } else {
            rawHeading
        }

    fun resetEncoders() {
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
        setMode(DcMotor.RunMode.RUN_USING_ENCODER)
    }
}