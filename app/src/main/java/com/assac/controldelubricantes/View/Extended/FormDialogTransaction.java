package com.assac.controldelubricantes.View.Extended;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.assac.controldelubricantes.Entities.CompartmentEntity;
import com.assac.controldelubricantes.Entities.DataFormEntity;
import com.assac.controldelubricantes.Entities.Driver;
import com.assac.controldelubricantes.Entities.Plate;
import com.assac.controldelubricantes.Entities.ReasonEntity;
import com.assac.controldelubricantes.Entities.TransactionEntity;
import com.assac.controldelubricantes.Listeners.EmbeddedWifiListener;
import com.assac.controldelubricantes.R;
import com.assac.controldelubricantes.Storage.DB.CRUDOperations;
import com.assac.controldelubricantes.Storage.DB.MyDatabase;
import com.assac.controldelubricantes.View.Activity.MainActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FormDialogTransaction extends AlertDialog {

    private Activity context;
    private FragmentActivity fragmentActivity;
    private Button btnRegistrar;

    private EmbeddedWifiListener embeddedWifiListener;

    //Region cabecera
    private TextView tvNombreManguera;

    //Región Cuerpo
    private LinearLayout lyPlaca, lyKilometraje, lyHorometro, lyPreSeteo, lyConductor, lyRazon, lyComentario, lyCompartimiento;
    private LinearLayout lyPlacaNFC;
    private LinearLayout lySiguiente;
    private TextView tvTitulo;
    private EditText etPlaca, etKilometraje, etHorometro, etPreSeteo, etComentario, etIdConductor, etCompartimiento, etValidacion;
    private LinearLayout lyConductorQR;
    private LinearLayout lyEditTextCompartimiento, lySpinnerCompartimiento;

    private TextView messageErrorPlate, messageErrorIdDriver;

    //Mensaje para cada inputLayout
    String strMessageHoro, strMessageKilo, strMessagePreSe, strMessageScaneo;

    private DataFormEntity dataFormEntity;
    private int idBomba=0;
    private byte[] ResponseDataDevice;

    public boolean validarVehiculo;
    public boolean validarConductor;

    public Spinner spRazones, spCompartimientos;

    //Animación de pestañeo
    private AnimationDrawable rocketAnimationNFC, rocketAnimationQR;

    List<ReasonEntity> reasonEntities = new ArrayList<>();

    private CRUDOperations crudOperations;

    private int IdProducto=20;
    private boolean sendForm=false;

    private List<CompartmentEntity> compartmentEntities = new ArrayList<>();

    protected FormDialogTransaction(@NonNull Context context) {
        super(context);
    }

    public FormDialogTransaction(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = (Activity) context;
    }

    public FormDialogTransaction(@NonNull Context context, int themeResId, DataFormEntity dataFormEntity, EmbeddedWifiListener embeddedWifiListener) {
        super(context, themeResId);
        this.context = (Activity) context;
        this.dataFormEntity= dataFormEntity;
        this.embeddedWifiListener = embeddedWifiListener;
        validarVehiculo=false;
        validarConductor=false;
        sendForm=false;
    }

    protected FormDialogTransaction(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected FormDialogTransaction(@NonNull Context context, int themeResId, Fragment fragment) {
        super(context, themeResId);
        this.context = (Activity) context;
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState)
    {
        //View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_formulario_transaction, null);
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_form_transaction, null);
        //setTitle("Registro Transaction");
        setView(v);
        initComponent(v);
        super.onCreate(savedInstanceState);
    }

    private void initComponent(View v) {
        tvNombreManguera = (TextView) v.findViewById(R.id.tvNombreManguera);
        tvTitulo = (TextView) v.findViewById(R.id.tvTitulo);

        lyPlaca= (LinearLayout) v.findViewById(R.id.lyPlaca);
        lyPlacaNFC = (LinearLayout) v.findViewById(R.id.lyPlacaNFC);
        lyHorometro = (LinearLayout) v.findViewById(R.id.lyHorometro);
        lyKilometraje = (LinearLayout) v.findViewById(R.id.lyKilometraje);
        lyPreSeteo = (LinearLayout) v.findViewById(R.id.lyPreSeteo);
        lyConductor = (LinearLayout) v.findViewById(R.id.lyConductor);
        lyCompartimiento = (LinearLayout) v.findViewById(R.id.lyCompartimiento);
        lyRazon = (LinearLayout) v.findViewById(R.id.lyRazon);
        lyComentario = (LinearLayout) v.findViewById(R.id.lyComentario);
        lyConductorQR = (LinearLayout) v.findViewById(R.id.lyConductorQR);
        lySiguiente = (LinearLayout) v.findViewById(R.id.lySiguiente);
        lyEditTextCompartimiento= (LinearLayout) v.findViewById(R.id.lyEditTextCompartimiento);
        lySpinnerCompartimiento= (LinearLayout) v.findViewById(R.id.lySpinnerCompartimiento);

        etPlaca = (EditText) v.findViewById(R.id.etPlaca);
        etHorometro = (EditText) v.findViewById(R.id.etHorometro);
        etKilometraje = (EditText) v.findViewById(R.id.etKilometraje);
        etPreSeteo = (EditText) v.findViewById(R.id.etPreSeteo);
        etIdConductor = (EditText) v.findViewById(R.id.etIdConductor);
        etComentario = (EditText) v.findViewById(R.id.etComentario);
        etCompartimiento = (EditText) v.findViewById(R.id.etCompartimiento);
        etValidacion = (EditText) v.findViewById(R.id.etValidacion);

        messageErrorPlate = (TextView) v.findViewById(R.id.messageErrorPlate);
        messageErrorIdDriver = (TextView) v.findViewById(R.id.messageErrorIdDriver);

        btnRegistrar = (Button) v.findViewById(R.id.btnRegistrar);

        spRazones = (Spinner) v.findViewById(R.id.spRazones);
        spCompartimientos = (Spinner) v.findViewById(R.id.spCompartimientos);

        lyConductorQR.setBackgroundResource(R.drawable.bg_blink_qr);
        rocketAnimationQR = (AnimationDrawable) lyConductorQR.getBackground();

        crudOperations = new CRUDOperations(new MyDatabase(getContext()));

        switch (dataFormEntity.getTag()) {
            case "A":
            case "E":
                    validarConductor=false;
                    validarVehiculo=true;
                    lyPlacaNFC.setVisibility(View.GONE);
                    lyCompartimiento.setVisibility(View.GONE);
                    break;
            default:
                    validarConductor=true;
                    validarVehiculo=false;
                    lyPlacaNFC.setBackgroundResource(R.drawable.bg_blink_nfc);
                    rocketAnimationNFC = (AnimationDrawable) lyPlacaNFC.getBackground();
                    rocketAnimationNFC.start();
                    lyPlacaNFC.setVisibility(View.VISIBLE);
                    lyCompartimiento.setVisibility(View.VISIBLE);
                    break;
        }

        loadHelper();

        lyConductorQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                embeddedWifiListener.scanearQRCode(dataFormEntity.indiceBomba);
            }
        });

        lyPlacaNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                embeddedWifiListener.readNFCPlate(dataFormEntity.indiceBomba, dataFormEntity.numeroBomba);
            }
        });



        lySiguiente.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!validarDatos()) {
                            if (lyKilometraje.getVisibility()==View.VISIBLE){
                                if (dataFormEntity.isSolicitaHorometro()) {
                                    lyKilometraje.setVisibility(View.GONE);
                                    lyComentario.setVisibility(View.GONE);
                                    lyRazon.setVisibility(View.GONE);
                                    lyHorometro.setVisibility(View.VISIBLE);
                                    lyPlacaNFC.setVisibility(View.GONE);
                                    etValidacion.setText(strMessageHoro);
                                }else if (dataFormEntity.isSolicitaPreseteo()) {
                                    tvTitulo.setText("PRESETEO");
                                    lyKilometraje.setVisibility(View.GONE);
                                    lyPlacaNFC.setVisibility(View.GONE);
                                    lyComentario.setVisibility(View.GONE);
                                    lyRazon.setVisibility(View.GONE);
                                    lyPreSeteo.setVisibility(View.VISIBLE);
                                    etValidacion.setText(strMessagePreSe);
                                }else if (dataFormEntity.isSolicitaConductor()) {
                                    tvTitulo.setText("CONDUCTOR");
                                    lyPlaca.setVisibility(View.GONE);
                                    lyPlacaNFC.setVisibility(View.GONE);
                                    lyCompartimiento.setVisibility(View.GONE);
                                    lyKilometraje.setVisibility(View.GONE);
                                    lyComentario.setVisibility(View.GONE);
                                    lyConductor.setVisibility(View.VISIBLE);
                                    lyRazon.setVisibility(View.GONE);
                                    etValidacion.setText(strMessageScaneo);
                                    rocketAnimationQR.start();
                                }else{
                                    lyPlacaNFC.setVisibility(View.GONE);
                                    tvTitulo.setText("RAZONES");
                                    lyPlaca.setVisibility(View.GONE);
                                    lyCompartimiento.setVisibility(View.GONE);
                                    lyCompartimiento.setVisibility(View.GONE);
                                    lyKilometraje.setVisibility(View.GONE);
                                    lyConductor.setVisibility(View.GONE);
                                    lyComentario.setVisibility(View.GONE);

                                    mostrarRazones();

                                }
                            }else if (lyHorometro.getVisibility()==View.VISIBLE){
                                if (dataFormEntity.isSolicitaPreseteo()) {
                                    tvTitulo.setText("PRESETEO");
                                    lyPlacaNFC.setVisibility(View.GONE);
                                    lyHorometro.setVisibility(View.GONE);
                                    lyPreSeteo.setVisibility(View.VISIBLE);
                                    etValidacion.setText(strMessagePreSe);
                                }else if (dataFormEntity.isSolicitaConductor()) {
                                    tvTitulo.setText("CONDUCTOR");
                                    lyPlacaNFC.setVisibility(View.GONE);
                                    lyPlaca.setVisibility(View.GONE);
                                    lyCompartimiento.setVisibility(View.GONE);
                                    lyHorometro.setVisibility(View.GONE);
                                    lyConductor.setVisibility(View.VISIBLE);
                                    etValidacion.setText(strMessageScaneo);
                                    rocketAnimationQR.start();
                                }else{
                                    tvTitulo.setText("RAZONES");
                                    lyPlacaNFC.setVisibility(View.GONE);
                                    lyPlaca.setVisibility(View.GONE);
                                    lyCompartimiento.setVisibility(View.GONE);
                                    lyKilometraje.setVisibility(View.GONE);
                                    lyConductor.setVisibility(View.GONE);

                                    mostrarRazones();
                                }
                            }else if (lyPreSeteo.getVisibility()==View.VISIBLE){
                                if (dataFormEntity.isSolicitaConductor()) {
                                    tvTitulo.setText("CONDUCTOR");
                                    lyPlaca.setVisibility(View.GONE);
                                    lyPlacaNFC.setVisibility(View.GONE);
                                    lyCompartimiento.setVisibility(View.GONE);
                                    lyPreSeteo.setVisibility(View.GONE);
                                    lyConductor.setVisibility(View.VISIBLE);
                                    etValidacion.setText(strMessageScaneo);
                                    rocketAnimationQR.start();
                                }else{
                                    tvTitulo.setText("RAZONES");
                                    lyPlacaNFC.setVisibility(View.GONE);
                                    lyPlaca.setVisibility(View.GONE);
                                    lyCompartimiento.setVisibility(View.GONE);
                                    lyKilometraje.setVisibility(View.GONE);
                                    lyConductor.setVisibility(View.GONE);
                                    lyComentario.setVisibility(View.GONE);

                                    mostrarRazones();
                                }
                            }else if (lyConductor.getVisibility()==View.VISIBLE){
                                    validarConductor=true;
                                    tvTitulo.setText("RAZONES");
                                    lyPlacaNFC.setVisibility(View.GONE);
                                    lyPlaca.setVisibility(View.GONE);
                                    lyCompartimiento.setVisibility(View.GONE);
                                    lyKilometraje.setVisibility(View.GONE);
                                    lyConductor.setVisibility(View.GONE);
                                    lyComentario.setVisibility(View.GONE);

                                    mostrarRazones();
                            } else if (lyPlaca.getVisibility()==View.VISIBLE){
                                tvTitulo.setText("RAZONES");
                                lyPlaca.setVisibility(View.GONE);
                                lyPlacaNFC.setVisibility(View.GONE);
                                lyCompartimiento.setVisibility(View.GONE);
                                lyKilometraje.setVisibility(View.GONE);
                                lyConductor.setVisibility(View.GONE);
                                lyComentario.setVisibility(View.GONE);


                                mostrarRazones();
                            } else if (lyRazon.getVisibility()==View.VISIBLE){
                                tvTitulo.setText("COMENTARIO");
                                lyPlaca.setVisibility(View.GONE);
                                lyPlacaNFC.setVisibility(View.GONE);
                                lyCompartimiento.setVisibility(View.GONE);
                                lyKilometraje.setVisibility(View.GONE);
                                lyConductor.setVisibility(View.GONE);
                                lyRazon.setVisibility(View.GONE);
                                lyComentario.setVisibility(View.VISIBLE);
                                etValidacion.setText("Comentario máximo: 20 caracteres.");
                            }else if (lyComentario.getVisibility()==View.VISIBLE){
                                if(dataFormEntity.getTag().equals(""))
                                    embeddedWifiListener.sendBytesEmbedded(ResponseDataDevice,dataFormEntity.direccion,dataFormEntity.numeroBomba, dataFormEntity.comentario, dataFormEntity.razon);
                                else
                                    embeddedWifiListener.receiveDataForm(dataFormEntity);

                                dismiss();
                            }


                        }

                    }
                }
        );

        tvNombreManguera.append(dataFormEntity.getNombreManguera());

        if(dataFormEntity.getPlaca()!=null)
            etPlaca.setText(dataFormEntity.getPlaca());

        if (dataFormEntity.isSolicitaKilometraje()) {
            tvTitulo.setText("VEHÍCULO");
            lyPlacaNFC.setVisibility(View.GONE);
            lyKilometraje.setVisibility(View.VISIBLE);
            etValidacion.setText(strMessageKilo);
        }else if (dataFormEntity.isSolicitaHorometro()) {
            tvTitulo.setText("VEHÍCULO");
            lyPlacaNFC.setVisibility(View.GONE);
            lyHorometro.setVisibility(View.VISIBLE);
            etValidacion.setText(strMessageHoro);
        }else if (dataFormEntity.isSolicitaPreseteo()) {
            tvTitulo.setText("PRESETEO");
            lyPlacaNFC.setVisibility(View.GONE);
            lyPreSeteo.setVisibility(View.VISIBLE);
            etValidacion.setText(strMessagePreSe);
        }else if (dataFormEntity.isSolicitaConductor()) {
            lyPlacaNFC.setVisibility(View.GONE);
            lyConductor.setVisibility(View.VISIBLE);
            etValidacion.setText(strMessageScaneo);
        }else if(lyPlaca.getVisibility() == View.GONE){
            lyPlacaNFC.setVisibility(View.GONE);
            lyComentario.setVisibility(View.VISIBLE);
            etValidacion.setText("Comentario máximo: 20 caracteres.");
        }  else{
            etValidacion.setText(strMessageScaneo);
        }
    }

    private void loadHelper(){
        strMessageHoro=strMessageKilo=strMessagePreSe="";
        //horometro
        if (dataFormEntity.isSolicitaHorometro()){
            if (dataFormEntity.isValidaHorometro())
                strMessageHoro="Horómetro admitido: "+dataFormEntity.getHorometroMinimo() + " - " + dataFormEntity.getHorometroMaximo();
            else
                strMessageHoro="Horómetro admitido: Mayor a "+ dataFormEntity.getHorometroActual();
        }

        //kilometraje
        if (dataFormEntity.isSolicitaKilometraje()){
            if (dataFormEntity.isValidaKilometraje())
                strMessageKilo="Kilometraje admitido: "+dataFormEntity.getKilometrajeMinimo() + " - " + dataFormEntity.getKilometrajeMaximo();
            else
                strMessageKilo="Kilometraje admitido: Mayor a "+ dataFormEntity.getKilometrajeActual();
        }

        //PreSeteo
        if (dataFormEntity.isSolicitaPreseteo()) {
            strMessagePreSe = "PreSeteo admitido: " + dataFormEntity.getPreseteoMinimo() + " - " + dataFormEntity.getPreseteoMaximo();
        }

        strMessageScaneo = "Presione el botón, para scanear";
    }

    private Boolean validarDatos(){
        Boolean response = false;

        messageErrorPlate.setText("");
        messageErrorPlate.setVisibility(View.GONE);

        if (lyKilometraje.getVisibility()==View.VISIBLE){
            try {
                Double kilometraje = Double.parseDouble(etKilometraje.getText().toString());

                Log.v("Formulario", ""+kilometraje);

                if(kilometraje > dataFormEntity.getKilometrajeActual()){
                    if (dataFormEntity.isValidaKilometraje()){
                        if(!(dataFormEntity.getKilometrajeMinimo()<=kilometraje && dataFormEntity.getKilometrajeMaximo()>=kilometraje)){
                            response=true;
                            //+inputLayoutKilometraje.setError(strMessageKilo);
                            //inputLayoutKilometraje.setError("Valor permitido entre "+dataFormEntity.getKilometrajeMinimo() + " y " + dataFormEntity.getKilometrajeMaximo());
                            //Toast.makeText(context, "El valor del kilometraje debe estar entre "+ dataFormEntity.getKilometrajeMinimo() + " y "+dataFormEntity.getKilometrajeMaximo(), Toast.LENGTH_SHORT).show();
                        }else{
                            //+inputLayoutKilometraje.setError(null);
                            dataFormEntity.setKilometraje(""+kilometraje);
                        }
                    }else{
                        //+inputLayoutKilometraje.setError(null);
                        dataFormEntity.setKilometraje(""+kilometraje);
                    }
                }else{
                    response=true;
                    //+inputLayoutKilometraje.setError(strMessageKilo);
                    //inputLayoutKilometraje.setError("Valor debe ser mayor a "+ dataFormEntity.getKilometrajeActual());
                    //Toast.makeText(context, "El kilometraje debe ser mayor al kilometraje actual.", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e) {
                response=true;
                //+inputLayoutKilometraje.setError(strMessageKilo);
                //inputLayoutKilometraje.setError("Valor no puede ser vacio ");
                //Toast.makeText(context, "El valor del Kilometraje no puede ser vacio.", Toast.LENGTH_SHORT).show();
            }
        }else if (lyHorometro.getVisibility()==View.VISIBLE){
            try {
                Double horometro = Double.parseDouble(etHorometro.getText().toString());

                if(horometro > dataFormEntity.getHorometroActual()){
                    if (dataFormEntity.isValidaHorometro()){
                        if(!(dataFormEntity.getHorometroMinimo()<=horometro && dataFormEntity.getHorometroMaximo()>=horometro)){
                            response=true;
                            //Toast.makeText(context, "El valor del horometro debe estar entre "+ dataFormEntity.getHorometroMinimo() + " y "+dataFormEntity.getHorometroMaximo(), Toast.LENGTH_SHORT).show();
                            //+inputLayoutHorometro.setError(strMessageHoro);
                        }else{
                            dataFormEntity.setHorometro(""+horometro);
                            //+inputLayoutHorometro.setError(null);
                        }
                    }else{
                        dataFormEntity.setHorometro(""+horometro);
                        //+inputLayoutHorometro.setError(null);
                    }
                }else{
                    response=true;
                    //inputLayoutHorometro.setError("Valor debe ser mayor a "+ dataFormEntity.getHorometroActual());
                    //+inputLayoutHorometro.setError(strMessageHoro);
                    //Toast.makeText(context, "El horometro debe ser mayor al horometro actual.", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e) {
                response=true;
                //+inputLayoutHorometro.setError(strMessageHoro);
                //inputLayoutHorometro.setError("Valor no puede ser vacio ");
                //Toast.makeText(context, "El valor del horometro no puede ser vacio.", Toast.LENGTH_SHORT).show();
            }

        } else if (lyPreSeteo.getVisibility()==View.VISIBLE) {
            try {
                int preSeteo = Integer.parseInt(etPreSeteo.getText().toString());
                if(!(dataFormEntity.getPreseteoMinimo()<=preSeteo && dataFormEntity.getPreseteoMaximo()>=preSeteo)){
                    response=true;
                    //+inputLayoutPreSeteo.setError(strMessagePreSe);
                    //inputLayoutPreSeteo.setError("Valor permitido entre "+dataFormEntity.getPreseteoMinimo() + " y " + dataFormEntity.getPreseteoMaximo());
                }else{
                    //+inputLayoutPreSeteo.setError(null);
                    dataFormEntity.setPreSeteo(preSeteo);
                }
            } catch (Exception e) {
                response=true;
                //+inputLayoutPreSeteo.setError(strMessagePreSe);
                //inputLayoutPreSeteo.setError("Valor no puede ser vacio ");
            }
        }else if (lyConductor.getVisibility()==View.VISIBLE) {
            if(etIdConductor.getText().toString().equals("")){
                response = true;

                final TextView myView = new TextView(getContext());
                myView.setText("Por favor, escanee el código QR.");
                myView.setTextSize(15);

                LinearLayout layout1       = new LinearLayout(getContext());
                layout1.setOrientation(LinearLayout.HORIZONTAL);
                layout1.addView(myView);
                layout1.setGravity(Gravity.CENTER);
                //layout1.setBackgroundColor(Color.BLUE);
                AlertDialog builder = new Builder(
                        getContext()
                ).create();

                builder.setTitle("Mensaje");
                builder.setView(layout1);
                //builder.setMessage("Por favor, escanee el código QR.");

                builder.setButton(Dialog.BUTTON_POSITIVE, "OK", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.setOnShowListener( new OnShowListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onShow(DialogInterface arg0) {
                        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                    }
                });
                builder.show();
            }else{
                dataFormEntity.setIdConductor(String.format("%16s", etIdConductor.getText().toString()).replace(" ","0"));
                Log.v("IdsConductor",dataFormEntity.idConductor);
            }
        }else if (lyRazon.getVisibility()==View.VISIBLE){
            int spRazonesId =  (spRazones.getSelectedItemPosition());
            dataFormEntity.setRazon(reasonEntities.get(spRazonesId).getReasonNumber());

        } else if (lyComentario.getVisibility()==View.VISIBLE) {

            String comentario = etComentario.getText().toString();

            if(comentario.length()>20){
                response=true;
                etValidacion.setText("Comentario máximo: 20 caracteres.");
            }else{
                dataFormEntity.setComentario(String.format("%-20s",etComentario.getText().toString()));
            }

            //Log.v("Comentario",String.format("%20s",etComentario.getText().toString()));
        } else if (lyCompartimiento.getVisibility()==View.VISIBLE){
            if(compartmentEntities.size()>0){
                int spCompartimientoId =  (spCompartimientos.getSelectedItemPosition());
                ResponseDataDevice[30]=(byte)compartmentEntities.get(spCompartimientoId).getIdCompartment();
            }
        }

        if(etPlaca.getText().toString().equals("")){
            response = true;
            messageErrorPlate.setText("El campo placa no puede ser vacio.");
            messageErrorPlate.setVisibility(View.GONE);
        }


        return response;
    }

    //Existe solo 1 compartimiento
    public void escribirDataReadNfc(byte[] responseDataDevice, String Placa, String compartimiento){
        ResponseDataDevice = responseDataDevice;
        IdProducto=responseDataDevice[18];
        messageErrorPlate.setVisibility(View.GONE);
        etValidacion.setText("");
        dataFormEntity.setPlaca(Placa);
        etPlaca.setText(Placa);
        etCompartimiento.setText(compartimiento);
        lyEditTextCompartimiento.setVisibility(View.VISIBLE);
        lySpinnerCompartimiento.setVisibility(View.GONE);
    }

    //Existe más de 1 compartimiento
    public void escribirDataReadNfc2(byte[] responseDataDevice, String Placa, List<CompartmentEntity> compartmentEntities){
        ResponseDataDevice = responseDataDevice;
        IdProducto=responseDataDevice[18];
        messageErrorPlate.setVisibility(View.GONE);
        etValidacion.setText("");
        dataFormEntity.setPlaca(Placa);
        etPlaca.setText(Placa);
        this.compartmentEntities=compartmentEntities;

        ArrayList<String> strings = new ArrayList<>();
        for(int i=0; i<compartmentEntities.size(); i++){
            strings.add(compartmentEntities.get(i).getCompartmentName());
        }
        ArrayAdapter<String> stringArrayAdapter  = new ArrayAdapter<String>(getContext(),R.layout.spinner_theme_1,strings);
        stringArrayAdapter.setDropDownViewResource(R.layout.spinner_theme_2);
        spCompartimientos.setAdapter(stringArrayAdapter);

        lyEditTextCompartimiento.setVisibility(View.GONE);
        lySpinnerCompartimiento.setVisibility(View.VISIBLE);
    }

    public void escribirErrorPlaca(String ErrorPlaca){
        etValidacion.setText(ErrorPlaca);
        //messageErrorPlate.setText(ErrorPlaca);
        //messageErrorPlate.setVisibility(View.VISIBLE);
    }

    public void escribirIdConductor(String IdConductor){
        messageErrorIdDriver.setVisibility(View.GONE);
        etValidacion.setText("");
        dataFormEntity.setIdConductor(IdConductor);
        etIdConductor.setText(IdConductor);
    }

    public void escribirErrorIdConductor(String ErrorIdConductor){
        etValidacion.setText(ErrorIdConductor);
        //messageErrorIdDriver.setText(ErrorIdConductor);
        //messageErrorIdDriver.setVisibility(View.VISIBLE);
    }

    private void mostrarRazones(){
        reasonEntities = crudOperations.getReasonsForNumberProduct(IdProducto);

        if(reasonEntities.size()>0){
            ArrayList<String> strings = new ArrayList<>();
            for(int i=0; i<reasonEntities.size(); i++){
                strings.add(reasonEntities.get(i).getReasonName());
            }
            ArrayAdapter<String> stringArrayAdapter  = new ArrayAdapter<String>(getContext(),R.layout.spinner_theme_1,strings);
            stringArrayAdapter.setDropDownViewResource(R.layout.spinner_theme_2);
            spRazones.setAdapter(stringArrayAdapter);
        }else
            Toast.makeText(context, "No existen razones con el producto "+IdProducto, Toast.LENGTH_SHORT).show();

        lyRazon.setVisibility(View.VISIBLE);
        etValidacion.setText("Seleccione una razón.");
    }

    public void setSendForm(boolean sendForm){
        this.sendForm = sendForm;
    }

    public boolean getSendForm(){
        return this.sendForm;
    }
}
