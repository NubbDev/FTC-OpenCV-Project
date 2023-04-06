package org.firstinspires.ftc.teamcode.src.models.abot.utils

import kotlin.math.PI

private const val BOT_RADIUS = 9.5//8.5
private const val COUNTS_PER_MOTOR_REV = 529.2 // eg: GoBILDA 312 RPM Yellow Jacket
private const val DRIVE_GEAR_REDUCTION = 1.0 // No External Gearing.
private const val WHEEL_DIAMETER_INCHES = 4.0 // For figuring circumference
private const val COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION / (WHEEL_DIAMETER_INCHES * Math.PI)
fun inchToTick(inches: Double): Double {
    return inches * COUNTS_PER_INCH
}

fun targetDegrees(degrees: Double): Double {
    return inchToTick(((BOT_RADIUS * PI) / 180 ) * degrees) * 4/3
}

fun convertAngle(angleIn: Double): Double{
    if(angleIn < 0)
    {
        return -((-angleIn + 180) % 360 - 180);
    }
    return (angleIn + 180) % 360 - 180;
}