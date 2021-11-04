package com.assac.controldelubricantes.Listeners;

import com.assac.controldelubricantes.Entities.DataFormEntity;

public interface EmbeddedWifiListener {

    void sendBytesEmbedded(byte[] responseDataDevice, int direccion, int numeroBomba);

    void receiveDataForm(DataFormEntity dataFormEntity);

    void scanearQRCode(int indiceBomba);

    void receiveQRCode(String stringQRCode);

    void readNFCPlate(int indiceBomba);

    void receiveNFCPlate(byte[] responseDataDevice);

}
