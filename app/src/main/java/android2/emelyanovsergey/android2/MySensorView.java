package android2.emelyanovsergey.android2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MySensorView extends View {

    private static final String TAG = "MySensorView";
    private Paint paint;

    private String strTemperature;
    private String strHumidity;

    public MySensorView(Context context) {
        super(context);
    }

    public MySensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(null);
    }

    public MySensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public MySensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.d(TAG, "widthSize:" + widthSize);
        Log.d(TAG, "heightSize:" + heightSize);

        setMeasuredDimension(widthSize, heightSize);

        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private void initView(@Nullable AttributeSet attrs) {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(60.0f);

        paint.setShadowLayer(5.0f, 6.0f, 6.0f, Color.GRAY);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MySensorView);
            strTemperature = (String) typedArray.getText(R.styleable.MySensorView_humidity);
            strHumidity = (String) typedArray.getText(R.styleable.MySensorView_temperature);

            typedArray.recycle();

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawText("Температура:" + strTemperature, 10, 10 + paint.getTextSize(), paint);
        canvas.drawText("Влажность:" + strHumidity, 10, 2 * (10 + paint.getTextSize()), paint);

    }

    public void setTemperature(String temperature) {
        strTemperature = temperature;
    }
    public void setHumidity(String humidity) {
        strHumidity = humidity;
    }
}
