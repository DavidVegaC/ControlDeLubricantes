package com.assac.controldelubricantes.Util.Wifi;

import android.util.Log;

import com.assac.controldelubricantes.Entities.DataFormEntity;

import static com.assac.controldelubricantes.Util.Utils.byteArrayToHexString;
import static com.assac.controldelubricantes.Util.Utils.hexStringToBytes;
import static com.assac.controldelubricantes.Util.Utils.intTobyteArray;

/**
 * Created by Proyectos on 21/06/2017.
 */

public class EmbeddedPtcl {
    public final static int b_ext_solicita_estado = 0x0A;
    public final static int b_ext_leer_num_trans = 0x16;

    /*ESTADO CONFIGURACION TABLET*/

    public final static int b_ext_configuracion = 0x06;
    public final static int b_ext_cambio_estado = 0x01;
    public final static int b_ext_ack_false = 0x10;
    public final static int b_ext_enviar_data_nfc = 0x02;
    public final static int b_ext_enviar_data_formulario = 0x08;
    public final static int b_ext_solicitar_transaccion_for_ticket = 0x03;
    public final static int b_ext_solicitar_info_transacciones = 0x04;


    //ESTADO MPC - VALOR DE OPCODE
    public final static int v_opc_MPC = 0x67;

    //ESTADO MPC - VALOR DE OPCODE
    public final static int v_opc_TR_Procesada = 0x69;

    //TIPOS DE TAG VEHICULO (VID Y ANILLO)
    public final static int tag_vehiculo = 0x41;//Vehiculo: A
    public final static int tag_id_vehiculo = 0x44;//Vehiculo ID: D
    public final static int tag_vehiculo_driver_id_v = 0x45;//Vehiculo con conductor: E
    public final static int tag_vehiculo_customer_v = 0x46;//Vehiculo con cliente: F
    public final static int tag_vehiculo_department_v = 0x47;//Vehiculo con departamento: G

    //TIPOS DE TAG LLAVERO
    public final static int tag_driver_v = 0x42;//Vehiculo: B
    public final static int tag_grifero = 0x43;//Grifero: C
    public final static int tag_driver_id = 0x48;//Conductor: H
    public final static int tag_vehiculo_driver_id_k = 0x49;//Vehiculo con conductor: I
    public final static int tag_vehiculo_customer_k = 0x4A;//Vehiculo con cliente: J
    public final static int tag_vehiculo_department_k = 0x4B;//Vehiculo con departamento: K
    public final static int tag_vehiculo_recarga = 0x4C;//Vehiculo cisterna de recarga: L

    //TAG POR MODELO
    public final static int tag_model_04 = 0x04;
    public final static int tag_model_07 = 0x07;

    //NO HAY ABASTECIMIENTO - VALOR DE XOR(Código de Verificación)
    public final static int v_XOR_sin_abastecimiento = 0x6f;
    //INICIA ABASTECIMIENTO - VALOR DE XOR(Código de Verificación)
    public final static int v_XOR_inicia_abastecimiento = 0x6d;
    //AUTORIZA ABASTECIMIENTO - VALOR DE XOR(Código de Verificación)
    public final static int v_XOR_autoriza_abastecimiento = 0x6c;
    //TERMINA ABASTECIMIENTO - VALOR DE XOR(Código de Verificación)
    public final static int v_XOR_termina_abastecimiento = 0x73;
    //ABASTECIMIENTO PROCESADO - VALOR DE XOR(Código de Verificación)
    public final static int v_XOR_abastecimiento_procesado = 0x16;

    //ABASTECIMIENTO PROCESADO - VALOR DE XOR(Código de Verificación)
    public final static int v_estado_sin_abastecimiento = 0x00;
    //VALIDA VEHICULO - VALOR DE XOR(Código de Verificación)
    public final static int v_estado_valida_vehiculo_abastecimiento = 0x01;
    //INICIA ABASTECIMIENTO - VALOR DE XOR(Código de Verificación)
    public final static int v_estado_inicia_abastecimiento = 0x02;
    //AUTORIZA ABASTECIMIENTO - VALOR DE XOR(Código de Verificación)
    public final static int v_estado_autoriza_abastecimiento = 0x03;
    //TERMINA ABASTECIMIENTO - VALOR DE XOR(Código de Verificación)
    public final static int v_estado_termina_abastecimiento = 0x04;

