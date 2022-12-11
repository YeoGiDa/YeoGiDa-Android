package com.starters.yeogida.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import java.io.File
import java.io.OutputStream

object UriUtil {
    fun toFile(context: Context, uri: Uri): File {
        val fileName = getFileName(context, uri)

        val file = FileUtil.createTempFile(context, fileName)
        FileUtil.copyToFile(context, uri, file)

        return File(file.absolutePath)
    }

    // get file name & extension
    fun getFileName(context: Context, uri: Uri): String {
        val name = uri.toString().split("/").last()
        val ext = context.contentResolver.getType(uri)!!.split("/").last()

        return "$name.$ext"
    }

    fun toBitmap(context: Context, imageUri: Uri): Bitmap? {
        var bitmap: Bitmap? = null

        if (Build.VERSION.SDK_INT >= 29) {
            bitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(context.contentResolver, imageUri)
            )
        } else {
            bitmap = getBitmap(context.contentResolver, imageUri)
        }

        return bitmap
    }

    fun bitmapToUri(context: Context, bitmap: Bitmap?, fileName: String): Uri? {
        val filename = "${fileName}.jpg"

        //Output stream
        var fos: OutputStream? = null

        var imageUri: Uri? = null

        //getting the contentResolver
        context.contentResolver?.also { resolver ->

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
            }

            imageUri =
                resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )    // Uri 값을 가져오기 위해서 이 부분에서 bitmap에 해당되는 사진이 갤러리에 저장됨.

            //Opening an outputstream with the Uri that we got
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        return imageUri
    }
}