package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class WaveView extends View {

    private Paint paint;
    private Path wavePath;

    private float phase = 0f;

    private final Runnable waveRunnable = new Runnable() {
        @Override
        public void run() {

            phase += 0.15f;

            invalidate();

            postDelayed(this, 30);
        }
    };


    // ==========================================
    // Constructors
    // ==========================================

    public WaveView(Context context) {
        super(context);
        init();
    }


    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public WaveView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }


    // ==========================================
    // Initialize
    // ==========================================

    private void init() {

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(3f);

        paint.setColor(0xFFB66CFF);

        paint.setStrokeCap(Paint.Cap.ROUND);

        wavePath = new Path();

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        startWave();
    }


    // ==========================================
    // Start Animation
    // ==========================================

    private void startWave() {

        removeCallbacks(waveRunnable);

        post(waveRunnable);
    }


    // ==========================================
    // Draw Wave
    // ==========================================

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float width = getWidth();

        float height = getHeight();

        float centerY = height / 2f;


        wavePath.reset();


        boolean firstPoint = true;


        for (float x = 0; x <= width; x += 4) {

            // Create moving waveform
            float y =
                    centerY
                            + (float)
                            Math.sin(
                                    (x * 0.08f) + phase
                            ) * 18f;


            // Add a second smaller wave
            y +=
                    (float)
                            Math.sin(
                                    (x * 0.035f) + phase * 1.5f
                            ) * 8f;


            if (firstPoint) {

                wavePath.moveTo(x, y);

                firstPoint = false;

            } else {

                wavePath.lineTo(x, y);
            }
        }


        canvas.drawPath(
                wavePath,
                paint
        );
    }


    // ==========================================
    // Stop Animation
    // ==========================================

    public void stopWave() {

        removeCallbacks(waveRunnable);
    }


    // ==========================================
    // Resume Animation
    // ==========================================

    public void resumeWave() {

        startWave();
    }


    // ==========================================
    // Cleanup
    // ==========================================

    @Override
    protected void onDetachedFromWindow() {

        removeCallbacks(waveRunnable);

        super.onDetachedFromWindow();
    }
}