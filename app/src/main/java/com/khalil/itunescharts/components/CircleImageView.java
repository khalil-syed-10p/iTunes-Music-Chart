package com.khalil.itunescharts.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.khalil.itunescharts.R;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

/**
 * Created on 8/25/16.
 */
public class CircleImageView extends ImageView {

    private int strokeWidth;
    private int strokeColor;

    public CircleImageView( Context context ) {
        super( context );
    }

    public CircleImageView( Context context, AttributeSet attrs ) {
        super( context, attrs );
        setupAttributes( attrs );
    }

    public CircleImageView( Context context, AttributeSet attrs, int defStyle ) {
        super( context, attrs, defStyle );
        setupAttributes( attrs );
    }

    private void setupAttributes( AttributeSet attrs ) {
        if ( isInEditMode() )
            return;

        TypedArray a = this.getContext().getTheme().obtainStyledAttributes( attrs, R.styleable.CircleImageView, 0, 0 );

        try {
            this.strokeWidth = a.getDimensionPixelSize( R.styleable.CircleImageView_strokeWidth, 0 );
            this.strokeColor = a.getColor( R.styleable.CircleImageView_strokeColor, Color.TRANSPARENT );
        } finally {
            a.recycle();
        }
    }

    @Override
    protected RequestCreator applyTransform(RequestCreator requestCreator ) {
        if ( requestCreator == null ) {
            return null;
        }
        return requestCreator.transform( getCircleTransform() );
    }

    public void setStrokeWidth( int strokeWidth ) {
        this.strokeWidth = strokeWidth;
        setImageDrawable( getDrawable() );
    }

    public void setStrokeColor( int strokeColor ) {
        this.strokeColor = strokeColor;
        setImageDrawable( getDrawable() );
    }

    public void setStroke( int strokeWidth, int strokeColor ) {
        this.strokeWidth = strokeWidth;
        this.strokeColor = strokeColor;
        setImageDrawable( getDrawable() );
    }

    @Override
    public void setImageBitmap( Bitmap bm ) {
        if ( isInEditMode() ) {
            super.setImageBitmap( bm );
            return;
        }

        super.setImageBitmap(getCircleTransform()
                .transform( bm ));
    }

    private Transformation getCircleTransform() {

        return new CircleTransform();
    }


    private static class CircleTransform implements Transformation {


        private static final String KEY = "circleImageTransformation";

        private int strokeWidth;
        private int strokeColor;

        CircleTransform() {
            this.strokeWidth = 0;
            this.strokeColor = Color.TRANSPARENT;
        }

        CircleTransform(int strokeWidth, int strokeColor) {
            this.strokeWidth = strokeWidth;
            this.strokeColor = strokeColor;
        }

        @Override
        public Bitmap transform(Bitmap source) {

            int minEdge = Math.min(source.getWidth(), source.getHeight());
            int dx = (source.getWidth() - minEdge) / 2;
            int dy = (source.getHeight() - minEdge) / 2;

            // Init shader
            Shader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Matrix matrix = new Matrix();
            matrix.setTranslate(-dx, -dy);   // Move the target area to center of the source bitmap
            shader.setLocalMatrix(matrix);

            // Init paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setShader(shader);
            Paint paintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintBorder.setColor(strokeColor);

            // Create and draw circle bitmap
            Bitmap output = Bitmap.createBitmap(minEdge, minEdge, source.getConfig());
            Canvas canvas = new Canvas(output);
            canvas.drawOval(new RectF(0, 0, minEdge, minEdge), paintBorder);
            canvas.drawOval(new RectF(strokeWidth, strokeWidth, minEdge - strokeWidth, minEdge - strokeWidth), paint);


            // Recycle the source bitmap, because we already generate a new one
            source.recycle();

            return output;
        }

        @Override
        public String key() {
            return KEY;
        }
    }

}
