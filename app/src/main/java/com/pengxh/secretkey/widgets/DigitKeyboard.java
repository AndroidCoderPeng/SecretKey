package com.pengxh.secretkey.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pengxh.secretkey.R;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/28 10:13
 */
public class DigitKeyboard extends LinearLayout implements View.OnClickListener {
    private DigitKeyboardClickListener mListener;

    public DigitKeyboard(Context context) {
        this(context, null);
    }

    public DigitKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DigitKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.digit_keyboard, this);
        setChildViewOnclick(this);
    }

    /**
     * 设置键盘子View的点击事件
     */
    private void setChildViewOnclick(ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            // 不断的递归设置点击事件
            View view = parent.getChildAt(i);
            if (view instanceof ViewGroup) {
                setChildViewOnclick((ViewGroup) view);
                continue;
            }
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        View clickView = v;
        if (clickView instanceof TextView) {
            // 如果点击的是TextView
            String number = ((TextView) clickView).getText().toString();
            if (!TextUtils.isEmpty(number)) {
                if (mListener != null) {
                    // 回调
                    mListener.onClick(number);
                }
            }
        } else if (clickView instanceof ImageView) {
            // 如果是图片那肯定点击的是删除
            if (mListener != null) {
                mListener.onDelete();
            }
        }
    }

    public boolean dispatchKeyEventInFullScreen(KeyEvent event) {
        if (event == null) {
            return false;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (isShown()) {
                setVisibility(GONE);
                return true;
            }
        }
        return false;
    }

    /**
     * 设置键盘的点击回调监听
     */
    public void setOnDigitKeyboardClickListener(DigitKeyboardClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 点击键盘的回调监听
     */
    public interface DigitKeyboardClickListener {
        void onClick(String number);

        void onDelete();
    }
}
