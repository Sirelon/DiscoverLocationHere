package com.sirelon.discover.location.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.collection.LruCache
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created on 2019-09-12 23:57 for DiscoverLocationHere.
 */
object ImageLoader {

    private val cache = LruCache<String, Bitmap>(20)

    /**
     * suspension function for loading bitmap from network
     */
    suspend fun loadAsBitmap(url: String) = suspendCoroutine<Bitmap> { con ->
        val fromCache = cache.get(url)
        if (fromCache != null) {
            con.resume(fromCache)
            return@suspendCoroutine
        }

        Picasso.get().load(url).into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                e ?: return
                con.resumeWithException(e)
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmap ?: return
                cache.put(url, bitmap)
                con.resume(bitmap)
            }
        })
    }

    fun showImage(imageView: ImageView, url: String) {
        Picasso.get().load(url).into(imageView)
    }

}