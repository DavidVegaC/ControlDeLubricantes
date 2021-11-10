package com.assac.controldelubricantes.View.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.assac.controldelubricantes.Listeners.MainListener;
import com.assac.controldelubricantes.R;
import com.assac.controldelubricantes.Storage.DB.CRUDOperations;
import com.assac.controldelubricantes.Storage.DB.MyDatabase;
import com.assac.controldelubricantes.Storage.PreferencesHelper;
import com.assac.controldelubricantes.Util.Const;
import com.assac.controldelubricantes.Util.CustomAnimation;
import com.assac.controldelubricantes.Util.CustomProgressDialog;
import com.assac.controldelubricantes.Util.NavigationFragment;
import com.assac.controldelubricantes.Util.Utils;
import com.assac.controldelubricantes.Util.Wifi.EmbeddedPtcl;
import com.assac.controldelubricantes.View.Fragment.EmbeddedPtclWifi;
import com.assac.controldelubricantes.View.Fragment.PreCierreFragment;
import com.assac.controldelubricantes.View.Fragment.TicketFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainListener {

    //ADDITIONAL CODE
    private LinearLayout btnEstacion;
    private LinearLayout btnDuplicadoTicket;
    private LinearLayout btnPreCierre;
    private LinearLayout lySettings;

    private CRUDOperations crudOperations;

    public EmbeddedPtclWifi embeddedPtclWifi = new EmbeddedPtclWifi();
    public TicketFragment ticketFragment = new TicketFragment(this);
    public PreCierreFragment preCierreFragment = new PreCierreFragment();

    private NfcAdapter mNfcAdapter;

    public static final String TAG = "NfcTag";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    CustomProgressDialog customProgressDialog = new CustomProgressDialog(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
    }

    private void initComponent(){
        btnEstacion = findViewById(R.id.btnEstacion);
        btnDuplicadoTicket = findViewById(R.id.btnDuplicadoTicket);
        btnPreCierre = findViewById(R.id.btnPreCierre);
        lySettings = findViewById(R.id.lySettings);

        crudOperations = new CRUDOperations(new MyDatabase(this));

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        /*if (mNfcAdapter == null) {
            // ES NECESARIO QUE EL DISPOSITIVO SOPORTE NFC
            Toast.makeText(this, "El dispositivo no soporta NFC.", Toast.LENGTH_LONG).show();
            //finish();
            //return;
        }else {
            if (!mNfcAdapter.isEnabled()) {
                Toast.makeText(this, "Activa NFC para continuar.", Toast.LENGTH_LONG).show();
            }
        }*/


        btnEstacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEmbeddedFragment();
            }
        });

        btnDuplicadoTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(validateConfigurationStation()){
                    goToTicketFragment();
                }else{
                    goToConfigurationFragment();
                }*/
                goToTicketFragment();
                btnDuplicadoTicket.setFocusable(true);
            }
        });

        btnPreCierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    goToPreCierreFragment();
            }
        });

        lySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseSession();
            }
        });

        goToEmbeddedFragment();
        //bottomNavigationView =findViewById(R.id.bottom_navigation);
        //bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EscenarioEmbeddedAnillosLLaverosFragment()).commit();
    }

    public void goToEmbeddedFragment(){
        NavigationFragment.addFragment(null, embeddedPtclWifi, "EmbeddedComunicationPlcl", this,
                R.id.main_activity_content, false, CustomAnimation.LEFT_RIGHT);

        if(!embeddedPtclWifi.isVisible())paintDrawableNavigation(R.drawable.bg_para_boton_barra_activo,R.drawable.bg_para_boton_barra_inactivo,R.drawable.bg_para_boton_barra_inactivo);
    }

    public void goToTicketFragment(){
        NavigationFragment.addFragment(null, ticketFragment, "TicketFragment", this,
                R.id.main_activity_content, false, CustomAnimation.LEFT_RIGHT);

        if(!ticketFragment.isVisible())paintDrawableNavigation(R.drawable.bg_para_boton_barra_inactivo,R.drawable.bg_para_boton_barra_activo,R.drawable.bg_para_boton_barra_inactivo);

    }

    public void goToPreCierreFragment(){
        NavigationFragment.addFragment(null, preCierreFragment, "PreCierreFragment", this,
                R.id.main_activity_content, false, CustomAnimation.LEFT_RIGHT);
        if(!preCierreFragment.isVisible())paintDrawableNavigation(R.drawable.bg_para_boton_barra_inactivo,R.drawable.bg_para_boton_barra_inactivo,R.drawable.bg_para_boton_barra_activo);
    }

    private void paintDrawableNavigation(int station, int ticket, int precierre){
        btnEstacion.setBackground(getDrawable(station));
        btnDuplicadoTicket.setBackground(getDrawable(ticket));
        btnPreCierre.setBackground(getDrawable(precierre));
    }

    @Override
    public void showProgressDialog(String message) {
        customProgressDialog.showProgressDialog(message);
    }

    @Override
    public void dismissProgressDialog() {
        customProgressDialog.dismissProgressDialog();
    }

    //1 . ACTIVA EL ADAPTADOR DE NFC PARA QUE ESTÉ EN MODO ESCUCHA DE DISPOSITIVOS
    @Override
    public void enableForegroundDispatchSystem() {

        if(mNfcAdapter!= null){
            Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            IntentFilter[] intentFilters = new IntentFilter[]{};

            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }else{
            Toast.makeText(this, "El dispositivo no soporta NFC.", Toast.LENGTH_SHORT).show();
        }


    }

    //2 . DESACTIVA EL ADAPTADOR DE NFC PARA QUE ESTÉ EN MODO ESCUCHA DE DISPOSITIVOS
    @Override
    public void disableForegroundDispatchSystem() {
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Const.CONTADOR_LECTURAS_NFC ++;
        Const.ESTADO_NFC = true;
        if(Const.CONTADOR_LECTURAS_NFC <= 1){
            handleIntent(intent);
        }
    }

    private void handleIntent(Intent intent) {
        Log.v(TAG, "ENTRÓ handleIntent");
        String action = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Log.v(TAG, "ACTION_NDEF_DISCOVERED");
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            Log.v(TAG, "ACTION_TECH_DISCOVERED");

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
            Log.v(TAG, "ACTION_TAG_DISCOVERED");

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            //new NdefReaderTask().execute(tag);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new NdefReaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,tag);
                }
            });
        }


    }

    private class NdefReaderTask extends AsyncTask<Tag, Void,  byte[]> {

        private Boolean readCorrect=true;

        @Override
        protected byte[] doInBackground(Tag... params) {
            Log.v(TAG, "NdefReaderTask:doInBackground");
            readCorrect=true;
            NfcV tag5=null;
            Tag tag;
            byte[] responseDataDevice= new byte[300];
            try {
                tag = params[0];
                MifareClassic mifareClassicTag = MifareClassic.get(tag);

                //LECTURA DE ID

                byte[] tagId = tag.getId();

                //LECTURA DE DATOS
                tag5 = NfcV.get(tag);


                int blockAddress = 13;
                byte[] cmd = new byte[] {
                        (byte)0x20,  // FLAGS
                        (byte)0x20,  // READ_SINGLE_BLOCK
                        0, 0, 0, 0, 0, 0, 0, 0, // ID TAG
                        (byte)(blockAddress & 0x0ff)
                };

                Log.v("TagID", ""+Utils.byteArrayToHexString(tagId, tagId.length));

                //ESCRITURA
                byte[] cmd5 = new byte[] {
                        (byte)0x60,  // FLAGS
                        (byte)0x21,  // WRITE_SINGLE_BLOCK
                        0, 0, 0, 0, 0, 0, 0, 0, //ID TAG
                        (byte)(blockAddress & 0x0ff),
                        (byte)0x4c,  // data block that you want to write (same length as the blocks that you read) Deben ser 4 bytes por bloque
                        (byte)0x45,
                        (byte)0x4f,
                        (byte)0x4f,
                };
                System.arraycopy(tagId, 0, cmd5, 2, 8);
                System.arraycopy(tagId, 0, cmd, 2, 8);

                System.arraycopy(tagId, 0, responseDataDevice, 292, 8);
                tag5.connect();
                byte[] blockResponseDataDevice;

                //responseDataDevice = tag5.transceive(cmd);
                int c=0;
                int initBlockRead=0;
                Log.v("Tag", ""+tagId[1]);
                switch (tagId[6]){
                    case EmbeddedPtcl.tag_model_04:
                        initBlockRead=0;
                        break;
                    case EmbeddedPtcl.tag_model_07:
                        initBlockRead=1;
                        break;
                }

                for(int x = initBlockRead; x< blockAddress; x++){
                    cmd[10]=(byte)(x & 0x0ff);
                    blockResponseDataDevice = tag5.transceive(cmd);

                    for(int i =1 ; i<blockResponseDataDevice.length-1;i++){
                        responseDataDevice[c]=blockResponseDataDevice[i];
                        c++;
                    }

                }


            } catch (IOException e) {
                //e.printStackTrace();
                Const.ESTADO_NFC = false;
                Const.CONTADOR_LECTURAS_NFC  = 0;
                Log.v(TAG, e.getMessage());
                readCorrect=false;
            } finally {
                try {
                    tag5.close();
                } catch (IOException e) {
                    readCorrect=false;
                    e.printStackTrace();
                }
            }

            return responseDataDevice;
        }

        @Override
        protected void onPostExecute(byte[] result) {
            if(readCorrect) {
                if (result != null ) {
                    String responseDataTexto = "";
                    responseDataTexto = Utils.byteArrayToHexString(result, result.length);
                    embeddedPtclWifi.receiveNFCPlate(result);
                }
            }else
                    Toast.makeText(MainActivity.this, "No se completó correctamente la lectura por NFC.\nInténtelo nuevamente.", Toast.LENGTH_SHORT).show();
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();
            // Codificación
            String textEncoding;
            if ((payload[0] & 128) != 0) textEncoding = "UTF-16";
            else textEncoding = "UTF-8";

            // Lenguaje
            int languageCodeLength = payload[0] & 0063;
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data
        );

        if(intentResult.getContents() != null){
            /*AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(
                    MainActivity.this
            );

            builder.setTitle("Result");
            builder.setMessage(intentResult.getContents());

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();*/
            embeddedPtclWifi.receiveQRCode(intentResult.getContents());
        }else{
            Toast.makeText(this, "No se escaneo nada.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void CloseSession() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        PreferencesHelper.signOut(getApplication());
        this.finish();
    }

}