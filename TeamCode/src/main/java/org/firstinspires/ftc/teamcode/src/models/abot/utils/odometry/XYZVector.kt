package org.firstinspires.ftc.teamcode.src.utils.odometry

import org.firstinspires.ftc.teamcode.src.utils.odometry.XYZVector

class XYZVector {
    var x: Double
    var y: Double
    var angle: Double

    constructor(X: Double, Y: Double, Angle: Double) {
        x = X
        y = Y
        angle = Angle
    }

    constructor(vector: XYZVector) {
        x = vector.x
        y = vector.y
        angle = vector.angle
    }
}