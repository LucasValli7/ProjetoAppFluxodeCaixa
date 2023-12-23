package br.com.lucas.valli.fluxodecaixa.FragmentActivity;

import static br.com.lucas.valli.fluxodecaixa.R.color.green;
import static br.com.lucas.valli.fluxodecaixa.R.color.red;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.type.Color;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import br.com.lucas.valli.fluxodecaixa.R;
public class FragmentResumeMensal extends Fragment {


    private String usuarioID;
    View v;
    private Date x = new Date();
    private String mes = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(x);
    private String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
    Locale ptBr = new Locale("pt", "BR");
    private Double vazio = Double.parseDouble("0.00");

    private TextView txtValorSaidas, txtValorEntradas, txtValorFimDoMes;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.mensal_resume_fragment, container, false);
        RecuperarTextView();
        RecuperarTotalSaidasResumo();
        return v;

    }
    public void RecuperarTextView(){
        txtValorSaidas =  v.findViewById(R.id.txt_valorSaidas);
        txtValorEntradas =  v.findViewById(R.id.txt_valorEntradas);
        txtValorFimDoMes =  v.findViewById(R.id.txt_ValorFimDoMes);
    }
    public void RecuperarTotalSaidasResumo(){


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //document reference total Geral
        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de Saidas")
                .document("Total");


        //document reference total Entradas
        DocumentReference documentReferenceEntradas = db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("Total de Entradas")
                .document("Total");


        //document reference total(Geral)
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshotSaida, @Nullable FirebaseFirestoreException error) {

                documentReferenceEntradas.addSnapshotListener(new EventListener<DocumentSnapshot>() {


                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshotEntradas, @Nullable FirebaseFirestoreException error) {
                        if (!documentSnapshotSaida.exists()) {
                            String vazioFormatado = NumberFormat.getCurrencyInstance(ptBr).format(vazio);
                            txtValorSaidas.setText(vazioFormatado);

                        }else {
                            Double totalSaida = Double.parseDouble(documentSnapshotSaida.getString("ResultadoDaSomaSaida"));
                            String totalSaidaFormatado = NumberFormat.getCurrencyInstance(ptBr).format(totalSaida);
                            txtValorSaidas.setText(totalSaidaFormatado);

                        }

                        if (!documentSnapshotEntradas.exists()) {
                            String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(vazio);
                            txtValorEntradas.setText(valorConvertido);

                        }else {

                            Double SomaEntrada = Double.parseDouble(documentSnapshotEntradas.getString("ResultadoDaSomaEntrada"));

                            String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(SomaEntrada);
                            txtValorEntradas.setText(valorConvertido);
                        }




                        // se existir, permita que recupere dados do BD
                        if (!documentSnapshotEntradas.exists() && !documentSnapshotSaida.exists()) {
                            String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(vazio);
                            txtValorFimDoMes.setText(valorConvertido);
                        } else if (!documentSnapshotEntradas.exists() && documentSnapshotSaida.exists()) {
                            Double SomaSaida = Double.parseDouble(documentSnapshotSaida.getString("ResultadoDaSomaSaida"));
                            String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(SomaSaida);
                            txtValorFimDoMes.setText(valorConvertido);
                        } else if (documentSnapshotEntradas.exists() && !documentSnapshotSaida.exists()) {
                            Double SomaEntrada = Double.parseDouble(documentSnapshotEntradas.getString("ResultadoDaSomaEntrada"));
                            String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(SomaEntrada);
                            txtValorFimDoMes.setText(valorConvertido);

                        } else if (documentSnapshotEntradas.exists() && documentSnapshotSaida.exists()){
                            Double SomaEntrada = Double.parseDouble(documentSnapshotEntradas.getString("ResultadoDaSomaEntrada"));
                            Double SomaSaida = Double.parseDouble(documentSnapshotSaida.getString("ResultadoDaSomaSaida"));


                            // operação com os totais
                            if (SomaEntrada < SomaSaida){
                                Double subtracao = SomaEntrada - SomaSaida;
                                String SomaConvertida = NumberFormat.getCurrencyInstance(ptBr).format(subtracao);
                                txtValorFimDoMes.setText(String.valueOf(SomaConvertida));
                            }else {
                                Double subtracao = SomaEntrada - SomaSaida;
                                String SomaConvertida = NumberFormat.getCurrencyInstance(ptBr).format(subtracao);
                                txtValorFimDoMes.setText(String.valueOf(SomaConvertida));

                            }

                        }
                    }
                });

            }

        });


    }

}
