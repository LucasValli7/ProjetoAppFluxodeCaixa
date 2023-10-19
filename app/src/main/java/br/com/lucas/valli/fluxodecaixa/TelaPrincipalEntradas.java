package br.com.lucas.valli.fluxodecaixa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.lucas.valli.fluxodecaixa.Adapter.AdapterDadosEntrada;
import br.com.lucas.valli.fluxodecaixa.Model.DadosEntrada;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityTelaPrincipalEntradasBinding;

public class TelaPrincipalEntradas extends AppCompatActivity {

    private ActivityTelaPrincipalEntradasBinding binding;
    private AdapterDadosEntrada adapterDados;
    private List<DadosEntrada> dadosEntradas;
    private FirebaseFirestore db;
    private String usuarioID;
    private Date x = new Date();
    private String mes = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(x);
    private String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);

    Locale ptbr = new Locale("pt", "BR");
    private Double vazio = Double.parseDouble("0.00");




    @Override
    protected void onStart() {
        super.onStart();
        RecuperarDadosListaEntradas();
        RecuperarTotalEntradas();
        AbrirCalendario();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityTelaPrincipalEntradasBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        binding.tolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaPrincipalEntradas.this, TelaPrincipal.class);
                startActivity(intent);
            }
        });


    }
    public void AbrirCalendario() {

        Date x = new Date();
        String mes = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(x);
        String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
        binding.txtTeste.setText(mes+ "/" + ano);


    }
    public void RecuperarDadosListaEntradas(){

        dadosEntradas = new ArrayList<>();
        adapterDados = new AdapterDadosEntrada(getApplicationContext(), dadosEntradas);
        binding.ListaTipoEntrada.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.ListaTipoEntrada.setHasFixedSize(true);
        binding.ListaTipoEntrada.setAdapter(adapterDados);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("nova entrada")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                DadosEntrada dadosEntrada = queryDocumentSnapshot.toObject(DadosEntrada.class);
                                dadosEntradas.add(dadosEntrada);
                                adapterDados.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
    public void RecuperarTotalEntradas(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("Total de Entradas")
                .document("Total");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (!documentSnapshot.exists()) {
                    String vazioFormatado = NumberFormat.getCurrencyInstance(ptbr).format(vazio);
                    binding.ValorTotalEntradas.setText(vazioFormatado);
                }else {
                    Double totalEntrada = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaEntrada"));
                    String totalEntradaFormat = NumberFormat.getCurrencyInstance(ptbr).format(totalEntrada);
                    binding.ValorTotalEntradas.setText(totalEntradaFormat);
                }

                binding.btnAddEntrada.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!documentSnapshot.exists()) {
                            String valorVazioString = String.valueOf(vazio);
                            Intent intent = new Intent(TelaPrincipalEntradas.this, NovaEntrada.class);
                            intent.putExtra("valor", valorVazioString);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(TelaPrincipalEntradas.this, NovaEntrada.class);
                            intent.putExtra("valor", documentSnapshot.getString("ResultadoDaSomaEntrada"));
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }


}

