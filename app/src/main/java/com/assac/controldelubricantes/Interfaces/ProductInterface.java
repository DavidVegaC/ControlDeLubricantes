package com.assac.controldelubricantes.Interfaces;

import com.assac.controldelubricantes.Entities.ProductEntity;
import com.assac.controldelubricantes.Entities.UserEntity;
import com.assac.controldelubricantes.Util.Const;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ProductInterface {

    String SERVICE_NAME_USER= Const.SERVICE_NAME + "Product";

    @Headers({
            "Content-Type: application/json",
            "application-id: B9D12B47-6B88-8471-FFAD-2B4FFD1EA100",
            "secret-key: 46C1AEC7-6BA7-D1C7-FF6A-FD9EA95C0C00",
            "application-type: REST"
    })
    @GET(SERVICE_NAME_USER + "/GetlistarProductoParaDispositivoMovil?")
    Call<List<ProductEntity>> GetlistarProductoParaDispositivoMovil(
            @Query("idDispositivo") String idDispositivo,
            @Query("acceso") String acceso
    );

    @Headers({
            "Content-Type: application/json",
            "application-id: B9D12B47-6B88-8471-FFAD-2B4FFD1EA100",
            "secret-key: 46C1AEC7-6BA7-D1C7-FF6A-FD9EA95C0C00",
            "application-type: REST"
    })
    @GET(SERVICE_NAME_USER + "/GetactualizarEstadoMigracionDispositivoMovil?")
    Call<String> GetactualizarEstadoMigracionDispositivoMovil(
            @Query("idDispositivo") String idDispositivo,
            @Query("idProducto") int idProducto,
            @Query("idUsuarioRegistro") int idUsuarioRegistro,
            @Query("acceso") String acceso
    );
}
