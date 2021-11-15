package com.assac.controldelubricantes.View.Extended;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.assac.controldelubricantes.Entities.DataFormEntity;
import com.assac.controldelubricantes.Entities.TransactionEntity;
import com.assac.controldelubricantes.Listeners.EmbeddedWifiListener;
import com.assac.controldelubricantes.R;
import com.assac.controldelubricantes.Storage.DB.CRUDOperations;
import com.google.android.material.textfield.TextInputLayout;

public class FormDialogTransaction2 extends AlertDialog {

    private Activity context;
    private FragmentActivity fragmentActivity;
    private Button btnCancelar, btnRegistrar;

    private EmbeddedWifiListener embeddedWifiListener;

    //Region cabecera
    private TextView tvNombreManguera, tvNumTicket;

    //Secciones
    private LinearLayout lyVehiculo, lyConductor;
    private LinearLayout lyHorometro, lyKilometraje, lyPreSeteo;

    //Region Cuerpo
    //Región Vehiculo
    private LinearLayout lyPlacaNFC;
    private TextInputLayout inputLayoutHorometro, inputLayoutKilometraje, inputLayoutPreSeteo;
    private EditText etPlaca, etHorometro, etKilometraje, etPreSeteo;
    private TextView messageErrorPlate;

    //Mensaje para cada inputLayout
    String strMessageHoro, strMessageKilo, strMessagePreSe;

    //Región Conductor
    private LinearLayout lyConductorQR;
    private EditText etIdConductor;
    private TextInputLayout inputLayoutNombreConductor;
    private TextView messageErrorIdDriver;
    //private EditText etNombreConductor;

    private DataFormEntity dataFormEntity;

    private CRUDOperations crudOperations;
    private int idBomba=0;

    private String Tag_Button="";

    //Insertar Tabla Transaccion
    //private long nroTicketTransaction=0;
    private int numBomba=0;
    private String txtPlaca="";
    private String txtManguera="";
    private String txtProducto="";
    private String idConductor="";
    private String idOperator="";
    private String txtKilometraje="";
    private String txtHorometro="";
    private String fechaInicio="";
    private String horaInicio="";
    private String txtVolumen="";
    private String txtTemperatura="";
    private String txtContometroInicial="";
    private String txtContometroFinal="";
    private String fechaFin="";
    private String horaFin="";
    private String idUsuario="1";
    private String totalizador="";

    Double volumen;

    private TransactionEntity transactionEntity;

    private byte[] ResponseDataDevice;

    protected FormDialogTransaction2(@NonNull Context context) {
        super(context);
    }

    protected FormDialogTransaction2(@NonNull Context context, FragmentActivity fragmentActivity, int themeResId, CRUDOperations crudOperations, int idBomba, String totalizador) {
        super(context, themeResId);
        this.context = (Activity) context;
        this.fragmentActivity = fragmentActivity;
        this.crudOperations = crudOperations;
        this.idBomba =idBomba;
        this.totalizador = totalizador;
    }

    public FormDialogTransaction2(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = (Activity) context;
    }

    public FormDialogTransaction2(@NonNull Context context, int themeResId, DataFormEntity dataFormEntity, EmbeddedWifiListener embeddedWifiListener) {
        super(context, themeResId);
        this.context = (Activity) context;
        this.dataFormEntity= dataFormEntity;
        this.embeddedWifiListener = embeddedWifiListener;
    }

