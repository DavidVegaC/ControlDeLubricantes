package com.assac.controldelubricantes.Util;

import android.app.ProgressDialog;
import android.content.Context;

import com.assac.controldelubricantes.R;


public class CustomProgressDialog {

    Context ctx;
    ProgressDialog mProgressDialog;

    public CustomProgressDialog(Context context) {
        ctx = context;
    }

    public void showProgressDialog(String msj){

        mProgressDialog = new ProgressDialog(ctx, R.style.AppThemeAssacDialog);
        mProgressDialog.setMessage(msj);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
       // mProgressDialog.setContentView(R.layout.progress_dialog);

       // mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
           // @Override
           // public void onCancel(DialogInterface dialogInterface) {
           //     finCarga();
           // }
       // });

    }

    public void dismissProgressDialog(){
        if(mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        //mProgressDialog = null;
    }
}
