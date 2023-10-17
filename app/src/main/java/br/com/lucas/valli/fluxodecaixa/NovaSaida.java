package br.com.lucas.valli.fluxodecaixa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import br.com.lucas.valli.fluxodecaixa.databinding.ActivityNovaSaidaBinding;

public class NovaSaida extends AppCompatActivity {
    private ActivityNovaSaidaBinding binding;
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
        binding = ActivityNovaSaidaBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NovaSaida.this, TelaPrincipal.class);
                Toast.makeText(NovaSaida.this,"Salvo com Sucesso",Toast.LENGTH_SHORT).show();
                finish();
                EnviarDadosListaEntradaBD();
                EnviarTotalBD();
            }
        });

        binding.tolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
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
        valorTotal.put("ResultadoDaSomaSaida", Valor);

        db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de Saidas")
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

        String DadosSaida = binding.editNovaSaida.getText().toString();
        Double ValorSaida = Double.parseDouble(binding.editNovoValor.getText().toString());
        String ValorSaidaConvertido = NumberFormat.getCurrencyInstance(ptbr).format(ValorSaida);
        String dataSaida = binding.addData.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> saidas = new HashMap<>();
        saidas.put("TipoDeSaida", DadosSaida);
        saidas.put("ValorDeSaida", ValorSaidaConvertido);
        saidas.put("dataDeSaida", dataSaida);
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                .collection("nova saida").document(DadosSaida);
        documentReference.set(saidas).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    public void PassarDataAutomatica(){
        Date x = new Date();
        String dia = new SimpleDateFormat("dd", new Locale("pt", "BR")).format(x);
        String mes = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(x);
        String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
        binding.addData.setText(dia+ "/" + mes+ "/" + ano);
    }
    public void AbrirCalendario(View view){
        Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(NovaSaida.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                String formato = "dd/MM/yyyy";
                SimpleDateFormat dpd = new SimpleDateFormat(formato, Locale.ENGLISH);
                Date date;

                try {
                    date = dpd.parse(dpd.format(calendar.getTime()));
                    String dayS = new  SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                    String monthS = new  SimpleDateFormat("MM", Locale.ENGLISH).format(date);
                    String yearS = new  SimpleDateFormat("yyyy", Locale.ENGLISH).format(date);
                    binding.addData.setText((dayS + "/" + monthS + "/" + yearS));
                }catch (ParseException e){

                }
            }
        },dia, mes, ano);
        dpd.show();

    }

}