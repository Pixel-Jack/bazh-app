package fr.clement.rennsurrection.bluesound.Objects;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.widget.TextView;

import fr.clement.rennsurrection.bluesound.R;

/**
 * Created by Clément on 12/04/2017.
 *
 * http://www.tutos-android.com/utiliser-des-polices-personnalisees
 */

public class CustomTextView extends TextView {

    private static LruCache<String, Typeface> TYPEFACE_CACHE = new LruCache<String, Typeface>(4);

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        }

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String fontName = styledAttrs.getString(R.styleable.CustomTextView_font);
        styledAttrs.recycle();
        setTypeface(fontName);
    }

    public void setTypeface(String fontName) {
        if(fontName != null){
            try{
                Typeface typeface = TYPEFACE_CACHE.get(fontName);

                if(typeface == null){
                    typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);

                    TYPEFACE_CACHE.put(fontName, typeface);
                }

                setTypeface(typeface);
            } catch (Exception e) {
                Log.e("FONT", fontName + "not found", e);
            }
        }
    }
}