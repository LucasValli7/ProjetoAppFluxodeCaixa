package br.com.lucas.valli.fluxodecaixa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityTelaPrincipalBinding;

public class TelaPrincipal extends AppCompatActivity {

    private ActivityTelaPrincipalBinding binding;
    private String usuarioID;
    private Locale ptBr = new Locale("pt", "BR");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Date x = new Date();
    private String mes = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(x);
    private String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);

    @Override
    protected void onStart() {
        super.onStart();
        RecuperarTotalEntradas();
        RecuperarTotalSaidas();
        SomaResumoCaixa();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int ItemID = item.getItemId();
        if (ItemID == R.id.perfil){
            Intent intent = new Intent(TelaPrincipal.this, PerfilUsuario.class);
            startActivity(intent);

        } else if (ItemID == R.id.Entradas) {
            Intent intent = new Intent(TelaPrincipal.this, TelaPrincipalEntradas.class);
            startActivity(intent);

        } else if (ItemID == R.id.Saidas) {
            Intent intent = new Intent(TelaPrincipal.this, TelaPrincipalSaidas.class);
            startActivity(intent);

        } else if (ItemID == R.id.historico) {
            Intent intent = new Intent(TelaPrincipal.this, PerfilHistoricos.class);
            startActivity(intent);

        } else if (ItemID == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(TelaPrincipal.this,"Usuário deslogado",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TelaPrincipal.this, Form_Login.class);
            startActivity(intent);
            finish();

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityTelaPrincipalBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

    }
    public void SomaResumoCaixa() {

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReferenceEntrada = db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("Total de Entradas")
                .document("Total");
        documentReferenceEntrada.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshotEntrada, @Nullable FirebaseFirestoreException error) {
                DocumentReference documentReferenceSaida =db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de Saidas")
                        .document("Total");
                documentReferenceSaida.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshotSaida, @Nullable FirebaseFirestoreException error) {
                        if (!documentSnapshotEntrada.exists() && !documentSnapshotSaida.exists()) {
                            Double valorFimDoMes = Double.parseDouble("0.00");
                            String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(valorFimDoMes);
                            binding.txtValorFimDoMes.setText(valorConvertido);
                        } else if (!documentSnapshotEntrada.exists() && documentSnapshotSaida.exists()) {
                            Double SomaSaida = Double.parseDouble(documentSnapshotSaida.getString("ResultadoDaSomaSaida"));
                            String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(SomaSaida);
                            binding.txtValorFimDoMes.setText(valorConvertido);
                        } else if (documentSnapshotEntrada.exists() && !documentSnapshotSaida.exists()) {
                            Double SomaEntrada = Double.parseDouble(documentSnapshotEntrada.getString("ResultadoDaSomaEntrada"));
                            String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(SomaEntrada);
                            binding.txtValorFimDoMes.setText(valorConvertido);
                    } else if (documentSnapshotEntrada.exists() && documentSnapshotSaida.exists()){
                            Double SomaEntrada = Double.parseDouble(documentSnapshotEntrada.getString("ResultadoDaSomaEntrada"));
                            Double SomaSaida = Double.parseDouble(documentSnapshotSaida.getString("ResultadoDaSomaSaida"));

                            if (SomaEntrada < SomaSaida){
                                Double subtracao = SomaEntrada - SomaSaida;
                                String SomaConvertida = NumberFormat.getCurrencyInstance(ptBr).format(subtracao);
                                binding.txtValorFimDoMes.setText(String.valueOf(SomaConvertida));
                                binding.txtValorFimDoMes.setTextColor(getColor(R.color.red));
                            }else {
                                Double subtracao = SomaEntrada - SomaSaida;
                                String SomaConvertida = NumberFormat.getCurrencyInstance(ptBr).format(subtracao);
                                binding.txtValorFimDoMes.setText(String.valueOf(SomaConvertida));
                                binding.txtValorFimDoMes.setTextColor(getColor(R.color.white));
                            }

                        }
                    }
                });

            }
        });

    }

    public void RecuperarTotalEntradas(){

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("Total de Entradas")
                .document("Total");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (!documentSnapshot.exists()) {

                    Double txtValorEntrada = Double.parseDouble("0.00");
                    String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(txtValorEntrada);
                    binding.txtValorEntradas.setText(valorConvertido);

                }else {
                    Double SomaEntrada = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaEntrada"));
                    String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(SomaEntrada);
                    binding.txtValorEntradas.setText(valorConvertido);

                }
            }
        });
    }
    public void RecuperarTotalSaidas(){

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de Saidas")
                .document("Total");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (!documentSnapshot.exists()) {
                    Double txtValorSaida= Double.parseDouble("0.00");
                    String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(txtValorSaida);
                    binding.txtValorSaidas.setText(valorConvertido);
                }else {

                    Double SomaSaida = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaSaida"));
                    String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(SomaSaida);
                    binding.txtValorSaidas.setText(valorConvertido);

                }
            }
        });
    }

}