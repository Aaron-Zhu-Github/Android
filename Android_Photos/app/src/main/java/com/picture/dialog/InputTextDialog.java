package com.picture.dialog;

import android.content.Context;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;

/**
 * Input box
 */
public class InputTextDialog extends BaseDialog {
    private String title;

    public InputTextDialog(Context context, int calledByViewId, String title) {
        super(context);
        this.title = title;
        this.calledByViewId = calledByViewId;
        initialize();
    }

    public InputTextDialog(Fragment fragment, int calledByViewId, String title) {
        this(fragment.getActivity(), calledByViewId, title);
        setResult(fragment);
    }

    private void initialize() {
        title(title);
        input(null, "", (dialog, input) -> {
            if (TextUtils.isEmpty(input)) {
                return;
            }
            onResultClick(DialogAction.POSITIVE, String.valueOf(input));
            autoDismiss = true;
        });

        positiveText("OK");
        onPositive((dialog, which) -> autoDismiss = false);
        negativeText("Cancel");
        onNegative((dialog, which) -> {
            onResultClick(DialogAction.NEGATIVE);
            autoDismiss = true;
        });
    }
}
