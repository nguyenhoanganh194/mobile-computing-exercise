package com.example.composetutorial.ui.features
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object BitmapUtils {

    private const val REQUEST_EXTERNAL_STORAGE = 1

    fun saveBitmapToExternalStorage(context: Context, bitmap: Bitmap, path: String): Boolean {
        // Check for permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(context as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_EXTERNAL_STORAGE)
            return false // Bitmap not saved due to lack of permission
        }

        // Permission is granted, proceed to save bitmap
        val file = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), path)

        if (!file.parentFile.exists() && !file.parentFile.mkdirs()) {
            return false // Failed to create directory
        }

        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            return true // Successfully saved bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            return false // Failed to save bitmap
        }
    }
}
