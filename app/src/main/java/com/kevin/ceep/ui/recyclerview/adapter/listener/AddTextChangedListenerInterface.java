package com.kevin.ceep.ui.recyclerview.adapter.listener;

import android.text.Editable;
import android.text.TextWatcher;

import com.kevin.ceep.model.Profissao;

public interface AddTextChangedListenerInterface extends TextWatcher {
    @Override
    void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2);

    @Override
    void onTextChanged(CharSequence charSequence, int i, int i1, int i2);

    @Override
    void afterTextChanged(Editable editable);

    void afterTextChangedMeu(Profissao profissao, Editable text);
}
