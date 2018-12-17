package com.example.junmung.hangangparksmap.ARGuide;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.Matrix;
import android.view.View;

import com.example.junmung.hangangparksmap.R;


public class AROverlayView extends View {
    Context context;
    private float[] rotatedProjectionMatrix = new float[16];
    private Point currentPoint;
    private Point destPoint;
    private Bitmap bitmap;
    private Paint paint;

    public AROverlayView(Context context, Point firstPoint) {
        super(context);
        this.context = context;
        destPoint = firstPoint;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ar_location_pin);
        bitmap = Bitmap.createScaledBitmap(bitmap, 180, 206, true);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentPoint(Point updatedPoint){
        currentPoint = updatedPoint;
        invalidate();
    }

    public void updateDestPoint(Point destPoint){
        this.destPoint = destPoint;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentPoint == null || destPoint == null)
            return;

        destPoint.getLocation().setAltitude(0);
        currentPoint.getLocation().setAltitude(0);


        float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentPoint.getLocation());
        float[] pointInECEF = LocationHelper.WSG84toECEF(destPoint.getLocation());
        float[] pointInENU = LocationHelper.ECEFtoENU(currentPoint.getLocation(), currentLocationInECEF, pointInECEF);

        float[] cameraCoordinateVector = new float[4];
        Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

        // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
        // if z > 0, the point will display on the opposite
        if (cameraCoordinateVector[2] < 0) {
            float x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas.getWidth();
            float y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * canvas.getHeight();
            int distance = currentPoint.distanceTo(destPoint);
            String description = destPoint.getName()+" ( "+distance+"m )";


            Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
            paint.setStyle(Paint.Style.FILL);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            paint.setTextSize(60);
            paint.getFontMetrics(fontMetrics);

            paint.setColor(context.getResources().getColor(R.color.colorHalfInvisibleBlack));
            canvas.drawRect(x - (30 * description.length() / 2) - 20, y+20,
                    x + (30 * description.length() / 2) + 50, y+100, paint);


            paint.setColor(Color.WHITE);

            canvas.drawText(description, x - (30 * description.length() / 2), y + 80, paint);
            canvas.drawBitmap(bitmap, x - bitmap.getWidth() / 2, y - bitmap.getHeight(), paint);
        }
    }
}
