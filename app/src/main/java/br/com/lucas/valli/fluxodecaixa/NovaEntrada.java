package br.com.lucas.valli.fluxodecaixa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Locale;
import java.util.Map;

import br.com.lucas.valli.fluxodecaixa.Adapter.AdapterDadosEntrada;
import br.com.lucas.valli.fluxodecaixa.Model.ConversorDeMoeda;
import br.com.lucas.valli.fluxodecaixa.Model.DadosEntrada;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityNovaEntradaBinding;
public class NovaEntrada extends AppCompatActivity {
    private ActivityNovaEntradaBinding binding;
    private String usuarioID;
    private Locale ptbr = new Locale("pt", "BR");
    private Date x = new Date();
    private String mes = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(x);
    private String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);

    @Override
    protected void onStart() {
        super.onStart();
        PassarDataAutomatica();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityNovaEntradaBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NovaEntrada.this, TelaPrincipalEntradas.class);
                Toast.makeText(NovaEntrada.this,"Salvo com Sucesso",Toast.LENGTH_SHORT).show();
                finish();
                EnviarDadosListaEntradaBD();
                EnviarTotalBD();

            }
        });
        binding.tolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
        binding.addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirCalendario(v);
            }
        });


    }


    public void EnviarTotalBD(){

        Double valor = Double.parseDouble(getIntent().getExtras().getString("valor"));
        Double valor1 = Double.parseDouble(binding.editNovoValor.getText().toString());
        Double soma = valor + valor1;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String Valor = String.valueOf(soma);
        Map<String, Object> valorTotal = new HashMap<>();
        valorTotal.put("ResultadoDaSomaEntrada", Valor);

        db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("Total de Entradas")
                .document("Total").set(valorTotal).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    public void EnviarDadosListaEntradaBD(){

        String DadosEntrada = binding.editNovaEntrada.getText().toString();
        Double ValorEntrada = Double.parseDouble(binding.editNovoValor.getText().toString());
        String ValorEntradaConvertido = NumberFormat.getCurrencyInstance(ptbr).format(ValorEntrada);
        String dataEntrada = binding.addData.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> entradas = new HashMap<>();
        entradas.put("TipoDeEntrada", DadosEntrada);
        entradas.put("ValorDeEntrada", ValorEntradaConvertido);
        entradas.put("dataDeEntrada", dataEntrada);
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes)
                .document("entradas")
                .collection("nova entrada").document(DadosEntrada);
        documentReference.set(entradas).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    public void AbrirCalendario(View view) {
        Calendar calendar = Calendar.getInstance();

        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(NovaEntrada.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                String formato = "dd/MM/yyyy";
                SimpleDateFormat dpd = new SimpleDateFormat(formato, new Locale("pt", "BR"));
                Date date;

                try {
                    date = dpd.parse(dpd.format(calendar.getTime()));
                    String dayS = new SimpleDateFormat("dd", new Locale("pt", "BR")).format(date);
                    String monthS = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(date);
                    String yearS = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(date);
                    binding.addData.setText((dayS + "/" + monthS + "/" + yearS));
                } catch (ParseException e) {

                }
            }
        }, ano, mes, dia);
        dpd.show();

    }
    public void PassarDataAutomatica(){
        String dia = new SimpleDateFormat("dd", new Locale("pt", "BR")).format(x);
        String mes = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(x);
        String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
        binding.addData.setText(dia+ "/" + mes+ "/" + ano);
    }

}
