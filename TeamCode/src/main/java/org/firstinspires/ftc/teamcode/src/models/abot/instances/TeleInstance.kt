package org.firstinspires.ftc.teamcode.src.instances

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.src.models.abot.instances.AutoInstance
import org.firstinspires.ftc.teamcode.src.models.abot.utils.RobotClass
import org.firstinspires.ftc.teamcode.src.models.abot.utils.convertAngle
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

class TeleInstance(Instance: LinearOpMode) {
    private val instance = Instance
    val bot = RobotClass(instance)
    var heading = 0.0
    var lockHeading = true

    fun gamePadOne(gamePad: Gamepad = instance.gamepad1) {
        moveControls(gamePad)
    }

    fun gamePadTwo(gamePad: Gamepad = instance.gamepad2) {
    }

    private fun moveControls(gp: Gamepad) {
        val leftY = -gp.left_stick_y.toDouble()
        val leftX = gp.left_stick_x.toDouble() * 1.1
        val rightY = -gp.right_stick_y.toDouble()
        val rightX = gp.right_stick_x.toDouble()
        val pow = 0.8

        val denominator = max(abs(leftY) + abs(leftX) + abs(rightX), 1.0)

        if (abs(leftX) > 0 || abs(leftY) > 0 || abs(rightX) > 0) {
            if (abs(leftX) > 0) {
                heading = bot.absoluteHeading
            }
            lockHeading = false
            bot.move(
                ((leftY + rightX + leftX ) / denominator) * pow,
                ((leftY - rightX - leftX ) / denominator) * pow,
                ((leftY - rightX + leftX) / denominator) * pow,
                ((leftY + rightX - leftX) / denominator) * pow
            )
        } else {
            lockHeading = true
        }

    }

    fun holdHeading() {
        var diff = convertAngle(heading) - convertAngle(bot.rawHeading)
        while (abs(diff) > 1) {
            diff = convertAngle(heading) - convertAngle(bot.rawHeading)
            val pow = maxOf(AutoInstance.Bot.power * ((abs(diff) / 100)), 0.1)
            val value = if (abs(diff) > 180) -sign(diff)  else sign(diff)

            if (value > 0) bot.move(-pow, pow, -pow, pow) else bot.move(pow, -pow, pow, -pow)
        }
        bot.move(0.0, 0.0, 0.0, 0.0)
    }
}