    public static int crcEasyFuel2(byte[] buffer){
        int crc=0;
        int longitud=((int)buffer[1])|(((int)buffer[2])<<8);
        int i;
        for(i=0;i<longitud-2;i++){
            crc=crc^buffer[i];
        }
        return crc;
    }

    public static int crcEasyFuel1(byte[] buffer){
        int crc=0;
        int longitud=(int)buffer[1];
        int i;
        for(i=0;i<longitud-2;i++){
            crc=crc^buffer[i];
        }
        return crc;
    }

    public static int EmbeddedPtclSolicitaEstado(byte[] buffer, int direccion){
        int longitud=7;
        buffer[0]=0x02;
        buffer[1]=(byte)(longitud>>0);
        buffer[2]=(byte)(longitud>>8);
        buffer[3]=(byte)direccion;
        buffer[4]=(byte)b_ext_solicita_estado;
       // buffer[5]=(byte)crcEasyFuel2(buffer);
        buffer[6]=0x03;
        return longitud;
    }

    public static int EmbeddedPtclSolicitaEstado1(byte[] buffer, int direccion){
        int longitud=6;
        buffer[0]=0x02;
        buffer[1]=(byte)(longitud);
        buffer[2]=(byte)direccion;
        buffer[3]=(byte)b_ext_solicita_estado;
        buffer[4]=(byte)crcEasyFuel1(buffer);
        buffer[5]=0x03;
        return longitud;
    }

    public static int EmbeddedPtclCantidadTransacciones(byte[] buffer, int direccion){
        int longitud=7;
        buffer[0]=0x02;
        buffer[1]=(byte)(longitud>>0);
        buffer[2]=(byte)(longitud>>8);
        buffer[3]=(byte)direccion;
        buffer[4]=(byte)b_ext_leer_num_trans;
       // buffer[5]=(byte)crcEasyFuel2(buffer);
        buffer[6]=0x03;
        return longitud;
    }
    //NUEVAS TRAMAS 26082020
    //TRAMA DE CONFIGURACION

    public static int aceptarTramaConfiguracion(byte[] buffer, int direccion, int opcCodePrincipal, int opcCodeSecundario){
        int longitud=11;
        buffer[0]=0x02;
        buffer[1]=0x0B;
        buffer[2]=0x00;
        buffer[3]=(byte)direccion;
        buffer[4]=(byte)opcCodePrincipal; //opcode principal
        buffer[5]=(byte)opcCodeSecundario;
        buffer[6]=0x00;
        buffer[7]=0x00;
        buffer[8]=0x01;
        buffer[9]=(byte)crcEasyFuel2(buffer);
        buffer[10]=0x03;
        return longitud;
    }

    //TRAMA DE ESTADOS ADICIONALES

    public static int ackWifi(byte[] buffer, int direccion, int opcCodePrincipal, int opcCodeSecundario, int indiceBomba, int numeroBomba,int flagError, int codigoError){
        int longitud=12;
        buffer[0]=0x02;
        buffer[1]=0x0C;
        buffer[2]=0x00;
        buffer[3]=(byte)direccion;
        buffer[4]=(byte)opcCodePrincipal; //opcode principal
        buffer[5]=(byte)opcCodeSecundario;
        buffer[6]=(byte)indiceBomba;
        buffer[7]=(byte)numeroBomba;
        buffer[8]=(byte)flagError;
        buffer[9]=(byte)codigoError;
        buffer[10]=(byte)crcEasyFuel2(buffer);
        buffer[11]=0x03;
        return longitud;
    }

