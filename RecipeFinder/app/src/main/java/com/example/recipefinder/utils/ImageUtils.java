package com.example.recipefinder.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class ImageUtils {

    /**
     * Creates a circular bitmap from the source bitmap
     * 
     * @param bitmap Source bitmap
     * @return Circular bitmap
     */
    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        
        // Draw the circular shape
        int radius = Math.min(width, height) / 2;
        canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        
        // Apply the bitmap using SRC_IN transfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        
        return output;
    }
    
    /**
     * Creates a circular bitmap from a drawable
     * 
     * @param drawable Source drawable
     * @return Circular bitmap
     */
    public static Bitmap getCircularBitmap(Drawable drawable) {
        if (drawable == null) return null;
        
        if (drawable instanceof BitmapDrawable) {
            return getCircularBitmap(((BitmapDrawable) drawable).getBitmap());
        }
        
        // Create bitmap from drawable
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        
        return getCircularBitmap(bitmap);
    }
    
    /**
     * Sets a circular image on an ImageView
     * 
     * @param imageView ImageView to update
     * @param drawable Drawable to make circular
     */
    public static void setCircularImage(ImageView imageView, Drawable drawable) {
        if (imageView == null || drawable == null) return;
        
        Bitmap circularBitmap = getCircularBitmap(drawable);
        imageView.setImageBitmap(circularBitmap);
    }
} 