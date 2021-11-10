package com.assac.controldelubricantes.Interfaces;

import com.assac.controldelubricantes.Entities.ModelCompartmentEntity;
import com.assac.controldelubricantes.Entities.VehicleEntity;
import com.assac.controldelubricantes.Util.Const;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ModelCompartmentInterface {
    String SERVICE_NAME_USER= Const.SERVICE_NAME + "ModelCompartment";

    @Headers({
            "Content-Type: application/json",
            "application-id: B9D12B47-6B88-8471-FFAD-2B4FFD1EA100",
            "secret-key: 46C1AEC7-6BA7-D1C7-FF6A-FD9EA95C0C00",
            "application-type: REST"
    })
    @GET(SERVICE_NAME_USER + "/GetlistarModeloCompartimientoParaDispositivoMovil?")
    Call<List<ModelCompartmentEntity>> GetlistarModeloCompartimientoParaDispositivoMovil(
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
            @Query("idModeloCompartimiento") int idModeloCompartimiento,
            @Query("idUsuarioRegistro") int idUsuarioRegistro,
            @Query("acceso") String acceso
    );
}
