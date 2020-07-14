package com.zjl.image.carmera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zjl.image.facedetect.FaceDetectActivity;
import com.zjl.image.surfuce.CarmeraActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.tv1, R.id.tv2})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv1:
                intent.setClass(this, CarmeraActivity.class);
                break;
            case R.id.tv2:
                intent.setClass(this, FaceDetectActivity.class);
                break;
        }
        startActivity(intent);
    }
}
