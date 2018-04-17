package com.espacepiins.messenger.ui.binding;

import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.widget.TextView;

import java.util.Date;

public class TextViewBindingAdapter {
    @BindingAdapter({"bind:timestamp"})
    public static void timestamp(TextView view, Long timestamp) {
        CharSequence timestampAsText = "";
        if (timestamp != null)
            timestampAsText = DateUtils.getRelativeTimeSpanString(timestamp, new Date().getTime(), DateUtils.SECOND_IN_MILLIS);

        view.setText(timestampAsText);
    }

    @BindingAdapter({"bind:textStyle"})
    public static void textStyle(TextView view, String style) {
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
