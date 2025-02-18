package com.xfei33.quicktodo.data.local.converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromByteArray(value: ByteArray?): ImageBitmap? {
        return value?.let {
            BitmapFactory.decodeByteArray(value, 0, value.size).asImageBitmap()
        }
    }

    @TypeConverter
    fun toByteArray(bitmap: ImageBitmap?): ByteArray? {
        return bitmap?.let {
            val bmp = it.asAndroidBitmap()
            ByteArrayOutputStream().use { stream ->
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            }
        }
    }
} 