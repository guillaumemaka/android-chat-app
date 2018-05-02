package com.espacepiins.messenger.ui.binding;

import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.espacepiins.messenger.application.Constants;

import java.util.Date;

public class TextViewBindingAdapter {
    @BindingAdapter("app:timestamp")
    public static void setTimestamp(TextView view, Long timestamp) {
        CharSequence timestampAsText = "";
        if (timestamp != null)
            timestampAsText = DateUtils.getRelativeTimeSpanString(timestamp, new Date().getTime(), Constants.DEFAULT_DATE_MIN_RESOLUTION);

        view.setText(timestampAsText);
    }

    @BindingAdapter("app:textStyle")
    public static void setTextStyle(TextView view, String style) {
        switch (style) {
            case "bold":
                view.setTypeface(null, Typeface.BOLD);
                break;
            case "bold|italic":
                view.setTypeface(null, Typeface.BOLD_ITALIC);
                break;
            case "normal":
            default:
                view.setTypeface(null, Typeface.NORMAL);
                break;
        }
    }
}
