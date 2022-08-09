package com.example.smproject.util

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream


class BitmapConverter{
    fun Base64ToBitmap(base64: String?): Bitmap {
        var base64Replaced = base64?.replace("data:image/png;base64,","")
        Log.d("base64Replaced => ","$base64Replaced")
        val decodedString: ByteArray = Base64.decode(base64Replaced, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
//    fun uriToBitmap(uri:Uri?,context:Context):Bitmap{
//        return MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
//    }
    fun bitmapToBase64(bitmap: Bitmap?): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return(Base64.encodeToString(byteArray, Base64.DEFAULT))
    }
//    fun imgToBase64(iv: Drawable): String? {
//    var bitmapDrawable = iv as BitmapDrawable
//    var bitmap =  bitmapDrawable.bitmap
//    val bos = ByteArrayOutputStream()
//    bitmap.compress(CompressFormat.PNG, 100, bos)
//    val bb = bos.toByteArray()
//    return(Base64.encodeToString(bb, Base64.DEFAULT))
//}
}