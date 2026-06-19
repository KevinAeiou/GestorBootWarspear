package com.kevin.gestorproducao.repository.helper;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import com.kevin.gestorproducao.repository.Resource;

import java.util.function.Consumer;

public class FirebaseTimeoutHelper {

    private static final long TIMEOUT_MS = 10000;

    public static <T> MutableLiveData<Resource<T>> execute(
        Consumer<Callback<T>> operation
    ) {
        MutableLiveData<Resource<T>> liveData = new MutableLiveData<>();

        Handler handler = new Handler(Looper.getMainLooper());

        Runnable timeoutRunnable = () -> liveData.postValue(
            new Resource<>(
                null,
                "A operação demorou muito. Tente novamente."
            )
        );

        handler.postDelayed(timeoutRunnable, TIMEOUT_MS);

        operation.accept(new Callback<T>() {
            @Override
            public void sucesso(T dado) {
                handler.removeCallbacks(timeoutRunnable);
                liveData.postValue(
                    new Resource<>(dado, null)
                );
            }

            @Override
            public void erro(String mensagem) {
                handler.removeCallbacks(timeoutRunnable);
                liveData.postValue(
                    new Resource<>(null, mensagem)
                );
            }
        });

        return liveData;
    }

    public interface Callback<T> {

        void sucesso(T dado);
        void erro(String mensagem);
    }
}