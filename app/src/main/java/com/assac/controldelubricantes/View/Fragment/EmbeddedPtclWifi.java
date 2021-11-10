package com.assac.controldelubricantes.View.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.assac.controldelubricantes.Entities.DataFormEntity;
import com.assac.controldelubricantes.Entities.Hose;
import com.assac.controldelubricantes.Entities.InformationHMIEntity;
import com.assac.controldelubricantes.Entities.LayoutHoseEntity;
import com.assac.controldelubricantes.Entities.Maestros;
import com.assac.controldelubricantes.Entities.TransactionEntity;
import com.assac.controldelubricantes.Listeners.EmbeddedWifiListener;
import com.assac.controldelubricantes.Listeners.MainListener;
import com.assac.controldelubricantes.R;
import com.assac.controldelubricantes.Storage.DB.CRUDOperations;
import com.assac.controldelubricantes.Storage.DB.MyDatabase;
import com.assac.controldelubricantes.Storage.PreferencesHelper;
import com.assac.controldelubricantes.Util.Bluetooth.ImpresionTicketAsync;
import com.assac.controldelubricantes.Util.Bluetooth.PrinterBluetooth;
import com.assac.controldelubricantes.Util.Const;
import com.assac.controldelubricantes.Util.CustomProgressDialog;
import com.assac.controldelubricantes.Util.Utils;
import com.assac.controldelubricantes.Util.Wifi.EmbeddedPtcl;
import com.assac.controldelubricantes.Util.Wifi.NetworkUtil;
import com.assac.controldelubricantes.View.Extended.FormDialogTransaction;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.assac.controldelubricantes.Util.Utils.byteArrayToHexInt;
import static com.assac.controldelubricantes.Util.Utils.byteArrayToHexInt2;
import static com.assac.controldelubricantes.Util.Utils.byteArrayToHexIntGeneral;
import static com.assac.controldelubricantes.Util.Utils.byteArrayToHexString;
import static com.assac.controldelubricantes.Util.Utils.byteArrayToHexString2;
import static com.assac.controldelubricantes.Util.Utils.byteArrayToHexString3;
import static com.assac.controldelubricantes.Util.Utils.hexToAscii;
import static com.assac.controldelubricantes.Util.Utils.isBetween;

