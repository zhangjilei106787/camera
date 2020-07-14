package com.zjl.image.surfuce;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.zjl.image.utils.BitmapUtils;
import com.zjl.image.utils.FaceUtils;
import com.zjl.image.utils.FileUtils;
import com.zjl.image.view.FaceView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraHelper implements Camera.PreviewCallback {

    public Camera camera;
    public SurfaceView surfaceView;
    public int winth = 2610;
    public int height = 3840;
    public SurfaceHolder surfaceHolder;
    public Activity activity;
    public PreViewCallback callback;
    public Camera.Parameters parameters;
    public int mDisplayOrientation;
    public int cameraFace = Camera.CameraInfo.CAMERA_FACING_BACK;//默认后置摄像头
    public String dirPath = Environment.getExternalStorageDirectory() + "/camerafile";
    private boolean startFaceDetect = false;
    private FaceView faceView;

    public CameraHelper(Activity activity, SurfaceView surfaceView) {
        this.activity = activity;
        this.surfaceView = surfaceView;
        surfaceHolder = surfaceView.getHolder();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }

    public void setFaceView(FaceView faceView) {
        this.faceView = faceView;
    }

    private void savePicture(byte[] bytes) {
        if (!new File(dirPath).exists()) {
            new File(dirPath).mkdirs();
        }
        new Thread(() -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap bitmap1 = null;
                if (cameraFace == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    bitmap1 = BitmapUtils.INSTANCE.rotate(bitmap, 90f);
                } else {
                    bitmap1 = BitmapUtils.INSTANCE.rotate(bitmap, 270f);

                }
                FileUtils.writeFile(BitmapUtils.INSTANCE.toByteArray(bitmap1), dirPath + "/" + System.currentTimeMillis() + ".jpg");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void takePhoto() {
        camera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                camera.startPreview();
                savePicture(bytes);
            }
        });
    }

    public void swithCamera() {
        if (getCameraNum() > 1) {
            this.cameraFace = cameraFace == Camera.CameraInfo.CAMERA_FACING_FRONT ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
            stopRreview();
            initCamera();
        } else {
            Toast.makeText(activity, "暂不支持前置摄像头", Toast.LENGTH_SHORT).show();
        }

    }

    public interface PreViewCallback {
        void callBack(byte[] bytes, Camera camera);
    }

    public void initCamera() {
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                //surfacefView创建完毕 ,开始渲染
                if (camera == null) {
                    openCamera(cameraFace);
                }
                startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                releaseCamera();
            }
        });
    }

    public void setFaceDetect(boolean isface) {
        this.startFaceDetect = isface;
    }

    public void stopRreview() {
        if (camera != null) {
            releaseCamera();
        }
    }

    public void startPreview() {
        if (camera != null) {
            try {
                // 相机关联到surfaceView上 渲染
                camera.setPreviewDisplay(surfaceHolder);
                //旋转图像
                setCameraDisplayOrientation(activity);
                camera.startPreview();
                if (startFaceDetect) {
                    startFaceDetect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startFaceDetect() {
        camera.startFaceDetection();
        camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                Log.e("ceshi", faces.length + "");
                FaceUtils faceUtils = new FaceUtils();
                ArrayList<RectF> rectFS = faceUtils.transForm(cameraFace, mDisplayOrientation, surfaceView, faces);
                faceView.setFaces(rectFS);
            }
        });
    }

    private void setCameraDisplayOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraFace, info);
        //获取当前窗口旋转方向
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int screenRotation = 0;//设置预览旋转角度
        switch (rotation) {
            case Surface.ROTATION_0:
                screenRotation = 0;
                break;
            case Surface.ROTATION_90:
                screenRotation = 90;
                break;
            case Surface.ROTATION_180:
                screenRotation = 180;
                break;
            case Surface.ROTATION_270:
                screenRotation = 270;
                break;
        }


        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mDisplayOrientation = (info.orientation + screenRotation) % 360;
            mDisplayOrientation = (360 - mDisplayOrientation) % 360;      // compensate the mirror
        } else {
            mDisplayOrientation = (info.orientation - screenRotation + 360) % 360;
        }
        camera.setDisplayOrientation(mDisplayOrientation);
    }

    private void openCamera(int cameraInfo) {
        if (isSuporrtCamera(cameraInfo)) {
            // 支持该摄像头,首先打开
            camera = Camera.open(cameraInfo);
            initParmerter(camera);
            camera.setPreviewCallback(this);
        }
    }

    private void initParmerter(Camera camera) {
        try {
            //获取相机的参数
            parameters = camera.getParameters();
            //设置相机预览图片的格式
            parameters.setPreviewFormat(ImageFormat.NV21);
            //设置预览大小
            Camera.Size bestSize = getBestSize(winth, height, parameters.getSupportedPreviewSizes());

            parameters.setPreviewSize(bestSize.width, bestSize.height);
            //设置保存图片大小
            Camera.Size bestSize1 = getBestSize(winth, height, parameters.getSupportedPictureSizes());
            parameters.setPictureSize(bestSize1.width, bestSize1.height);

//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            camera.setParameters(parameters);
        } catch (Exception e) {
            Toast.makeText(activity, "初始化相机失败", Toast.LENGTH_SHORT).show();
        }


    }

    private Camera.Size getBestSize(int winth, int height, List<Camera.Size> supportedPreviewSizes) {
        //相机输出尺寸默认是横向的（宽>高），手机窗口一般是竖向的（不考虑旋转横置的情况），
        // 所以比较时将输出尺寸的 宽高比 与 预览窗口的 高宽比 进行比较。
        Camera.Size bestSize = null;
        int targetRatio = height / winth;
        int minDiff = targetRatio;
        for (Camera.Size size : supportedPreviewSizes) {
            if (size.width == height && size.height == winth) {
                bestSize = size;
                break;
            }

            int suporrtedRatio = size.width / size.height;
            Log.e("tag", "系统支持的尺寸 :" + size.width + "/height===" + size.height + "/suporrtratio===" + suporrtedRatio);
            if (Math.abs(suporrtedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(suporrtedRatio - targetRatio);
                bestSize = size;
            }
        }
        return bestSize;
    }

    private boolean isSuporrtCamera(int cameraInfo) {
        boolean isSuporrent = false;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            if (i == cameraInfo) {
                isSuporrent = true;
            }
        }
        return isSuporrent;
    }

    public int getCameraNum() {
        return Camera.getNumberOfCameras();
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }
}