    protected FormDialogTransaction2(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected FormDialogTransaction2(@NonNull Context context, int themeResId, Fragment fragment) {
        super(context, themeResId);
        this.context = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_formulario_transaction, null);
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_form_transaction2, null);
        //setTitle("Registro Transaction");
        setView(v);
        initComponent(v);
        super.onCreate(savedInstanceState);
    }

    private void initComponent(View v) {
        tvNombreManguera = (TextView) v.findViewById(R.id.tvNombreManguera);

        //Sección Vehículo
        lyVehiculo = (LinearLayout) v.findViewById(R.id.lyVehiculo);

        lyPlacaNFC = (LinearLayout) v.findViewById(R.id.lyPlacaNFC);
        lyHorometro = (LinearLayout) v.findViewById(R.id.lyHorometro);
        lyKilometraje = (LinearLayout) v.findViewById(R.id.lyKilometraje);
        lyPreSeteo = (LinearLayout) v.findViewById(R.id.lyPreSeteo);

        inputLayoutHorometro = (TextInputLayout) v.findViewById(R.id.inputLayoutHorometro);
        inputLayoutKilometraje = (TextInputLayout) v.findViewById(R.id.inputLayoutKilometraje);
        inputLayoutPreSeteo = (TextInputLayout) v.findViewById(R.id.inputLayoutPreSeteo);

        messageErrorPlate = (TextView) v.findViewById(R.id.messageErrorPlate);

        etPlaca = (EditText) v.findViewById(R.id.etPlaca);
        etHorometro = (EditText) v.findViewById(R.id.etHorometro);
        etKilometraje = (EditText) v.findViewById(R.id.etKilometraje);
        etPreSeteo = (EditText) v.findViewById(R.id.etPreSeteo);

        //Sección Conductor
        lyConductor = (LinearLayout) v.findViewById(R.id.lyConductor);

        lyConductorQR = (LinearLayout) v.findViewById(R.id.lyConductorQR);
        etIdConductor = (EditText) v.findViewById(R.id.etIdConductor);
        messageErrorIdDriver = (TextView) v.findViewById(R.id.messageErrorIdDriver);
        inputLayoutNombreConductor = (TextInputLayout) v.findViewById(R.id.inputLayoutNombreConductor);

        //etNombreConductor = (EditText) v.findViewById(R.id.etNombreConductor);

        btnCancelar = (Button) v.findViewById(R.id.btnCancelar);
        btnRegistrar = (Button) v.findViewById(R.id.btnRegistrar);

        switch (dataFormEntity.getTag()) {
            case "A":
                lyPlacaNFC.setVisibility(View.GONE);
                btnRegistrar.setText("ENVIAR");
                break;
            case "E":
                lyPlacaNFC.setVisibility(View.GONE);
                btnRegistrar.setTag("Tag_Vehiculo");
                btnRegistrar.setText("SIGUIENTE");
                break;
            default:
                lyPlacaNFC.setVisibility(View.VISIBLE);
                btnRegistrar.setText("ENVIAR");
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
                embeddedWifiListener.readNFCPlate(dataFormEntity.indiceBomba, 0);
            }
        });

        btnCancelar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (dataFormEntity.getTag()){
                            case "A":
                                dismiss();
                                break;
                            case "E":
                                Tag_Button=btnRegistrar.getTag().toString();
                                switch (Tag_Button){
                                    case "Tag_Vehiculo":
                                        dismiss();
                                        break;
                                    case "Tag_Conductor":
                                        btnCancelar.setText("CANCELAR");
                                        btnRegistrar.setTag("Tag_Vehiculo");
                                        btnRegistrar.setText("SIGUIENTE");
                                        lyVehiculo.setVisibility(View.VISIBLE);
                                        lyConductor.setVisibility(View.GONE);
                                        break;
                                }
                                break;
                            default:
                                dismiss();
                                break;
                        }

                    }
                }
        );

        btnRegistrar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (dataFormEntity.getTag()){
                            case "A":
                                if(!validarDatosVehiculo()){
                                    embeddedWifiListener.receiveDataForm(dataFormEntity);
                                    dismiss();
                                }
                                break;
                            case "E":
                                Tag_Button=btnRegistrar.getTag().toString();

                                switch (Tag_Button){
                                    case "Tag_Vehiculo":
                                        if(!validarDatosVehiculo()){
                                            btnRegistrar.setTag("Tag_Conductor");
                                            btnCancelar.setText("ANTERIOR");
                                            btnRegistrar.setText("ENVIAR");
                                            lyVehiculo.setVisibility(View.GONE);
                                            lyConductor.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                    case "Tag_Conductor":
                                        if(!validarDatosConductor()) {
                                            embeddedWifiListener.receiveDataForm(dataFormEntity);
                                            dismiss();
                                        }
                                        break;
                                }
                                break;
                            default:
                                if(!validarDatosVehiculo()){
                                    embeddedWifiListener.sendBytesEmbedded(ResponseDataDevice,dataFormEntity.direccion,dataFormEntity.numeroBomba,"",0);
                                    dismiss();
                                }
                                break;
                        }

                    }
                }
        );

        tvNombreManguera.append(dataFormEntity.getNombreManguera());

        if(dataFormEntity.getPlaca()!=null)
            etPlaca.setText(dataFormEntity.getPlaca());

        if (!dataFormEntity.isSolicitaHorometro())
            lyHorometro.setVisibility(View.GONE);

        if (!dataFormEntity.isSolicitaKilometraje())
            lyKilometraje.setVisibility(View.GONE);

        if (!dataFormEntity.isSolicitaPreseteo())
            lyPreSeteo.setVisibility(View.GONE);
    }

    private void loadHelper(){
        strMessageHoro=strMessageKilo=strMessagePreSe="";
        //horometro
        if (dataFormEntity.isSolicitaHorometro()){
            if (dataFormEntity.isValidaHorometro())
                strMessageHoro="Valor permitido entre "+dataFormEntity.getHorometroMinimo() + " y " + dataFormEntity.getHorometroMaximo();
            else
                strMessageHoro="Valor debe ser mayor a "+ dataFormEntity.getHorometroActual();

            inputLayoutHorometro.setHelperText(strMessageHoro);
        }

        //kilometraje
        if (dataFormEntity.isSolicitaKilometraje()){
            if (dataFormEntity.isValidaKilometraje())
                strMessageKilo="Valor permitido entre "+dataFormEntity.getKilometrajeMinimo() + " y " + dataFormEntity.getKilometrajeMaximo();
            else
                strMessageKilo="Valor debe ser mayor a "+ dataFormEntity.getKilometrajeActual();

            inputLayoutKilometraje.setHelperText(strMessageKilo);
        }

        //PreSeteo
        if (dataFormEntity.isSolicitaPreseteo()) {
            strMessagePreSe = "Valor permitido entre " + dataFormEntity.getPreseteoMinimo() + " y " + dataFormEntity.getPreseteoMaximo();

            inputLayoutPreSeteo.setHelperText(strMessagePreSe);
        }
    }

    private Boolean validarDatosVehiculo(){
        Boolean response = false;

        inputLayoutHorometro.setError(null);
        inputLayoutKilometraje.setError(null);
        inputLayoutPreSeteo.setError(null);
        messageErrorPlate.setText("");
        messageErrorPlate.setVisibility(View.GONE);

        if(etPlaca.getText().toString().equals("")){
            messageErrorPlate.setText("El campo placa no puede ser vacia.");
            messageErrorPlate.setVisibility(View.VISIBLE);
        }

        if (dataFormEntity.isSolicitaHorometro()){
            try {
                Double horometro = Double.parseDouble(etHorometro.getText().toString());

                if(horometro > dataFormEntity.getHorometroActual()){
                    if (dataFormEntity.isValidaHorometro()){
                        if(!(dataFormEntity.getHorometroMinimo()<=horometro && dataFormEntity.getHorometroMaximo()>=horometro)){
                            response=true;
                            //Toast.makeText(context, "El valor del horometro debe estar entre "+ dataFormEntity.getHorometroMinimo() + " y "+dataFormEntity.getHorometroMaximo(), Toast.LENGTH_SHORT).show();
                            inputLayoutHorometro.setError(strMessageHoro);
                        }else{
                            dataFormEntity.setHorometro(""+horometro);
                            inputLayoutHorometro.setError(null);
                        }
                    }else{
                        dataFormEntity.setHorometro(""+horometro);
                        inputLayoutHorometro.setError(null);
                    }
                }else{
                    response=true;
                    //inputLayoutHorometro.setError("Valor debe ser mayor a "+ dataFormEntity.getHorometroActual());
                    inputLayoutHorometro.setError(strMessageHoro);
                    //Toast.makeText(context, "El horometro debe ser mayor al horometro actual.", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e) {
                response=true;
                inputLayoutHorometro.setError(strMessageHoro);
                //inputLayoutHorometro.setError("Valor no puede ser vacio ");
                //Toast.makeText(context, "El valor del horometro no puede ser vacio.", Toast.LENGTH_SHORT).show();
            }

        }

        if (dataFormEntity.isSolicitaKilometraje()){
            try {
                Double kilometraje = Double.parseDouble(etKilometraje.getText().toString());

                if(kilometraje > dataFormEntity.getKilometrajeActual()){
                    if (dataFormEntity.isValidaHorometro()){
                        if(!(dataFormEntity.getKilometrajeMinimo()<=kilometraje && dataFormEntity.getKilometrajeMaximo()>=kilometraje)){
                            response=true;
                            inputLayoutKilometraje.setError(strMessageKilo);
                            //inputLayoutKilometraje.setError("Valor permitido entre "+dataFormEntity.getKilometrajeMinimo() + " y " + dataFormEntity.getKilometrajeMaximo());
                            //Toast.makeText(context, "El valor del kilometraje debe estar entre "+ dataFormEntity.getKilometrajeMinimo() + " y "+dataFormEntity.getKilometrajeMaximo(), Toast.LENGTH_SHORT).show();
                        }else{
                            inputLayoutKilometraje.setError(null);
                            dataFormEntity.setKilometraje(""+kilometraje);
                        }
                    }else{
                        inputLayoutKilometraje.setError(null);
                        dataFormEntity.setKilometraje(""+kilometraje);
                    }
                }else{
                    response=true;
                    inputLayoutKilometraje.setError(strMessageKilo);
                    //inputLayoutKilometraje.setError("Valor debe ser mayor a "+ dataFormEntity.getKilometrajeActual());
                    //Toast.makeText(context, "El kilometraje debe ser mayor al kilometraje actual.", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e) {
                response=true;
                inputLayoutKilometraje.setError(strMessageKilo);
                //inputLayoutKilometraje.setError("Valor no puede ser vacio ");
                //Toast.makeText(context, "El valor del Kilometraje no puede ser vacio.", Toast.LENGTH_SHORT).show();
            }
        }

        if (dataFormEntity.isSolicitaPreseteo()) {
            try {
                int preSeteo = Integer.parseInt(etPreSeteo.getText().toString());
                if(!(dataFormEntity.getPreseteoMinimo()<=preSeteo && dataFormEntity.getPreseteoMaximo()>=preSeteo)){
                    response=true;
                    inputLayoutPreSeteo.setError(strMessagePreSe);
                    //inputLayoutPreSeteo.setError("Valor permitido entre "+dataFormEntity.getPreseteoMinimo() + " y " + dataFormEntity.getPreseteoMaximo());
                }else{
                    inputLayoutPreSeteo.setError(null);
                    dataFormEntity.setPreSeteo(preSeteo);
                }
            } catch (Exception e) {
                response=true;
                inputLayoutPreSeteo.setError(strMessagePreSe);
                //inputLayoutPreSeteo.setError("Valor no puede ser vacio ");
            }
        }

        return response;
    }

    private Boolean validarDatosConductor(){
        Boolean response = false;

        //NOMBRE DEL CONDUCTOR
        /*
        inputLayoutNombreConductor.setError(null);
        int  lengthNombreConductor = etNombreConductor.getText().length();

        if(lengthNombreConductor > 20){
            inputLayoutNombreConductor.setError("Máximo 20 carácteres aceptados");
            response = true;
        }else{
            dataFormEntity.setNombreConductor(String.format("%-20s", etNombreConductor.getText().toString()));
            inputLayoutNombreConductor.setError(null);
        }*/



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


        return response;
    }

    public void escribirPlaca(byte[] responseDataDevice, String Placa){
        ResponseDataDevice = responseDataDevice;
        messageErrorPlate.setVisibility(View.GONE);
        dataFormEntity.setPlaca(Placa);
        etPlaca.setText(Placa);
    }

    public void escribirErrorPlaca(String ErrorPlaca){
        messageErrorPlate.setText(ErrorPlaca);
        messageErrorPlate.setVisibility(View.VISIBLE);
    }

    public void escribirIdConductor(String IdConductor){
        messageErrorIdDriver.setVisibility(View.GONE);
        dataFormEntity.setIdConductor(IdConductor);
        etIdConductor.setText(IdConductor);
    }

    public void escribirErrorIdConductor(String ErrorIdConductor){
        messageErrorIdDriver.setText(ErrorIdConductor);
        messageErrorIdDriver.setVisibility(View.VISIBLE);
    }


}
