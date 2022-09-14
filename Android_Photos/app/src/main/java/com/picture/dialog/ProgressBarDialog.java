package com.picture.dialog;

import android.content.Context;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Loading loading box
 */
public class ProgressBarDialog extends BaseDialog {
    private ProgressTask task;
    private MaterialDialog progressDialog;

    public ProgressBarDialog(Context context, int calledByViewId) {
        super(context);
        setCalledByViewId(calledByViewId);
    }

    public ProgressBarDialog(Fragment fragment, int calledByViewId) {
        this(fragment.getActivity(), calledByViewId);
        setResult(fragment);
    }

    private void initialize() {
        content("processing...");
        progress(true, 0);
    }

    @Override
    public MaterialDialog show() {
        initialize();
        progressDialog = super.show();
        task = new ProgressTask();
        task.execute();
        return progressDialog;
    }

    public void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public class ProgressTask extends AsyncTask<Void, Integer, IProgressBarResult> {

        public ProgressTask() {
        }

        @Override
        protected IProgressBarResult doInBackground(Void... arg0) {
            IProgressBarResult result = null;
            handlerProgress();
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0].intValue());
        }

        @Override
        protected void onPostExecute(IProgressBarResult result) {
            if (isCancelled()) {
                result.destroy();
                return;
            }
            progressDialog.dismiss();
            onProgressBarResult(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        private void handlerProgress() {
            if (resultObject instanceof OnProgressBarDialogExecuteListener) {
                OnProgressBarDialogExecuteListener execute = (OnProgressBarDialogExecuteListener) resultObject;
                execute.exec(calledByViewId);
            }
        }
    }

    public interface IProgressBarResult {
        void destroy();
    }
}
