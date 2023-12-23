package br.com.lucas.valli.fluxodecaixa.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.lucas.valli.fluxodecaixa.Adapter.AdapterDadosSaidaD;
import br.com.lucas.valli.fluxodecaixa.Model.DadosSaidaD;
import br.com.lucas.valli.fluxodecaixa.NovaSaida;
import br.com.lucas.valli.fluxodecaixa.R;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FragmentDP extends Fragment {

    private List<DadosSaidaD> dadosSaidaEListD;
    private FirebaseFirestore db;
    private String usuarioID;
    private Date x = new Date();
    private String mes = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(x);
    private String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
    View v;
    private RecyclerView recyclerViewList;

    private TextView ValorTotalSaidas;
    private TextView ValorTotalSaidasEPD;
    private Button btnAddSaida;

    private Locale ptbr = new Locale("pt", "BR");
    private Double vazio = Double.parseDouble("0.00");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dp_fragment, container, false);
        RecuperarDadosSaidasD();
        RecuperarTotalSaida();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    public void RecuperarDadosSaidasD(){
        dadosSaidaEListD = new ArrayList<>();

        recyclerViewList = (RecyclerView) v.findViewById(R.id.ListaTipoSaidaD);

        AdapterDadosSaidaD adapterDadosSaidaD = new AdapterDadosSaidaD(getContext(),dadosSaidaEListD);
        recyclerViewList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewList.setAdapter(adapterDadosSaidaD);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                .collection("nova saida").document("categoria").collection("Desejos Pessoais")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){

                                DadosSaidaD dadosSaidaD = queryDocumentSnapshot.toObject(DadosSaidaD.class);
                                dadosSaidaEListD.add(dadosSaidaD);
                                adapterDadosSaidaD.notifyDataSetChanged();

                                //efeito Swipe
                                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                                    @Override
                                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                        return false;
                                    }

                                    @Override
                                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                        final int position = viewHolder.getLayoutPosition();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Atenção");
                                        builder.setMessage("deseja excluir esse item?");
                                        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                DadosSaidaD item = dadosSaidaEListD.get(position);

                                                //document reference total Geral
                                                DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de Saidas")
                                                        .document("Total");
                                                //document reference total Desejos Pessoais
                                                DocumentReference documentReferenceD = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasD")
                                                        .document("Total");


                                                DocumentReference documentReferenceV = db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                                                        .collection("nova saida").document("categoria").collection("Desejos Pessoais")
                                                        .document(item.getId());

                                                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                                                        documentReferenceD.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshotD, @Nullable FirebaseFirestoreException error) {
                                                                documentReferenceV.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshotV, @Nullable FirebaseFirestoreException error) {
                                                                        if (documentSnapshotV.exists()){
                                                                            Double totalSaida = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaSaida"));
                                                                            Double totalSaida2 = Double.parseDouble(documentSnapshotD.getString("ResultadoDaSomaSaidaD"));
                                                                            Double totalSaida3 = Double.parseDouble(documentSnapshotV.getString("ValorDeSaidaDouble"));
                                                                            Double soma = totalSaida - totalSaida3;
                                                                            Double soma2 = totalSaida2 - totalSaida3;
                                                                            String cv = String.valueOf(soma);
                                                                            String cv2 = String.valueOf(soma2);

                                                                            db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de Saidas")
                                                                                    .document("Total").update("ResultadoDaSomaSaida",cv);
                                                                            db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasD")
                                                                                    .document("Total").update("ResultadoDaSomaSaidaD",cv2);

                                                                            db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                                                                                    .collection("nova saida").document("categoria").collection("Desejos Pessoais")
                                                                                    .document(item.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()){
                                                                                                Toast.makeText(getContext(), "item excluido com sucesso", Toast.LENGTH_LONG).show();
                                                                                                dadosSaidaEListD.remove(viewHolder.getAdapterPosition());
                                                                                                adapterDadosSaidaD.notifyDataSetChanged();

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


                                            }
                                        });
                                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                adapterDadosSaidaD.notifyItemChanged(viewHolder.getAdapterPosition());
                                            }
                                        });
                                        builder.show();

                                    }

                                    public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                                .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red))
                                                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                                .addSwipeLeftLabel("Excluir")
                                                .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                                                .create()
                                                .decorate();

                                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                    }

                                };
                                new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewList);
                            }
                        }
                    }
                });



    }
    public void RecuperarTotalSaida(){

        ValorTotalSaidas = (TextView) v.findViewById(R.id.ValorTotalSaidas);
        ValorTotalSaidasEPD = (TextView) v.findViewById(R.id.ValorTotalSaidasEPD);
        btnAddSaida = (Button) v.findViewById(R.id.btn_addSaida);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de Saidas")
                .document("Total");

        //document reference total Essenciais
        DocumentReference documentReferenceE = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasE")
                .document("Total");

        //document reference total Pagamento de Dívidas
        DocumentReference documentReferenceP = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasP")
                .document("Total");

        //document reference total Desejos Pessoais
        DocumentReference documentReferenceD = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasD")
                .document("Total");

        //document reference total Contas À pagar
        DocumentReference documentReferenceC = db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                .collection("Total Contas A Pagar").document("Total");

        //document reference total(Geral)
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                //document reference total Essenciais
                documentReferenceE.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshotE, @Nullable FirebaseFirestoreException error) {

                        //document reference total Pagamento de Dívidas
                        documentReferenceP.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshotP, @Nullable FirebaseFirestoreException error) {

                                //document reference total Desejos Pessoais
                                documentReferenceD.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshotD, @Nullable FirebaseFirestoreException error) {

                                        documentReferenceC.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshotC, @Nullable FirebaseFirestoreException error) {

                                                if (!documentSnapshotD.exists()) {
                                                    String vazioFormatado = NumberFormat.getCurrencyInstance(ptbr).format(vazio);
                                                    ValorTotalSaidasEPD.setText(vazioFormatado);

                                                }else {
                                                    Double totalSaidaD = Double.parseDouble(documentSnapshotD.getString("ResultadoDaSomaSaidaD"));
                                                    String totalSaidaFormatadoD = NumberFormat.getCurrencyInstance(ptbr).format(totalSaidaD);
                                                    ValorTotalSaidasEPD.setText(totalSaidaFormatadoD);
                                                }

                                                btnAddSaida.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String valorVazioString = String.valueOf(vazio);
                                                        Intent intent = new Intent(getContext(), NovaSaida.class);

                                                        if (!documentSnapshot.exists()) {
                                                            intent.putExtra("valor", valorVazioString);

                                                        } else {
                                                            intent.putExtra("valor", documentSnapshot.getString("ResultadoDaSomaSaida"));

                                                        }

                                                        if (!documentSnapshotE.exists()) {
                                                            intent.putExtra("valorE", valorVazioString);

                                                        } else {
                                                            intent.putExtra("valorE", documentSnapshotE.getString("ResultadoDaSomaSaidaE"));

                                                        }

                                                        if (!documentSnapshotP.exists()) {
                                                            intent.putExtra("valorP", valorVazioString);

                                                        } else {
                                                            intent.putExtra("valorP", documentSnapshotP.getString("ResultadoDaSomaSaidaP"));

                                                        }

                                                        if (!documentSnapshotD.exists()){
                                                            intent.putExtra("valorD", valorVazioString);

                                                        }else {
                                                            intent.putExtra("valorD", documentSnapshotD.getString("ResultadoDaSomaSaidaD"));
                                                        }

                                                        if (!documentSnapshotC.exists()){
                                                            intent.putExtra("valorC", valorVazioString);
                                                        }else {
                                                            intent.putExtra("valorC", documentSnapshotC.getString("ResultadoDaSomaSaidaC"));
                                                        }

                                                        startActivity(intent);

                                                    }
                                                });

                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });

                if (!documentSnapshot.exists()) {
                    String vazioFormatado = NumberFormat.getCurrencyInstance(ptbr).format(vazio);
                    ValorTotalSaidas.setText(vazioFormatado);

                }else {
                    Double totalSaida = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaSaida"));
                    String totalSaidaFormatado = NumberFormat.getCurrencyInstance(ptbr).format(totalSaida);
                    ValorTotalSaidas.setText(totalSaidaFormatado);

                }



            }

        });


    }

}