    public static int enviarDataFormularioWifi(byte[] buffer, int direccion, int opcCodePrincipal, int opcCodeSecundario, DataFormEntity dataFormEntity){


        int longitud=124;
        int intNumber=0, decNumberInt=0;
        String str="";
        buffer[0]=0x02;
        buffer[1]=0x7c;
        buffer[2]=0x00;
        buffer[3]=(byte)direccion;
        buffer[4]=(byte)opcCodePrincipal; //opcode principal
        buffer[5]=(byte)opcCodeSecundario;
        buffer[6]=0x00;
        buffer[7]=(byte)dataFormEntity.numeroBomba;

        //16 y 17 - PreSeteo

        //byte[] bufferPreSeteo= intTobyteArray(dataFormEntity.preSeteo,2);
        //buffer[16] = bufferPreSeteo[0];
        //buffer[17] = bufferPreSeteo[1];

        int c=0;
        if(dataFormEntity.isSolicitaKilometraje()){//8-11
            str = ""+dataFormEntity.kilometraje;

            Log.v("Kilo",""+str);
            intNumber = Integer.parseInt(str.substring(0, str.indexOf('.')));
            buffer[8] = (byte) (intNumber >> 16);
            buffer[9] = (byte) (intNumber >> 8);
            buffer[10] = (byte) (intNumber >> 0);

            decNumberInt = Integer.parseInt(str.substring(str.indexOf('.') + 1));
            buffer[11] = (byte) (decNumberInt >> 0);
        }

        if(dataFormEntity.isSolicitaHorometro()){//12-15
            str = String.valueOf(dataFormEntity.horometro);
            Log.v("Horo",""+str);
            intNumber = Integer.parseInt(str.substring(0, str.indexOf('.')));
            buffer[12] = (byte) (intNumber >> 16);
            buffer[13] = (byte) (intNumber >> 8);
            buffer[14] = (byte) (intNumber >> 0);

            decNumberInt = Integer.parseInt(str.substring(str.indexOf('.') + 1));
            buffer[15] = (byte) (decNumberInt >> 0);
        }


        if(dataFormEntity.isSolicitaPreseteo()){
            buffer[16] = (byte) (dataFormEntity.preSeteo >> 8);
            buffer[17] = (byte) (dataFormEntity.preSeteo >> 0);
        }

        if(dataFormEntity.isSolicitaConductor()){
            buffer[18]=0x01;
            //llenar datos conductor  buffer[19] - buffer[46]

            //IdConductor buffer[19] - buffer[26]
            c=0;
            /*char[] arrayId = dataFormEntity.getIdConductor().toCharArray();
            c=19;
            for(int i=0;i<arrayId.length;i++){
                buffer[c] = (byte)arrayId[i];
                c++;
            }*/

            c=19;
            for(int i=0;i<=7;i++){
                buffer[c]=(byte)(((((int)dataFormEntity.getIdConductor().charAt(2*i))-48)<<4)|(((int)dataFormEntity.getIdConductor().charAt(2*i+1))-48));
                c++;
            }


            /*
            //NombreConductor buffer[27] - buffer[46]
            char[] arrayNombre = dataFormEntity.getNombreConductor().toCharArray();
            c=27;
            for(int i=0;i<arrayNombre.length;i++){
                buffer[c] = (byte)arrayNombre[i];
                c++;
            }*/

        }else
            buffer[18]=0x00;

        if(dataFormEntity.isSolicitaOperador()){
            buffer[47]=0x01;
            //llenar datos operador buffer[48] - buffer[75]
            //IdConductor buffer[48] - buffer[55]
            //NombreConductor buffer[56] - buffer[75]
        }else
            buffer[47]=0x00;

        //llenar datos latitud buffer[76] - buffer[87]
        //llenar datos longitud buffer[88] - buffer[99]

        //llenar razon buffer[100]

        buffer[100] = (byte)dataFormEntity.razon;

        //llenar motivo buffer[101]
        buffer[101] = (byte)dataFormEntity.razon;

        //llenar comentario buffer[102] - buffer[121]
        char[] arrayComentario = dataFormEntity.getComentario().toCharArray();
        c=102;
        for(int i=0;i<arrayComentario.length;i++){
            buffer[c] = (byte)arrayComentario[i];
            c++;
        }

        buffer[122]=(byte)crcEasyFuel2(buffer);
        buffer[123]=0x03;
        return longitud;
    }

