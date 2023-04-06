package org.firstinspires.ftc.teamcode.src.models.abot.utils;

import com.vuforia.Image;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import java.io.File;

public class Cam {
    public OpenCvCamera getCamInstance(WebcamName name) {
        return OpenCvCameraFactory.getInstance().createWebcam(name);
    }
    public OpenCvCamera getCamInstance(WebcamName name, int id) {
        return OpenCvCameraFactory.getInstance().createWebcam(name, id);
    }
}
