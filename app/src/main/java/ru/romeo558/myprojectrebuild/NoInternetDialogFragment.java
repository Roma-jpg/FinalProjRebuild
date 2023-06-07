package ru.romeo558.myprojectrebuild;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class NoInternetDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Создание представления фрагмента из макета
        View view = inflater.inflate(R.layout.dialog_no_internet, container, false);

        // Настройка обработчика нажатия на кнопку "OK"
        Button okButton = view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(v -> {
            if (isInternetAvailable()) {
                // Интернет доступен, продолжаем работу приложения
                dismiss();
            } else {
                // Закрытие приложения и удаление его из списка последних приложений
                requireActivity().finishAndRemoveTask();
            }
        });

        // Установка полупрозрачного фона для корневого представления
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setDimAmount(0.45f);

        return view;
    }

    private boolean isInternetAvailable() {
        // Проверка доступности интернета
        ConnectivityManager connectivityManager =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
