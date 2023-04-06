package org.firstinspires.ftc.teamcode.src.pipelines

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.src.models.abot.instances.Position
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline

@Config
object HandTrackConfig {

    @JvmField
    var lowH = 128.0
    @JvmField
    var lowS = 0.0
    @JvmField
    var lowV = 0.0

    @JvmField
    var highH = 255.0
    @JvmField
    var highS = 255.0
    @JvmField
    var highV = 255.0

}

class HandtrackBlue() : OpenCvPipeline() {
    var PosX: Position = Position.Middle
    var PosY: Position = Position.Middle
    val contours = ArrayList<MatOfPoint>()
    private var biggestContour = MatOfPoint()
    var differenceX = 0.0
    var differenceY = 0.0
    object Center {
        var x = 0.0
        var y = 0.0
    }
    private val white = Scalar(255.0, 255.0, 255.0)

    override fun processFrame(input: Mat): Mat {
        val center = input.width() / 2.0
        val thirdOfHeight = input.height() / 3.0
        val bottom = input.height() - thirdOfHeight
        val mat = Mat()

        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV)
        if (mat.empty()) return input

        val thresh = Mat()

        val lowHSV = Scalar(90.0, 0.0, 0.0)
        val highHSV = Scalar(178.0, 255.0, 145.0)
        Core.inRange(mat, lowHSV, highHSV, thresh)

        if (thresh.empty()) return input

        val masked = Mat()
        Core.bitwise_and(mat,mat,masked,thresh)
        //calc avg HSV values of the white thresh values
        val avg = Core.mean(masked, thresh)

        //scale avg saturation to 150
        val scaledMask = Mat()
        masked.convertTo(scaledMask, -1, 150/avg.`val`[1], 0.0)

        val scaledThresh = Mat()
        val strictLowHSV = Scalar(0.0, 100.0, 18.0)
        val strictHighHSV = Scalar(255.0, 255.0, 255.0)

        Core.inRange(scaledMask, strictLowHSV, strictHighHSV, scaledThresh)

        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
            Size(5.0, 5.0)
        )

        val cleanup = Mat()
        Imgproc.morphologyEx(scaledThresh, cleanup, Imgproc.MORPH_OPEN, kernel)

        val finalCleanup = Mat()
        Imgproc.morphologyEx(cleanup, finalCleanup, Imgproc.MORPH_CLOSE, kernel)

        val contour = Mat()
        Imgproc.findContours(
            finalCleanup,
            contours,
            contour,
            Imgproc.RETR_LIST,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        val finalMask = Mat()
        Core.bitwise_and(mat,mat,finalMask,scaledThresh)


        if (contours.size != 0) {
            Imgproc.drawContours(
                input,
                contours,
                -1,
                Scalar(0.0, 255.0, 0.0),
                2
            )
            biggestContour = contours.maxByOrNull { Imgproc.contourArea(it) }!!
            val boundRect = Imgproc.boundingRect(biggestContour)
            val M = Imgproc.moments(biggestContour)
            Center.x = (M._m10/M._m00)
            Center.y = (M._m01/M._m00)

            Imgproc.rectangle(
                input,
                Point(boundRect.x.toDouble(), boundRect.y.toDouble()),
                Point(boundRect.x.toDouble() + boundRect.width, boundRect.y.toDouble() + boundRect.height),
                Scalar(255.0, 255.0, 0.0),
                2
            )
        } else {
            Center.x = 0.0
            Center.y = 0.0
        }

        Imgproc.circle(input, Point(Center.x, Center.y), 3, white, -1)

//        input.release()
//        Imgproc.cvtColor(masked, input, Imgproc.COLOR_HSV2RGB)
//        thresh.copyTo(input)
        mat.release()
        thresh.release()
        masked.release()
        scaledMask.release()
        scaledThresh.release()
        cleanup.release()
        finalCleanup.release()
        kernel.release()
        contour.release()
        contours.clear()

        if (Center.x <= center + 10 && Center.x >= center - 10) {
            PosX = Position.Middle
            differenceX = 0.0
        } else if (Center.x > center) {
            PosX = Position.Right
            differenceX = (center - Center.x) / 1000.0
        } else if (Center.x < center && Center.x != 0.0) {
            PosX = Position.Left
            differenceX = (center - Center.x) / 1000.0
        } else {
            PosX = Position.None
            differenceX = 0.0
        }

        if (Center.y >= bottom) {
            PosY = Position.Bottom
            differenceY = 0.0
        } else if (Center.y <= bottom && Center.y != 0.0) {
            PosY = Position.Top
            differenceY = (bottom - Center.y) / 1000.0
        } else {
            PosY = Position.None
            differenceY = 0.0
        }

        return input
    }

    fun getCenterOfObj(): Center {
        return Center
    }
}