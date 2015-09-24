package com.limelitelabs.simpletexttospeech.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.limelitelabs.simpletexttospeech.R;

public class TypefaceButton extends Button {

    public TypefaceButton(Context context) {
        super(context);
        applyTypeface(context, null);
    }

    public TypefaceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyTypeface(context, attrs);
    }

    public TypefaceButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyTypeface(context, attrs);
    }

    public void applyTypeface(Context context, AttributeSet attrs) {
        if(attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TypefaceButton);
            String fontName = ta.getString(R.styleable.TypefaceButton_fontName);
            if(fontName != null) {
                setTypeface(FontCache.getFont(context, fontName));
            }
            ta.recycle();
        }
    }
}