public class EmbeddedPtclWifi extends Fragment implements EmbeddedWifiListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ViewGroup rootView;

    private String IPLocal= "";

    private LocationManager ubicacion;

    //variables prueba con handler
    private int indiceBombaRead;

    DataFormEntity DataFormEntity;

    private byte[] bufferTemporal = new byte[300];
    Handler handlerSocket;
    final int handlerState = 0;
    //public static  String SERVER_IP = "192.168.1.98";
    public static  String SERVER_IP = "192.168.1.9";
    //public static  String SERVER_IP = "192.168.4.22";
    public static  int SERVER_PORT = 2230;

    private ClientTCPThread clientTCPThread;

    //Datos usados para la recepción
    private int indByte=0;
    private int longitudTemp=0;
    int longitudTramaRecepcion = 0;
    private int[] bufferRecepcion = new int[500];


    private boolean validarWIFI = true;
    private boolean conexSocket=true;
    private int tiempoEspera=0;


    byte[] bufferTransmision= new byte[300];

    //variables para la recepción de bombas activas
    int numBombas=0;
    List<TransactionEntity> hoseEntities = new ArrayList<>();
    List<Hose> hoseMasters = new ArrayList<>();

    private String pintarBytes="";

    private NetworkUtil networkUtil;

    //private String SSID="TP-LINK_AP_F2D8";
    private String SSID="MOVISTAR_1B9E";
    //private String SSID="EMBEDDED";
    private String Password="123456789";
    //private String Password="6XGE8bA5Ka8oRqzhkfCm";

    private ViewGroup layout;

    List<LayoutHoseEntity> layoutsHose;

    LinearLayout lyStatusComunication;

    //Trabajar con las mangueras
    LinearLayout ly_cuadrante;
    LinearLayout ly_cuadrante_estado_disponible;
    LinearLayout ly_cuadrante_estado_llamando;
    LinearLayout ly_cuadrante_estado_abasteciendo;

    LinearLayout ly_nombre_manguera;
    LinearLayout ly_nombre_producto;
    TextView txt_producto;
    TextView txt_nombre;
    ImageView iv_estado_abastecimiento;
    TextView txt_estado_abastecimiento;

    TextView txt_placa;
    TextView txt_conductor;

    TextView txt_galones;
    TextView txt_ultimo_ticket;

    TextView txt_ultimo_galon_p2;
    TextView txt_ultimo_ticket_p2;

    CRUDOperations crudOperations;

    private PrinterBluetooth printerBluetooth;

    //determina la ejecución o no del hilo clientTCPThread
    public volatile boolean running;

    //determina si se puede cambiar a otro fragment
    public volatile boolean changeFragment=true;

    private AlertDialog dialog;
    private CustomProgressDialog mDialog;
    private Dialog dialogShowData;

    private MainListener mainListener;

    Handler handlerDialog = new Handler();

    public FormDialogTransaction formDialogTransaction = null;

    byte[] ResponseDataDevice;

    public int direccionResponse=0;
    public int numeroBombaResponse=0;
    public String comentarioResponse="";

    //Animación de pestañeo
    private AnimationDrawable rocketAnimationCommunication;

    LinearLayout lySyncTransactions;
    TextView tvFecha, tvHora, tvBateria, tvTituloBateria, tvTemperature;

    Thread thread;
    Handler handler = new Handler();

    Thread threadBateria;
    Handler handlerBateria = new Handler();

    //TRANSACCIONES PENDIENTES
    int primeraTransaccionAlmacenada, ultimaTransaccionAlmacenada, ticketAlmacenadoAPP, ticketASolicitar, ticketsLeidos;
    List<Integer> listTicketsPending;

    private TextView tvNombreOperador;

    public EmbeddedPtclWifi() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_embedded_ptcl_wifi, container, false);
        //initComponent();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponent();
        clientTCPThread =new ClientTCPThread();
        clientTCPThread.execute("");
    }

    private void initComponent() {

        running=true;

        layout = (ViewGroup) rootView.findViewById(R.id.LayoutContentHoses);

        layoutsHose = new ArrayList<>();


        handlerSocket = new Handler() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            public void handleMessage(android.os.Message msg) {

                if (msg.what == handlerState) {
                    //if message is what we want
                    if(recepciontTwoEasyFuel((byte[])msg.obj,msg.arg1)){           //Modificar metodo para nuevo protocolo
                        changeFragment=false;
                        procesarTramaEasyFuel();
                        //String mostrar = armarMensajeMuestra();

                        //tv1.append("\n"+pintarBytes + "\n" +mostrar);
                        //Log.d("Daviddd", "Pasooo");
                        //Toast.makeText(rootView.getContext(),byteArrayToHexString(bufferRecepcion,longitudTemp) + "\n",Toast.LENGTH_LONG);
                    }
                }
            }
        };

        printerBluetooth = new PrinterBluetooth();
        networkUtil= new NetworkUtil(rootView.getContext());
        crudOperations = new CRUDOperations(new MyDatabase(getContext()));
        mDialog = new CustomProgressDialog(rootView.getContext());
        dialogShowData = new Dialog(rootView.getContext());

        tvNombreOperador = (TextView) rootView.findViewById(R.id.tvNombreOperador);
        tvNombreOperador.setText(PreferencesHelper.getNameUserSession(getActivity()));

        lyStatusComunication = (LinearLayout) rootView.findViewById(R.id.lyStatusComunication);
        tvFecha = (TextView) rootView.findViewById(R.id.tvFecha);
        tvHora = (TextView) rootView.findViewById(R.id.tvHora);
        actualizarTiempo();

        tvBateria = (TextView) rootView.findViewById(R.id.tvBateria);
        tvTituloBateria = (TextView) rootView.findViewById(R.id.tvTituloBateria);
        actualizaBateria();

        tvTemperature = (TextView) rootView.findViewById(R.id.tvTemperature);

        lySyncTransactions = (LinearLayout) rootView.findViewById(R.id.lySyncTransactions);

        lySyncTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateStatusHose()){
                    mainListener.showProgressDialog("Recuperando Transacciones Pendientes.");
                    ticketAlmacenadoAPP = crudOperations.getLastTicketTransaction();
                    clientTCPThread.write(EmbeddedPtcl.b_ext_solicitar_info_transacciones);
                }else
                    mostrarMensajeUsuario("Hay mangueras abasteciendose");


            }
        });

    }

    private void actualizarTiempo(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(!running)
                        break;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                            tvFecha.setText(date.substring(0,10));
                            tvHora.setText(date.substring(11));
                        }
                    });
                    try{
                        Thread.sleep(1000);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void actualizaBateria(){
        threadBateria = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(!running)
                        break;
                    handlerBateria.post(new Runnable() {
                        @Override
                        public void run() {
                            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                            Intent batteryStatus = getActivity().registerReceiver(null, ifilter);

                            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                            int battery = (int)((level / (float)scale)*100);
                            tvBateria.setText(battery+" %");


                            //Determinar si està cargando el dispositivo
                            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                    status == BatteryManager.BATTERY_STATUS_FULL;

                            int colorBattery =R.color.md_charged_assac;

                            if(isCharging){
                                colorBattery=R.color.md_charged_assac;
                            }else{
                                if (isBetween(battery, 50, 100)) {
                                    colorBattery=R.color.md_high_assac;
                                } else if (isBetween(battery, 20, 49)) {
                                    colorBattery=R.color.md_medium_level_assac;
                                } else {
                                    colorBattery=R.color.md_low_level_assac;
                                }
                            }

                            tvTituloBateria.setTextColor(ContextCompat.getColor(getActivity(),colorBattery));
                            tvBateria.setTextColor(ContextCompat.getColor(getActivity(),colorBattery));

                        }
                    });
                    try{
                        Thread.sleep(10000);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        threadBateria.start();
    }

    public void mostrarMensajeUsuario(final String mensaje){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                //tv1.setText(mensaje);
                Toast.makeText(rootView.getContext(), mensaje,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ClientTCPThread extends AsyncTask<String, String, Boolean> {
        //public String SERVER_IP = "192.168.1.15";
        //public int SERVER_PORT = 2230;
        private OutputStream mBufferOut;

        // used to read messages from the server
        private InputStream mBufferIn;
        private Socket wifiSocket = null;
        private boolean mRun=true;
        public int longitud;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... message) {
            boolean result = false;

            while(conexSocket&&running) {
                if ( clientTCPThread.isCancelled()) break;
                try {
                    Thread.sleep(tiempoEspera);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tiempoEspera=0000;
                while (validarWIFI&&running) {
                    if ( clientTCPThread.isCancelled()) break;
                    try {
                        Thread.sleep(tiempoEspera);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    tiempoEspera = 4000;
                    if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getContext(),
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getContext(),
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            validateLocation();
                            //validarWIFI=false;
                        }
                    } else {
                        validateLocation();
                        //validarWIFI=false;
                        //boolean connect= connectToHotspot("EMBEDDED DEMO", "123456789");
                    }
                }


                try {
                    SocketAddress sockaddr = new InetSocketAddress(SERVER_IP, SERVER_PORT);
                    wifiSocket = new Socket();

                    Log.d("TCP Client", "C: Connecting...");
                    wifiSocket.connect(sockaddr, 5000);

                    mostrarMensajeUsuario("Se conectó al Socket con éxito.");
                    //conexSocket=false;
                    try {
                        lyStatusComunication.setBackgroundResource(R.drawable.bg_status_communication);
                        rocketAnimationCommunication = (AnimationDrawable) lyStatusComunication.getBackground();
                        rocketAnimationCommunication.start();
                        Log.d("TCP Client", "C: Connectado..");
                        int bytes;
                        //sends the message to the server
                        mBufferOut = wifiSocket.getOutputStream();

                        //receives the message which the server sends back
                        mBufferIn = wifiSocket.getInputStream();

                        //in this while the client listens for the messages sent by the server
                        int tamBytes = 0;
                        while (mRun&&running) {
                            if ( clientTCPThread.isCancelled()) break;
                            bufferTemporal = new byte[300];
                            if (!wifiSocket.isClosed()) {
                                bytes = 0;
                                //mBufferIn.read(lenBytes, 0, 10);
                                bytes = mBufferIn.read(bufferTemporal);

                                handlerSocket.obtainMessage(handlerState, bytes, -1, bufferTemporal).sendToTarget();

                            }

                        }
                    } catch (IOException e) {

                        result = true;
                        mRun = false;

                        rocketAnimationCommunication.stop();
                        lyStatusComunication.setBackgroundResource(R.drawable.bg_para_pulsos_0);
                        mostrarMensajeUsuario("Se perdió la conexión con el socket del servidor");
                        Log.d("TCP Client", "C: Se perdió conexión");

                        validarWIFI=true;
                        conexSocket=true;
                        return result;
                    } catch (Exception e) {
                        result = true;
                        Log.e("TCP", "S: Error", e);
                    } finally {
                        mRun = false;
                        wifiSocket.close();
                        wifiSocket=null;
                        mBufferIn=null;
                        mBufferOut=null;
                        Log.d("ADP Client", "C: Se perdió conexión");
                    }
                } catch (UnknownHostException e) {
                    Log.e("TCP", e.getMessage(), e);
                } catch (IOException e) {
                    mostrarMensajeUsuario("No se logró establecer conexión con el socket del servidor");
                    Log.d("TCP Client", "C: No se pudo conectar");
                    tiempoEspera=4000;
                    conexSocket=true;
                } catch (Exception e) {
                    Log.e("TCP", "C: Error", e);
                }finally {
                    validarWIFI=true;
                }
            }

            if(running==false){
                /*try {
                    wifiSocket.close();
                } catch (IOException e) {
                    Log.v("Error ",e.getMessage());
                }*/
                Log.v("AsyncTask", "onPostExecute: Completed.");
            }

            //onPostExecute(result);
            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //response received from server
            Log.d("test", "response " + values[0]);
            //process server response here....
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                Log.i("AsyncTask", "onPostExecute: Completed with an Error.");
                //Toast.makeText(rootView.getContext(), , Toast.LENGTH_SHORT).show();
                //mostrarMensajeUsuario("No se logro establecer conexión con el socket del servidor");
                //clientTCPThread.doInBackground();
                //new ClientTCPThread().execute();

                if (!isCancelled() && running == true) {
                    validarWIFI=true;
                    crearNuevoSocket();
                }

            } else {
                Log.i("AsyncTask", "onPostExecute: Completed.");
            }

        }

        public void write(int opcode) {
            bufferTransmision = new byte[300];
            longitud=0;
            switch(opcode){
                //TRAMA DE CONFIGURACION
                case EmbeddedPtcl.b_ext_configuracion:
                    longitud = EmbeddedPtcl.aceptarTramaConfiguracion(bufferTransmision, bufferRecepcion[3],bufferRecepcion[4],bufferRecepcion[5]);
                    Log.v("", "" + "Longitud   "+ longitud);
                    Log.v("", "" + "Transmision   "+ byteArrayToHexString(bufferTransmision,0x0b));
                    break;
                //ACK TRUE
                case EmbeddedPtcl.b_ext_cambio_estado:
                    if(bufferRecepcion[5]!=0x03){
                        longitud = EmbeddedPtcl.ackWifi(bufferTransmision,bufferRecepcion[3],bufferRecepcion[4],bufferRecepcion[5],bufferRecepcion[6],bufferRecepcion[7],1,0);
                        Log.v("", "" + "Longitud   "+ longitud);
                        Log.v("", "" + "Transmision   " + byteArrayToHexString(bufferTransmision,0x0c));
                    }
                    break;
                //ACK FALSE
                case EmbeddedPtcl.b_ext_ack_false:
                    if(bufferRecepcion[5]!=0x03){
                        longitud = EmbeddedPtcl.ackWifi(bufferTransmision,bufferRecepcion[3],bufferRecepcion[4],bufferRecepcion[5],bufferRecepcion[6],bufferRecepcion[7],0,0);
                        Log.v("", "" + "Longitud   "+ longitud);
                        Log.v("", "" + "Transmision   " + byteArrayToHexString(bufferTransmision,0x0c));
                    }
                    break;
                //ENVIAR DATA DE FORMULARIO COMPLETADO
                case EmbeddedPtcl.b_ext_enviar_data_formulario:
                    longitud=EmbeddedPtcl.enviarDataFormularioWifi(bufferTransmision,direccionResponse,1,8,DataFormEntity);
                    Log.v("", "" + "Longitud   "+ longitud);
                    Log.v("", "" + "Transmision   FormData" + byteArrayToHexString(bufferTransmision,0x7c));
                    break;
                //ENVIAR DATA DE NFC LEÍDO
                case EmbeddedPtcl.b_ext_enviar_data_nfc:
                    longitud=EmbeddedPtcl.enviarDataNFC(bufferTransmision,ResponseDataDevice,direccionResponse,2,numeroBombaResponse,comentarioResponse);
                    Log.v("", "" + "Longitud   "+ longitud);
                    Log.v("", "" + "Transmision   NFCData" + byteArrayToHexString(bufferTransmision,longitud));
                    break;
                case EmbeddedPtcl.b_ext_solicitar_info_transacciones:
                    longitud=EmbeddedPtcl.solicitarInfo(bufferTransmision,1,4,2);
                    Log.v("", "" + "Longitud   "+ longitud);
                    Log.v("", "" + "Transmision SolicitarInfo" + byteArrayToHexString(bufferTransmision,longitud));
                    break;
                case EmbeddedPtcl.b_ext_solicitar_transaccion_for_ticket:
                    longitud=EmbeddedPtcl.solicitarTransactionForTicket(bufferTransmision,1,3,3,ticketASolicitar);
                    Log.v("", "" + "Longitud   "+ longitud);
                    Log.v("", "" + "Transmision TransacciónPorTicket" + byteArrayToHexString(bufferTransmision,longitud));
                    break;
            }


            if (wifiSocket.isConnected()) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            mBufferOut.write(bufferTransmision,0,longitud);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            //timerNoComunicacion(1500);
        }

        @Override
        protected void onCancelled() {
            //clientTCPThread=null;
            super.onCancelled();
            cancel(true);
            try {
                if(wifiSocket!=null)
                    wifiSocket.close();
            } catch (IOException e) {
                Log.i("AsyncTask", "onPostExecute: Completed.");
            }
        }
    }

    public void crearNuevoSocket(){
        //clientTCPThread.cancel(true);
        clientTCPThread = null;

        if (clientTCPThread == null) {
            clientTCPThread = new ClientTCPThread();
            clientTCPThread.execute("");
        }

    }

    //Verificación de Ubicación
    private void validateLocation(){

        if(estadoGPS()){
            connectWIFI();
        }else{
            mostrarMensajeUsuario("Debe activar su ubicación.");
        }
    }

    //validar si la ubicación está activa
    private boolean estadoGPS(){
        ubicacion = (LocationManager)rootView.getContext().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if(!ubicacion.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return false;
        }

        return true;
    }

    //CONEXIÓN A LA RED EMBEEDED
    private void connectWIFI(){
        validarWIFI = networkUtil.connectToHotspot(SSID,Password);
        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }*/
        if(validarWIFI){
            if (networkUtil.isOutputWifi()) {
                validarWIFI=false;
                //connectSocket();
            } else {
                //Toast.makeText(getActivity(), "Debe desactivar sus datos móviles.", Toast.LENGTH_LONG).show();
                mostrarMensajeUsuario("Debe desactivar sus datos móviles.");
            }
        }else{
            validarWIFI=true;
            mostrarMensajeUsuario(networkUtil.mensajeError);
        }
    }

    //INTERPRETAR TRAMA RECIBIDA
    private boolean recepciontTwoEasyFuel(byte[] temporal, int cantidad){

        boolean hasPacket=false;
        int datoTemporal;
        int i;

        if(cantidad>0) {
            for(i=0;i<cantidad;i++){
                datoTemporal=0xFF&temporal[i];

                if(indByte==0){
                    if(datoTemporal==0x02){
                        bufferRecepcion[indByte]=datoTemporal;
                        indByte++;
                    }
                    else{
                        longitudTemp=0;
                        indByte=0;
                    }
                }
                else{
                    if(indByte==1 || indByte==2){
                        if(indByte==1){
                            longitudTemp = datoTemporal;
                            bufferRecepcion[indByte]=datoTemporal;
                            indByte++;
                        }
                        else{
                            if(indByte==2){
                                longitudTemp = (longitudTemp)|(((short)datoTemporal)<<8);
                                //GUARDAR LONGITUD
                                longitudTramaRecepcion = longitudTemp;
                                //Log.v("", "" + "Longitud   " + longitudTramaRecepcion);
                                bufferRecepcion[indByte] = datoTemporal;
                                indByte++;
                                if((longitudTemp>500)||(longitudTemp<8)){
                                    longitudTemp = 0;
                                    indByte = 0;
                                }
                            }
                        }
                    }
                    else{
                        if(indByte>2){
                            bufferRecepcion[indByte] = datoTemporal;
                            indByte++;
                            if(indByte==longitudTemp){
                                if(datoTemporal==0x03){
                                    hasPacket=true;
                                    pintarBytes = byteArrayToHexString2(bufferRecepcion,longitudTemp);
                                    Log.v("", "" + "Longitud   " + longitudTemp);
                                    Log.v("", "" + "Recepcion   " + pintarBytes);
                                    //mMessageListener.messageReceived(byteArrayToHexString(bufferRecepcion,longitudTemp) + "\n");
                                    //mConnectedThread.write(EmbeddedPtcl.b_ext_configuracion);
                                    //showText(""+ byteArrayToHexString(bufferRecepcion,longitudTemp));
                                    indByte=0;
                                    longitudTemp=0;
                                }
                                else{
                                    indByte=0;
                                    longitudTemp=0;
                                    bufferRecepcion = new int[500];
                                    //ALERTAR QUE LA TRAMA ES INVÁLIDA
                                }
                            }
                        }
                    }
                }

            }
        }
        else{
            //NO SE RECIBIERON DATOS
            Log.v("recepcionEasyFuel", "NO SE RECIBIERON DATOS");
        }

        return hasPacket;
    }

    //PROCESAR TRAMA COMPLETA RECIBIDA
    public void procesarTramaEasyFuel(){
        int indiceLayoutHose=0;
        int[] arrayIdBomba = new int[1];
        int idBomba = 0;
        switch (bufferRecepcion[4]){

            //CAMBIO DE ESTADO
            case 0x01:
                indiceLayoutHose=0;
                //Capturar idBomba
                arrayIdBomba[0] = bufferRecepcion[7];
                idBomba = Integer.parseInt(byteArrayToHexIntGeneral(arrayIdBomba,1));

                for(int i=0;i<hoseEntities.size();i++ ){
                    if(hoseEntities.get(i).idBomba==idBomba) {
                        indiceLayoutHose = i;
                        break;
                    }
                }

                switch (bufferRecepcion[5]){
                    case 0x01:
                        //Cambio de estado
                        if(hoseEntities.size() > 0){
                            Log.v("Estadoo",""+bufferRecepcion[8]);
                            layoutsHose.get(indiceLayoutHose).setState(bufferRecepcion[8]);
                            cambioEstado(indiceLayoutHose, bufferRecepcion[8]);
                        }
                        clientTCPThread.write(EmbeddedPtcl.b_ext_cambio_estado);//0x01
                        break;
                    case 0x02:
                        //Estado actual de Mangueras
                        layoutsHose.get(indiceLayoutHose).setState(bufferRecepcion[8]);
                        cambioEstado(indiceLayoutHose, bufferRecepcion[8]);
                        clientTCPThread.write(EmbeddedPtcl.b_ext_cambio_estado);//0x01
                        break;
                    case 0x03:
                        //Cambio de Pulsos
                        switch (bufferRecepcion[9]){
                            case 0x01:
                                // FLUJO
                                if(hoseEntities.size() > 0){
                                    cambiarPulsos(indiceLayoutHose);
                                }
                                break;
                            case 0x02:
                                // INICIO NO FLUJO
                                if(hoseEntities.size() > 0){
                                    cambiarEstadoSinFlujo(indiceLayoutHose);
                                }
                                break;
                            case 0x03:
                                // NO FLUJO
                                if(hoseEntities.size() > 0){
                                    cambiarEstadoCierreHook(indiceLayoutHose);
                                }
                                break;
                        }
                        break;
                    case 0x04:
                        if(layoutsHose.get(indiceLayoutHose).getState() == 2){
                            //Vehiculo Leido
                            if(hoseEntities.size() > 0){
                                cambiarPlaca(indiceLayoutHose);
                            }

                            clientTCPThread.write(EmbeddedPtcl.b_ext_cambio_estado);//0x01
                        }else
                            clientTCPThread.write(EmbeddedPtcl.b_ext_ack_false);


                        break;
                    case 0x05:
                        //Conductor Leido

                        clientTCPThread.write(EmbeddedPtcl.b_ext_cambio_estado);//0x01
                        break;
                    case 0x06:
                        //Operador Leido
                        break;
                    case 0x07:
                        //Ultima transaccion
                        if(hoseEntities.size() > 0){
                            llenarDatosTransaccion(hoseEntities.get(indiceLayoutHose),indiceLayoutHose);
                        }
                        clientTCPThread.write(EmbeddedPtcl.b_ext_cambio_estado);//0x01
                        break;
                    case 0x08:

                        //Completar Datos - Abrir formulario
                        if(longitudTramaRecepcion==12){
                            //Recibir ok de embedded
                            //layoutsHose.get(indiceLayoutHose).formDialogTransaction=null;
                        }else{
                            if(layoutsHose.get(indiceLayoutHose).getState() == 1){
                                clientTCPThread.write(EmbeddedPtcl.b_ext_cambio_estado);
                                if(layoutsHose.get(indiceLayoutHose).formDialogTransaction==null) {
                                    mostrarFormulario(hoseEntities.get(indiceLayoutHose), indiceLayoutHose);
                                }
                            }else
                                clientTCPThread.write(EmbeddedPtcl.b_ext_ack_false);

                        }

                        //formDialogTransaction.escribirNumeroTicket(Integer.parseInt(hoseEntities.get(indiceLayoutHose).numeroTransaccion));
                        break;
                }
                break;

            //RESULTADO DE SOLICITAR VALIDACIONES - EMBEDDED ENVIA ACK
            case 0x02:
                indiceLayoutHose=0;
                //Capturar idBomba
                arrayIdBomba[0] = bufferRecepcion[7];
                idBomba = Integer.parseInt(byteArrayToHexIntGeneral(arrayIdBomba,1));

                for(int i=0;i<hoseEntities.size();i++ ){
                    if(hoseEntities.get(i).idBomba==idBomba) {
                        indiceLayoutHose = i;
                        break;
                    }
                }

                switch(bufferRecepcion[5]){
                    case 0x04:
                    case 0x05:
                    case 0x06:
                        break;
                }
                break;

            //RECUPERAR TRANSACCIONES
            case 0x03:
                switch(bufferRecepcion[5]){
                    case 0x03:
                        //proceso transacción

                        if(bufferRecepcion[6]!=0){
                            arrayIdBomba[0] = bufferRecepcion[22];
                            idBomba = Integer.parseInt(byteArrayToHexIntGeneral(arrayIdBomba,1));

                            for(int i=0;i<hoseEntities.size();i++ ){
                                if(hoseEntities.get(i).idBomba==idBomba) {
                                    indiceLayoutHose = i;
                                    break;
                                }
                            }

                            procesarTransaccionPendiente(hoseEntities.get(indiceLayoutHose));
                        }

                        //valido si es que habrá otra transacción. Si hay, incremento
                        if(ticketsLeidos == listTicketsPending.size()){
                            mainListener.dismissProgressDialog();
                        }else if (ticketsLeidos < listTicketsPending.size()){
                            ticketASolicitar=listTicketsPending.get(ticketsLeidos);
                            clientTCPThread.write(EmbeddedPtcl.b_ext_solicitar_transaccion_for_ticket);
                            ticketsLeidos++;
                        }

                        break;
                }

                break;

            //RESULTADO DE SOLICITAR INFORMACIÓN TRANSACCIONES - EMBEDDED ENVIA ACK
            case 0x04:
                switch(bufferRecepcion[5]){
                    case 0x01:
                        break;
                    case 0x02:
                        procesarInfoTransacciones();
                        break;
                }
                break;

            //TRAMA DE CONFIGURACION
            case 0x06:
                if(bufferRecepcion[5] == 0x01 || bufferRecepcion[5] == 0x02){
                    // PROCESAR TRAMA DE CONFIGURACION
                    llenarDatosConfiguracion();
                    agregarImagenEstaciones();
                    insertarMaestrosSQLite();
                    clientTCPThread.write(EmbeddedPtcl.b_ext_configuracion); //0x06
                }
                break;

            case 0x07:
                switch(bufferRecepcion[5]){
                    case 0x01:
                        break;
                }
                break;

            //RESULTADO DE VALIDACIONES
            case 0x08:
                indiceLayoutHose=0;
                //Capturar idBomba
                arrayIdBomba[0] = bufferRecepcion[7];
                idBomba = Integer.parseInt(byteArrayToHexIntGeneral(arrayIdBomba,1));

                for(int i=0;i<hoseEntities.size();i++ ){
                    if(hoseEntities.get(i).idBomba==idBomba) {
                        indiceLayoutHose = i;
                        break;
                    }
                }

                switch(bufferRecepcion[5]){
                    case 0x04:
                        if(layoutsHose.get(indiceLayoutHose).formDialogTransaction!=null ) {
                            if(!layoutsHose.get(indiceLayoutHose).formDialogTransaction.validarVehiculo)
                                validarVehiculo(indiceLayoutHose);
                        }
                        clientTCPThread.write(EmbeddedPtcl.b_ext_cambio_estado);//0x01
                        break;
                    case 0x05:
                        if(layoutsHose.get(indiceLayoutHose).formDialogTransaction!=null) {
                            validarConductor(indiceLayoutHose);
                        }
                        clientTCPThread.write(EmbeddedPtcl.b_ext_cambio_estado);//0x01
                        break;
                }
                break;

        }
        changeFragment=true;
    }

    //PROCESAR TRAMA DE CONFIGURACIÓN
    public void llenarDatosConfiguracion(){

        hoseEntities = new ArrayList<>();
        //**********************************************************

        //Capturar Numero primera transaccion

        //Capturar último Abastecimiento

        //Capturar Nombre Host Bluetooth/WIFI
        int[] tramaNombreEmbedded = new int[16];
        int c = 0;
        for(int i = 14; i<= 29;  i++){
            tramaNombreEmbedded[c] = bufferRecepcion[i];
            c++;
        }

        Log.v("NOMBRE EMBEDDED", "" + hexToAscii(byteArrayToHexString(tramaNombreEmbedded,tramaNombreEmbedded.length)));
        //**********************************************************
        //Capturar MAC Bluetooth/WIFI
        int[] tramaMACWIFI= new int[6];
        c = 0;
        for(int i = 30; i<= 35;  i++){
            tramaMACWIFI[c] = bufferRecepcion[i];
            c++;
        }
        Log.v("MAC EMBEDDED", "" + hexToAscii(byteArrayToHexString(tramaMACWIFI,tramaMACWIFI.length)));


        //**********************************************************
        //Capturar PIN Host
        int[] pinHostEmbedded = new int[11];
        c = 0;
        for(int i = 36; i<= 46;  i++){
            pinHostEmbedded[c] = bufferRecepcion[i];
            c++;
        }

        Log.v("PIN HOST EMBEDDED", "" + hexToAscii(byteArrayToHexString(pinHostEmbedded,pinHostEmbedded.length)));

        //**********************************************************
        //Capturar Nro Bombas
        int[] numeroBombas = new int[1];
        numeroBombas[0] = bufferRecepcion[47];
        numBombas = Integer.parseInt(byteArrayToHexIntGeneral(numeroBombas,1));

        Log.v("NUMERO BOMBAS", "" + numBombas);

        //**********************************************************
        //int pIinicial = 42;

        //obtener solo IDbombas
        int[] idBombas = new int[numBombas];

        int pIinicial = 48;
        for(int i = 0; i< numBombas;  i++){
            idBombas[i] = bufferRecepcion[pIinicial];
            Log.v("ID BOMBA "+ i, "" + idBombas[i]);
            pIinicial+=1;
        }

        int auxIdBomba=0;
        for(int i = 0; i< numBombas; i++){
            Hose hose  = new Hose();
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setEstadoRegistro("P");
            //**********************************************************
            //Capturar idBomba
            int[] idBomba = new int[1];
            idBomba[0] = bufferRecepcion[pIinicial];
            auxIdBomba=Integer.parseInt((byteArrayToHexIntGeneral(idBomba,1)));

            Log.v("INICIO", "--------------");

            Log.v("BOMBA "+ i, "ID- " + auxIdBomba);
            transactionEntity.setIdBomba(auxIdBomba);
            hose.setHoseNumber(auxIdBomba);
            hose.setHardwareId(1);
            hose.setLastTicket(0);
            hose.setFuelQuantity(0.0);

            //**********************************************************
            //Capturar idProducto
            int[] idProducto = new int[1];
            idProducto[0] = bufferRecepcion[pIinicial + 1];
            Log.v("BOMBA "+ i, "IDPRODUCTO- " + idProducto[0]);
            transactionEntity.setIdProducto(Integer.parseInt(byteArrayToHexIntGeneral(idProducto,1)));

            //**********************************************************
            //Capturar cantidadDecimales
            int[] cantidadDecimales = new int[1];
            cantidadDecimales[0] = bufferRecepcion[pIinicial + 2];
            Log.v("BOMBA "+ i, "DECIMALES- " + cantidadDecimales[0]);
            transactionEntity.setCantidadDecimales(Integer.parseInt(byteArrayToHexIntGeneral(cantidadDecimales,1)));
            //**********************************************************
            //Capturar nombreManguera
            int[] nombreManguera = new int[10];
            int contadorMangueraInicial = pIinicial + 3;
            int contadorMangueraFinal = contadorMangueraInicial + 9;
            int contadorIteracionesManguera = 0;
            for(int j=contadorMangueraInicial; j<=contadorMangueraFinal; j++){
                nombreManguera[contadorIteracionesManguera] = bufferRecepcion[j];
                contadorIteracionesManguera ++;
            }
            String manguera=hexToAscii(byteArrayToHexString(nombreManguera,nombreManguera.length)).trim();
            Log.v("BOMBA "+ i, "MANGUERA- " + manguera);
            transactionEntity.setNombreManguera(manguera);
            hose.setHoseName(manguera);

            //Capturar nombreProducto
            int[] nombreProducto = new int[10];
            int contadorProductoInicial = pIinicial + 13;
            int contadorProductoFinal = contadorProductoInicial + 9;
            int contadorIteracionesProducto = 0;
            for(int k=contadorProductoInicial; k<=contadorProductoFinal; k++){
                nombreProducto[contadorIteracionesProducto] = bufferRecepcion[k];
                contadorIteracionesProducto ++;
            }
            String producto = hexToAscii(byteArrayToHexString(nombreProducto,nombreProducto.length));
            Log.v("BOMBA "+ i, "PRODUCTO- " + manguera);
            transactionEntity.setNombreProducto(producto.trim());
            hose.setNameProduct(producto);

            hoseEntities.add(transactionEntity);
            hoseMasters.add(hose);
            pIinicial = pIinicial + 23;
            Log.v("FIN", "--------------");
        }

    }

    public void procesarInfoTransacciones(){
        int c, ticketAux;

        int[] tramaPrimeraTransaccion = new int[3];
        c = 0;
        for (int i = 9; i >= 7; i--) {
            tramaPrimeraTransaccion[c] = bufferRecepcion[i];
            c++;
        }
        primeraTransaccionAlmacenada = byteArrayToHexInt(tramaPrimeraTransaccion, tramaPrimeraTransaccion.length);

        int[] tramaUltimaTransaccion = new int[3];
        c = 0;
        for (int i = 12; i >= 10; i--) {
            tramaUltimaTransaccion[c] = bufferRecepcion[i];
            c++;
        }
        ultimaTransaccionAlmacenada = byteArrayToHexInt(tramaUltimaTransaccion, tramaUltimaTransaccion.length);

        listTicketsPending = new ArrayList<>();
        ticketsLeidos=0;

        if( (ultimaTransaccionAlmacenada-ticketAlmacenadoAPP) == 0){
            ticketAux=crudOperations.getFirstTicketTransactionDaysAgo();
            if(ticketAux!=0){
                for(int i=ticketAux+1;i<ticketAlmacenadoAPP;i++){
                    if(crudOperations.getTransaction(""+i,"P").size()==0) {
                        Log.v("aa0", "Entroo2");
                        listTicketsPending.add(i);
                    }
                }

            }else{
                mainListener.dismissProgressDialog();
            }

        }else{
            for(int i=ticketAlmacenadoAPP+1;i<=ultimaTransaccionAlmacenada;i++){
                //ticketASolicitar=i;
                //clientTCPThread.write(EmbeddedPtcl.b_ext_solicitar_transaccion_for_ticket);
                listTicketsPending.add(i);
            }
        }

        if(listTicketsPending.size()!=0) {
            ticketASolicitar=listTicketsPending.get(0);
            clientTCPThread.write(EmbeddedPtcl.b_ext_solicitar_transaccion_for_ticket);
            ticketsLeidos++;
        }else
            mainListener.dismissProgressDialog();


    }

    //INSERTAR MAESTROS SQLITE
    public void insertarMaestrosSQLite(){
        Maestros maestros = new Maestros();
        maestros.setHoses(hoseMasters);
        crudOperations.clearTablesMasters();
        crudOperations.insertMastersSQLite(maestros);
    }

    //GENERAR MANGUERAS DINAMICAMENTE SEGÚN CANTIDAD DE MANGUERAS


    /*
    @SuppressLint("InlinedApi")
    private void agregarImagenEstaciones()
    {
        layout.removeAllViews();
        layoutsHose = new ArrayList<>();
        int id=0;
        int idBomba=0;
        for(int i=0;i <hoseEntities.size();i++){
            idBomba =hoseEntities.get(i).idBomba;
            LayoutInflater inflater = LayoutInflater.from(rootView.getContext());
            id = R.layout.layout_lubricante_hose;
            LinearLayout hoseLayout = (LinearLayout) inflater.inflate(id, null, false);
            final int finalIdBomba = idBomba;
            txt_nombre = hoseLayout.findViewById(R.id.txt_nombre);
            //txt_nombre.append(""+finalIdBomba);
            txt_nombre.setText(""+hoseEntities.get(i).nombreManguera.trim());
            ly_cuadrante = hoseLayout.findViewById(R.id.ly_cuadrante);
            txt_producto = hoseLayout.findViewById(R.id.txt_producto);
            txt_producto.setText(hoseEntities.get(i).getNombreProducto().trim());


            //ly_cuadrante.setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        int indiceLayoutHose=0;
            //        for(int i=0;i<hoseEntities.size();i++ ){
            //            if(hoseEntities.get(i).idBomba==finalIdBomba) {
            //                indiceLayoutHose = i;
            //                break;
            //            }
            //        }

            //      if(layoutsHose.get(indiceLayoutHose).formDialogTransaction!= null)
            //            layoutsHose.get(indiceLayoutHose).formDialogTransaction.show();

            //    }
            //});

            layout.addView(hoseLayout,i);

            LayoutHoseEntity layoutHose = new LayoutHoseEntity(hoseLayout,idBomba);
            layoutsHose.add(layoutHose);

        }

    }
    */

    @SuppressLint("InlinedApi")
    private void agregarImagenEstaciones()
    {
        layout.removeAllViews();
        layoutsHose = new ArrayList<>();
        int id=0;
        int idBomba=0;
        LinearLayout parent = null;
        int c=0;
        for(int i=0;i <hoseEntities.size();i++){
            if(i%3==0) {
                c=0;
                parent = new LinearLayout(rootView.getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 2, 0, 12);
                parent.setLayoutParams(layoutParams);
                parent.setOrientation(LinearLayout.HORIZONTAL);

            }

            idBomba =hoseEntities.get(i).idBomba;
            LayoutInflater inflater = LayoutInflater.from(rootView.getContext());
            id = R.layout.layout_lubricante_hose2;
            LinearLayout hoseLayout = (LinearLayout) inflater.inflate(id, null, false);
            final int finalIdBomba = idBomba;
            txt_nombre = hoseLayout.findViewById(R.id.txt_nombre);
            //txt_nombre.append(""+finalIdBomba);
            txt_nombre.setText(""+hoseEntities.get(i).nombreManguera.trim());
            ly_cuadrante = hoseLayout.findViewById(R.id.ly_cuadrante);
            txt_producto = hoseLayout.findViewById(R.id.txt_producto);
            txt_producto.setText(hoseEntities.get(i).getNombreProducto().trim());

            parent.addView(hoseLayout);

            LayoutHoseEntity layoutHose = new LayoutHoseEntity(hoseLayout,idBomba);
            layoutsHose.add(layoutHose);

            c++;

            if(c==3)
                layout.addView(parent);

        }

        if(c%3!=0)
            layout.addView(parent);

    }

    //Cambio de estados de Abastecimiento
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cambioEstado(int indiceLayoutHose, int pEstadoActual){

        if(pEstadoActual == EmbeddedPtcl.v_estado_sin_abastecimiento){
            cambiarEstadoSinAbastecimiento(indiceLayoutHose);
        } else if(pEstadoActual == EmbeddedPtcl.v_estado_inicia_abastecimiento){
            cambiarEstadoIniciaAbastecimiento(indiceLayoutHose);
        }else if(pEstadoActual == EmbeddedPtcl.v_estado_autoriza_abastecimiento){
            cambiarEstadoAutorizarAbastecimiento(indiceLayoutHose);
        } else if(pEstadoActual == EmbeddedPtcl.v_estado_termina_abastecimiento){
            cambiarEstadoTerminaAbastecimiento(indiceLayoutHose);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cambiarEstadoSinAbastecimiento(int indiceLayoutHose){

        if(layoutsHose.get(indiceLayoutHose).formDialogTransaction!=null){
            if(layoutsHose.get(indiceLayoutHose).formDialogTransaction.isShowing())
                layoutsHose.get(indiceLayoutHose).formDialogTransaction.dismiss();

            layoutsHose.get(indiceLayoutHose).formDialogTransaction=null;
        }

        ly_cuadrante = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante);
        ly_cuadrante_estado_disponible = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_disponible);
        ly_cuadrante_estado_llamando = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_llamando);
        ly_cuadrante_estado_abasteciendo = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_abasteciendo);

        //ly_nombre_manguera = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_manguera);
        //ly_nombre_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_producto);

        txt_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_producto);
        txt_nombre = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_nombre);
        iv_estado_abastecimiento = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.iv_estado_abastecimiento);

        //txt_estado_abastecimiento = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_estado_abastecimiento);

        txt_placa = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_placa);
        txt_conductor= layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_conductor);

        txt_galones = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_galones);
        txt_ultimo_ticket = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket);

        txt_ultimo_galon_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_galon_p2);
        txt_ultimo_ticket_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket_p2);

        ly_cuadrante.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_cuadrante_manguera_estado_pausa));

        //ly_nombre_manguera.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_disponible));
        //ly_nombre_producto.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_disponible));

        iv_estado_abastecimiento.setImageResource(R.drawable.ic_lubricant_disponible);

        //txt_estado_abastecimiento.setText("Disponible");
        //txt_estado_abastecimiento.setTextColor(ContextCompat.getColor(getActivity(),R.color.md_yellow_assac));


        ly_cuadrante_estado_disponible.setVisibility(View.VISIBLE);
        ly_cuadrante_estado_llamando.setVisibility(View.GONE);
        ly_cuadrante_estado_abasteciendo.setVisibility(View.GONE);


        ly_cuadrante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutsHose.get(indiceLayoutHose).formDialogTransaction!= null)
                    layoutsHose.get(indiceLayoutHose).formDialogTransaction.show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cambiarEstadoIniciaAbastecimiento(int indiceLayoutHose){

        if(layoutsHose.get(indiceLayoutHose).formDialogTransaction!=null){
            if(layoutsHose.get(indiceLayoutHose).formDialogTransaction.isShowing())
                layoutsHose.get(indiceLayoutHose).formDialogTransaction.dismiss();
        }else{
            DataFormEntity dataFormEntity = new DataFormEntity();
            dataFormEntity.setTag("");

            dataFormEntity.setIndiceBomba(indiceLayoutHose);
            dataFormEntity.setNumeroBomba(hoseEntities.get(indiceLayoutHose).idBomba);
            dataFormEntity.setNombreManguera(hoseEntities.get(indiceLayoutHose).nombreManguera);
            dataFormEntity.setDireccion(bufferRecepcion[3]);
            layoutsHose.get(indiceLayoutHose).formDialogTransaction=new FormDialogTransaction(getActivity(),R.style.AppThemeAssacFormularioDialog, dataFormEntity, this);
        }

        ly_cuadrante = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante);
        ly_cuadrante_estado_disponible = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_disponible);
        ly_cuadrante_estado_llamando = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_llamando);
        ly_cuadrante_estado_abasteciendo = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_abasteciendo);

        //ly_nombre_manguera = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_manguera);
        //ly_nombre_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_producto);

        txt_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_producto);
        txt_nombre = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_nombre);
        iv_estado_abastecimiento = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.iv_estado_abastecimiento);

        //txt_estado_abastecimiento = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_estado_abastecimiento);

        txt_placa = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_placa);
        txt_conductor= layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_conductor);

        txt_galones = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_galones);
        txt_ultimo_ticket = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket);

        txt_ultimo_galon_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_galon_p2);
        txt_ultimo_ticket_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket_p2);

        ly_cuadrante.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_cuadrante_manguera_estado_llamando));

        //ly_nombre_manguera.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_llamando));
        //ly_nombre_producto.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_llamando));

        iv_estado_abastecimiento.setImageResource(R.drawable.ic_lubricant_llamando);

        //txt_estado_abastecimiento.setText("Llamando");
        //txt_estado_abastecimiento.setTextColor(ContextCompat.getColor(getActivity(),R.color.md_orange_assac));


        txt_galones.setText("0.00");
        txt_placa.setText("-");
        txt_conductor.setText("-");

        ly_cuadrante_estado_disponible.setVisibility(View.GONE);
        ly_cuadrante_estado_llamando.setVisibility(View.VISIBLE);
        ly_cuadrante_estado_abasteciendo.setVisibility(View.GONE);

        ly_cuadrante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutsHose.get(indiceLayoutHose).formDialogTransaction!= null) {
                    layoutsHose.get(indiceLayoutHose).formDialogTransaction.show();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cambiarEstadoAutorizarAbastecimiento(int indiceLayoutHose){
        ly_cuadrante = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante);
        ly_cuadrante_estado_disponible = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_disponible);
        ly_cuadrante_estado_llamando = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_llamando);
        ly_cuadrante_estado_abasteciendo = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_abasteciendo);

        //ly_nombre_manguera = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_manguera);
        //ly_nombre_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_producto);

        txt_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_producto);
        txt_nombre = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_nombre);
        iv_estado_abastecimiento = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.iv_estado_abastecimiento);

        //txt_estado_abastecimiento = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_estado_abastecimiento);

        txt_placa = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_placa);
        txt_conductor= layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_conductor);

        txt_galones = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_galones);
        txt_ultimo_ticket = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket);

        txt_ultimo_galon_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_galon_p2);
        txt_ultimo_ticket_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket_p2);

        ly_cuadrante.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_cuadrante_manguera_estado_autorizado));

        //ly_nombre_manguera.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_autorizado));
        //ly_nombre_producto.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_autorizado));


        iv_estado_abastecimiento.setImageResource(R.drawable.ic_lubricant_abasteciendo);

        //txt_estado_abastecimiento.setText("Abasteciendo");
        //txt_estado_abastecimiento.setTextColor(ContextCompat.getColor(getActivity(),R.color.md_green_assac));


        txt_galones.setText("0.00");

        ly_cuadrante_estado_disponible.setVisibility(View.GONE);
        ly_cuadrante_estado_llamando.setVisibility(View.GONE);
        ly_cuadrante_estado_abasteciendo.setVisibility(View.VISIBLE);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cambiarEstadoTerminaAbastecimiento(int indiceLayoutHose){
        layoutsHose.get(indiceLayoutHose).formDialogTransaction=null;

        ly_cuadrante = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante);
        ly_cuadrante_estado_disponible = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_disponible);
        ly_cuadrante_estado_llamando = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_llamando);
        ly_cuadrante_estado_abasteciendo = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_abasteciendo);

        //ly_nombre_manguera = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_manguera);
        //ly_nombre_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_producto);

        txt_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_producto);
        txt_nombre = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_nombre);
        iv_estado_abastecimiento = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.iv_estado_abastecimiento);

        //txt_estado_abastecimiento = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_estado_abastecimiento);

        txt_placa = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_placa);
        txt_conductor= layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_conductor);

        txt_galones = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_galones);
        txt_ultimo_ticket = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket);

        txt_ultimo_galon_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_galon_p2);
        txt_ultimo_ticket_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket_p2);

        ly_cuadrante.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_cuadrante_manguera_estado_pausa));

        //ly_nombre_manguera.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_disponible));
        //ly_nombre_producto.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_disponible));


        txt_galones.setText(hoseEntities.get(indiceLayoutHose).getVolumen());
        iv_estado_abastecimiento.setImageResource(R.drawable.ic_lubricant_disponible);
        txt_ultimo_galon_p2.setText(hoseEntities.get(indiceLayoutHose).getVolumen());

        //txt_estado_abastecimiento.setText("Disponible");
        //txt_estado_abastecimiento.setTextColor(ContextCompat.getColor(getActivity(),R.color.md_yellow_assac));

        ly_cuadrante_estado_disponible.setVisibility(View.GONE);
        ly_cuadrante_estado_llamando.setVisibility(View.GONE);
        ly_cuadrante_estado_abasteciendo.setVisibility(View.VISIBLE);

        ly_cuadrante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutsHose.get(indiceLayoutHose).formDialogTransaction!= null)
                    layoutsHose.get(indiceLayoutHose).formDialogTransaction.show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cambiarEstadoSinFlujo(int indiceLayoutHose){

        ly_cuadrante = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante);
        ly_cuadrante_estado_disponible = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_disponible);
        ly_cuadrante_estado_llamando = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_llamando);
        ly_cuadrante_estado_abasteciendo = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_abasteciendo);

        //ly_nombre_manguera = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_manguera);
        //ly_nombre_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_producto);

        txt_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_producto);
        txt_nombre = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_nombre);
        iv_estado_abastecimiento = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.iv_estado_abastecimiento);

        //txt_estado_abastecimiento = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_estado_abastecimiento);

        txt_placa = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_placa);
        txt_conductor= layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_conductor);

        txt_galones = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_galones);
        txt_ultimo_ticket = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket);

        txt_ultimo_galon_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_galon_p2);
        txt_ultimo_ticket_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket_p2);

        ly_cuadrante.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_cuadrante_manguera_estado_sin_flujo));

        //ly_nombre_manguera.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_sin_flujo));
        //ly_nombre_producto.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_sin_flujo));

        iv_estado_abastecimiento.setImageResource(R.drawable.ic_lubricante_no_flujo1);

        //txt_estado_abastecimiento.setText("No Flujo");
        //txt_estado_abastecimiento.setTextColor(ContextCompat.getColor(getActivity(),R.color.md_red_assac));


        ly_cuadrante_estado_disponible.setVisibility(View.GONE);
        ly_cuadrante_estado_llamando.setVisibility(View.GONE);
        ly_cuadrante_estado_abasteciendo.setVisibility(View.VISIBLE);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cambiarEstadoCierreHook(int indiceLayoutHose){

        ly_cuadrante = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante);
        ly_cuadrante_estado_disponible = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_disponible);
        ly_cuadrante_estado_llamando = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_llamando);
        ly_cuadrante_estado_abasteciendo = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante_estado_abasteciendo);

        //ly_nombre_manguera = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_manguera);
        //ly_nombre_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_nombre_producto);

        txt_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_producto);
        txt_nombre = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_nombre);
        iv_estado_abastecimiento = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.iv_estado_abastecimiento);

        //txt_estado_abastecimiento = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_estado_abastecimiento);

        txt_placa = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_placa);
        txt_conductor= layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_conductor);

        txt_galones = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_galones);
        txt_ultimo_ticket = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket);

        txt_ultimo_galon_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_galon_p2);
        txt_ultimo_ticket_p2 = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket_p2);

        ly_cuadrante.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_cuadrante_manguera_estado_sin_flujo));

        //ly_nombre_manguera.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_sin_flujo));
        //ly_nombre_producto.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_para_fondo_titulo_sin_flujo));

        iv_estado_abastecimiento.setImageResource(R.drawable.ic_lubricante_no_flujo1);

        //txt_estado_abastecimiento.setText("Cierre Hook");
        //txt_estado_abastecimiento.setTextColor(ContextCompat.getColor(getActivity(),R.color.md_red_assac));

    }

    public void procesarTransaccionPendiente(TransactionEntity entity){

        int contador;

        //**********************************************************
        //Capturar Nro Transaccion
        int[] tramaNroTransaccion = new int[3];
        contador = 0;
        for (int i = 11; i <= 13; i++) {
            tramaNroTransaccion[contador] = bufferRecepcion[i];
            contador++;
        }
        String nroTransaccion = "" + byteArrayToHexInt(tramaNroTransaccion, tramaNroTransaccion.length);
        entity.setNumeroTransaccion(nroTransaccion);
        Log.v("Nro. Transacción", entity.getNumeroTransaccion());

        if (!nroTransaccion.equals("0")){

            //**********************************************************
            //Capturar Fecha Inicio
            int[] tramaFechaInicio = new int[1];
            tramaFechaInicio[0] = bufferRecepcion[14];
            String dia = "" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);
            tramaFechaInicio[0] = bufferRecepcion[15];
            String mes = "" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);
            tramaFechaInicio[0] = bufferRecepcion[16];
            String anio = "20" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);
            tramaFechaInicio[0] = bufferRecepcion[19];
            String hora = "" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);
            tramaFechaInicio[0] = bufferRecepcion[18];
            String minuto = "" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);
            tramaFechaInicio[0] = bufferRecepcion[17];
            String segundo = "" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);

            //String fechaInicio = dia + "-" + mes + "-" + anio + "";
            String fechaInicio = anio + "-" + mes + "-" + dia + "";
            String horaInicio = hora + ":" + minuto + ":" + segundo;
            //fechaInicio = "" + hexToAscii(byteArrayToHexString(tramaFechaInicio,tramaFechaInicio.length));
            entity.setFechaInicio(fechaInicio);
            entity.setHoraInicio(horaInicio);
            Log.v("Fecha Inicio", entity.getFechaInicio());
            Log.v("Hora Inicio", entity.getHoraInicio());

            //**********************************************************
            //Capturar Turno
            int[] tramaTurno = new int[2];
            contador = 0;
            for (int i = 20; i <= 21; i++) {
                tramaTurno[contador] = bufferRecepcion[i];
                contador++;
            }
            int turno = byteArrayToHexInt2(tramaTurno, tramaTurno.length);
            entity.setTurno(turno);
            Log.v("Turno", "" + entity.getTurno());

            //**********************************************************
            //Numero de Tanque
            int[] tramaNumeroTanque = new int[1];
            contador = 0;
            for (int i = 23; i <= 23; i++) {
                tramaNumeroTanque[contador] = bufferRecepcion[i];
                contador++;
            }

            int numeroTanque = Integer.parseInt(byteArrayToHexIntGeneral(tramaNumeroTanque, 1));
            entity.setNumeroTanque(numeroTanque);

            Log.v("Tanque", "" + entity.getNumeroTanque());

            //**********************************************************
            //Tipo de Vehiculo
            int[] tramaTipoVehiculo = new int[1];
            contador = 0;
            for (int i = 24; i <= 24; i++) {
                tramaTipoVehiculo[contador] = bufferRecepcion[i];
                contador++;
            }

            int tipoVehiculo = Integer.parseInt(byteArrayToHexIntGeneral(tramaTipoVehiculo, 1));
            entity.setTipoVehiculo(tipoVehiculo);

            Log.v("Tipo Vehiculo", "" + entity.getTipoVehiculo());


            //**********************************************************
            //Capturar IdVehiculo
            int[] tramaIdVehiculo = new int[8];
            contador = 0;
            for (int i = 25; i <= 32; i++) {
                tramaIdVehiculo[contador] = bufferRecepcion[i];
                contador++;
            }
            String IdVehiculo = hexToAscii(byteArrayToHexString(tramaIdVehiculo, tramaIdVehiculo.length));
            entity.setIdVehiculo(IdVehiculo);

            Log.v("IdVehiculo", entity.getIdVehiculo());

            //**********************************************************
            //Capturar Placa
            int[] tramaPlaca = new int[10];
            contador = 0;
            for (int i = 33; i <= 42; i++) {
                tramaPlaca[contador] = bufferRecepcion[i];
                contador++;
            }
            String placa = hexToAscii(byteArrayToHexString(tramaPlaca, tramaPlaca.length));
            entity.setPlaca(placa);

            Log.v("Placa", entity.getPlaca());

            //**********************************************************
            //Capturar Kilometro

            int[] tramaKilometroParteEntera = new int[3];
            contador = 0;
            for (int i = 43; i <= 45; i++) {
                tramaKilometroParteEntera[contador] = bufferRecepcion[i];
                contador++;
            }
            int kilometroParteEntera = byteArrayToHexInt(tramaKilometroParteEntera, tramaKilometroParteEntera.length);

            int[] tramaKilometroParteDecimal = new int[1];
            contador = 0;
            for (int i = 46; i <= 46; i++) {
                tramaKilometroParteDecimal[contador] = bufferRecepcion[i];
                contador++;
            }
            int kilometroParteDecimal = byteArrayToHexInt(tramaKilometroParteDecimal, tramaKilometroParteDecimal.length);

            //double kilometro = kilometroParteEntera + kilometroParteDecimal*0.1;
            double kilometro = Double.valueOf(kilometroParteEntera + "." + kilometroParteDecimal);
            entity.setKilometraje("" + kilometro);

            Log.v("Kilometro", "" + kilometro);

            //**********************************************************
            //Capturar Horometro

            int[] tramaHorometroParteEntera = new int[3];
            contador = 0;
            for (int i = 47; i <= 49; i++) {
                tramaHorometroParteEntera[contador] = bufferRecepcion[i];
                contador++;
            }
            int horometroParteEntera = byteArrayToHexInt(tramaHorometroParteEntera, tramaHorometroParteEntera.length);

            int[] tramaHorometroParteDecimal = new int[1];
            contador = 0;
            for (int i = 50; i <= 50; i++) {
                tramaKilometroParteDecimal[contador] = bufferRecepcion[i];
                contador++;
            }

            double horometro = 0.0;

            if (tramaHorometroParteDecimal[0] == 255) {
                horometro = horometroParteEntera / 10D;
            } else {
                int horometroParteDecimal = byteArrayToHexInt(tramaKilometroParteDecimal, tramaKilometroParteDecimal.length);
                //horometro = horometroParteEntera + horometroParteDecimal*0.1;
                horometro = Double.valueOf(horometroParteEntera + "." + horometroParteDecimal);
            }

            entity.setHorometro("" + horometro);

            Log.v("Horometro", "" + horometro);

            //**********************************************************
            //Capturar IdConductor
            int[] tramaIdConductor = new int[8];
            contador = 0;
            for (int i = 51; i <= 58; i++) {
                tramaIdConductor[contador] = bufferRecepcion[i];
                contador++;
            }
            String IdConductor = hexToAscii(byteArrayToHexString(tramaIdConductor, tramaIdConductor.length));
            entity.setIdConductor(IdConductor);

            Log.v("IdConductor", entity.getIdConductor());

            //**********************************************************
            //Capturar IdOperador
            int[] tramaIdOperador = new int[8];
            contador = 0;
            for (int i = 59; i <= 66; i++) {
                tramaIdOperador[contador] = bufferRecepcion[i];
                contador++;
            }
            String IdOperador = hexToAscii(byteArrayToHexString(tramaIdOperador, tramaIdOperador.length));
            entity.setIdOperador(IdOperador);

            Log.v("IdOperador", entity.getIdOperador());

            //**********************************************************
            //Tipo de Transacción
            int[] tramaTipoTransaccion = new int[1];
            contador = 0;
            for (int i = 67; i <= 67; i++) {
                tramaTipoTransaccion[contador] = bufferRecepcion[i];
                contador++;
            }

            int tipoTransaccion = Integer.parseInt(byteArrayToHexIntGeneral(tramaTipoTransaccion, 1));
            entity.setTipoTransaccion(tipoTransaccion);

            Log.v("Tipo Transacción", "" + entity.getTipoTransaccion());

            //**********************************************************
            //Capturar Latitud
            int[] tramaLatitud = new int[12];
            contador = 0;
            for (int i = 68; i <= 79; i++) {
                tramaLatitud[contador] = bufferRecepcion[i];
                contador++;
            }
            String latitud = hexToAscii(byteArrayToHexString(tramaLatitud, tramaLatitud.length));
            entity.setLatitud(latitud);

            Log.v("Latitud", entity.getLatitud());

            //**********************************************************
            //Capturar Longitud
            int[] tramaLongitud = new int[12];
            contador = 0;
            for (int i = 80; i <= 91; i++) {
                tramaLongitud[contador] = bufferRecepcion[i];
                contador++;
            }
            String longitud = hexToAscii(byteArrayToHexString(tramaLongitud, tramaLongitud.length));
            entity.setLongitud(longitud);

            Log.v("Longitud", entity.getLongitud());

            //**********************************************************
            //Capturar Tipo Error Pre-Seteo
            int[] tramaTipoErrorPreseteo = new int[1];
            contador = 0;
            for (int i = 92; i <= 92; i++) {
                tramaTipoErrorPreseteo[contador] = bufferRecepcion[i];
                contador++;
            }

            int tipoErrorPreseteo = Integer.parseInt(byteArrayToHexIntGeneral(tramaTipoErrorPreseteo, 1));
            entity.setTipoErrorPreseteo(tipoErrorPreseteo);

            Log.v("Tipo Error Preseteo", "" + entity.getTipoErrorPreseteo());

            //**********************************************************
            //Capturar Volumen Autorizado
            int[] tramaVolumenAutorizado = new int[2];
            contador = 0;
            for (int i = 93; i <= 94; i++) {
                tramaVolumenAutorizado[contador] = bufferRecepcion[i];
                contador++;
            }
            int volumenAutorizado = byteArrayToHexInt2(tramaVolumenAutorizado, tramaVolumenAutorizado.length);
            entity.setVolumenAutorizado(volumenAutorizado);
            Log.v("Volumen Autorizado", "" + entity.getVolumenAutorizado());

            //**********************************************************
            //Capturar Volumen Aceptado
            int[] tramaVolumenAceptado = new int[2];
            contador = 0;
            for (int i = 95; i <= 96; i++) {
                tramaVolumenAceptado[contador] = bufferRecepcion[i];
                contador++;
            }
            int volumenAceptado = byteArrayToHexInt2(tramaVolumenAceptado, tramaVolumenAceptado.length);
            entity.setVolumenAceptado(volumenAceptado);
            Log.v("Volumen Aceptado", "" + entity.getVolumenAceptado());

            //**********************************************************
            //Capturar Código Cliente
            int[] tramaCodigoCliente = new int[2];
            contador = 0;
            for (int i = 102; i <= 103; i++) {
                tramaCodigoCliente[contador] = bufferRecepcion[i];
                contador++;
            }
            int codigoCliente = byteArrayToHexInt2(tramaCodigoCliente, tramaCodigoCliente.length);
            entity.setCodigoCliente(codigoCliente);
            Log.v("Codigo Cliente", "" + entity.getCodigoCliente());

            //**********************************************************
            //Capturar codigo area
            int[] tramaCodigoArea = new int[1];
            contador = 0;
            for (int i = 104; i <= 104; i++) {
                tramaCodigoArea[contador] = bufferRecepcion[i];
                contador++;
            }

            int CodigoArea = Integer.parseInt(byteArrayToHexIntGeneral(tramaCodigoArea, 1));
            entity.setCodigoArea(CodigoArea);

            Log.v("Codigo Area", "" + entity.getCodigoArea());

            //**********************************************************
            //Capturar tipo TAG
            int[] tramaTipoTAG = new int[1];
            contador = 0;
            for (int i = 105; i <= 105; i++) {
                tramaTipoTAG[contador] = bufferRecepcion[i];
                contador++;
            }

            int tipoTAG = Integer.parseInt(byteArrayToHexIntGeneral(tramaTipoTAG, 1));
            entity.setTipoTag(tipoTAG);

            Log.v("Tipo TAG", "" + entity.getTipoTag());

            //**********************************************************
            //Capturar Volumen Abastecido
            int[] tramaVolumen = new int[9];
            contador = 0;
            for (int i = 106; i <= 114; i++) {
                tramaVolumen[contador] = bufferRecepcion[i];
                contador++;
            }
            String volumen = "" + hexToAscii(byteArrayToHexString(tramaVolumen, tramaVolumen.length));
            String[] parts = volumen.split("\\.");
            if (parts.length > 1) {
                volumen = parts[0] + "." + parts[1].substring(0, (0 + entity.getCantidadDecimales()));
            }

            entity.setVolumen(volumen);

            Log.v("Volumen Abastecido", entity.getVolumen());

            //**********************************************************
            //Capturar Temperatura
            int[] tramaTemperatura = new int[5];
            contador = 0;
            for (int i = 115; i <= 119; i++) {
                tramaTemperatura[contador] = bufferRecepcion[i];
                contador++;
            }

            String temperatura = "" + hexToAscii(byteArrayToHexString(tramaTemperatura,tramaTemperatura.length));
            entity.setTemperatura(temperatura);
            temperatura =  temperatura.substring(0,temperatura.length()-1);
            //**********************************************************
            //Capturar Fecha Fin
            int[] tramaFechaFin = new int[1];
            tramaFechaFin[0] = bufferRecepcion[120];
            String diaFin = "" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);
            tramaFechaFin[0] = bufferRecepcion[121];
            String mesFin = "" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);
            tramaFechaFin[0] = bufferRecepcion[122];
            String anioFin = "20" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);
            tramaFechaFin[0] = bufferRecepcion[125];
            String horaFin = "" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);
            tramaFechaFin[0] = bufferRecepcion[124];
            String minutoFin = "" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);
            tramaFechaFin[0] = bufferRecepcion[123];
            String segundoFin = "" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);

            //String fechaFin = diaFin + "-" + mesFin + "-" + anioFin + "";
            String fechaFin = anioFin + "-" + mesFin + "-" + diaFin + "";
            String horaFin1 = horaFin + ":" + minutoFin + ":" + segundoFin;
            //fechaInicio = "" + hexToAscii(byteArrayToHexString(tramaFechaInicio,tramaFechaInicio.length));
            entity.setFechaFin(fechaFin);
            entity.setHoraFin(horaFin1);

            Log.v("Fecha Fin", entity.getFechaFin());
            Log.v("Hora Fin", entity.getHoraFin());

            //**********************************************************
            //Capturar Tipo de Cierre
            int[] tramaTipoCierre = new int[1];
            contador = 0;
            for (int i = 126; i <= 126; i++) {
                tramaTipoCierre[contador] = bufferRecepcion[i];
                contador++;
            }

            int tipoCierre = Integer.parseInt(byteArrayToHexIntGeneral(tramaTipoCierre, 1));
            entity.setTipoCierre(tipoCierre);

            Log.v("Tipo Cierre", "" + entity.getTipoCierre());
        }else{
            entity.setFechaInicio("00-00-0000");
            entity.setHoraInicio("00:00:00");
            entity.setTurno(0);
            entity.setNumeroTanque(0);
            entity.setTipoVehiculo(0);
            entity.setIdVehiculo("0");
            entity.setPlaca("");
            entity.setKilometraje("0.00");
            entity.setHorometro("0.00");
            entity.setIdConductor("0");
            entity.setIdOperador("0");
            entity.setTipoTransaccion(0);
            entity.setLatitud("");
            entity.setLongitud("");
            entity.setTipoErrorPreseteo(0);
            entity.setVolumenAutorizado(0);
            entity.setVolumenAceptado(0);
            entity.setCodigoCliente(0);
            entity.setCodigoArea(0);
            entity.setTipoTag(0);
            entity.setVolumen("0.00");
            entity.setTemperatura("");
            entity.setFechaFin("00-00-0000");
            entity.setHoraFin("00:00:00");
            entity.setTipoCierre(0);
        }

        if (!nroTransaccion.equals("0"))
            guardarTransaccionBD(entity);


    }

    //CARGAR ULTIMA TRANSACCION POR LUBRICANTE
    public void llenarDatosTransaccion(TransactionEntity entity, int indiceLayoutHose) {
        //**********************************************************
        int contador = 0;
        Log.v("INICIO", "**********************************************************");

        Log.v("Bomba", String.valueOf(entity.getIdBomba()));
        //**********************************************************
        //EstadoActual
        int[] tramaEstadoActual = new int[1];
        contador = 0;
        for (int i = 8; i <= 8; i++) {
            tramaEstadoActual[contador] = bufferRecepcion[i];
            contador++;
        }
        //String estadoActual = byteArrayToHexString(tramaEstadoActual,tramaEstadoActual.length);
        int estadoActual = Integer.parseInt(byteArrayToHexIntGeneral(tramaEstadoActual, 1));
        entity.setEstadoActual(estadoActual);

        Log.v("Estado", String.valueOf(entity.getEstadoActual()));
        cambioEstado(indiceLayoutHose, estadoActual);

        //**********************************************************
        //Capturar Nro Transaccion
        int[] tramaNroTransaccion = new int[3];
        contador = 0;
        for (int i = 9; i <= 11; i++) {
            tramaNroTransaccion[contador] = bufferRecepcion[i];
            contador++;
        }
        String nroTransaccion = "" + byteArrayToHexInt(tramaNroTransaccion, tramaNroTransaccion.length);
        entity.setNumeroTransaccion(nroTransaccion);
        Log.v("Nro. Transacción", entity.getNumeroTransaccion());

        if (!nroTransaccion.equals("0")){

            //**********************************************************
            //Capturar Fecha Inicio
            int[] tramaFechaInicio = new int[1];
            tramaFechaInicio[0] = bufferRecepcion[12];
            String dia = "" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);
            tramaFechaInicio[0] = bufferRecepcion[13];
            String mes = "" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);
            tramaFechaInicio[0] = bufferRecepcion[14];
            String anio = "20" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);
            tramaFechaInicio[0] = bufferRecepcion[17];
            String hora = "" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);
            tramaFechaInicio[0] = bufferRecepcion[16];
            String minuto = "" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);
            tramaFechaInicio[0] = bufferRecepcion[15];
            String segundo = "" + byteArrayToHexString(tramaFechaInicio, tramaFechaInicio.length);

            //String fechaInicio = dia + "-" + mes + "-" + anio + "";
            String fechaInicio = anio + "-" + mes + "-" + dia + "";
            String horaInicio = hora + ":" + minuto + ":" + segundo;
            //fechaInicio = "" + hexToAscii(byteArrayToHexString(tramaFechaInicio,tramaFechaInicio.length));
            entity.setFechaInicio(fechaInicio);
            entity.setHoraInicio(horaInicio);
            Log.v("Fecha Inicio", entity.getFechaInicio());
            Log.v("Hora Inicio", entity.getHoraInicio());

            //**********************************************************
            //Capturar Turno
            int[] tramaTurno = new int[2];
            contador = 0;
            for (int i = 18; i <= 19; i++) {
                tramaTurno[contador] = bufferRecepcion[i];
                contador++;
            }
            int turno = byteArrayToHexInt2(tramaTurno, tramaTurno.length);
            entity.setTurno(turno);
            Log.v("Turno", "" + entity.getTurno());

            //**********************************************************
            //Numero de Tanque
            int[] tramaNumeroTanque = new int[1];
            contador = 0;
            for (int i = 21; i <= 21; i++) {
                tramaNumeroTanque[contador] = bufferRecepcion[i];
                contador++;
            }

            int numeroTanque = Integer.parseInt(byteArrayToHexIntGeneral(tramaNumeroTanque, 1));
            entity.setNumeroTanque(numeroTanque);

            Log.v("Tanque", "" + entity.getNumeroTanque());

            //**********************************************************
            //Tipo de Vehiculo
            int[] tramaTipoVehiculo = new int[1];
            contador = 0;
            for (int i = 22; i <= 22; i++) {
                tramaTipoVehiculo[contador] = bufferRecepcion[i];
                contador++;
            }

            int tipoVehiculo = Integer.parseInt(byteArrayToHexIntGeneral(tramaTipoVehiculo, 1));
            entity.setTipoVehiculo(tipoVehiculo);

            Log.v("Tipo Vehiculo", "" + entity.getTipoVehiculo());


            //**********************************************************
            //Capturar IdVehiculo
            int[] tramaIdVehiculo = new int[8];
            contador = 0;
            for (int i = 23; i <= 30; i++) {
                tramaIdVehiculo[contador] = bufferRecepcion[i];
                contador++;
            }
            String IdVehiculo = hexToAscii(byteArrayToHexString(tramaIdVehiculo, tramaIdVehiculo.length));
            entity.setIdVehiculo(IdVehiculo);

            Log.v("IdVehiculo", entity.getIdVehiculo());

            //**********************************************************
            //Capturar Placa
            int[] tramaPlaca = new int[10];
            contador = 0;
            for (int i = 31; i <= 40; i++) {
                tramaPlaca[contador] = bufferRecepcion[i];
                contador++;
            }
            String placa = hexToAscii(byteArrayToHexString(tramaPlaca, tramaPlaca.length));
            entity.setPlaca(placa);

            Log.v("Placa", entity.getPlaca());

            //**********************************************************
            //Capturar Kilometro

            int[] tramaKilometroParteEntera = new int[3];
            contador = 0;
            for (int i = 41; i <= 43; i++) {
                tramaKilometroParteEntera[contador] = bufferRecepcion[i];
                contador++;
            }
            int kilometroParteEntera = byteArrayToHexInt(tramaKilometroParteEntera, tramaKilometroParteEntera.length);

            int[] tramaKilometroParteDecimal = new int[1];
            contador = 0;
            for (int i = 44; i <= 44; i++) {
                tramaKilometroParteDecimal[contador] = bufferRecepcion[i];
                contador++;
            }
            int kilometroParteDecimal = byteArrayToHexInt(tramaKilometroParteDecimal, tramaKilometroParteDecimal.length);

            //double kilometro = kilometroParteEntera + kilometroParteDecimal*0.1;
            double kilometro = Double.valueOf(kilometroParteEntera + "." + kilometroParteDecimal);
            entity.setKilometraje("" + kilometro);

            Log.v("Kilometro", "" + kilometro);

            //**********************************************************
            //Capturar Horometro

            int[] tramaHorometroParteEntera = new int[3];
            contador = 0;
            for (int i = 45; i <= 47; i++) {
                tramaHorometroParteEntera[contador] = bufferRecepcion[i];
                contador++;
            }
            int horometroParteEntera = byteArrayToHexInt(tramaHorometroParteEntera, tramaHorometroParteEntera.length);

            int[] tramaHorometroParteDecimal = new int[1];
            contador = 0;
            for (int i = 48; i <= 48; i++) {
                tramaKilometroParteDecimal[contador] = bufferRecepcion[i];
                contador++;
            }

            double horometro = 0.0;

            if (tramaHorometroParteDecimal[0] == 255) {
                horometro = horometroParteEntera / 10D;
            } else {
                int horometroParteDecimal = byteArrayToHexInt(tramaKilometroParteDecimal, tramaKilometroParteDecimal.length);
                //horometro = horometroParteEntera + horometroParteDecimal*0.1;
                horometro = Double.valueOf(horometroParteEntera + "." + horometroParteDecimal);
            }

            entity.setHorometro("" + horometro);

            Log.v("Horometro", "" + horometro);

            //**********************************************************
            //Capturar IdConductor
            int[] tramaIdConductor = new int[8];
            contador = 0;
            for (int i = 49; i <= 56; i++) {
                tramaIdConductor[contador] = bufferRecepcion[i];
                contador++;
            }
            String IdConductor = hexToAscii(byteArrayToHexString(tramaIdConductor, tramaIdConductor.length));
            entity.setIdConductor(IdConductor);

            Log.v("IdConductor", entity.getIdConductor());

            //**********************************************************
            //Capturar IdOperador
            int[] tramaIdOperador = new int[8];
            contador = 0;
            for (int i = 57; i <= 64; i++) {
                tramaIdOperador[contador] = bufferRecepcion[i];
                contador++;
            }
            String IdOperador = hexToAscii(byteArrayToHexString(tramaIdOperador, tramaIdOperador.length));
            entity.setIdOperador(IdOperador);

            Log.v("IdOperador", entity.getIdOperador());

            //**********************************************************
            //Tipo de Transacción
            int[] tramaTipoTransaccion = new int[1];
            contador = 0;
            for (int i = 65; i <= 65; i++) {
                tramaTipoTransaccion[contador] = bufferRecepcion[i];
                contador++;
            }

            int tipoTransaccion = Integer.parseInt(byteArrayToHexIntGeneral(tramaTipoTransaccion, 1));
            entity.setTipoTransaccion(tipoTransaccion);

            Log.v("Tipo Transacción", "" + entity.getTipoTransaccion());

            //**********************************************************
            //Capturar Latitud
            int[] tramaLatitud = new int[12];
            contador = 0;
            for (int i = 66; i <= 77; i++) {
                tramaLatitud[contador] = bufferRecepcion[i];
                contador++;
            }
            String latitud = hexToAscii(byteArrayToHexString(tramaLatitud, tramaLatitud.length));
            entity.setLatitud(latitud);

            Log.v("Latitud", entity.getLatitud());

            //**********************************************************
            //Capturar Longitud
            int[] tramaLongitud = new int[12];
            contador = 0;
            for (int i = 78; i <= 89; i++) {
                tramaLongitud[contador] = bufferRecepcion[i];
                contador++;
            }
            String longitud = hexToAscii(byteArrayToHexString(tramaLongitud, tramaLongitud.length));
            entity.setLongitud(longitud);

            Log.v("Longitud", entity.getLongitud());

            //**********************************************************
            //Capturar Tipo Error Pre-Seteo
            int[] tramaTipoErrorPreseteo = new int[1];
            contador = 0;
            for (int i = 90; i <= 90; i++) {
                tramaTipoErrorPreseteo[contador] = bufferRecepcion[i];
                contador++;
            }

            int tipoErrorPreseteo = Integer.parseInt(byteArrayToHexIntGeneral(tramaTipoErrorPreseteo, 1));
            entity.setTipoErrorPreseteo(tipoErrorPreseteo);

            Log.v("Tipo Error Preseteo", "" + entity.getTipoErrorPreseteo());

            //**********************************************************
            //Capturar Volumen Autorizado
            int[] tramaVolumenAutorizado = new int[2];
            contador = 0;
            for (int i = 91; i <= 92; i++) {
                tramaVolumenAutorizado[contador] = bufferRecepcion[i];
                contador++;
            }
            int volumenAutorizado = byteArrayToHexInt2(tramaVolumenAutorizado, tramaVolumenAutorizado.length);
            entity.setVolumenAutorizado(volumenAutorizado);
            Log.v("Volumen Autorizado", "" + entity.getVolumenAutorizado());

            //**********************************************************
            //Capturar Volumen Aceptado
            int[] tramaVolumenAceptado = new int[2];
            contador = 0;
            for (int i = 93; i <= 94; i++) {
                tramaVolumenAceptado[contador] = bufferRecepcion[i];
                contador++;
            }
            int volumenAceptado = byteArrayToHexInt2(tramaVolumenAceptado, tramaVolumenAceptado.length);
            entity.setVolumenAceptado(volumenAceptado);
            Log.v("Volumen Aceptado", "" + entity.getVolumenAceptado());

            //**********************************************************
            //Capturar Código Cliente
            int[] tramaCodigoCliente = new int[2];
            contador = 0;
            for (int i = 100; i <= 101; i++) {
                tramaCodigoCliente[contador] = bufferRecepcion[i];
                contador++;
            }
            int codigoCliente = byteArrayToHexInt2(tramaCodigoCliente, tramaCodigoCliente.length);
            entity.setCodigoCliente(codigoCliente);
            Log.v("Codigo Cliente", "" + entity.getCodigoCliente());

            //**********************************************************
            //Capturar codigo area
            int[] tramaCodigoArea = new int[1];
            contador = 0;
            for (int i = 102; i <= 102; i++) {
                tramaCodigoArea[contador] = bufferRecepcion[i];
                contador++;
            }

            int CodigoArea = Integer.parseInt(byteArrayToHexIntGeneral(tramaCodigoArea, 1));
            entity.setCodigoArea(CodigoArea);

            Log.v("Codigo Area", "" + entity.getCodigoArea());

            //**********************************************************
            //Capturar tipo TAG
            int[] tramaTipoTAG = new int[1];
            contador = 0;
            for (int i = 103; i <= 103; i++) {
                tramaTipoTAG[contador] = bufferRecepcion[i];
                contador++;
            }

            int tipoTAG = Integer.parseInt(byteArrayToHexIntGeneral(tramaTipoTAG, 1));
            entity.setTipoTag(tipoTAG);

            Log.v("Tipo TAG", "" + entity.getTipoTag());

            //**********************************************************
            //Capturar Volumen Abastecido
            int[] tramaVolumen = new int[9];
            contador = 0;
            for (int i = 104; i <= 112; i++) {
                tramaVolumen[contador] = bufferRecepcion[i];
                contador++;
            }
            String volumen = "" + hexToAscii(byteArrayToHexString(tramaVolumen, tramaVolumen.length));
            String[] parts = volumen.split("\\.");
            if (parts.length > 1) {
                volumen = parts[0] + "." + parts[1].substring(0, (0 + entity.getCantidadDecimales()));
            }

            entity.setVolumen(volumen);

            Log.v("Volumen Abastecido", entity.getVolumen());

            //**********************************************************
            //Capturar Temperatura
            int[] tramaTemperatura = new int[5];
            contador = 0;
            for (int i = 113; i <= 117; i++) {
                tramaTemperatura[contador] = bufferRecepcion[i];
                contador++;
            }

            String temperatura = "" + hexToAscii(byteArrayToHexString(tramaTemperatura,tramaTemperatura.length));
            entity.setTemperatura(temperatura);
            String unidad = temperatura.substring(temperatura.length()-1);
            temperatura =  temperatura.substring(0,temperatura.length()-1);

            tvTemperature.setText(temperatura +" °"+unidad);
            Log.v("Temperatura", entity.getTemperatura());
            //Log.v("Temperatura2",temperatura.substring());

            //**********************************************************
            //Capturar Fecha Fin
            int[] tramaFechaFin = new int[1];
            tramaFechaFin[0] = bufferRecepcion[118];
            String diaFin = "" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);
            tramaFechaFin[0] = bufferRecepcion[119];
            String mesFin = "" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);
            tramaFechaFin[0] = bufferRecepcion[120];
            String anioFin = "20" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);
            tramaFechaFin[0] = bufferRecepcion[123];
            String horaFin = "" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);
            tramaFechaFin[0] = bufferRecepcion[122];
            String minutoFin = "" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);
            tramaFechaFin[0] = bufferRecepcion[121];
            String segundoFin = "" + byteArrayToHexString(tramaFechaFin, tramaFechaFin.length);

            //String fechaFin = diaFin + "-" + mesFin + "-" + anioFin + "";
            String fechaFin = anioFin + "-" + mesFin + "-" + diaFin + "";
            String horaFin1 = horaFin + ":" + minutoFin + ":" + segundoFin;
            //fechaInicio = "" + hexToAscii(byteArrayToHexString(tramaFechaInicio,tramaFechaInicio.length));
            entity.setFechaFin(fechaFin);
            entity.setHoraFin(horaFin1);

            Log.v("Fecha Fin", entity.getFechaFin());
            Log.v("Hora Fin", entity.getHoraFin());

            //**********************************************************
            //Capturar Tipo de Cierre
            int[] tramaTipoCierre = new int[1];
            contador = 0;
            for (int i = 124; i <= 124; i++) {
                tramaTipoCierre[contador] = bufferRecepcion[i];
                contador++;
            }

            int tipoCierre = Integer.parseInt(byteArrayToHexIntGeneral(tramaTipoCierre, 1));
            entity.setTipoCierre(tipoCierre);

            Log.v("Tipo Cierre", "" + entity.getTipoCierre());
        }else{
            entity.setFechaInicio("00-00-0000");
            entity.setHoraInicio("00:00:00");
            entity.setTurno(0);
            entity.setNumeroTanque(0);
            entity.setTipoVehiculo(0);
            entity.setIdVehiculo("0");
            entity.setPlaca("");
            entity.setKilometraje("0.00");
            entity.setHorometro("0.00");
            entity.setIdConductor("0");
            entity.setIdOperador("0");
            entity.setTipoTransaccion(0);
            entity.setLatitud("");
            entity.setLongitud("");
            entity.setTipoErrorPreseteo(0);
            entity.setVolumenAutorizado(0);
            entity.setVolumenAceptado(0);
            entity.setCodigoCliente(0);
            entity.setCodigoArea(0);
            entity.setTipoTag(0);
            entity.setVolumen("0.00");
            entity.setTemperatura("");
            entity.setFechaFin("00-00-0000");
            entity.setHoraFin("00:00:00");
            entity.setTipoCierre(0);
        }


        txt_galones = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_galones);
        txt_ultimo_ticket = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket);
        txt_placa = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_placa);
        txt_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_producto);
        txt_ultimo_galon_p2 = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_galon_p2);
        txt_ultimo_ticket_p2= layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_ultimo_ticket_p2);

        txt_producto.setText(entity.getNombreProducto());
        txt_placa.setText(entity.getPlaca());
        txt_galones.setText(entity.getVolumen());
        txt_ultimo_galon_p2.setText(entity.getVolumen());
        txt_ultimo_ticket.setText(entity.getNumeroTransaccion());
        txt_ultimo_ticket_p2.setText(entity.getNumeroTransaccion());

        if (!nroTransaccion.equals("0"))
            guardarTransaccionBD(entity);

    }

    private void guardarTransaccionBD(TransactionEntity entity){
        List<TransactionEntity> lst = crudOperations.getTransactionByTransactionAndHose(entity.getNumeroTransaccion(),"P");
        int resultado = 0;
        if(lst.size() == 0){
            entity.setEstadoRegistro("P");
            resultado = crudOperations.addTransaction(entity);
            //imprimirTransaccion(entity);
        }
    }

    private void imprimirTransaccion(final TransactionEntity entity){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                //tv1.setText(mensaje);
                new ImpresionTicketAsync(entity,printerBluetooth,null,getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

    }

    private void mostrarFormulario(TransactionEntity entity, int indiceLayoutHose){

        DataFormEntity dataFormEntity = new DataFormEntity();

        dataFormEntity.setIndiceBomba(indiceLayoutHose);
        dataFormEntity.setNumeroBomba(entity.idBomba);
        dataFormEntity.setNombreManguera(entity.nombreManguera.trim());
        dataFormEntity.setDireccion(bufferRecepcion[3]);

        int[] tramaPlaca = new int[10];
        int c = 0;
        for(int i = 9; i<= 18;  i++){
            tramaPlaca[c] = bufferRecepcion[i];
            c++;
        }
        dataFormEntity.setPlaca(hexToAscii(byteArrayToHexString(tramaPlaca,tramaPlaca.length)).trim());

        switch (bufferRecepcion[19]){
            case EmbeddedPtcl.tag_vehiculo:
                dataFormEntity.setSolicitaConductor(false);
                dataFormEntity.setTag("A");
                break;
            case EmbeddedPtcl.tag_vehiculo_driver_id_v:
                dataFormEntity.setSolicitaConductor(true);
                dataFormEntity.setTag("E");
                break;
            default:
                dataFormEntity.setTag("");
                break;
        }

        dataFormEntity.setSolicitaOperador(bufferRecepcion[20]!=0);
        dataFormEntity.setSolicitaHorometro(bufferRecepcion[21]!=0);
        dataFormEntity.setSolicitaKilometraje(bufferRecepcion[22]!=0);
        dataFormEntity.setValidaHorometro(bufferRecepcion[23]!=0);
        dataFormEntity.setValidaKilometro(bufferRecepcion[24]!=0);
        dataFormEntity.setSolicitaPreseteo(bufferRecepcion[25]!=0);

        //Capturar Kilometraje Actual
        if (dataFormEntity.isSolicitaKilometraje()) {
            int[] tramaKilometrajeParteEntera = new int[3];
            c = 0;
            for (int i = 28; i >= 26; i--) {
                tramaKilometrajeParteEntera[c] = bufferRecepcion[i];
                c++;
            }
            int kilometrajeParteEntera = byteArrayToHexInt(tramaKilometrajeParteEntera, tramaKilometrajeParteEntera.length);

            int[] tramaKilometrajeParteDecimal = new int[1];
            tramaKilometrajeParteDecimal[0] = bufferRecepcion[29];
            int kilometrajeParteDecimal = byteArrayToHexInt(tramaKilometrajeParteDecimal, tramaKilometrajeParteDecimal.length);
            dataFormEntity.setKilometrajeActual(Double.valueOf(kilometrajeParteEntera + "." + kilometrajeParteDecimal));
            Log.v("Formulario", ""+dataFormEntity.getKilometrajeActual() );
        }

        if (dataFormEntity.isValidaKilometraje()) {
            //Capturar Kilometraje Minimo
            int[] tramaKilometrajeMinimo = new int[2];
            tramaKilometrajeMinimo[0] = bufferRecepcion[31];
            tramaKilometrajeMinimo[1] = bufferRecepcion[30];
            int kilometrajeMinimo = byteArrayToHexInt(tramaKilometrajeMinimo, 2);
            dataFormEntity.setKilometrajeMinimo(dataFormEntity.kilometrajeActual + kilometrajeMinimo);
            Log.v("Formulario", ""+dataFormEntity.getKilometrajeMinimo() );

            //Capturar Kilometraje Maximo
            int[] tramaKilometrajeMaximo = new int[2];
            tramaKilometrajeMaximo[0] = bufferRecepcion[33];
            tramaKilometrajeMaximo[1] = bufferRecepcion[32];
            int kilometrajeMaximo = byteArrayToHexInt(tramaKilometrajeMaximo, 2);
            dataFormEntity.setKilometrajeMaximo(dataFormEntity.kilometrajeActual + kilometrajeMaximo);
            Log.v("Formulario", ""+dataFormEntity.getKilometrajeMaximo() );
        }

        //Capturar Horometro Actual
        if (dataFormEntity.isSolicitaHorometro()) {
            //Capturar Horometro Actual
            int[] tramaHorometroParteEntera = new int[3];
            c = 0;
            for (int i = 36; i >= 34; i--) {
                tramaHorometroParteEntera[c] = bufferRecepcion[i];
                c++;
            }
            int horometroParteEntera = byteArrayToHexInt(tramaHorometroParteEntera, tramaHorometroParteEntera.length);

            int[] tramaHorometroParteDecimal = new int[1];
            tramaHorometroParteDecimal[0] = bufferRecepcion[37];
            int horometroParteDecimal = byteArrayToHexInt(tramaHorometroParteDecimal, tramaHorometroParteDecimal.length);

            dataFormEntity.setHorometroActual(Double.valueOf(horometroParteEntera + "." + horometroParteDecimal));
        }

        if(dataFormEntity.isValidaHorometro()) {
            //Capturar Horometro Minimo
            int[] tramaHorometroMinimo = new int[2];
            tramaHorometroMinimo[0] = bufferRecepcion[39];
            tramaHorometroMinimo[1] = bufferRecepcion[38];
            int horometroMinimo = byteArrayToHexInt(tramaHorometroMinimo, 2);
            dataFormEntity.setHorometroMinimo(dataFormEntity.horometroActual + horometroMinimo);

            //Capturar Horometro Maximo
            int[] tramaHorometroMaximo = new int[2];
            tramaHorometroMaximo[0] = bufferRecepcion[41];
            tramaHorometroMaximo[1] = bufferRecepcion[40];
            int horometroMaximo = byteArrayToHexInt(tramaHorometroMaximo, 2);
            dataFormEntity.setHorometroMaximo(dataFormEntity.horometroActual + horometroMaximo);
        }

        if(dataFormEntity.isSolicitaPreseteo()){
            int[] tramaPreseteoMinimo = new int[2];
            tramaPreseteoMinimo[0] = bufferRecepcion[43];
            tramaPreseteoMinimo[1] = bufferRecepcion[42];
            int preseteoMinimo = byteArrayToHexInt(tramaPreseteoMinimo, 2);
            dataFormEntity.setPreseteoMinimo(preseteoMinimo);

            int[] tramaPreseteoMaximo = new int[2];
            tramaPreseteoMaximo[0] = bufferRecepcion[45];
            tramaPreseteoMaximo[1] = bufferRecepcion[44];
            int preseteoMaximo = byteArrayToHexInt(tramaPreseteoMaximo, 2);
            dataFormEntity.setPreseteoMaximo(preseteoMaximo);
        }


        layoutsHose.get(indiceLayoutHose).formDialogTransaction = new FormDialogTransaction(getActivity(),R.style.AppThemeAssacFormularioDialog, dataFormEntity, this);
        if(validateShowForm())
            layoutsHose.get(indiceLayoutHose).formDialogTransaction.show();
    }

    private void validarConductor(int indiceLayoutHose){
        String nombre="";
        boolean conductorValido, prefixValido, bloqueado, registrado, habilitado, expiracion;

        conductorValido=bufferRecepcion[37]!=0;

        if(conductorValido) {
            layoutsHose.get(indiceLayoutHose).formDialogTransaction=null;

            int c;

            //Capturar IdConductor
            int[] tramaIdConductor = new int[8];
            c = 0;
            for (int i = 29; i <= 36; i++) {
                if(bufferRecepcion[i]!=0x00) {
                    tramaIdConductor[c] = bufferRecepcion[i];
                    c++;
                }
            }
            String idConductor = byteArrayToHexString3(tramaIdConductor, c).trim();

            txt_conductor = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_conductor);
            txt_conductor.setText(idConductor);

            int[] tramaConductor = new int[20];
            c = 0;
            for (int i = 9; i <= 28; i++) {
                tramaConductor[c] = bufferRecepcion[i];
                c++;
            }
            nombre = hexToAscii(byteArrayToHexString(tramaConductor, tramaConductor.length)).trim();

            ly_cuadrante = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.ly_cuadrante);

            String finalNombre = nombre;


            ly_cuadrante.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogShowDriver(finalNombre, hoseEntities.get(indiceLayoutHose).nombreManguera);
                }
            });
            dialogShowWelcomeDriver(finalNombre);
            handlerDialog.postDelayed(new Runnable() {
                public void run() {
                    dialogShowData.dismiss();
                }
            }, 4000);
        }else{

            String messageErrorIdDriver="";

            prefixValido=bufferRecepcion[38]!=0;
            bloqueado=bufferRecepcion[39]!=0;
            registrado=bufferRecepcion[40]!=0;
            habilitado=bufferRecepcion[41]!=0;
            expiracion=bufferRecepcion[42]!=0;

            if(!registrado)
                messageErrorIdDriver="El ID del conductor no está registrado.";
            else if(!prefixValido)
                messageErrorIdDriver="El ID del conductor con prefix no válido.";
            else if(!habilitado)
                messageErrorIdDriver="El ID del conductor no está habilitado.";
            else if(!bloqueado)
                messageErrorIdDriver="El ID del conductor está bloqueado.";
            else if(!expiracion)
                messageErrorIdDriver="El ID del conductor ha expirado.";

            layoutsHose.get(indiceLayoutHose).formDialogTransaction.escribirErrorIdConductor(messageErrorIdDriver);

            if(!layoutsHose.get(indiceLayoutHose).formDialogTransaction.isShowing())
                layoutsHose.get(indiceLayoutHose).formDialogTransaction.show();

            Toast.makeText(getActivity(), "Conductor Inválido.", Toast.LENGTH_LONG).show();
        }

    }

    private void validarVehiculo(int indiceLayoutHose){
        String placa="";
        boolean vehiculoValido, prefixValido, productoCorrecto, bloqueado, registrado, habilitado, expiracion, placaId, abastecimientosDia, volumenAsignado;

        vehiculoValido=bufferRecepcion[27]!=0;

        if(vehiculoValido) {
            layoutsHose.get(indiceLayoutHose).setState(1);
            layoutsHose.get(indiceLayoutHose).formDialogTransaction=null;
            //Capturar Placa
            int[] tramaPlaca = new int[10];
            int c = 0;
            for(int i = 9; i<= 18;  i++){
                tramaPlaca[c] = bufferRecepcion[i];
                c++;
            }
            placa = hexToAscii(byteArrayToHexString(tramaPlaca,tramaPlaca.length)).trim();

            txt_placa = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_placa);
            //txt_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_producto);

            txt_placa.setText(placa);
            //txt_placa.setGravity(Gravity.CENTER);
            //txt_placa.setTextAlignment(Layout.Alignment.ALIGN_CENTER);
            //txt_producto.setText(hoseEntities.get(indiceLayoutHose).getNombreProducto());
            //txt_producto.setGravity(Gravity.CENTER);

        }else{

            String messageErrorPlate="";

            prefixValido=bufferRecepcion[28]!=0;
            productoCorrecto=bufferRecepcion[29]!=0;
            bloqueado=bufferRecepcion[30]!=0;
            registrado=bufferRecepcion[31]!=0;
            habilitado=bufferRecepcion[32]!=0;
            expiracion=bufferRecepcion[33]!=0;
            placaId=bufferRecepcion[34]!=0;
            abastecimientosDia=bufferRecepcion[35]!=0;
            volumenAsignado=bufferRecepcion[36]!=0;

            if(!registrado)
                messageErrorPlate="La placa no está registrada.";
            else if(!prefixValido)
                messageErrorPlate="La placa con prefix no válido.";
            else if(!productoCorrecto)
                messageErrorPlate="Producto de placa incorrecto.";
            else if(!habilitado)
                messageErrorPlate="La placa no está habilitada.";
            else if(!bloqueado)
                messageErrorPlate="La placa está bloqueada.";
            else if(!expiracion)
                messageErrorPlate="La placa ha expirado.";
            else if(!abastecimientosDia)
                messageErrorPlate="La placa superó abastecimientos diarios.";
            else if(!volumenAsignado)
                messageErrorPlate="La placa superó volumen diario.";

            layoutsHose.get(indiceLayoutHose).formDialogTransaction.escribirErrorPlaca(messageErrorPlate);

            if(!layoutsHose.get(indiceLayoutHose).formDialogTransaction.isShowing())
                layoutsHose.get(indiceLayoutHose).formDialogTransaction.show();

            Toast.makeText(getActivity(), "Conductor Inválido.", Toast.LENGTH_LONG).show();
        }




    }

    //CAMBIAR PLACA ACTUAL
    public void cambiarPlaca(int indiceLayoutHose){
        layoutsHose.get(indiceLayoutHose).setState(1);
        if(layoutsHose.get(indiceLayoutHose).formDialogTransaction!=null){
            if(layoutsHose.get(indiceLayoutHose).formDialogTransaction.isShowing())
                layoutsHose.get(indiceLayoutHose).formDialogTransaction.dismiss();

            layoutsHose.get(indiceLayoutHose).formDialogTransaction=null;
        }


        //Capturar Placa
        int[] tramaPlaca = new int[10];
        String placa = "";
        int c = 0;
        for(int i = 9; i<= 18;  i++){
            tramaPlaca[c] = bufferRecepcion[i];
            c++;
        }
        placa = hexToAscii(byteArrayToHexString(tramaPlaca,tramaPlaca.length));


        int[] tramaAutorizadoPlaca = new int[1];
        for(int i = 28; i<= 28;  i++){
            tramaAutorizadoPlaca[0] = bufferRecepcion[i];
        }
        //String estadoActual = byteArrayToHexString(tramaEstadoActual,tramaEstadoActual.length);
        int autorizado = Integer.parseInt(byteArrayToHexIntGeneral(tramaAutorizadoPlaca,1));
        Log.v("Autorizado",""+ autorizado);

        int[] tramaPrefixValido = new int[1];
        for(int i = 29; i<= 29;  i++){
            tramaPrefixValido[0] = bufferRecepcion[i];
        }
        int prefix = Integer.parseInt(byteArrayToHexIntGeneral(tramaPrefixValido,1));
        Log.v("Prefix",""+ prefix);

        int[] tramaProductoCorrecto = new int[1];
        for(int i = 30; i<= 30;  i++){
            tramaProductoCorrecto[0] = bufferRecepcion[i];
        }
        int producto = Integer.parseInt(byteArrayToHexIntGeneral(tramaProductoCorrecto,1));

        Log.v("Producto",""+ producto);

        int[] tramaBloqueado = new int[1];
        for(int i = 31; i<= 31;  i++){
            tramaBloqueado[0] = bufferRecepcion[i];
        }
        int bloqueado = Integer.parseInt(byteArrayToHexIntGeneral(tramaBloqueado,1));

        Log.v("Bloqueado",""+ bloqueado);

        int[] tramaRegistrado = new int[1];
        for(int i = 32; i<= 32;  i++){
            tramaRegistrado[0] = bufferRecepcion[i];
        }
        int registrado = Integer.parseInt(byteArrayToHexIntGeneral(tramaRegistrado,1));

        Log.v("Registrado",""+ registrado);

        int[] tramaHabilitado = new int[1];
        for(int i = 33; i<= 33;  i++){
            tramaHabilitado[0] = bufferRecepcion[i];
        }
        int habilitado = Integer.parseInt(byteArrayToHexIntGeneral(tramaHabilitado,1));

        Log.v("Habilitado",""+ habilitado);

        int[] tramaExpiracionValida = new int[1];
        for(int i = 34; i<= 34;  i++){
            tramaExpiracionValida[0] = bufferRecepcion[i];
        }
        int expiracionValida = Integer.parseInt(byteArrayToHexIntGeneral(tramaExpiracionValida,1));

        Log.v("Expiracion Valida",""+ expiracionValida);


        txt_placa = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_placa);
        //txt_producto = layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_producto);

        txt_placa.setText(placa.trim());
        //txt_placa.setGravity(Gravity.CENTER);
        //txt_placa.setTextAlignment(Layout.Alignment.ALIGN_CENTER);
        //txt_producto.setText(hoseEntities.get(indiceLayoutHose).getNombreProducto());
        //txt_producto.setGravity(Gravity.CENTER);
    }

    //CAMBIAR PULSOS
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cambiarPulsos(int indiceLayoutHose){
        cambiarEstadoAutorizarAbastecimiento(indiceLayoutHose);

        txt_galones = (TextView) layoutsHose.get(indiceLayoutHose).inflater.findViewById(R.id.txt_galones);

        //**********************************************************
        //Capturar Volumen Abastecido
        int contador;
        int[] tramaVolumen = new int[(longitudTramaRecepcion-2)-13];
        contador = 0;
        for(int i = 13; i<= longitudTramaRecepcion-3;  i++){
            tramaVolumen[contador] = bufferRecepcion[i];
            contador++;
        }
        String volumen = ""+ hexToAscii(byteArrayToHexString(tramaVolumen,tramaVolumen.length));
        String[] parts = volumen.split("\\.");
        if(parts.length > 1) {
            volumen = parts[0] + "." + parts[1].substring(0,(0+hoseEntities.get(indiceLayoutHose).getCantidadDecimales()));
        }

        Log.v("Volumen Abastecido",volumen);

        txt_galones.setText(volumen);
        hoseEntities.get(indiceLayoutHose).setVolumen(volumen);
    }

    public boolean validateShowForm(){
        boolean response=true;
        for(int i=0; i < layoutsHose.size() ; i++){
            if(layoutsHose.get(i).formDialogTransaction!=null){
                if(layoutsHose.get(i).formDialogTransaction.isShowing()){
                    response=false;
                    break;
                }
            }
        }
        return response;
    }

    public boolean validateStatusHose(){
        boolean response=true;
        for(int i=0; i < layoutsHose.size() ; i++){
            if(layoutsHose.get(i).getState()!=0 & layoutsHose.get(i).getState()!=-1){
                response=false;
                break;
            }
        }
        return response;
    }

    //CREAMOS EL DIALOG DE BIENVENIDA PERSONALIZADO
    private void dialogShowWelcomeDriver(String driver){

        TextView nameDriver;

        dialogShowData.setContentView(R.layout.dialog_welcome_driver);

        nameDriver = dialogShowData.findViewById(R.id.nameDriver);

        nameDriver.setText(driver);

        dialogShowData.setCancelable(false);
        dialogShowData.show();

    }

    //CREAMOS EL DIALOG PERSONALIZADO - ONCLICK
    private void dialogShowDriver(String driver, String hose ){
        Button okDialog;
        TextView nameHose, nameDriver;

        dialogShowData.setContentView(R.layout.dialog_driver);

        nameDriver = dialogShowData.findViewById(R.id.nameDriver);
        nameHose = dialogShowData.findViewById(R.id.nameHose);
        okDialog = dialogShowData.findViewById(R.id.okDialog);

        nameDriver.setText(driver);
        nameHose.setText(hose);

        okDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowData.dismiss();
            }
        });

        dialogShowData.setCancelable(true);
        dialogShowData.show();

    }

    @Override
    public void sendBytesEmbedded(byte[] responseDataDevice, int direccion, int numeroBomba, String comentario) {
        ResponseDataDevice = responseDataDevice;
        direccionResponse = direccion;
        numeroBombaResponse = numeroBomba;
        comentarioResponse = comentario;

        //longitud=EmbeddedPtcl.enviarDataFormularioWifi(bufferTransmision,1,8,2,dataFormEntity);
        //Log.v("", "" + "Transmision   FormData" + byteArrayToHexString(bufferTransmision,0x64));
        clientTCPThread.write(EmbeddedPtcl.b_ext_enviar_data_nfc);
        Toast.makeText(getActivity(), "Enviar dato a Embedded.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void receiveDataForm(DataFormEntity dataFormEntity) {
        DataFormEntity = dataFormEntity;
        direccionResponse = dataFormEntity.direccion;
        bufferTransmision = new byte[300];
        int longitud=0;
        //longitud=EmbeddedPtcl.enviarDataFormularioWifi(bufferTransmision,1,8,2,dataFormEntity);
        //Log.v("", "" + "Transmision   FormData" + byteArrayToHexString(bufferTransmision,0x64));
        clientTCPThread.write(EmbeddedPtcl.b_ext_enviar_data_formulario);
        Toast.makeText(getActivity(), "Enviar dato a Embedded.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scanearQRCode(int indiceBomba) {
        indiceBombaRead = indiceBomba;
        IntentIntegrator intentIntegrator = new IntentIntegrator(
                getActivity()
        );

        //intentIntegrator.setPrompt("For flash use volume up key");
        intentIntegrator.setPrompt("Activar/Desactivar flash use botones de volumen.");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        //intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.initiateScan();
    }

    @Override
    public void receiveQRCode(String stringQRCode) {
        if(layoutsHose.get(indiceBombaRead).formDialogTransaction!=null){
            layoutsHose.get(indiceBombaRead).formDialogTransaction.escribirIdConductor(stringQRCode);
        }
    }

    @Override
    public void readNFCPlate(int indiceBomba) {
        indiceBombaRead = indiceBomba;
        Const.ESTADO_NFC = false;
        Const.CONTADOR_LECTURAS_NFC  = 0;
        mainListener.enableForegroundDispatchSystem();
        mainListener.showProgressDialog("Buscando dispositivo ...");

        handlerDialog.postDelayed(new Runnable() {
            public void run() {
                try{
                    // función a ejecutar
                    mainListener.dismissProgressDialog();
                    mainListener.disableForegroundDispatchSystem();
                    if(Const.ESTADO_NFC == false){
                        Toast.makeText(getActivity(),"No se encontró ningún dispositivo", Toast.LENGTH_LONG).show();
                    }
                    Const.ESTADO_NFC = false;
                }catch (Exception ex){

                }
                //handler.postDelayed(this, 500);
            }

        }, 10*1000);
    }

    @Override
    public void receiveNFCPlate(byte[] responseDataDevice) {
        String placa = "";
        mainListener.dismissProgressDialog();
        mainListener.disableForegroundDispatchSystem();
        String responseDataTexto = "";

        //procesarInfoHMI(responseDataDevice);

        responseDataTexto = Utils.byteArrayToHexString(responseDataDevice,responseDataDevice.length);
        Log.v("TAG", responseDataTexto);

        int[] tramaPlaca = new int[10];
        int c = 0;
        for(int i = 1; i<= 10;  i++){
            tramaPlaca[c] = responseDataDevice[i];
            c++;
        }

        placa = hexToAscii(byteArrayToHexString(tramaPlaca,tramaPlaca.length)).trim();

        if(layoutsHose.get(indiceBombaRead).formDialogTransaction!=null){
            layoutsHose.get(indiceBombaRead).formDialogTransaction.escribirPlaca(responseDataDevice, placa);
        }

    }

    private void procesarInfoHMI(byte[] responseDataDevice) {
        String placa = "";
        InformationHMIEntity informationHMIEntity = new InformationHMIEntity();

        switch (responseDataDevice[0]){
            case EmbeddedPtcl.tag_vehiculo:
                informationHMIEntity.setTipoTAG("A");
                break;
            case EmbeddedPtcl.tag_vehiculo_driver_id_v:
                break;
        }

        int[] tramaPlaca = new int[10];
        int c = 0;
        for(int i = 1; i<= 10;  i++){
            tramaPlaca[c] = bufferRecepcion[i];
            c++;
        }
        placa = hexToAscii(byteArrayToHexString(tramaPlaca,tramaPlaca.length)).trim();


    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mainListener = (MainListener) context;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("David","1232456"+clientTCPThread.getStatus());
        //clientTCPThread.cancel(false);
        //clientTCPThread.onPostExecute(false);
        //running=false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        running=false;
        clientTCPThread.cancel(true);
        clientTCPThread.onCancelled();

        handlerSocket=null;
        clientTCPThread = null;
    }

}
