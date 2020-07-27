package com.pengxh.secretkey.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.secretkey.R;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/27 15:29
 */
public class PasswordEditText extends AppCompatEditText {

    private static final String TAG = "PasswordEditText";

    private int passwordLength;//矩形数目
    private int strokeWidth;//边框宽度
    private int strokeColor; //边框颜色
    private int viewHeight;//密码框的高度
    private Paint borderPaint;//边框画笔
    private int textColor;//文字的颜色
    private int textSize;//文字的大小
    private int startX;//开始坐标
    private Paint textPaint;//文字画笔
    private int position = 0;//当前输入的位置
    private boolean isDrawDot;//是绘制圆点还是文字；true绘制圆点，false绘制文字
    private int dotRadius;//如果不显示文字则绘制圆点，此为圆点半径
    private int dotColor;//如果不显示文字则绘制圆，此为圆点填充色
    private Paint dotPaint;//圆形画笔
    private Paint focusBorderPaint;//焦点边框画笔
    private int focusStrokeColor;//焦点边框颜色
    private int columnWidth = 0;//边框宽度

    public PasswordEditText(Context context) {
        this(context, null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText);
        passwordLength = typedArray.getInt(R.styleable.PasswordEditText_passwordLength, 4);
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.PasswordEditText_strokeWidth, DensityUtil.dp2px(context, 1));
        strokeColor = typedArray.getColor(R.styleable.PasswordEditText_strokeColor, Color.BLACK);
        viewHeight = typedArray.getDimensionPixelSize(R.styleable.PasswordEditText_viewHeight, DensityUtil.dp2px(context, 40));
        textColor = typedArray.getColor(R.styleable.PasswordEditText_textColor, Color.parseColor("#666666"));
        textSize = typedArray.getDimensionPixelSize(R.styleable.PasswordEditText_textSize, 50);
        isDrawDot = typedArray.getBoolean(R.styleable.PasswordEditText_isDrawDot, true);
        dotRadius = typedArray.getDimensionPixelSize(R.styleable.PasswordEditText_dotRadius, DensityUtil.dp2px(context, 10));
        dotColor = typedArray.getColor(R.styleable.PasswordEditText_dotColor, Color.BLACK);
        focusStrokeColor = typedArray.getColor(R.styleable.PasswordEditText_focusStrokeColor, Color.GREEN);
        columnWidth = typedArray.getDimensionPixelSize(R.styleable.PasswordEditText_strokeWidth, columnWidth);
        typedArray.recycle();

        setBackgroundColor(Color.TRANSPARENT);
        setCursorVisible(false);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(passwordLength)});
        init();
    }

    private void init() {
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStrokeWidth(strokeWidth);
        borderPaint.setColor(strokeColor);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);

        focusBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        focusBorderPaint.setStrokeWidth(strokeWidth);
        focusBorderPaint.setColor(focusStrokeColor);
        focusBorderPaint.setAntiAlias(true);
        focusBorderPaint.setStyle(Paint.Style.STROKE);

        if (!isDrawDot) {
            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(textSize);
            textPaint.setColor(textColor);
        } else {
            dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            dotPaint.setAntiAlias(true);
            dotPaint.setStrokeWidth(2);
            dotPaint.setStyle(Paint.Style.FILL);
            dotPaint.setColor(dotColor);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (passwordLength * viewHeight > w) {//todo ???
            throw new IllegalArgumentException("View must be less than the width of the screen!");
        }
        startX = (w - (passwordLength * viewHeight) - (passwordLength - 1) * columnWidth) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawRectBorder(canvas);
        drawRectFocused(canvas, position);
        if (!isDrawDot) {
            drawText(canvas);
        } else {
            drawDot(canvas);
        }
    }

    /**
     * 绘制圆点
     *
     * @param canvas
     */
    private void drawDot(Canvas canvas) {
        char[] chars = getText().toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            drawRectFocused(canvas, i);
            canvas.drawCircle(startX + i * viewHeight + i * columnWidth + viewHeight / 2, viewHeight / 2, dotRadius, dotPaint);
        }
    }

    /**
     * 绘制默认状态
     *
     * @param canvas
     */
    private void drawRectBorder(Canvas canvas) {
        for (int i = 0; i < passwordLength; i++) {
            canvas.drawRect(startX + i * viewHeight + i * columnWidth,
                    1, startX + i * viewHeight + i * columnWidth + viewHeight,
                    viewHeight, borderPaint);
        }
    }

    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        char[] chars = getText().toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            drawRectFocused(canvas, i);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            int baseLineY = (int) (viewHeight / 2 - fontMetrics.top / 2 - fontMetrics.bottom / 2);
            canvas.drawText(String.valueOf(chars[i]), startX + i * viewHeight + i * columnWidth + viewHeight / 2, baseLineY, textPaint);
        }
    }

    /**
     * 绘制输入状态
     *
     * @param canvas
     * @param position
     */
    private void drawRectFocused(Canvas canvas, int position) {
        if (position > passwordLength - 1) {
            return;
        }
        canvas.drawRect(startX + position * viewHeight + position * columnWidth,
                1, startX + position * viewHeight + position * columnWidth + viewHeight,
                viewHeight, focusBorderPaint);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        position = start + lengthAfter;
        if (!TextUtils.isEmpty(text) && text.toString().length() == passwordLength)
            if (onFinishListener != null) {
                onFinishListener.onFinish(text.toString());
            }
        invalidate();
    }

    /**
     * 供外部调用
     */
    private OnFinishListener onFinishListener;

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public interface OnFinishListener {
        void onFinish(String password);
    }
}
