package com.thisara.mypocket.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

object AvatarCropper {
    private const val AVATAR_MAX_SIDE_PX = 256
    private const val AVATAR_JPEG_QUALITY = 82
    private const val AVATAR_MAX_BYTES = 160_000

    fun loadBitmap(context: Context, imageUri: Uri): Bitmap {
        return context.contentResolver.openInputStream(imageUri).use { input ->
            BitmapFactory.decodeStream(input)
        } ?: throw IllegalArgumentException("Could not read the selected photo.")
    }

    fun cropToAvatarBitmap(source: Bitmap, zoom: Float, offsetX: Float, offsetY: Float): Bitmap {
        val safeZoom = zoom.coerceIn(1f, 3f)
        val cropSide = (minOf(source.width, source.height) / safeZoom)
            .roundToInt()
            .coerceAtLeast(1)
        val maxLeft = (source.width - cropSide).coerceAtLeast(0)
        val maxTop = (source.height - cropSide).coerceAtLeast(0)
        val left = ((maxLeft / 2f) + (offsetX.coerceIn(-1f, 1f) * maxLeft / 2f))
            .roundToInt()
            .coerceIn(0, maxLeft)
        val top = ((maxTop / 2f) + (offsetY.coerceIn(-1f, 1f) * maxTop / 2f))
            .roundToInt()
            .coerceIn(0, maxTop)
        val cropped = Bitmap.createBitmap(source, left, top, cropSide, cropSide)
        return Bitmap.createScaledBitmap(cropped, AVATAR_MAX_SIDE_PX, AVATAR_MAX_SIDE_PX, true)
    }

    fun cropUriToDataUri(
        context: Context,
        imageUri: Uri,
        zoom: Float,
        offsetX: Float,
        offsetY: Float,
    ): String {
        val source = loadBitmap(context, imageUri)
        val avatar = cropToAvatarBitmap(source, zoom, offsetX, offsetY)
        return avatar.toAvatarDataUri()
    }

    private fun Bitmap.toAvatarDataUri(): String {
        val bytes = ByteArrayOutputStream().use { output ->
            compress(Bitmap.CompressFormat.JPEG, AVATAR_JPEG_QUALITY, output)
            output.toByteArray()
        }
        if (bytes.size > AVATAR_MAX_BYTES) {
            throw IllegalArgumentException("Choose a smaller profile photo.")
        }
        return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
