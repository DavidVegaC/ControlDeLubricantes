package com.assac.controldelubricantes.Listeners;

import com.assac.controldelubricantes.Entities.DataFormEntity;

public interface EmbeddedWifiListener {

    void sendBytesEmbedded(byte[] responseDataDevice, int direccion, int numeroBomba, String comentario, int razon);

    void receiveDataForm(DataFormEntity dataFormEntity);

    void scanearQRCode(int indiceBomba);

    void receiveQRCode(String stringQRCode);

    void readNFCPlate(int indiceBomba, int numeroBomba);

    void receiveNFCPlate(byte[] responseDataDevice);

}
