package com.exercise.mapdatabasetest.Controller;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import androidx.core.content.res.ResourcesCompat;

import com.exercise.mapdatabasetest.R;

public class MarkerIconHandler {

    int height = 200;
    int width = 200;

    /*public Bitmap returnIconDrawable(int type) {

        BitmapDrawable bitmapDrawable;

        switch (type) {
            case 1:
                bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_charity_3dmarker);
                break;
            case 2:
                bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_course_3dmarker);
                break;
            case 3:
                bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_discount_3dmarker);
                break;
            case 4:
                bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_course_3dmarker);
                break;
            case 5:
                bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_sport_3dmarker);
                break;
            default:
                bitmapDrawable = (BitmapDrawable) ResourcesCompat.getDrawable(R.drawable.icon_sport_3dmarker);
        }

        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap bitmapMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);

        return bitmapMarker;
    }*/
}
