package com.assac.controldelubricantes.View.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.assac.controldelubricantes.Entities.OperatorEntity;
import com.assac.controldelubricantes.Entities.UserEntity;
import com.assac.controldelubricantes.Listeners.LoginListener;
import com.assac.controldelubricantes.R;
import com.assac.controldelubricantes.Storage.DB.CRUDOperations;
import com.assac.controldelubricantes.Storage.DB.MyDatabase;
import com.assac.controldelubricantes.Storage.PreferencesHelper;
import com.assac.controldelubricantes.Util.Const;
import com.assac.controldelubricantes.Util.Wifi.NetworkUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /***VARIABLES PERSONALIZADAS***/
    private TextInputLayout textInputLayout;
    // private TextInputEditText txtUsuario;
    private EditText txtUsuario;
    private EditText txtClave;
    //  private TextInputEditText txtClave;
    private MaterialButton btnIngresar, btnSync;

    private LoginListener mListener;
    private Button btnCrearUsuario;
    private Button btnListarUsuario;
    private Button btnBuscarUsuario;
    private Button btnActualizarUsuario;
    private Button btnConsumirServicio;
    private NetworkUtil networkUtil;
    private CRUDOperations crudOperations;
    private TextView txtIdDispositivoFL;

    //ADDITIONAL CODE
    private ProgressDialog progressDialog;
    Handler handler = new Handler();
    /*****************************************************************/

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponent();
    }

    private void initComponent() {
        txtUsuario = (EditText) getActivity().findViewById(R.id.txtUsuario);
        txtClave = (EditText) getActivity().findViewById(R.id.txtPassword);
        btnIngresar = (MaterialButton) getActivity().findViewById(R.id.btnIngresar);
        btnSync = (MaterialButton) getActivity().findViewById(R.id.btnSync);

        btnSincronizarClic sincronizarClic = new btnSincronizarClic();
        btnSync.setOnClickListener(sincronizarClic);

        btnIngresarClic ingresarLogin = new btnIngresarClic();
        btnIngresar.setOnClickListener(ingresarLogin);

        btnCrearUsuario = (Button) getActivity().findViewById(R.id.btnCrearUsuario);
        btnListarUsuario = (Button) getActivity().findViewById(R.id.btnListarUsuario);
        btnBuscarUsuario = (Button) getActivity().findViewById(R.id.btnBuscarUsuario);
        btnActualizarUsuario = (Button) getActivity().findViewById(R.id.btnActualizarUsuario);
        btnConsumirServicio = (Button) getActivity().findViewById(R.id.btnConsumirServicio);
        txtIdDispositivoFL = (TextView) getActivity().findViewById(R.id.txtIdDispositivoFL);
        networkUtil = new NetworkUtil(getActivity());
        crudOperations = new CRUDOperations(new MyDatabase(getActivity()));
        txtIdDispositivoFL.setText("ID-Dispositivo: " + Const.getAndroidDeviceId(getActivity()));

        btnCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        btnListarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnBuscarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               UserEntity userEntity = new UserEntity();
//                userEntity =  crudOperations.getUser(1);
//                if(userEntity == null){
//                    Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG).show();
//                }else{
//                    Toast.makeText(getActivity(), "\nid: "+userEntity.getIdUser() + "\nNombre: " + userEntity.getPersonName(), Toast.LENGTH_LONG).show();
//                }

            }
        });

        btnActualizarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnConsumirServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    //ADDITIONAL CODE
    private void savePreferences(int idUser, int idPerson, String name, String lastname, String user, String pass, String date, String operationName, String accessPoint) {
        //PreferencesHelper.saveSession(getActivity(),idUser,idPerson, user,pass, name,lastname,date, operationName, accessPoint);
    }

    private class btnIngresarClic implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            String usuario, clave;
            usuario = txtUsuario.getText().toString().trim();
            clave = txtClave.getText().toString().trim();

            CRUDOperations crudOperations = new CRUDOperations(new MyDatabase(getActivity()));

            //savePreferences(1, 1, "Pedro","Pérez","Gian","123456","", "Automation Service S.A.C.", "");

            /*int totalUsers = crudOperations.getUserCount();

            if(totalUsers > 0){
                UserEntity userEntity = new UserEntity();
                userEntity = crudOperations.getUserForLogin(usuario.toUpperCase(),clave);
                if(userEntity.getIdUser() > 0){
                    if(userEntity.getRegistrationStatus().equals("A")){
                        savePreferences(userEntity.getIdUser(), userEntity.getULevel(), userEntity.getPersonName(), userEntity.getFirstLastName(),userEntity.getSecondLastName(),userEntity.getUUser(),userEntity.getUPassword(),"");
                        mListener.goToMain();
                    }else{
                        Toast.makeText(getActivity(),"El usuario ha sido dado de baja. Contacte al administrador del sistema.", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getActivity(),"Credenciales incorrectas", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getActivity(),"No existen usuarios registrados en el aplicativo. Verifique si existen usuarios registrados en el Gestor Web o de lo contrario contacte al administrador del sistema.", Toast.LENGTH_LONG).show();
            }*/

            savePreferences(1, 1, "David", "Vega","Cordova","","","");
            mListener.goToMain();

        }
    }

    private class btnSincronizarClic implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            //ValidateConn validateConn = new ValidateConn(getActivity(),"1");
            //validateConn.mstartConn();
            mListener.loadService();
        }
    }

    //ADDITIONAL CODE
    private void savePreferences(int idUser, int level, String name, String firstname, String lastname, String user, String pass, String date) {
        PreferencesHelper.saveSession(getActivity(),idUser, level, user,pass, name,firstname, lastname,date);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            mListener= (LoginListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}