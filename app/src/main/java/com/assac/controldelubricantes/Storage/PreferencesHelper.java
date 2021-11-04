package com.assac.controldelubricantes.Storage;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static final String LOGINPREFERENCES = "LOGINPREFERENCES";
    private static final String PREFERENCES_ID_USER = LOGINPREFERENCES + ".idUsuario";
    private static final String PREFERENCES_USERLEVEL = LOGINPREFERENCES + ".userLevel";
    private static final String PREFERENCES_USERNAME = LOGINPREFERENCES + ".userName";
    private static final String PREFERENCES_PASSWORD = LOGINPREFERENCES + ".password";
    private static final String PREFERENCES_NAME = LOGINPREFERENCES + ".name";
    private static final String PREFERENCES_FIRSTNAME = LOGINPREFERENCES + ".firstName";
    private static final String PREFERENCES_LASTNAME = LOGINPREFERENCES + ".lastName";
    private static final String PREFERENCES_REGISTRATIONDATE = LOGINPREFERENCES + ".regisDate";


    private PreferencesHelper() {
        //no instance
    }

    public static void signOut(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(PREFERENCES_ID_USER);
        editor.remove(PREFERENCES_USERLEVEL);
        editor.remove(PREFERENCES_USERNAME);
        editor.remove(PREFERENCES_PASSWORD);
        editor.remove(PREFERENCES_NAME);
        editor.remove(PREFERENCES_FIRSTNAME);
        editor.remove(PREFERENCES_LASTNAME);
        editor.remove(PREFERENCES_REGISTRATIONDATE);
        editor.apply();
    }

    public static void saveSession(Context context, int idUser, int userLevel, String userName, String password, String name, String firstName, String lastName, String date)
    {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(PREFERENCES_ID_USER, idUser);
        editor.putInt(PREFERENCES_USERLEVEL, userLevel);
        editor.putString(PREFERENCES_USERNAME, userName);
        editor.putString(PREFERENCES_PASSWORD, password);
        editor.putString(PREFERENCES_NAME, name);
        editor.putString(PREFERENCES_FIRSTNAME, firstName);
        editor.putString(PREFERENCES_LASTNAME, lastName);
        editor.putString(PREFERENCES_REGISTRATIONDATE, date);
        editor.apply();
    }

    public static void updateSession(Context context, String operationName, String accessPoint)
    {
        SharedPreferences.Editor editor = getEditor(context);
        //editor.putString(PREFERENCES_OPERATIONNAME, operationName);
        //editor.putString(PREFERENCES_ACCESSPOINT, accessPoint);
        editor.apply();
    }

    public static String getUserSession(Context context)
    {
        SharedPreferences sharedPreferences= getSharedPreferences(context);
        String username= sharedPreferences.getString(PREFERENCES_USERNAME,null);

        return username;
    }

    public static String getNameUserSession(Context context)
    {
        SharedPreferences sharedPreferences= getSharedPreferences(context);
        String name= sharedPreferences.getString(PREFERENCES_NAME,"")+" "+sharedPreferences.getString(PREFERENCES_FIRSTNAME,"");

        return name;
    }

    public static int getIdUserSession(Context context)
    {
        SharedPreferences sharedPreferences= getSharedPreferences(context);
        int idUser= sharedPreferences.getInt(PREFERENCES_ID_USER,1);

        return idUser;
    }


    public static boolean isSignedIn(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFERENCES_USERNAME) &&
                preferences.contains(PREFERENCES_PASSWORD);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(LOGINPREFERENCES, Context.MODE_PRIVATE);
    }
}