    public static int enviarDataNFC(byte[] buffer, byte[] responseDataDevice, int direccion, int opcCodePrincipal, int numeroBomba, String comentario, int razon){
        int longitud=86;

        buffer[0]=0x02;
        buffer[1]=0x56;
        buffer[2]=0x00;

        buffer[3]=(byte)direccion;
        buffer[4]=(byte)opcCodePrincipal; //opcode principal

        switch (responseDataDevice[0]){
            case tag_vehiculo:
            case tag_driver_v:
                                buffer[5]=0x04;
                                break;
            default:
                                break;
        }

        buffer[6]=0x00;
        buffer[7]=(byte)numeroBomba;

        //PLACA
        System.arraycopy(responseDataDevice, 1, buffer, 8, 10);

        //IDVEHICULO
        System.arraycopy(responseDataDevice, 292, buffer, 18, 8);

        //VID
        System.arraycopy(responseDataDevice, 11, buffer, 26, 4);

        //TIPO TAG
        buffer[30]=responseDataDevice[0];

        //CODIGO PRODUCTO
        buffer[31]=responseDataDevice[18];

        switch (responseDataDevice[0]){
            //ANILLO - VEHICULO
            case tag_vehiculo:
                //IDCOMPARTIMIENTO
                buffer[32]=responseDataDevice[30];

                //CODIGO CLIENTE
                System.arraycopy(responseDataDevice, 21, buffer, 33, 2);

                //CODIGO AREA
                buffer[35]=responseDataDevice[23];

                //PREFIX
                System.arraycopy(responseDataDevice, 28, buffer, 36, 2);
                break;
            //LLAVERO - VEHICULO
            case tag_driver_v:
                //IDCOMPARTIMIENTO
                //buffer[32]=responseDataDevice[30];

                //CODIGO CLIENTE
                System.arraycopy(responseDataDevice, 41, buffer, 33, 2);

                //CODIGO AREA
                buffer[35]=responseDataDevice[43];

                //PREFIX
                System.arraycopy(responseDataDevice, 45, buffer, 36, 2);
                break;
            default:
                break;
        }


        //LATITUD


        //LONGITUD


        //RAZON
        buffer[62]=(byte)razon;

        //MOTIVO
        buffer[63]=(byte)razon;

        //COMENTARIO
        char[] arrayComentario = comentario.toCharArray();
        int c=64;
        for(int i=0;i<arrayComentario.length;i++){
            buffer[c] = (byte)arrayComentario[i];
            c++;
        }

        buffer[84]=(byte)crcEasyFuel2(buffer);
        buffer[85]=0x03;
        return longitud;
    }

    public static int solicitarInfo(byte[] buffer, int direccion, int opcCodePrincipal, int opcCodeSecundario){
        int longitud=10;
        buffer[0]=0x02;
        buffer[1]=0x0A;
        buffer[2]=0x00;
        buffer[3]=(byte)direccion;
        buffer[4]=(byte)opcCodePrincipal; //opcode principal
        buffer[5]=(byte)opcCodeSecundario;
        buffer[6]=0x00;
        buffer[7]=0x00;
        buffer[8]=(byte)crcEasyFuel2(buffer);
        buffer[9]=0x03;
        return longitud;
    }

    public static int solicitarTransactionForTicket(byte[] buffer, int direccion, int opcCodePrincipal, int opcCodeSecundario, int ticketASolicitar){
        int longitud=13;
        buffer[0]=0x02;
        buffer[1]=0x0D;
        buffer[2]=0x00;
        buffer[3]=(byte)direccion;
        buffer[4]=(byte)opcCodePrincipal; //opcode principal
        buffer[5]=(byte)opcCodeSecundario;
        buffer[6]=0x00;
        buffer[7]=0x00;

        buffer[8] = (byte) (ticketASolicitar >> 16);
        buffer[9] = (byte) (ticketASolicitar >> 8);
        buffer[10] = (byte) (ticketASolicitar >> 0);

        buffer[11]=(byte)crcEasyFuel2(buffer);
        buffer[12]=0x03;
        return longitud;
    }

}
