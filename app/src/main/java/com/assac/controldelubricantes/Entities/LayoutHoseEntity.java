package com.assac.controldelubricantes.Entities;

import android.widget.LinearLayout;

import com.assac.controldelubricantes.View.Extended.FormDialogTransaction;

import java.util.ArrayList;
import java.util.List;

public class LayoutHoseEntity {

    public LinearLayout inflater;
    public int idBomba;
    public int state;
    public FormDialogTransaction formDialogTransaction;

    public LayoutHoseEntity(LinearLayout inflater, int idBomba) {
        this.inflater = inflater;
        this.idBomba = idBomba;
        state=-1;
    }

    public LayoutHoseEntity() {
    }

    public LinearLayout getInflater() {
        return inflater;
    }

    public void setInflater(LinearLayout inflater) {
        this.inflater = inflater;
    }

    public int getIdBomba() {
        return idBomba;
    }

    public void setIdBomba(int idBomba) {
        this.idBomba = idBomba;
    }

    public FormDialogTransaction getFormDialogTransaction() {
        return formDialogTransaction;
    }

    public void setFormDialogTransaction(FormDialogTransaction formDialogTransaction) {
        this.formDialogTransaction = formDialogTransaction;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
