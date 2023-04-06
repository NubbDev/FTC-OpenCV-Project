package org.firstinspires.ftc.teamcode.src.models.abot.utils

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/*
No need to rewrite these functions, just change the value of these constants
 */

private const val BOT_RADIUS = 9.5 //in Inches
private const val COUNTS_PER_MOTOR_REV = 529.2 // eg: Rev Hex Ultraplanetary 4:1 5:1 gear ratio
private const val DRIVE_GEAR_REDUCTION = 1.0 // No External Gearing.
private const val WHEEL_DIAMETER_INCHES = 4.0 // For figuring circumference
private const val COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION / (WHEEL_DIAMETER_INCHES * PI)

const val L = 15.0 // distance between encoder 1 and 2
const val B = 10.0 // distance between encoder 1 + 2 and encoder 3
const val R = 3.5 / 2.54 // radius of the omniwheels in inches
const val N = 8192.0 // ticks / rev (counts/rev)
const val C = (2.0 * PI * R) / N

//fun odometry(bot: RobotClass) {
//    bot.oldLPos = bot.currentLPos
//    bot.oldRPos = bot.currentRPos
//    bot.oldBackPos = bot.currentBackPos
//
//    bot.currentLPos = bot.posL.currentPosition
//    bot.currentRPos = bot.posR.currentPosition
//    bot.currentBackPos = bot.posBack.currentPosition
//
//    val dn1 = (bot.currentLPos - bot.oldLPos).toDouble()
//    val dn2 = (bot.currentRPos - bot.oldRPos).toDouble()
//    val dn3 = (bot.currentBackPos - bot.oldBackPos).toDouble()
//
//    // Bot's small movements (turn + move)
//    val dX = C * (dn1 + dn2) / 2.0
//    val dAngle = C * (dn2 - dn1) / L
//    val dY = C * (dn3 - B * (dn2 - dn1) / L)
//
//    // Update the positions
//    val angle = bot.pos.angle + (dAngle / 2.0)
//    bot.pos.x += (dX * cos(angle) - dY * sin(angle))
//    bot.pos.y += ((dX * sin(angle)) + dY * cos(angle))
//    bot.pos.angle += dAngle
//}