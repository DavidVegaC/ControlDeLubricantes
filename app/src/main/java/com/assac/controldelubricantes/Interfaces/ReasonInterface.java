package com.assac.controldelubricantes.Interfaces;

import com.assac.controldelubricantes.Entities.ProductEntity;
import com.assac.controldelubricantes.Entities.ReasonEntity;
import com.assac.controldelubricantes.Util.Const;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ReasonInterface {

    String SERVICE_NAME_USER= Const.SERVICE_NAME + "Reason";

    @Headers({
            "Content-Type: application/json",
            "application-id: B9D12B47-6B88-8471-FFAD-2B4FFD1EA100",
            "secret-key: 46C1AEC7-6BA7-D1C7-FF6A-FD9EA95C0C00",
            "application-type: REST"
    })
    @GET(SERVICE_NAME_USER + "/GetlistarRazonParaDispositivoMovil?")
    Call<List<ReasonEntity>> GetlistarRazonParaDispositivoMovil(
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
            @Query("idRazon") int idRazon,
            @Query("idUsuarioRegistro") int idUsuarioRegistro,
            @Query("acceso") String acceso
    );
}
