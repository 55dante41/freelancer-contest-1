package com.limelitelabs.simpletexttospeech.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.limelitelabs.simpletexttospeech.R;


public class TypefaceTextView extends TextView {

    public TypefaceTextView(Context context) {
        super(context);
        applyTypeface(context, null);
    }

    public TypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyTypeface(context, attrs);
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyTypeface(context, attrs);
    }

    public void applyTypeface(Context context, AttributeSet attrs) {
        if(attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TypefaceTextView);
            String fontName = ta.getString(R.styleable.TypefaceTextView_fontName);
            if(fontName != null) {
                setTypeface(FontCache.getFont(context, fontName));
            }
            ta.recycle();
        }
    }
}

