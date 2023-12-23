package br.com.lucas.valli.fluxodecaixa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.lucas.valli.fluxodecaixa.Adapter.AdapterContasApagar;
import br.com.lucas.valli.fluxodecaixa.Adapter.AdapterDadosEntrada;
import br.com.lucas.valli.fluxodecaixa.Adapter.AdapterHistoricoEntrada;
import br.com.lucas.valli.fluxodecaixa.Model.ContasApagar;
import br.com.lucas.valli.fluxodecaixa.Model.DadosEntrada;
import br.com.lucas.valli.fluxodecaixa.Model.DadosSaidaE;
import br.com.lucas.valli.fluxodecaixa.Model.HistoricoEntrada;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityContasApagarBinding;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class ContasAPagar extends AppCompatActivity {
    private ActivityContasApagarBinding binding;
    private AdapterContasApagar adapterContasApagar;
    private List<ContasApagar> contasApagar;
    private FirebaseFirestore db;
    private String usuarioID;
    private Date x = new Date();
    private String mes = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(x);
    private String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
    private String dia = new SimpleDateFormat("dd", new Locale("pt", "BR")).format(x);
    private String testeData = dia + mes + ano;
    Locale ptbr = new Locale("pt", "BR");
    private Double vazio = Double.parseDouble("0.00");
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityContasApagarBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.btnPesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ano = binding.autoCompleteTextAno.getText().toString();
                String mes = binding.autoCompleteTextMes.getText().toString();

                if (mes.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Selecione um mês", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    snackbar.show();

                } else if (ano.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Selecione um ano", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    snackbar.show();
                } else {
                    RecuperarDadosContasAPagar();
                    RecuperarTotalContasAPagar();

                }
            }




        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        RecuperarDadosContasAPagarI();
        RecuperarTotalContasAPagarI();
        Meses();
        Anos();
    }


    public void RecuperarTotalContasAPagar(){

        String ano2 = binding.autoCompleteTextAno.getText().toString();
        String mes2 = binding.autoCompleteTextMes.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection(usuarioID).document(ano2).collection(mes2).document("saidas")
                .collection("Total Contas A Pagar").document("Total");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (!documentSnapshot.exists()) {
                    String vazioFormatado = NumberFormat.getCurrencyInstance(ptbr).format(vazio);
                    binding.ValorTotalContasAPagar.setText(vazioFormatado);
                }else {
                    Double totalContasP = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaSaidaC"));
                    String totalContasPFormat = NumberFormat.getCurrencyInstance(ptbr).format(totalContasP);
                    binding.ValorTotalContasAPagar.setText(totalContasPFormat);
                }

            }
        });
    }
    public void RecuperarDadosContasAPagar(){

        String ano2 = binding.autoCompleteTextAno.getText().toString();
        String mes2 = binding.autoCompleteTextMes.getText().toString();

        contasApagar = new ArrayList<>();
        adapterContasApagar = new AdapterContasApagar(getApplicationContext(), contasApagar);
        binding.listaContasAPagar.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.listaContasAPagar.setHasFixedSize(true);
        binding.listaContasAPagar.setAdapter(adapterContasApagar);


        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(usuarioID).document(ano2).collection(mes2).document("saidas").collection("ContasApagar")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                ContasApagar contasAPagar = queryDocumentSnapshot.toObject(ContasApagar.class);
                                contasApagar.add(contasAPagar);
                                adapterContasApagar.notifyDataSetChanged();


                                // efeito Swipe
                                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                                    @Override
                                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                        return false;
                                    }

                                    @Override
                                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                        final int position = viewHolder.getLayoutPosition();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ContasAPagar.this);
                                        builder.setTitle("Atenção");
                                        builder.setMessage("deseja excluir esse item?");
                                        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ContasApagar item = contasApagar.get(position);

                                                DocumentReference documentReferenceCTotal = db.collection(usuarioID).document(ano2).collection(mes2).document("saidas")
                                                        .collection("Total Contas A Pagar").document("Total");

                                                DocumentReference documentReferenceC = db.collection(usuarioID).document(ano2).collection(mes2).document("saidas").collection("ContasApagar")
                                                        .document(item.getId());

                                                documentReferenceCTotal.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshotCTotal, @Nullable FirebaseFirestoreException error) {
                                                        documentReferenceC.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshotC, @Nullable FirebaseFirestoreException error) {
                                                                if (documentSnapshotC.exists()){
                                                                    Double totalContasAPagar = Double.parseDouble(documentSnapshotCTotal.getString("ResultadoDaSomaSaidaC"));
                                                                    Double valorDoubleC = Double.parseDouble(documentSnapshotC.getString("ValorDeSaidaDouble"));
                                                                    Double operacao = totalContasAPagar - valorDoubleC;
                                                                    String cv = String.valueOf(operacao);

                                                                    db.collection(usuarioID).document(ano2).collection(mes2).document("saidas")
                                                                            .collection("Total Contas A Pagar").document("Total").update("ResultadoDaSomaSaidaC", cv);

                                                                    db.collection(usuarioID).document(ano2).collection(mes2).document("saidas").collection("ContasApagar")
                                                                            .document(item.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        Toast.makeText(getApplicationContext(), "item excluido com sucesso", Toast.LENGTH_LONG).show();
                                                                                        contasApagar.remove(viewHolder.getAdapterPosition());
                                                                                        adapterContasApagar.notifyDataSetChanged();

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
                                                adapterContasApagar.notifyItemChanged(viewHolder.getAdapterPosition());
                                            }
                                        });
                                        builder.show();

                                    }

                                    public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                                .addSwipeLeftBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red))
                                                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                                .addSwipeLeftLabel("Excluir")
                                                .setSwipeLeftLabelColor(ContextCompat.getColor(getApplicationContext(), R.color.white))
                                                .create()
                                                .decorate();

                                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                    }

                                };
                                new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.listaContasAPagar);
                            }
                        }
                    }
                });
    }

    public void RecuperarTotalContasAPagarI(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                .collection("Total Contas A Pagar").document("Total");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (!documentSnapshot.exists()) {
                    String vazioFormatado = NumberFormat.getCurrencyInstance(ptbr).format(vazio);
                    binding.ValorTotalContasAPagar.setText(vazioFormatado);

                }else {
                    Double totalContasP = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaSaidaC"));
                    String totalContasPFormat = NumberFormat.getCurrencyInstance(ptbr).format(totalContasP);
                    binding.ValorTotalContasAPagar.setText(totalContasPFormat);
                }

            }
        });
    }
    public void RecuperarDadosContasAPagarI(){


        contasApagar = new ArrayList<>();
        adapterContasApagar = new AdapterContasApagar(getApplicationContext(), contasApagar);
        binding.listaContasAPagar.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.listaContasAPagar.setHasFixedSize(true);
        binding.listaContasAPagar.setAdapter(adapterContasApagar);


        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("ContasApagar")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                ContasApagar contasAPagar = queryDocumentSnapshot.toObject(ContasApagar.class);
                                contasApagar.add(contasAPagar);
                                adapterContasApagar.notifyDataSetChanged();


                                // efeito Swipe
                                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                                    @Override
                                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                        return false;
                                    }

                                    @Override
                                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                        final int position = viewHolder.getLayoutPosition();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ContasAPagar.this);
                                        builder.setTitle("Atenção");
                                        builder.setMessage("deseja excluir esse item?");
                                        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                ContasApagar item = contasApagar.get(position);
                                                DocumentReference documentReferenceCTotal = db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                                                        .collection("Total Contas A Pagar").document("Total");

                                                DocumentReference documentReferenceC = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("ContasApagar")
                                                        .document(item.getId());

                                                documentReferenceCTotal.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshotCTotal, @Nullable FirebaseFirestoreException error) {
                                                        documentReferenceC.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshotC, @Nullable FirebaseFirestoreException error) {
                                                                if (documentSnapshotC.exists()){
                                                                    Double totalContasAPagar = Double.parseDouble(documentSnapshotCTotal.getString("ResultadoDaSomaSaidaC"));
                                                                    Double valorDoubleC = Double.parseDouble(documentSnapshotC.getString("ValorDeSaidaDouble"));
                                                                    Double operacao = totalContasAPagar - valorDoubleC;
                                                                    String cv = String.valueOf(operacao);

                                                                    db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                                                                            .collection("Total Contas A Pagar").document("Total").update("ResultadoDaSomaSaidaC", cv);

                                                                    db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("ContasApagar")
                                                                            .document(item.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        Toast.makeText(getApplicationContext(), "item excluido com sucesso", Toast.LENGTH_LONG).show();
                                                                                        contasApagar.remove(viewHolder.getAdapterPosition());
                                                                                        adapterContasApagar.notifyDataSetChanged();

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
                                                adapterContasApagar.notifyItemChanged(viewHolder.getAdapterPosition());
                                            }
                                        });
                                        builder.show();

                                    }

                                    public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                                .addSwipeLeftBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red))
                                                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                                .addSwipeLeftLabel("Excluir")
                                                .setSwipeLeftLabelColor(ContextCompat.getColor(getApplicationContext(), R.color.white))
                                                .create()
                                                .decorate();

                                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                    }

                                };
                                new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.listaContasAPagar);
                            }
                        }
                    }
                });
    }
    public void IniciarNotificacao(){

        Intent intent = new Intent(ContasAPagar.this, ContasAPagar.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ContasAPagar.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        // Crie um gerenciador de notificações
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Verifique a versão do Android para criar um canal de notificação (obrigatório para versões >= Android 8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "MeuCanal";
            String channelName = "Canal de Notificação";

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Crie uma notificação usando NotificationCompat
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MeuCanal")
                .setSmallIcon(R.drawable.ic_contas_a_pagar)
                .setContentTitle("Atenção")
                .setContentText("Você tem Contas à Pagar em aberto!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        // Exiba a notificação
        notificationManager.notify(1, builder.build());
    }
    public void Meses(){
        String [] meses = {"01","02","03","04","05","06","07","08","09","10","11","12"};

        autoCompleteTextView = findViewById(R.id.auto_complete_text_mes);
        adapterItem = new ArrayAdapter<String>(this,R.layout.historico_perfil_item, meses);
        autoCompleteTextView.setAdapter(adapterItem);

        autoCompleteTextView.setInputType(InputType.TYPE_NULL);
        autoCompleteTextView.setFocusable(false);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(ContasAPagar.this, Item+ " selecionado", Toast.LENGTH_SHORT).show();


            }
        });

    }
    public void Anos(){

        String [] anos = {"2023","2024","2025","2026","2027","2028","2029","2030","2031","2032","2033","2034"};
        autoCompleteTextView = findViewById(R.id.auto_complete_text_ano);
        adapterItem = new ArrayAdapter<String>(this,R.layout.historico_perfil_item, anos);
        autoCompleteTextView.setAdapter(adapterItem);

        autoCompleteTextView.setInputType(InputType.TYPE_NULL);
        autoCompleteTextView.setFocusable(false);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(ContasAPagar.this, Item+ " selecionado", Toast.LENGTH_SHORT).show();
            }
        });

    }
}