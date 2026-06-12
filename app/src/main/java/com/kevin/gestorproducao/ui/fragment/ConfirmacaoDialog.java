package com.kevin.gestorproducao.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ConfirmacaoDialog extends DialogFragment {
    public interface OnConfirmarListener {
        void onConfirmar();
    }

    public interface OnCancelarListener {
        void onCancelar();
    }

    private String titulo;
    private String mensagem;
    private OnConfirmarListener listenerConfirmar;
    private OnCancelarListener listenerCancelar;

    public static ConfirmacaoDialog novaInstancia(
        String titulo,
        String mensagem,
        OnConfirmarListener listenerConfirmar,
        OnCancelarListener listenerCancelar
    ) {
        ConfirmacaoDialog dialog = new ConfirmacaoDialog();
        dialog.titulo = titulo;
        dialog.mensagem = mensagem;
        dialog.listenerConfirmar = listenerConfirmar;
        dialog.listenerCancelar = listenerCancelar;

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
            .setTitle(titulo)
            .setMessage(mensagem)
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                if (listenerCancelar != null) listenerCancelar.onCancelar();
                dismiss();
            })
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                if (listenerConfirmar != null) listenerConfirmar.onConfirmar();
            })
            .create();
    }

    @Override
    public void onCancel(@NonNull android.content.DialogInterface dialog) {
        super.onCancel(dialog);

        if (listenerCancelar != null) {
            listenerCancelar.onCancelar();
        }
    }
}
