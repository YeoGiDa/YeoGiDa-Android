package com.starters.yeogida.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import com.starters.yeogida.GlideApp
import java.io.File
import java.io.FileInputStream

object ImageUtil {
    /**
     * 이미지 회전 데이터 반환
     */
    private fun getExifDegrees(ei: ExifInterface): Float {
        val orientation: Int =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }

    fun getResizePicture(context: Context, imgUri: Uri): File? {
        val contentResolver = context.contentResolver
        var imageFile: File? = null

        val fileName = ""

        //1)회전할 각도 구하기
        var degrees = 0f
        contentResolver.openInputStream(imgUri)?.use { inputStream ->
            degrees = getExifDegrees(ExifInterface(inputStream))
        }

        //2)Resizing 할 BitmapOption 만들기
        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            contentResolver.openInputStream(imgUri)?.use { inputStream ->
                //get img dimension
                BitmapFactory.decodeStream(inputStream, null, this)
            }

            // Determine how much to scale down the image
            val targetW: Int = 1024
            val targetH: Int = 1024
            val scaleFactor: Int = Math.min(outWidth / targetW, outHeight / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }

        //3) Bitmap 생성 및 셋팅 (resized + rotated)
        contentResolver.openInputStream(imgUri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, bmOptions)?.also { bitmap ->
                val matrix = Matrix()
                matrix.preRotate(degrees, 0f, 0f)

                val resizedBitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)

                val compressedUri = UriUtil.bitmapToCompressedUri(context, resizedBitmap, fileName)

                compressedUri?.let { compressedUri ->
                    UriUtil.toFile(context, compressedUri)?.let {
                        if (it.exists()) {
                            imageFile = it
                            contentResolver.delete(
                                compressedUri,
                                null,
                                null
                            )   // Uri에 해당되는 값 갤러리에서 제거.
                        }
                    }

                    return imageFile
                }
            }
        }
        return imageFile
    }

    fun filePathToBitmap(path: String?): Bitmap? {
        return try {
            val f = File(path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            BitmapFactory.decodeStream(FileInputStream(f), null, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun preload(context: Context, imageFile: File) {
        GlideApp.with(context).load(imageFile)
            .preload(150, 150)
    }

    fun preload(context: Context, url: String) {
        GlideApp.with(context).load(url)
            .preload(150, 150)
    }

    fun preload(context: Context, uri: Uri) {
        GlideApp.with(context).load(uri)
            .preload(150, 150)
    }
}