package com.picture.camera;

import android.content.Context;
import android.view.View;

import com.picture.R;

/**
 * Picture save dialog
 */
public class BottomSaveDialog extends BottomDialogBase implements View.OnClickListener {

    private OnSaveListener listener;

    public BottomSaveDialog(Context context) {
        super(context);
        listener = (OnSaveListener) context;
    }

    @Override
    protected void onCreate() {
        setContentView(R.layout.dialog_save);
        findViewById(R.id.tvSaveAs).setOnClickListener(this);
        findViewById(R.id.tvCancel).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        dismiss();
        int id = v.getId();
        switch (id) {
            case R.id.tvSaveAs:
                listener.callbackSave(SaveEnum.SAVE_AS);
                break;
        }
    }

    public interface OnSaveListener {
        void callbackSave(SaveEnum saveEnum);
    }
}
