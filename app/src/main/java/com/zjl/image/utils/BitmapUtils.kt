package com.zjl.image.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.ByteArrayOutputStream

object BitmapUtils {
    fun mirror(rawBitmap: Bitmap): Bitmap {
        var matrix = Matrix()
        matrix.postScale(-1f, 1f)
        return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
    }

    //旋转
    fun rotate(rawBitmap: Bitmap, degree: Float): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
    }

    fun toByteArray(rawBitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        rawBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
       return baos.toByteArray()
    }
}