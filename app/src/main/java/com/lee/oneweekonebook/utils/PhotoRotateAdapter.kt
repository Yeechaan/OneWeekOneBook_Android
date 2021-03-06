package com.lee.oneweekonebook.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

object PhotoRotateAdapter {

    fun getRotatedImageBitmap(photoFile: File, context: Context?): Bitmap {
        val option = BitmapFactory.Options()
        option.inSampleSize = 4

        return modifyOrientation(
            BitmapFactory.decodeFile(photoFile.absolutePath, option),
            photoFile.absolutePath
        )
    }

    fun getRotatedImageFile(photoFile: File, context: Context?): File? {
        val option = BitmapFactory.Options()
        option.inSampleSize = 4

        val convertedBitmap: Bitmap =
            modifyOrientation(
                BitmapFactory.decodeFile(photoFile.absolutePath, option),
                photoFile.absolutePath
            )

        return saveImage(convertedBitmap, context)

    }

    private fun saveImage(image: Bitmap, context: Context?): File? {

        val filename = getImageFilePath(context)
        val imageFile = File(filename)

        val os = BufferedOutputStream(FileOutputStream(imageFile))
        image.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.close()
        return imageFile
    }

    private fun getImageFilePath(context: Context?): String {
        val filename = "${System.currentTimeMillis()}.jpg"
        val dir = context?.getExternalFilesDir(null)

        return if (dir == null) {
            filename
        } else {
            "${dir.absolutePath}/$filename"
        }
    }


    private fun modifyOrientation(bitmap: Bitmap, imageAbsolutePath: String): Bitmap {
        val ei: ExifInterface = ExifInterface(imageAbsolutePath);
        when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                return rotate(bitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                return rotate(bitmap, 180f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                return rotate(bitmap, 270f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                return rotate(bitmap, 270f)
            }
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                return flip(bitmap, true, vertical = false)
            }
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                return flip(bitmap, false, vertical = true)
            }
            else -> {
                return bitmap
            }
        }
    }

    private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale(if (horizontal) (-1f) else 1f, if (vertical) (-1f) else 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true);
    }

}