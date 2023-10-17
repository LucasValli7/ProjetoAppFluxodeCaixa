package br.com.lucas.valli.fluxodecaixa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.lucas.valli.fluxodecaixa.Adapter.AdapterDadosSaida;
import br.com.lucas.valli.fluxodecaixa.Model.DadosEntrada;
import br.com.lucas.valli.fluxodecaixa.Model.DadosSaida;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityTelaPrincipalSaidasBinding;

public class TelaPrincipalSaidas extends AppCompatActivity {
    ActivityTelaPrincipalSaidasBinding binding;
    private AdapterDadosSaida adapterDadosSaida;
    private List<DadosSaida> dadosSaidaList;
    private FirebaseFirestore db;
    private String usuarioID;

    Locale ptbr = new Locale("pt", "BR");
    private Double vazio = Double.parseDouble("0.00");

    @Override
    protected void onStart() {
        super.onStart();
        RecuperarDadosListaSaida();
        RecuperarTotalSaidas();
        AbrirCalendario();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityTelaPrincipalSaidasBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        binding.tolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }
    public void AbrirCalendario() {

        Date x = new Date();
        String Mes = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(x);
        String Ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
        binding.txtTeste.setText(Mes+ "/" + Ano);
    }
    public void RecuperarDadosListaSaida(){
        Date x = new Date();
        String mes = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(x);
        String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);

        dadosSaidaList = new ArrayList<>();
        adapterDadosSaida = new AdapterDadosSaida(getApplicationContext(),dadosSaidaList);
        binding.ListaTipoSaida.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.ListaTipoSaida.setHasFixedSize(true);
        binding.ListaTipoSaida.setAdapter(adapterDadosSaida);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("nova saida")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                DadosSaida dadosSaida = queryDocumentSnapshot.toObject(DadosSaida.class);
                                dadosSaidaList.add(dadosSaida);
                                adapterDadosSaida.notifyDataSetChanged();
                            }
                        }
                    }
                });

    }
    public void RecuperarTotalSaidas(){

        Date x = new Date();
        String mes = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(x);
        String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de Saidas")
                .document("Total");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (!documentSnapshot.exists()) {
                    String vazioFormatado = NumberFormat.getCurrencyInstance(ptbr).format(vazio);
                    binding.ValorTotalSaidas.setText(vazioFormatado);
                }else {
                    Double totalSaida = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaSaida"));
                    String totalSaidaFormatado = NumberFormat.getCurrencyInstance(ptbr).format(totalSaida);
                    binding.ValorTotalSaidas.setText(totalSaidaFormatado);
                }
                binding.btnAddSaida.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!documentSnapshot.exists()) {
                            String valorVazioString = String.valueOf(vazio);
                            Intent intent = new Intent(TelaPrincipalSaidas.this, NovaSaida.class);
                            intent.putExtra("valor", valorVazioString);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(TelaPrincipalSaidas.this,NovaSaida.class);
                            intent.putExtra("valor", documentSnapshot.getString("ResultadoDaSomaSaida"));
                            startActivity(intent);
                        }

                    }
                });
            }
        });
    }
}