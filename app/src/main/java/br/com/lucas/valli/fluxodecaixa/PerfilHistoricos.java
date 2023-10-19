package br.com.lucas.valli.fluxodecaixa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.lucas.valli.fluxodecaixa.Adapter.AdapterHistoricoEntrada;
import br.com.lucas.valli.fluxodecaixa.Adapter.AdapterHistoricoSaida;
import br.com.lucas.valli.fluxodecaixa.Model.HistoricoEntrada;
import br.com.lucas.valli.fluxodecaixa.Model.HistoricoSaida;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityPerfilHistoricosBinding;

public class PerfilHistoricos extends AppCompatActivity {
    private ActivityPerfilHistoricosBinding binding;
    private AdapterHistoricoEntrada adapterHistoricoEntrada;
    private List<HistoricoEntrada> historicoEntradaList;

    private AdapterHistoricoSaida adapterHistoricoSaida;
    private List<HistoricoSaida> historicoSaidaList;
    private FirebaseFirestore db;
    private String usuarioID;
    Locale ptbr = new Locale("pt", "BR");
    private Double vazio = Double.parseDouble("0.00");

    String [] meses = {"janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho",
            "agosto", "setembro", "outubro", "novembro", "dezembro"};
    String [] anos = {"2023","2024","2025","2026","2027","2028","2029","2030","2031","2032","2033","2034"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilHistoricosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        Meses();
        Anos();
        RecuperarMovimentacaoEntrada();

        binding.autoCompleteTextMes.addTextChangedListener(ButtonPesquisa);

        binding.btnPesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.checkEntrada.isChecked()){
                    RecuperarMovimentacaoEntrada();
                } else if (binding.checksSaida.isChecked()) {
                    RecuperarMovimentacaoSaida();
                }

            }
        });
    }
    public void RecuperarMovimentacaoEntrada(){

        String ano = binding.autoCompleteTextAno.getText().toString();
        String mes = binding.autoCompleteTextMes.getText().toString();

        historicoEntradaList = new ArrayList<>();
        adapterHistoricoEntrada = new AdapterHistoricoEntrada(getApplicationContext(), historicoEntradaList);
        binding.listHistorico.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.listHistorico.setHasFixedSize(true);
        binding.listHistorico.setAdapter(adapterHistoricoEntrada);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("nova entrada")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                HistoricoEntrada historicoPerfil = queryDocumentSnapshot.toObject(HistoricoEntrada.class);
                                historicoEntradaList.add(historicoPerfil);
                                adapterHistoricoEntrada.notifyDataSetChanged();

                            }
                            DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("Total de Entradas")
                                    .document("Total");
                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                                    if (!documentSnapshot.exists()) {
                                        String vazioFormatado = NumberFormat.getCurrencyInstance(ptbr).format(vazio);
                                        binding.ValorTotalMovimentacao.setText(vazioFormatado);
                                    }else {
                                        Double totalMovimentacaoEntrada = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaEntrada"));
                                        String totalMovimentacaoEntradaFormat = NumberFormat.getCurrencyInstance(ptbr).format(totalMovimentacaoEntrada);
                                        binding.ValorTotalMovimentacao.setText(totalMovimentacaoEntradaFormat);

                                    }
                                }
                            });
                        }
                    }
                });
    }
    public void RecuperarMovimentacaoSaida(){

        String ano = binding.autoCompleteTextAno.getText().toString();
        String mes = binding.autoCompleteTextMes.getText().toString();

        historicoSaidaList = new ArrayList<>();
        adapterHistoricoSaida = new AdapterHistoricoSaida(getApplicationContext(), historicoSaidaList);
        binding.listHistorico.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.listHistorico.setHasFixedSize(true);
        binding.listHistorico.setAdapter(adapterHistoricoSaida);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("nova saida")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                HistoricoSaida historicoPerfil = queryDocumentSnapshot.toObject(HistoricoSaida.class);
                                historicoSaidaList.add(historicoPerfil);
                                adapterHistoricoSaida.notifyDataSetChanged();

                            }
                            DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de Saidas")
                                    .document("Total");
                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                                    if (!documentSnapshot.exists()) {
                                        String vazioFormatado = NumberFormat.getCurrencyInstance(ptbr).format(vazio);
                                        binding.ValorTotalMovimentacao.setText(vazioFormatado);
                                    }else {
                                        Double totalMovimentacaoSaida = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaSaida"));
                                        String totalMovimentacaoSaidaFormat = NumberFormat.getCurrencyInstance(ptbr).format(totalMovimentacaoSaida);
                                        binding.ValorTotalMovimentacao.setText(totalMovimentacaoSaidaFormat);
                                    }
                                }
                            });
                        }
                    }
                });
    }
    public void Meses(){
        autoCompleteTextView = findViewById(R.id.auto_complete_text_mes);
        adapterItem = new ArrayAdapter<String>(this,R.layout.historico_perfil_item, meses);
        autoCompleteTextView.setAdapter(adapterItem);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(PerfilHistoricos.this, Item+ " selecionado", Toast.LENGTH_SHORT).show();


            }
        });

    }
    public void Anos(){
        autoCompleteTextView = findViewById(R.id.auto_complete_text_ano);
        adapterItem = new ArrayAdapter<String>(this,R.layout.historico_perfil_item, anos);
        autoCompleteTextView.setAdapter(adapterItem);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(PerfilHistoricos.this, Item+ " selecionado", Toast.LENGTH_SHORT).show();
            }
        });

    }
    TextWatcher ButtonPesquisa = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String  ano = binding.autoCompleteTextAno.getText().toString();

            if (ano == "Ano" ){
                binding.btnPesquisa.setEnabled(false);
            }else {
                binding.btnPesquisa.setEnabled(true);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}