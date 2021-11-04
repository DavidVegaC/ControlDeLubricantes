package com.assac.controldelubricantes.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imrankst1221@gmail.com
 *
 */

public class Utils {
    // UNICODE 0x23 = '#'
    public static final byte[] UNICODE_TEXT = new byte[] {0x23, 0x23, 0x23,
            0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,
            0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,
            0x23, 0x23, 0x23};
    // UNICODE 0x3D = '='
    public static final byte[] UNICODE_TEXT_3D = new byte[] {0x3D, 0x3D, 0x3D,
            0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D,
            0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D,
            0x3D, 0x3D, 0x3D};
    // UNICODE 0x2D = '-'
    public static final byte[] UNICODE_TEXT_2D = new byte[] {0x2D, 0x2D, 0x2D,
            0x2D, 0x2D, 0x2D,0x2D, 0x2D, 0x2D,0x2D, 0x2D, 0x2D,0x2D, 0x2D, 0x2D,
            0x2D, 0x2D, 0x2D,0x2D, 0x2D, 0x2D,0x2D, 0x2D, 0x2D,0x2D, 0x2D, 0x2D,
            0x2D, 0x2D, 0x2D};

    public static final byte[] ALIGN_LEFT =new byte[]{0x1B, 'a',0x00};
    public static final byte[] ALIGN_CENTER =new byte[]{0x1B, 'a', 0x01};
    public static final byte[] ALIGN_RIGHT =new byte[]{0x1B, 'a', 0x02};

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };

    public static byte[] decodeBitmap(Bitmap bmp){
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;


        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;

        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", " height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString+widthHexString+heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    public static String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    public static String byteArrayToHexIntGeneral(final int[] bytes, int cantidad) {
        int a = 0;
        double x = 0;
        //for(byte b : bytes){
        for(int i = 0; i<cantidad; i++){
            a=a<<8;
            a=a|((int)(0xFF&bytes[i]));
        }
        a=a&0x00FFFFFF;

        return "" + a;
    }

    public static int byteArrayToHexInt(final int[] bytes, int cantidad) {
        int a = 0;
        double x = 0;
        int indBuffer = 0;
        //for(byte b : bytes){
        for(int i = 0; i<cantidad; i++){
            //a=a<<8;
            //a=a|((int)(0xFF&bytes[i]));
            //if(i ==0){
            //a = bytes[i];
            //}
            //if(i>0) {
            //bytes[i] = (int) bytes[i] << 8;
            //}
            //a = a|(0xFF& bytes[i]);

            a = a | (bytes[indBuffer])<<(i*8);
            indBuffer++;
        }
        a=a&0x00FFFFFF;

        return  a;
    }

    public static int byteArrayToHexInt2(final int[] bytes, int cantidad) {
        int a = 0;
        double x = 0;
        //for(byte b : bytes){
        for(int i = cantidad-1; i>=0; i--){
            a=a<<8;
            a=a|((int)(0xFF&bytes[i]));
        }
        a=a&0x00FFFFFF;

        return  a;
    }

    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            if(!str.equals("00")){
                output.append((char) Integer.parseInt(str, 16));
            }

        }

        return output.toString();
    }

    public static String byteArrayToHexString(final byte[] bytes, int cantidad) {
        StringBuilder sb = new StringBuilder();
        //for(byte b : bytes){
        for(int i = 0; i<cantidad; i++){
            sb.append(String.format("[%02x]", bytes[i]&0xFF));
            //sb.append((char)(0xFF&bytes[i]));
        }
        return sb.toString();
    }

    public static String byteArrayToHexString(final int[] bytes, int cantidad) {
        StringBuilder sb = new StringBuilder();
        //for(byte b : bytes){
        for(int i = 0; i<cantidad; i++){
            sb.append(String.format("%02x", bytes[i]&0xff));
            //sb.append((char)(0xFF&bytes[i]));
        }
        return sb.toString();
    }

    public static String byteArrayToHexString2(final int[] bytes, int cantidad) {
        StringBuilder sb = new StringBuilder();
        //for(byte b : bytes){
        for(int i = 0; i<cantidad; i++){
            sb.append(String.format("[%02x]", bytes[i]&0xff));
            //sb.append((char)(0xFF&bytes[i]));
        }
        return sb.toString();
    }

    public static String byteArrayToHexString3(final int[] bytes, int cantidad) {
        StringBuilder sb = new StringBuilder();
        //for(byte b : bytes){
        for(int i = 0; i<cantidad; i++){
            sb.append(String.format("%x", bytes[i]&0xff));
            //sb.append((char)(0xFF&bytes[i]));
        }
        return sb.toString();
    }

    public static String byteArrayToHexIntB(final int[] bytes, int cantidad) {
        int a = 0;
        double x = 0;
        //for(byte b : bytes){
        for(int i = 0; i<cantidad; i++){
            a=a<<8;
            a=a|((int)(0xFF&bytes[i]));
        }
        a=a&0x00FFFFFF;
        x = a/10.0;
        //a = a/10;
        //DecimalFormat form = new DecimalFormat("0.00");
        //return String.format("%.2f", a);
        return "" + x;//form.format(x);
        //return ""+a;
    }

    public static byte[] intTobyteArray(int numero, int cantidad){
        byte[] result = new byte[cantidad];
        int c=0;

        for(int i=cantidad-1;i>=0;i--){
            result[c]=(byte) (numero >> 8*i);
        }

        //result[0] = (byte) (numero >> 8);
        //result[1] = (byte) (numero >> 0);

        return result;
    }

    //Para NFC

    public static String convertHexToString(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }
        System.out.println("Decimal : " + temp.toString());

        return sb.toString();
    }

    public static String toHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(int i = bytes.length -1; i >= 0; i--){
            int b = bytes[i] & 0xff;
            if(b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if(i > 0){
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    //Para Foto
    public static String getStringImagen(Context context,Uri imageUri){
        String encodedImagen="";

        try
        {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(),imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            //byte[] imageBytes = decodeBitmap(bmp);
            encodedImagen = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        }catch(Exception e){
            //Toast.makeText(rootView.getContext(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.v("Parseando Imagen", e.getMessage());
        }

        return encodedImagen;
    }

    //Para FechaSQLITE
    public static String dateToDateSQLite(String date){
        String dateSQLite="";

        dateSQLite= date.substring(6,10)+"-"+date.substring(3,5)+"-"+date.substring(0,2);

        return dateSQLite;
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

}
