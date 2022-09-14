package com.picture.camera;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.picture.R;

/**
 * The dialog base class pops up at the bottom
 */
public abstract class BottomDialogBase extends Dialog {

    public BottomDialogBase(Context context) {
        super(context);
        init();
    }

    protected void init() {
        Window win = this.getWindow();
        win.requestFeature(Window.FEATURE_NO_TITLE);
        onCreate();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.windowAnimations = R.style.style_bottom_in_and_out;
        lp.gravity = Gravity.BOTTOM;
        win.setAttributes(lp);
        win.setBackgroundDrawableResource(android.R.color.transparent);
    }

    protected abstract void onCreate();
}
