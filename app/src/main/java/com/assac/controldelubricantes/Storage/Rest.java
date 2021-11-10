package com.assac.controldelubricantes.Storage;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.assac.controldelubricantes.DI.BaseApplication;
import com.assac.controldelubricantes.Entities.CompartmentEntity;
import com.assac.controldelubricantes.Entities.ModelCompartmentEntity;
import com.assac.controldelubricantes.Entities.OperatorEntity;
import com.assac.controldelubricantes.Entities.ProductEntity;
import com.assac.controldelubricantes.Entities.ReasonEntity;
import com.assac.controldelubricantes.Entities.UserEntity;
import com.assac.controldelubricantes.Entities.VehicleEntity;
import com.assac.controldelubricantes.Interfaces.CompartmentInterface;
import com.assac.controldelubricantes.Interfaces.ModelCompartmentInterface;
import com.assac.controldelubricantes.Interfaces.OperatorInterface;
import com.assac.controldelubricantes.Interfaces.ProductInterface;
import com.assac.controldelubricantes.Interfaces.ReasonInterface;
import com.assac.controldelubricantes.Interfaces.UserInterface;
import com.assac.controldelubricantes.Interfaces.VehicleInterface;
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
                                        userEntities.get(i).getIdPerson(),  userEntities.get(i).getPhotocheck(),
                                        userEntities.get(i).getPersonName(),  userEntities.get(i).getFirstLastName(), userEntities.get(i).getSecondLastName(),
                                         userEntities.get(i).getULevel(),
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

    /*FOR PRODUCT*/
    public void receiveProductData(final String mmigrationStartDate){

        ProductInterface apiService = retrofit.create(ProductInterface.class);
        Call<List<ProductEntity>> call = apiService.GetlistarProductoParaDispositivoMovil(ANDROID_DEVICE, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<List<ProductEntity>>() {

            @Override
            public void onResponse(Call<List<ProductEntity>> call, Response<List<ProductEntity>> response) {

                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        List<ProductEntity> productEntities = response.body();
                        if (productEntities.size() > 0 && productEntities.get(0).getIdProduct() > 0) {

                            for (int i = 0; i < productEntities.size(); i++) {
                                ProductEntity productEntity = new ProductEntity();
                                ProductEntity productEntity1 = new ProductEntity(productEntities.get(i).getIdProduct(),
                                        productEntities.get(i).getNumberProduct(),  productEntities.get(i).getProductName(),
                                        productEntities.get(i).getMeasurementUnit(),  productEntities.get(i).getElipseCode(),
                                        productEntities.get(i).getRegistrationStatus());

                                //VALIDATE PRODUCTO
                                productEntity = crudOperations.getProduct(productEntities.get(i).getIdProduct());

                                if (productEntity.getIdSqlLite() == 0) {
                                    //INSERT ON DB
                                    int responseAddProduct = crudOperations.addProduct(productEntity1);
                                    if (responseAddProduct > 0) {
                                        Log.v(TAG, "insercion exitosa");
                                        updateStatusProductOnServer(productEntity1.getIdProduct(),1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "insercion no existosa"+responseAddProduct);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: receiveOperatorData / código: " + response.code() + " / Mensaje: Error al insertar el producto con ID " + productEntities.get(i).getIdProduct() + " Respuesta de la inserción: " + responseAddProduct, "1");
                                    }
                                } else {
                                    //UPDATE ON DB
                                    int responseUpdateProduct = crudOperations.updateProduct(productEntity1);

                                    if (responseUpdateProduct > 0) {
                                        Log.v(TAG, "actualización exitosa");
                                        updateStatusProductOnServer(productEntity1.getIdProduct(), 1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "actualización no exitosa" + responseUpdateProduct);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: productEntities / código: " + response.code() + " / Mensaje: Error al actualizar el producto con ID " + productEntities.get(i).getIdProduct() + " Respuesta de la actualización: " + responseUpdateProduct, "1");
                                    }
                                }
                            }

                        } else {
                            if (productEntities.size() > 0 && productEntities.get(0).getValorConsulta().equals("0")) {
                                Const.saveErrorData(ctx, mmigrationStartDate, "método: productEntities / código: " + response.code() + " Mensaje:" + productEntities.get(0).getMensajeConsulta(), "1");
                            }
                            //Const.saveErrorData(ctx,mmigrationStartDate,"método: receiveUserData / código: "+response.code() + " El servicio retornó 0 registros","1");
                            //Log.v(TAG,"el servicio retornó 0 registros");
                        }

                    } else {
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: productEntities / código: " + response.code(), "1");
                        //Log.v(TAG,"error 404");

                    }
                } else {
                    Const.saveErrorData(ctx, mmigrationStartDate, "método: productEntities / código: " + response.code(), "1");
                    //Log.v(TAG,"no exito");

                }
            }

            @Override
            public void onFailure(Call<List<ProductEntity>> call, Throwable t) {

                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: productEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: productEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }

                call.cancel();
                //progressDialog.dismiss();
            }

        });
    }

    public void updateStatusProductOnServer(int idProducto,int idUsuarioRegistro, final String mmigrationStartDate){

        ProductInterface apiService = retrofit.create(ProductInterface.class);
        Call<String> call = apiService.GetactualizarEstadoMigracionDispositivoMovil(ANDROID_DEVICE, idProducto, idUsuarioRegistro, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){

                    if (response.code() == 200){
                        String rx = response.body();
                        if(!rx.equals("1")){
                            Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusProductOnServer / código: "+response.code() + " / Mensaje: "+ rx,"1");
                        }
                    } else {
                        //Log.v(TAG,"error 404");
                        //progressDialog.dismiss();
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: updateStatusProductOnServer / código: " + response.code(), "1");
                    }
                } else{
                    //Log.v(TAG,"no exito");
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusProductOnServer / código: "+response.code(),"1");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusProductOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusProductOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }
                call.cancel();
                //progressDialog.dismiss();
            }
        });
    }

    /*FOR COMPARTMENT*/
    public void receiveCompartmentData(final String mmigrationStartDate){

        CompartmentInterface apiService = retrofit.create(CompartmentInterface.class);
        Call<List<CompartmentEntity>> call = apiService.GetlistarCompartimientoParaDispositivoMovil(ANDROID_DEVICE, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<List<CompartmentEntity>>() {

            @Override
            public void onResponse(Call<List<CompartmentEntity>> call, Response<List<CompartmentEntity>> response) {

                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        List<CompartmentEntity> compartmentEntities = response.body();
                        if (compartmentEntities.size() > 0 && compartmentEntities.get(0).getIdProduct() > 0) {

                            for (int i = 0; i < compartmentEntities.size(); i++) {
                                CompartmentEntity compartmentEntity = new CompartmentEntity();
                                CompartmentEntity compartmentEntity1 = new CompartmentEntity(compartmentEntities.get(i).getIdCompartment(),
                                        compartmentEntities.get(i).getIdProduct(),  compartmentEntities.get(i).getIdCompartmentType(),
                                        compartmentEntities.get(i).getCompartmentName(),  compartmentEntities.get(i).getCapacity(),
                                        compartmentEntities.get(i).getAlertCapacity(),  compartmentEntities.get(i).getRegistrationStatus());

                                //VALIDATE COMPARITMIENTO
                                compartmentEntity = crudOperations.getCompartment(compartmentEntities.get(i).getIdCompartment());

                                if (compartmentEntity.getIdSqlLite() == 0) {
                                    //INSERT ON DB
                                    int responseAddCompartment = crudOperations.addCompartment(compartmentEntity1);
                                    if (responseAddCompartment > 0) {
                                        Log.v(TAG, "insercion exitosa");
                                        updateStatusCompartmentOnServer(compartmentEntity1.getIdCompartment(),1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "insercion no existosa"+responseAddCompartment);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: receiveCompartmentData / código: " + response.code() + " / Mensaje: Error al insertar el compartimiento con ID " + compartmentEntities.get(i).getIdCompartment() + " Respuesta de la inserción: " + responseAddCompartment, "1");
                                    }
                                } else {
                                    //UPDATE ON DB
                                    int responseUpdateCompartment = crudOperations.updateCompartment(compartmentEntity1);

                                    if (responseUpdateCompartment > 0) {
                                        Log.v(TAG, "actualización exitosa");
                                        updateStatusCompartmentOnServer(compartmentEntity1.getIdCompartment(), 1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "actualización no exitosa" + responseUpdateCompartment);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: compartmentEntities / código: " + response.code() + " / Mensaje: Error al actualizar el compartimiento con ID " + compartmentEntities.get(i).getIdCompartment() + " Respuesta de la actualización: " + responseUpdateCompartment, "1");
                                    }
                                }
                            }

                        } else {
                            if (compartmentEntities.size() > 0 && compartmentEntities.get(0).getValorConsulta().equals("0")) {
                                Const.saveErrorData(ctx, mmigrationStartDate, "método: compartmentEntities / código: " + response.code() + " Mensaje:" + compartmentEntities.get(0).getMensajeConsulta(), "1");
                            }
                            //Const.saveErrorData(ctx,mmigrationStartDate,"método: receiveUserData / código: "+response.code() + " El servicio retornó 0 registros","1");
                            //Log.v(TAG,"el servicio retornó 0 registros");
                        }

                    } else {
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: compartmentEntities / código: " + response.code(), "1");
                        //Log.v(TAG,"error 404");

                    }
                } else {
                    Const.saveErrorData(ctx, mmigrationStartDate, "método: compartmentEntities / código: " + response.code(), "1");
                    //Log.v(TAG,"no exito");

                }
            }

            @Override
            public void onFailure(Call<List<CompartmentEntity>> call, Throwable t) {

                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: compartmentEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: compartmentEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }

                call.cancel();
                //progressDialog.dismiss();
            }

        });
    }

    public void updateStatusCompartmentOnServer(int idCompartimiento,int idUsuarioRegistro, final String mmigrationStartDate){

        CompartmentInterface apiService = retrofit.create(CompartmentInterface.class);
        Call<String> call = apiService.GetactualizarEstadoMigracionDispositivoMovil(ANDROID_DEVICE, idCompartimiento, idUsuarioRegistro, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){

                    if (response.code() == 200){
                        String rx = response.body();
                        if(!rx.equals("1")){
                            Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusCompartmentOnServer / código: "+response.code() + " / Mensaje: "+ rx,"1");
                        }
                    } else {
                        //Log.v(TAG,"error 404");
                        //progressDialog.dismiss();
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: updateStatusCompartmentOnServer / código: " + response.code(), "1");
                    }
                } else{
                    //Log.v(TAG,"no exito");
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusCompartmentOnServer / código: "+response.code(),"1");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusCompartmentOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusCompartmentOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }
                call.cancel();
                //progressDialog.dismiss();
            }
        });
    }

    /*FOR REASON*/
    public void receiveReasonData(final String mmigrationStartDate){

        ReasonInterface apiService = retrofit.create(ReasonInterface.class);
        Call<List<ReasonEntity>> call = apiService.GetlistarRazonParaDispositivoMovil(ANDROID_DEVICE, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<List<ReasonEntity>>() {

            @Override
            public void onResponse(Call<List<ReasonEntity>> call, Response<List<ReasonEntity>> response) {

                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        List<ReasonEntity> reasonEntities = response.body();
                        if (reasonEntities.size() > 0 && reasonEntities.get(0).getIdProduct() > 0) {

                            for (int i = 0; i < reasonEntities.size(); i++) {
                                ReasonEntity reasonEntity = new ReasonEntity();
                                ReasonEntity reasonEntity1 = new ReasonEntity(reasonEntities.get(i).getIdReason(),
                                        reasonEntities.get(i).getIdProduct(),  reasonEntities.get(i).getReasonName(),
                                        reasonEntities.get(i).getReasonNumber(), reasonEntities.get(i).getRegistrationStatus());

                                //VALIDATE COMPARITMIENTO
                                reasonEntity = crudOperations.getReason(reasonEntities.get(i).getIdReason());

                                if (reasonEntity.getIdSqlLite() == 0) {
                                    //INSERT ON DB
                                    int responseAddReason = crudOperations.addReason(reasonEntity1);
                                    if (responseAddReason > 0) {
                                        Log.v(TAG, "insercion exitosa");
                                        updateStatusReasonOnServer(reasonEntity1.getIdReason(),1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "insercion no existosa"+responseAddReason);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: receiveReasonData / código: " + response.code() + " / Mensaje: Error al insertar la razon con ID " + reasonEntities.get(i).getIdReason() + " Respuesta de la inserción: " + responseAddReason, "1");
                                    }
                                } else {
                                    //UPDATE ON DB
                                    int responseUpdateReason = crudOperations.updateReason(reasonEntity1);

                                    if (responseUpdateReason > 0) {
                                        Log.v(TAG, "actualización exitosa");
                                        updateStatusReasonOnServer(reasonEntity1.getIdReason(), 1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "actualización no exitosa" + responseUpdateReason);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: receiveReasonData / código: " + response.code() + " / Mensaje: Error al actualizar la razon con ID " + reasonEntities.get(i).getIdReason() + " Respuesta de la actualización: " + responseUpdateReason, "1");
                                    }
                                }
                            }

                        } else {
                            if (reasonEntities.size() > 0 && reasonEntities.get(0).getValorConsulta().equals("0")) {
                                Const.saveErrorData(ctx, mmigrationStartDate, "método: reasonEntities / código: " + response.code() + " Mensaje:" + reasonEntities.get(0).getMensajeConsulta(), "1");
                            }
                            //Const.saveErrorData(ctx,mmigrationStartDate,"método: receiveUserData / código: "+response.code() + " El servicio retornó 0 registros","1");
                            //Log.v(TAG,"el servicio retornó 0 registros");
                        }

                    } else {
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: compartmentEntities / código: " + response.code(), "1");
                        //Log.v(TAG,"error 404");

                    }
                } else {
                    Const.saveErrorData(ctx, mmigrationStartDate, "método: compartmentEntities / código: " + response.code(), "1");
                    //Log.v(TAG,"no exito");

                }
            }

            @Override
            public void onFailure(Call<List<ReasonEntity>> call, Throwable t) {

                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: reasonEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: reasonEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }

                call.cancel();
                //progressDialog.dismiss();
            }

        });
    }

    public void updateStatusReasonOnServer(int idReason,int idUsuarioRegistro, final String mmigrationStartDate){

        ReasonInterface apiService = retrofit.create(ReasonInterface.class);
        Call<String> call = apiService.GetactualizarEstadoMigracionDispositivoMovil(ANDROID_DEVICE, idReason, idUsuarioRegistro, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){

                    if (response.code() == 200){
                        String rx = response.body();
                        if(!rx.equals("1")){
                            Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusReasonOnServer / código: "+response.code() + " / Mensaje: "+ rx,"1");
                        }
                    } else {
                        //Log.v(TAG,"error 404");
                        //progressDialog.dismiss();
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: updateStatusReasonOnServer / código: " + response.code(), "1");
                    }
                } else{
                    //Log.v(TAG,"no exito");
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusReasonOnServer / código: "+response.code(),"1");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusReasonOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusReasonOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }
                call.cancel();
                //progressDialog.dismiss();
            }
        });
    }

    /*FOR VEHICLE*/
    public void receiveVehicleData(final String mmigrationStartDate){

        VehicleInterface apiService = retrofit.create(VehicleInterface.class);
        Call<List<VehicleEntity>> call = apiService.GetlistarVehiculoParaDispositivoMovil(ANDROID_DEVICE, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<List<VehicleEntity>>() {

            @Override
            public void onResponse(Call<List<VehicleEntity>> call, Response<List<VehicleEntity>> response) {

                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        List<VehicleEntity> vehicleEntities = response.body();
                        if (vehicleEntities.size() > 0 && vehicleEntities.get(0).getIdVehicle() > 0) {

                            for (int i = 0; i < vehicleEntities.size(); i++) {
                                VehicleEntity vehicleEntity = new VehicleEntity();
                                VehicleEntity vehicleEntity1 = new VehicleEntity(vehicleEntities.get(i).getIdVehicle(),
                                        vehicleEntities.get(i).getIdCompany(),  vehicleEntities.get(i).getIdModel(),
                                        vehicleEntities.get(i).getPlate(), vehicleEntities.get(i).getVehicleDescription(),vehicleEntities.get(i).getRegistrationStatus());

                                //VALIDATE VEHICULO
                                vehicleEntity = crudOperations.getVehicle(vehicleEntities.get(i).getIdVehicle());

                                if (vehicleEntity.getIdSqlLite() == 0) {
                                    //INSERT ON DB
                                    int responseAddVehicle = crudOperations.addVehicle(vehicleEntity1);
                                    if (responseAddVehicle > 0) {
                                        Log.v(TAG, "insercion exitosa");
                                        updateStatusVehicleOnServer(vehicleEntity1.getIdVehicle(),1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "insercion no existosa"+responseAddVehicle);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: receiveVehicleData / código: " + response.code() + " / Mensaje: Error al insertar vehiculo con ID " + vehicleEntities.get(i).getIdVehicle() + " Respuesta de la inserción: " + responseAddVehicle, "1");
                                    }
                                } else {
                                    //UPDATE ON DB
                                    int responseUpdateVehicle = crudOperations.updateVehicle(vehicleEntity1);

                                    if (responseUpdateVehicle > 0) {
                                        Log.v(TAG, "actualización exitosa");
                                        updateStatusVehicleOnServer(vehicleEntity1.getIdVehicle(), 1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "actualización no exitosa" + responseUpdateVehicle);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: receiveVehicleData / código: " + response.code() + " / Mensaje: Error al actualizar vehiculo con ID " + vehicleEntities.get(i).getIdVehicle() + " Respuesta de la actualización: " + responseUpdateVehicle, "1");
                                    }
                                }
                            }

                        } else {
                            if (vehicleEntities.size() > 0 && vehicleEntities.get(0).getValorConsulta().equals("0")) {
                                Const.saveErrorData(ctx, mmigrationStartDate, "método: vehicleEntities / código: " + response.code() + " Mensaje:" + vehicleEntities.get(0).getMensajeConsulta(), "1");
                            }
                            //Const.saveErrorData(ctx,mmigrationStartDate,"método: receiveUserData / código: "+response.code() + " El servicio retornó 0 registros","1");
                            //Log.v(TAG,"el servicio retornó 0 registros");
                        }

                    } else {
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: vehicleEntities / código: " + response.code(), "1");
                        //Log.v(TAG,"error 404");

                    }
                } else {
                    Const.saveErrorData(ctx, mmigrationStartDate, "método: vehicleEntities / código: " + response.code(), "1");
                    //Log.v(TAG,"no exito");

                }
            }

            @Override
            public void onFailure(Call<List<VehicleEntity>> call, Throwable t) {

                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: vehicleEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: vehicleEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }

                call.cancel();
                //progressDialog.dismiss();
            }

        });
    }

    public void updateStatusVehicleOnServer(int idVehicle,int idUsuarioRegistro, final String mmigrationStartDate){

        VehicleInterface apiService = retrofit.create(VehicleInterface.class);
        Call<String> call = apiService.GetactualizarEstadoMigracionDispositivoMovil(ANDROID_DEVICE, idVehicle, idUsuarioRegistro, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){

                    if (response.code() == 200){
                        String rx = response.body();
                        if(!rx.equals("1")){
                            Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusVehicleOnServer / código: "+response.code() + " / Mensaje: "+ rx,"1");
                        }
                    } else {
                        //Log.v(TAG,"error 404");
                        //progressDialog.dismiss();
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: updateStatusVehicleOnServer / código: " + response.code(), "1");
                    }
                } else{
                    //Log.v(TAG,"no exito");
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusVehicleOnServer / código: "+response.code(),"1");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusVehicleOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusVehicleOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }
                call.cancel();
                //progressDialog.dismiss();
            }
        });
    }

    /*FOR MODEL COMPARTMENT */
    public void receiveModelCompartmentData(final String mmigrationStartDate){

        ModelCompartmentInterface apiService = retrofit.create(ModelCompartmentInterface.class);
        Call<List<ModelCompartmentEntity>> call = apiService.GetlistarModeloCompartimientoParaDispositivoMovil(ANDROID_DEVICE, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<List<ModelCompartmentEntity>>() {

            @Override
            public void onResponse(Call<List<ModelCompartmentEntity>> call, Response<List<ModelCompartmentEntity>> response) {

                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        List<ModelCompartmentEntity> modelCompartmentEntities = response.body();
                        if (modelCompartmentEntities.size() > 0 && modelCompartmentEntities.get(0).getIdModelCompartment() > 0) {

                            for (int i = 0; i < modelCompartmentEntities.size(); i++) {
                                ModelCompartmentEntity modelCompartmentEntity = new ModelCompartmentEntity();
                                ModelCompartmentEntity modelCompartmentEntity1 = new ModelCompartmentEntity(modelCompartmentEntities.get(i).getIdModelCompartment(),
                                        modelCompartmentEntities.get(i).getIdModel(),  modelCompartmentEntities.get(i).getIdCompartment(),
                                        modelCompartmentEntities.get(i).getCompartmentNumber(),modelCompartmentEntities.get(i).getRegistrationStatus());

                                //VALIDATE MODELO COMPARTIMIENTO
                                modelCompartmentEntity = crudOperations.getModelCompartment(modelCompartmentEntities.get(i).getIdModelCompartment());

                                if (modelCompartmentEntity.getIdSqlLite() == 0) {
                                    //INSERT ON DB
                                    int responseAddModelCompartment = crudOperations.addModelCompartment(modelCompartmentEntity1);
                                    if (responseAddModelCompartment > 0) {
                                        Log.v(TAG, "insercion exitosa");
                                        updateStatusModelCompartmentOnServer(modelCompartmentEntity1.getIdModelCompartment(),1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "insercion no existosa"+responseAddModelCompartment);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: receiveModelCompartmentData / código: " + response.code() + " / Mensaje: Error al insertar modelo compartimiento con ID " + modelCompartmentEntities.get(i).getIdModelCompartment() + " Respuesta de la inserción: " + responseAddModelCompartment, "1");
                                    }
                                } else {
                                    //UPDATE ON DB
                                    int responseUpdateModelCompartment = crudOperations.updateModelCompartment(modelCompartmentEntity1);

                                    if (responseUpdateModelCompartment > 0) {
                                        Log.v(TAG, "actualización exitosa");
                                        updateStatusModelCompartmentOnServer(modelCompartmentEntity1.getIdModelCompartment(), 1, mmigrationStartDate);
                                    } else {
                                        Log.v(TAG, "actualización no exitosa" + responseUpdateModelCompartment);
                                        Const.saveErrorData(ctx, mmigrationStartDate, "método: receiveModelCompartmentData / código: " + response.code() + " / Mensaje: Error al actualizar modelo compartimiento con ID " + modelCompartmentEntities.get(i).getIdModelCompartment() + " Respuesta de la actualización: " + responseUpdateModelCompartment, "1");
                                    }
                                }
                            }

                        } else {
                            if (modelCompartmentEntities.size() > 0 && modelCompartmentEntities.get(0).getValorConsulta().equals("0")) {
                                Const.saveErrorData(ctx, mmigrationStartDate, "método: modelCompartmentEntities / código: " + response.code() + " Mensaje:" + modelCompartmentEntities.get(0).getMensajeConsulta(), "1");
                            }
                            //Const.saveErrorData(ctx,mmigrationStartDate,"método: receiveUserData / código: "+response.code() + " El servicio retornó 0 registros","1");
                            //Log.v(TAG,"el servicio retornó 0 registros");
                        }

                    } else {
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: modelCompartmentEntities / código: " + response.code(), "1");
                        //Log.v(TAG,"error 404");

                    }
                } else {
                    Const.saveErrorData(ctx, mmigrationStartDate, "método: modelCompartmentEntities / código: " + response.code(), "1");
                    //Log.v(TAG,"no exito");

                }
            }

            @Override
            public void onFailure(Call<List<ModelCompartmentEntity>> call, Throwable t) {

                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: modelCompartmentEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: modelCompartmentEntities / Mensaje: OnFailure "+t.getMessage(),"1");
                }

                call.cancel();
                //progressDialog.dismiss();
            }

        });
    }

    public void updateStatusModelCompartmentOnServer(int idCompartimiento,int idUsuarioRegistro, final String mmigrationStartDate){

        ModelCompartmentInterface apiService = retrofit.create(ModelCompartmentInterface.class);
        Call<String> call = apiService.GetactualizarEstadoMigracionDispositivoMovil(ANDROID_DEVICE, idCompartimiento, idUsuarioRegistro, Const.HTTP_SITE_KEY);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){

                    if (response.code() == 200){
                        String rx = response.body();
                        if(!rx.equals("1")){
                            Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusModelCompartmentOnServer / código: "+response.code() + " / Mensaje: "+ rx,"1");
                        }
                    } else {
                        //Log.v(TAG,"error 404");
                        //progressDialog.dismiss();
                        Const.saveErrorData(ctx, mmigrationStartDate, "método: updateStatusModelCompartmentOnServer / código: " + response.code(), "1");
                    }
                } else{
                    //Log.v(TAG,"no exito");
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusModelCompartmentOnServer / código: "+response.code(),"1");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t instanceof IOException){
                    //SERVICE_MENSAJE += "Android: Fallo en la red. \n";
                    //Log.v(TAG,"error" + t.getMessage());
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusModelCompartmentOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }else{
                    //Log.v(TAG,"error" + t.getMessage());
                    // SERVICE_MENSAJE += "Android: Problema en la conversión. \n";
                    Const.saveErrorData(ctx,mmigrationStartDate,"método: updateStatusModelCompartmentOnServer / Mensaje: OnFailure "+t.getMessage(),"1");
                }
                call.cancel();
                //progressDialog.dismiss();
            }
        });
    }
}
