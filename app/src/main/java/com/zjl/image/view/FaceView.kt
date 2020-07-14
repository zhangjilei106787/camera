package com.zjl.image.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class FaceView : View {
    lateinit var mPaint: Paint
    private var mCorlor = "#42ed45"
    var mFaces: ArrayList<RectF>? = null   //人脸信息

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.color = Color.parseColor(mCorlor)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics)
        mPaint.isAntiAlias = true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mFaces?.let {
            for (face in it) {           //因为会同时存在多张人脸，所以用循环
                canvas.drawRect(face, mPaint)   //绘制人脸所在位置的矩形
            }
        }
    }

    fun setFaces(faces: ArrayList<RectF>) {  //设置人脸信息，然后刷新FaceView
        this.mFaces = faces
        invalidate()
    }
}