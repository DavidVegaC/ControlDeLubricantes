package com.assac.controldelubricantes.Util.Internet;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.assac.controldelubricantes.R;
import com.assac.controldelubricantes.Storage.Rest;
import com.assac.controldelubricantes.Util.Const;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ValidateConn  {

    public Context ctx;
    public String qType = ""; //1: muestra un progressDialog duante la migración, 2: no muestra ningún progressDialog
    public String sType=""; //Tipo de sincronización a usar <==> 1: sincroniza maestros 2: migracion de registros de accesos a servidor y viceversa
    public Rest Rest;
    public  ValidateConn(Rest rest, Context context, String queryType, String syncronizeType) {
        Rest = rest;
        ctx = context;
        qType = queryType;
        sType = syncronizeType;
    }

    //DECLARACIÓN DE VARIABLES
    private static final String TAG = "ValidateConn";
    private ProgressDialog progressDialog;
    private String SERVICE_WEB = Const.HTTP_SITE;


    private String mmigrationStartDate;


    public void mstartConn(){
        new validateWifiConn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class validateWifiConn extends AsyncTask<String, Long, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (qType.equals("1")) {
                progressDialog = new ProgressDialog(ctx, R.style.AppThemeAssacDialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setTitle("Información");
                progressDialog.setMessage("Verificando conexion a internet");
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "0";
            WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();

            if (info != null && info.isAvailable() && info.isConnected()) {
                response = "1";
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (qType.equals("1")) {
                progressDialog.dismiss();
            }

            if (response == "1") {
                new validateServerConn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (response == "0") {

                if (qType.equals("1")) {
                    Toast.makeText(ctx, "El dispositivo no tiene salida a INTERNET. Verifique su conexión y vuelva a intentarlo.", Toast.LENGTH_LONG).show();
                }
                Const.saveErrorData(ctx, mmigrationStartDate, "método: validateWifiConn /No se tiene salida a INTERNET. Verifique su conexión y vuelva a intentarlo", "1");

            }
        }
    }

    public class validateServerConn extends AsyncTask<Boolean,Long, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (qType.equals("1")) {
                progressDialog = new ProgressDialog(ctx, R.style.AppThemeAssacDialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setTitle("Información");
                progressDialog.setMessage("Conectando con el servidor");
                progressDialog.show();
            }
            mmigrationStartDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            Const.LAST_MIGRATION_DATE = mmigrationStartDate;
        }

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            int timeout = 15000;

            boolean result = false;
            HttpURLConnection httpUrl = null;
            try {
                httpUrl = (HttpURLConnection) new URL(SERVICE_WEB)
                        .openConnection();
                httpUrl.setConnectTimeout(timeout);
                httpUrl.connect();
                result = true;
            } catch (IOException e) {
                Log.e(TAG, "ERROR DE CONEXIÓN AL SERVIDOR: " + e.getMessage());
                result = false;
            } finally {
                if (null != httpUrl) {
                    httpUrl.disconnect();
                }
                httpUrl = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (qType.equals("1")) {
                progressDialog.dismiss();
                if (s) {
                    Toast.makeText(ctx, "Conexión al servidor exitosa", Toast.LENGTH_SHORT).show();
                }
            }

            if (s) {

                if (qType.equals("1")) {
                    progressDialog = new ProgressDialog(ctx, R.style.AppThemeAssacDialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setTitle("Información");
                    progressDialog.setMessage("Sincronizando con el Servidor");
                    progressDialog.show();
                }

                switch (sType) {
                    case "1"://rutina1
                        //Receive Operator's from Server
                        //Rest.receiveOperatorData(mmigrationStartDate);
                        //Receive User's from Server
                        Rest.receiveUserData(mmigrationStartDate);
                        break;

                }

                if (qType.equals("1")) {
                    progressDialog.dismiss();
                }

            } else {
                if (qType.equals("1")) {
                    Toast.makeText(ctx, "El dispositivo no tiene conexión con el servidor. Contacte al administrador del sistema.", Toast.LENGTH_LONG).show();
                }
                Const.saveErrorData(ctx, mmigrationStartDate, "método: validateServerConn / No se tiene conexión con el servidor", "1");
            }
        }
    }

}