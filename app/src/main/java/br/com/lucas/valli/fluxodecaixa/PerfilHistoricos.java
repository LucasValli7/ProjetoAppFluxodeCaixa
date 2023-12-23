package br.com.lucas.valli.fluxodecaixa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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

    private Date x = new Date();
    private String mes = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(x);
    private String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItem;

    @Override
    protected void onStart() {
        super.onStart();
        Meses();
        Anos();
        Categoria();
        RecuperarMovimentacaoSaidaI();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilHistoricosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.btnPesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ano = binding.autoCompleteTextAno.getText().toString();
                String mes = binding.autoCompleteTextMes.getText().toString();
                String categoria = binding.autoCompleteTextCategoria.getText().toString();

                if (mes.isEmpty()  && ano.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, "Selecione uma data.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    snackbar.show();

                }else {
                    if (binding.checkEntrada.isChecked()){
                        RecuperarMovimentacaoEntrada();
                        Toast.makeText(PerfilHistoricos.this, "Categoria inexistente para essa opção.", Toast.LENGTH_LONG).show();

                    } else if (binding.checksSaida.isChecked()) {
                        if (categoria.isEmpty()){
                            Snackbar snackbar = Snackbar.make(v, "Selecione uma categoria", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                            snackbar.show();
                        }else {
                            RecuperarMovimentacaoSaida();
                        }
                    }
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
                                        AlertDialog.Builder builder = new AlertDialog.Builder(PerfilHistoricos.this);
                                        builder.setTitle("Atenção");
                                        builder.setMessage("deseja adicionar uma nova movimentação?");
                                        builder.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                AlertDialog.Builder builder = new AlertDialog.Builder(PerfilHistoricos.this);
                                                builder.setTitle("Òtimo!");
                                                builder.setMessage("Escolha um tipo de movimentação");
                                                builder.setPositiveButton("Saída", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                Intent intent = new Intent(PerfilHistoricos.this, NovaSaida.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });builder.setNegativeButton("Entrada", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(PerfilHistoricos.this, NovaEntrada.class);
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                });
                                                builder.show();

                                            }
                                        });
                                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();

                                            }
                                        });
                                        builder.show();

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
        String categoria = binding.autoCompleteTextCategoria.getText().toString();

        historicoSaidaList = new ArrayList<>();
        adapterHistoricoSaida = new AdapterHistoricoSaida(getApplicationContext(), historicoSaidaList);
        binding.listHistorico.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.listHistorico.setHasFixedSize(true);
        binding.listHistorico.setAdapter(adapterHistoricoSaida);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                .collection("nova saida").document("categoria").collection(categoria)
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
    public void RecuperarMovimentacaoSaidaI(){

        historicoSaidaList = new ArrayList<>();
        adapterHistoricoSaida = new AdapterHistoricoSaida(getApplicationContext(), historicoSaidaList);
        binding.listHistorico.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.listHistorico.setHasFixedSize(true);
        binding.listHistorico.setAdapter(adapterHistoricoSaida);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                .collection("nova saida").document("categoria").collection("Gastos Essenciais")
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
        String [] meses = {"01","02","03","04","05","06","07","08","09","10","11","12"};

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

        String [] anos = {"2023","2024","2025","2026","2027","2028","2029","2030","2031","2032","2033","2034"};
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
    public void Categoria(){
        String [] categoria = {"Gastos Essenciais", "Pagamento de Dívidas", "Desejos Pessoais"};
        autoCompleteTextView = findViewById(R.id.auto_complete_text_categoria);
        adapterItem = new ArrayAdapter<String>(this,R.layout.historico_perfil_item, categoria);
        autoCompleteTextView.setAdapter(adapterItem);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(PerfilHistoricos.this, Item+ " selecionado", Toast.LENGTH_SHORT).show();
            }
        });

    }
}