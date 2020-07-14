package com.zjl.image.surfuce;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zjl.image.carmera.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarmeraActivity extends AppCompatActivity {
    @BindView(R.id.surfucaeView)
    SurfaceView surfucaeView;
    @BindView(R.id.iv_take)
    ImageView ivTake;
    @BindView(R.id.iv_switch)
    ImageView ivSwitch;
    private CameraHelper cameraHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_carmeraactivity);
        ButterKnife.bind(this);
        cameraHelper = new CameraHelper(this, surfucaeView);
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

    @OnClick({R.id.iv_take, R.id.iv_switch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_take:
                cameraHelper.takePhoto();
                break;
            case R.id.iv_switch:
                cameraHelper.swithCamera();
                break;
        }
    }

}
