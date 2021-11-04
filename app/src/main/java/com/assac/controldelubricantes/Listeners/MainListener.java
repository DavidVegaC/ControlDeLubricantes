package com.assac.controldelubricantes.Listeners;

public interface MainListener {
    void enableForegroundDispatchSystem();
    void disableForegroundDispatchSystem();

    void showProgressDialog(String message);
    void dismissProgressDialog();

    void CloseSession();
}
