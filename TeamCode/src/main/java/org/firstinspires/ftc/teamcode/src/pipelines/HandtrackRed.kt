package org.firstinspires.ftc.teamcode.src.pipelines

import org.firstinspires.ftc.teamcode.src.models.abot.instances.Position
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline

class HandtrackRed() : OpenCvPipeline() {
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
    object Screen {
        var width = 0
        var height = 0
    }
    private val white = Scalar(255.0, 255.0, 255.0)

    override fun processFrame(input: Mat): Mat {
        val centerX = input.width() / 2.0
        val fifthY = input.height() / 5.0
        val bottom = input.height()
        Screen.height = input.height()
        Screen.width = input.width()

        val heightArray: DoubleArray = doubleArrayOf(0.0 * fifthY, 1.0 * fifthY, 2.0 * fifthY, 3.0 * fifthY, 4.0 * fifthY, 5.0 * fifthY)
        val widthArray: DoubleArray = doubleArrayOf(centerX - 15, centerX + 15)
        val mat = Mat()

        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV)
        if (mat.empty()) return input

        val thresh = Mat()

        val lowHSV = Scalar(0.0, 10.0, 0.0)
        val highHSV = Scalar(15.0, 255.0, 255.0)
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
            Center.y = boundRect.y.toDouble() + boundRect.height

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
//        scaledThresh.copyTo(input)
//        Imgproc.cvtColor(finalMask, input, Imgproc.COLOR_HSV2RGB)
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

        if (Center.x <= widthArray[1] && Center.x >= widthArray[0]) {
            PosX = Position.Middle
            differenceX = 0.0
        } else if (Center.x > widthArray[1]) {
            PosX = Position.Right
            differenceX = (centerX - Center.x) / 100.0
        } else if (Center.x < widthArray[0] && Center.x != 0.0) {
            PosX = Position.Left
            differenceX = (centerX - Center.x) / 100.0
        } else {
            PosX = Position.None
            differenceX = 0.0
        }

        if (Center.y >= heightArray[3] && Center.y <= heightArray[4]) {
            PosY = Position.Middle
            differenceY = 0.0
        } else if (Center.y < heightArray[3] && Center.y != heightArray[0]) {
            PosY = Position.Top
            differenceY = (heightArray[3] - Center.y) / 100.0
        } else if (Center.y > heightArray[4]) {
            PosY = Position.Bottom
            differenceY = (heightArray[4] - Center.y) / 100.0
        } else {
            PosY = Position.None
            differenceY = 0.0
        }

        return input
    }

    fun getCenterOfObj(): Center {
        return Center
    }
    fun getScreenSize(): Screen{
        return Screen
    }
}