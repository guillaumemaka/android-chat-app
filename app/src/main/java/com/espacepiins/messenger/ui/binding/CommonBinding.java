package com.espacepiins.messenger.ui.binding;

import android.databinding.BindingAdapter;
import android.widget.LinearLayout;


public class CommonBinding {
    @BindingAdapter("android:layout_gravity")
    public static void setLayoutGravity(LinearLayout view, int gravity) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.gravity = gravity;
        view.setLayoutParams(params);
    }
}
