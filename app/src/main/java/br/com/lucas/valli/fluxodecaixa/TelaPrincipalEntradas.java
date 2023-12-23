package br.com.lucas.valli.fluxodecaixa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
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
import br.com.lucas.valli.fluxodecaixa.Model.DadosSaidaE;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityTelaPrincipalEntradasBinding;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TelaPrincipalEntradas extends AppCompatActivity {

    private ActivityTelaPrincipalEntradasBinding binding;
    private AdapterDadosEntrada adapterDados;
    private List<DadosEntrada> dadosEntradas;
    private FirebaseFirestore db;
    private String usuarioID;
    private Date x = new Date();
    private String mes = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(x);
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

                                // efeito Swipe
                                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                                    @Override
                                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                        return false;
                                    }

                                    @Override
                                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                        final int position = viewHolder.getLayoutPosition();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(TelaPrincipalEntradas.this);
                                        builder.setTitle("Atenção");
                                        builder.setMessage("deseja excluir esse item?");
                                        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DadosEntrada item = dadosEntradas.get(position);

                                                //document reference total Geral
                                                DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("Total de Entradas")
                                                        .document("Total");

                                                DocumentReference documentReferenceV = db.collection(usuarioID).document(ano).collection(mes).document("entradas")
                                                        .collection("nova entrada").document(item.getId());

                                                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                                                        documentReferenceV.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshotV, @Nullable FirebaseFirestoreException error) {
                                                                if (documentSnapshotV.exists()){
                                                                    Double totalEntrada = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaEntrada"));
                                                                    Double totalEntrada3 = Double.parseDouble(documentSnapshotV.getString("ValorDeEntradaDouble"));

                                                                    Double soma = totalEntrada - totalEntrada3;
                                                                    String cv = String.valueOf(soma);

                                                                    db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("Total de Entradas")
                                                                            .document("Total").update("ResultadoDaSomaEntrada",cv);


                                                                    db.collection(usuarioID).document(ano).collection(mes).document("entradas")
                                                                            .collection("nova entrada")
                                                                            .document(item.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        Toast.makeText(TelaPrincipalEntradas.this, "item excluido com sucesso", Toast.LENGTH_LONG).show();
                                                                                        dadosEntradas.remove(viewHolder.getAdapterPosition());
                                                                                        adapterDados.notifyDataSetChanged();

                                                                                    }else {

                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

                                            }
                                        });
                                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                adapterDados.notifyItemChanged(viewHolder.getAdapterPosition());
                                            }
                                        });
                                        builder.show();

                                    }

                                    public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                                .addSwipeLeftBackgroundColor(ContextCompat.getColor(TelaPrincipalEntradas.this, R.color.red))
                                                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                                .addSwipeLeftLabel("Excluir")
                                                .setSwipeLeftLabelColor(ContextCompat.getColor(TelaPrincipalEntradas.this, R.color.white))
                                                .create()
                                                .decorate();

                                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                    }

                                };
                                new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.ListaTipoEntrada);


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

