package pk_tnuv_mis.zaiba;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DonutView extends View {
    private final Paint paint;
    private final RectF rectF;
    private float percentage = 75f; // Default to 75%

    private int strokeWidth = 60;

    public DonutView(Context context) {
        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();
    }

    public DonutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();
    }

    public DonutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();
    }

    public void setPercentage(float percent) {
        this.percentage = percent;
        invalidate(); // Redraw
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Default size if wrap_content is used
        int defaultSize = 200; // px
        int width = resolveSize(defaultSize, widthMeasureSpec);
        int height = resolveSize(defaultSize, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        int padding = strokeWidth / 2;

        rectF.set(padding, padding, size - padding, size - padding);

        // Background ring
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.LTGRAY);
        canvas.drawArc(rectF, 0, 360, false, paint);

        // Foreground arc
        paint.setColor(Color.GREEN);
        canvas.drawArc(rectF, -90, 360 * (percentage / 100f), false, paint);
    }
}
