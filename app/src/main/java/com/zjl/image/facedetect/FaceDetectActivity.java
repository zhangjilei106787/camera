package com.zjl.image.facedetect;

import android.os.Bundle;
import android.view.SurfaceView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zjl.image.carmera.R;
import com.zjl.image.surfuce.CameraHelper;
import com.zjl.image.view.FaceView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 测试相机人脸检测功能
 */
public class FaceDetectActivity extends AppCompatActivity {
    @BindView(R.id.surfucaeView)
    SurfaceView surfucaeView;
    @BindView(R.id.faceview)
    FaceView faceview;
    private CameraHelper cameraHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_face_detect);
        ButterKnife.bind(this);
        cameraHelper = new CameraHelper(this, surfucaeView);
        cameraHelper.setFaceDetect(true);
        cameraHelper.setFaceView(faceview);
        cameraHelper.initCamera();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraHelper.releaseCamera();
    }

}
