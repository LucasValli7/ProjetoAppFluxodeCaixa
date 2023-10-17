package br.com.lucas.valli.fluxodecaixa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import br.com.lucas.valli.fluxodecaixa.databinding.ActivityPerfilUsuarioBinding;

public class PerfilUsuario extends AppCompatActivity {

    private ActivityPerfilUsuarioBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
    }
}