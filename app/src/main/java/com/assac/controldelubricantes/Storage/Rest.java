package com.assac.controldelubricantes.Storage;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.assac.controldelubricantes.DI.BaseApplication;
import com.assac.controldelubricantes.Entities.OperatorEntity;
import com.assac.controldelubricantes.Entities.UserEntity;
import com.assac.controldelubricantes.Interfaces.OperatorInterface;
import com.assac.controldelubricantes.Interfaces.UserInterface;
import com.assac.controldelubricantes.Storage.DB.CRUDOperations;
import com.assac.controldelubricantes.Storage.DB.MyDatabase;
import com.assac.controldelubricantes.Util.Const;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Rest {

    private static final String TAG = "Rest";
    //  API Service
    /*
    HttpLoggingInterceptor interceptor;
    OkHttpClient client;
    Gson gson;
    Retrofit retrofit;
    */
    //Interfaces
    OperatorInterface operatorInterface;

    //Variables
    ProgressDialog mprogressDialog;
    String ANDROID_DEVICE;
    CRUDOperations crudOperations;

    Retrofit retrofit;

    private Context ctx;
    public Rest(Context context, Retrofit retrofit){
        ctx = context;

        this.retrofit=retrofit;
        //  Instantiate Response Api
        /*
        interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        //  Instantiate Retrofit2
        gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(Const.HTTP_SITE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

         */



        ANDROID_DEVICE = Const.getAndroidDeviceId(ctx);
        crudOperations = new CRUDOperations(new MyDatabase(ctx));
    }


    /*FOR OPERATOR*/
    public void receiveOperatorData(final String mmigrationStartDate){

        OperatorInterface apiService = retrofit.create(OperatorInterface.class);
        Call<List<OperatorEntity>> call = apiService.GetlistarOperadorParaDispositivoMovil(ANDROID_DEVICE, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<List<OperatorEntity>>() {

            @Override
            public void onResponse(Call<List<OperatorEntity>> call, Response<List<OperatorEntity>> response) {

                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        List<OperatorEntity> operatorEntities = response.body();
                        if (operatorEntities.size() > 0 && operatorEntities.get(0).getIdOperator() > 0) {

                            for (int i = 0; i < operatorEntities.size(); i++) {
                                OperatorEntity operatorEntity = new OperatorEntity();
                                OperatorEntity operatorEntity1 = new OperatorEntity(operatorEntities.get(i).getIdOperator(),
                                        operatorEntities.get(i).getOperatorKey(), operatorEntities.get(i).getOperatorUser(),
                                        operatorEntities.get(i).getOperatorPassword(), operatorEntities.get(i).getPersonName(),
                                        operatorEntities.get(i).getFirstLastName(), operatorEntities.get(i).getSecondLastName(),
                                        operatorEntities.get(i).getPhotocheck(), operatorEntities.get(i).getIdOperatorValidationMap(),
                                        operatorEntities.get(i).getValidationDescription(),
                                        operatorEntities.get(i).getRegistrationStatus());

                                //VALIDATE OPERATOR
                                operatorEntity = crudOperations.getOperator(operatorEntities.get(i).getIdOperator());

                                if (operatorEntity.getIdSqlLite() == 0) {
                                    //INSERT ON DB
                                    int responseAddOperator = crudOperations.addOperator(operatorEntity1);
                                    if (responseAddOperator > 0) {
                                        Log.v(TAG, "insercion exitosa");
                                        updateStatusOperatorOnServer(operatorEntity1.getIdOperator(), 1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "insercion no existosa"+responseAddOperator);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: receiveOperatorData / código: " + response.code() + " / Mensaje: Error al insertar el operador con ID " + operatorEntities.get(i).getIdOperator() + " Respuesta de la inserción: " + responseAddOperator, "1");
                                    }
                                } else {
                                    //UPDATE ON DB
                                    int responseUpdateOperator = crudOperations.updateOperator(operatorEntity1);

                                    if (responseUpdateOperator > 0) {
                                        Log.v(TAG, "actualización exitosa");
                                        updateStatusOperatorOnServer(operatorEntity1.getIdOperator(), 1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "actualización no exitosa" + responseUpdateOperator);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: operatorEntities / código: " + response.code() + " / Mensaje: Error al actualizar el operador con ID " + operatorEntities.get(i).getIdOperator() + " Respuesta de la actualización: " + responseUpdateOperator, "1");
                                    }
                                }
                            }

                        } else {
                            if (operatorEntities.size() > 0 && operatorEntities.get(0).getValorConsulta().equals("0")) {
                                Const.saveErrorData(ctx, mmigrationStartDate, "método: operatorEntities / código: " + response.code() + " Mensaje:" + operatorEntities.get(0).getMensajeConsulta(), "1");
                            }
                            //Const.saveErrorData(ctx,mmigrationStartDate,"método: receiveUserData / código: "+response.code() + " El servicio retornó 0 registros","1");
                            //Log.v(TAG,"el servicio retornó 0 registros");
                        }

                    } else {
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: operatorEntities / código: " + response.code(), "1");
                        //Log.v(TAG,"error 404");

                    }
                } else {
                    Const.saveErrorData(ctx, mmigrationStartDate, "método: operatorEntities / código: " + response.code(), "1");
                    //Log.v(TAG,"no exito");

                }
            }

            @Override
            public void onFailure(Call<List<OperatorEntity>> call, Throwable t) {

                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: operatorEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: operatorEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }

                call.cancel();
                //progressDialog.dismiss();
            }

        });
    }

    public void updateStatusOperatorOnServer(int idUsuario, int idUsuarioRegistro, final String mmigrationStartDate){

        OperatorInterface apiService = retrofit.create(OperatorInterface.class);
        Call<String> call = apiService.GetactualizarEstadoMigracionDispositivoMovil(ANDROID_DEVICE, idUsuario, idUsuarioRegistro, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){

                    if (response.code() == 200){
                        String rx = response.body();
                        if(!rx.equals("1")){
                            Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusOperatorOnServer / código: "+response.code() + " / Mensaje: "+ rx,"1");
                        }
                    } else {
                        //Log.v(TAG,"error 404");
                        //progressDialog.dismiss();
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: updateStatusOperatorOnServer / código: " + response.code(), "1");
                    }
                } else{
                    //Log.v(TAG,"no exito");
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusOperatorOnServer / código: "+response.code(),"1");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusOperatorOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusOperatorOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }
                call.cancel();
                //progressDialog.dismiss();
            }
        });
    }

    /*FOR USER*/
    public void receiveUserData(final String mmigrationStartDate){

        UserInterface apiService = retrofit.create(UserInterface.class);
        Call<List<UserEntity>> call = apiService.GetlistarUsuarioParaDispositivoMovil(ANDROID_DEVICE, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<List<UserEntity>>() {

            @Override
            public void onResponse(Call<List<UserEntity>> call, Response<List<UserEntity>> response) {

                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        List<UserEntity> userEntities = response.body();
                        if (userEntities.size() > 0 && userEntities.get(0).getIdUser() > 0) {

                            for (int i = 0; i < userEntities.size(); i++) {
                                UserEntity userEntity = new UserEntity();
                                UserEntity userEntity1 = new UserEntity(userEntities.get(i).getIdUser(),
                                        userEntities.get(i).getIdPerson(),  userEntities.get(i).getPersonName(),
                                        userEntities.get(i).getFirstLastName(), userEntities.get(i).getSecondLastName(),
                                        userEntities.get(i).getPhotocheck(), userEntities.get(i).getULevel(),
                                        userEntities.get(i).getUUser(),userEntities.get(i).getUPassword(),
                                        userEntities.get(i).getRegistrationStatus());

                                //VALIDATE OPERATOR
                                userEntity = crudOperations.getUser(userEntities.get(i).getIdUser(), userEntities.get(i).getULevel());

                                if (userEntity.getIdSqlLite() == 0) {
                                    //INSERT ON DB
                                    int responseAddUser = crudOperations.addUser(userEntity1);
                                    if (responseAddUser > 0) {
                                        Log.v(TAG, "insercion exitosa");
                                        updateStatusUserOnServer(userEntity1.getIdUser(), userEntity1.getULevel(),1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "insercion no existosa"+responseAddUser);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: receiveOperatorData / código: " + response.code() + " / Mensaje: Error al insertar el usuario con ID " + userEntities.get(i).getIdUser() + " Respuesta de la inserción: " + responseAddUser, "1");
                                    }
                                } else {
                                    //UPDATE ON DB
                                    int responseUpdateUser = crudOperations.updateUser(userEntity1);

                                    if (responseUpdateUser > 0) {
                                        Log.v(TAG, "actualización exitosa");
                                        updateStatusUserOnServer(userEntity1.getIdUser(),userEntity1.getULevel(), 1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "actualización no exitosa" + responseUpdateUser);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: userrEntities / código: " + response.code() + " / Mensaje: Error al actualizar el operador con ID " + userEntities.get(i).getIdUser() + " Respuesta de la actualización: " + responseUpdateUser, "1");
                                    }
                                }
                            }

                        } else {
                            if (userEntities.size() > 0 && userEntities.get(0).getValorConsulta().equals("0")) {
                                Const.saveErrorData(ctx, mmigrationStartDate, "método: userrEntities / código: " + response.code() + " Mensaje:" + userEntities.get(0).getMensajeConsulta(), "1");
                            }
                            //Const.saveErrorData(ctx,mmigrationStartDate,"método: receiveUserData / código: "+response.code() + " El servicio retornó 0 registros","1");
                            //Log.v(TAG,"el servicio retornó 0 registros");
                        }

                    } else {
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: userrEntities / código: " + response.code(), "1");
                        //Log.v(TAG,"error 404");

                    }
                } else {
                    Const.saveErrorData(ctx, mmigrationStartDate, "método: userrEntities / código: " + response.code(), "1");
                    //Log.v(TAG,"no exito");

                }
            }

            @Override
            public void onFailure(Call<List<UserEntity>> call, Throwable t) {

                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: userrEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: userrEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }

                call.cancel();
                //progressDialog.dismiss();
            }

        });
    }

    public void updateStatusUserOnServer(int idUsuario, int levelUsuario,int idUsuarioRegistro, final String mmigrationStartDate){

        UserInterface apiService = retrofit.create(UserInterface.class);
        Call<String> call = apiService.GetactualizarEstadoMigracionDispositivoMovil(ANDROID_DEVICE, idUsuario,levelUsuario, idUsuarioRegistro, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){

                    if (response.code() == 200){
                        String rx = response.body();
                        if(!rx.equals("1")){
                            Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusUserOnServer / código: "+response.code() + " / Mensaje: "+ rx,"1");
                        }
                    } else {
                        //Log.v(TAG,"error 404");
                        //progressDialog.dismiss();
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: updateStatusUserOnServer / código: " + response.code(), "1");
                    }
                } else{
                    //Log.v(TAG,"no exito");
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusUserOnServer / código: "+response.code(),"1");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusUserOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusUserOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }
                call.cancel();
                //progressDialog.dismiss();
            }
        });
    }

}
