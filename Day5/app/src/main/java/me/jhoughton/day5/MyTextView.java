package me.jhoughton.day5;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by 2017jhoughto on 10/8/2015.
 */
public class MyTextView extends TextView {

    private int mValue;

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(attrs);
        setText(mValue + "");
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementAndUpdate();
            }
        });
    }

    private void setupAttributes(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.MyTextView,0,0);
        try {
            mValue = a.getInt(R.styleable.MyTextView_initialValue, 0);
        } finally {
            a.recycle();
        }
    }

    public void incrementAndUpdate() {
        setText(mValue++ + "");
    }
}
