package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

public class WaveSeekBar extends AppCompatSeekBar {

    private final Paint wavePaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint progressPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint playHeadPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private float waveOffset = 0;

    public WaveSeekBar(Context context) {
        super(context);
        init();
    }

    public WaveSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveSeekBar(
            Context context,
            AttributeSet attrs,
            int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        // Remove normal SeekBar line
        setBackground(null);
        setProgressDrawable(null);

        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setStrokeWidth(3f);
        wavePaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(4f);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        playHeadPaint.setStyle(Paint.Style.FILL);

        setMax(100);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();

        float centerY = height / 2f;

        // Same wave spacing
        float spacing = 8f;

        // Large wave area
        float totalWaveWidth = width * 3f;

        float progress = 0;

        if (getMax() > 0) {
            progress =
                    getProgress() / (float) getMax();
        }

        // =========================================
        // MOVE WAVE
        // =========================================

        waveOffset =
                progress *
                        (totalWaveWidth - width);

        canvas.save();

        canvas.clipRect(
                0,
                0,
                width,
                height
        );

        canvas.translate(
                -waveOffset,
                0
        );

        // =========================================
        // UNPLAYED WAVE
        // =========================================

        LinearGradient backgroundGradient =
                new LinearGradient(
                        0,
                        0,
                        totalWaveWidth,
                        0,

                        new int[]{
                                0xFFBDBDBD,
                                0xFFD6D6D6,
                                0xFFBDBDBD
                        },

                        null,

                        Shader.TileMode.CLAMP
                );

        wavePaint.setShader(
                backgroundGradient
        );

        drawWave(
                canvas,
                totalWaveWidth,
                centerY,
                spacing,
                wavePaint
        );

        // =========================================
        // PLAYED WAVE
        // =========================================

        LinearGradient progressGradient =
                new LinearGradient(
                        0,
                        0,
                        totalWaveWidth,
                        0,

                        new int[]{
                                0xFF482050,
                                0xFF7B2CBF,
                                0xFFE06C9F,
                                0xFF482050
                        },

                        null,

                        Shader.TileMode.CLAMP
                );

        progressPaint.setShader(
                progressGradient
        );

        canvas.save();

        /*
         * Only the played portion gets
         * the gradient color.
         */
        canvas.clipRect(
                waveOffset,
                0,
                waveOffset + width,
                height
        );

        drawWave(
                canvas,
                totalWaveWidth,
                centerY,
                spacing,
                progressPaint
        );

        canvas.restore();

        canvas.restore();

        // =========================================
        // FIXED PLAY HEAD
        // =========================================

        playHeadPaint.setShader(null);

        playHeadPaint.setColor(
                0xFF482050
        );

        canvas.drawCircle(
                width / 2f,
                centerY,
                7f,
                playHeadPaint
        );
    }

    // =========================================
    // SAME HEARTBEAT WAVE
    // =========================================

    private void drawWave(
            Canvas canvas,
            float width,
            float centerY,
            float spacing,
            Paint paint) {

        for (
                float x = 0;
                x < width;
                x += spacing
        ) {

            // SAME HEARTBEAT STYLE
            float waveHeight =
                    20f +
                            (float)
                                    (
                                            Math.abs(
                                                    Math.sin(
                                                            x * 0.08
                                                    )
                                            ) * 40f
                                    );

            waveHeight +=
                    (float)
                            (
                                    Math.abs(
                                            Math.sin(
                                                    x * 0.021
                                            )
                                    ) * 25f
                            );

            canvas.drawLine(
                    x,
                    centerY - waveHeight,
                    x,
                    centerY + waveHeight,
                    paint
            );
        }
    }

    // =========================================
    // HEIGHT
    // =========================================

    @Override
    protected synchronized void onMeasure(
            int widthMeasureSpec,
            int heightMeasureSpec) {

        super.onMeasure(
                widthMeasureSpec,
                heightMeasureSpec
        );

        setMeasuredDimension(
                getMeasuredWidth(),
                dpToPx(90)
        );
    }

    private int dpToPx(int dp) {

        return (int)
                (
                        dp *
                                getResources()
                                        .getDisplayMetrics()
                                        .density
                                + 0.5f
                );
    }
}