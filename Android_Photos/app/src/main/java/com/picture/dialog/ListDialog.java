package com.picture.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

/**
 * List selection dialog
 */
public class ListDialog extends BaseDialog {
    private String title;
    private List<String> values;

    public ListDialog(Context context, String title, List<String> values) {
        super(context);
        this.title = title;
        this.values = values;
        initialize(context, values);
    }

    public ListDialog(Fragment fragment, String title, List<String> values) {
        this(fragment.getActivity(), title, values);
        setResult(fragment);
    }

    public ListDialog(Context context, int calledByViewId, String title, List<String> values) {
        super(context);
        this.title = title;
        this.calledByViewId = calledByViewId;
        this.values = values;
        initialize(context, values);
    }

    public ListDialog(Fragment fragment, int calledByViewId, String title, List<String> values) {
        this(fragment.getActivity(), calledByViewId, title, values);
        setResult(fragment);
    }

    private void itemsCallBack(int selectedIndex) {
        itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                onResultClick(DialogAction.POSITIVE, values.get(which));
                return false;
            }
        });
    }

    private void initialize(final Context context, List<String> values) {
        title(title);
        positiveText("OK");
        negativeText("Cancel");
        items(values);
        autoDismiss(false);
        itemsCallBack(0);
        onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                autoDismiss = true;
            }
        });
        onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                onResultClick(DialogAction.NEGATIVE);
                autoDismiss = true;
            }
        });
    }
}
