package br.com.lucas.valli.fluxodecaixa;

import static br.com.lucas.valli.fluxodecaixa.Model.ConversorDeMoeda.formatPriceSave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.MoreObjects;
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
import java.util.UUID;

import br.com.lucas.valli.fluxodecaixa.Model.ConversorDeMoeda;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityNovaEntradaBinding;
public class NovaEntrada extends AppCompatActivity {
    private ActivityNovaEntradaBinding binding;
    private String usuarioID;
    private Locale ptbr = new Locale("pt", "BR");
    private Date x = new Date();

    String [] formaPagamento = {"Cheque", "Dinheiro", "Cartão de Crédito"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItem;

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
        FormaPagamento();


        binding.editNovoValor.addTextChangedListener(new ConversorDeMoeda(binding.editNovoValor));
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String novaEntrada = binding.editNovaEntrada.getText().toString();
               String novoValor = binding.editNovoValor.getText().toString();
               if (novaEntrada.isEmpty() || novoValor.isEmpty()){
                   Snackbar snackbar = Snackbar.make(v,"Preencha todos os campos",Snackbar.LENGTH_INDEFINITE)
                           .setAction("OK", new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {

                               }
                           });
                   snackbar.show();
               }else {
                   Intent intent = new Intent(NovaEntrada.this, TelaPrincipalEntradas.class);
                   Toast.makeText(NovaEntrada.this,"Salvo com Sucesso",Toast.LENGTH_SHORT).show();
                   finish();
                   EnviarDadosListaEntradaBD();
                   EnviarTotalBD();
               }
            }
        });
        binding.tolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
        binding.li.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar.make(v,"Não é permitido adicionar movimentação com datas retroativas",Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                snackbar.show();
            }
        });


    }

    public void FormaPagamento(){

        autoCompleteTextView = findViewById(R.id.auto_complete_text_form);
        adapterItem = new ArrayAdapter<String>(this,R.layout.nova_entrada_item, formaPagamento);
        autoCompleteTextView.setAdapter(adapterItem);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(NovaEntrada.this, Item+ " selecionado", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void EnviarTotalBD(){

        String mes = binding.addData2.getText().toString();
        String ano = binding.addData3.getText().toString();

        Double valor = Double.parseDouble(getIntent().getExtras().getString("valor"));
        String str = formatPriceSave(binding.editNovoValor.getText().toString());
        Double valor1 = Double.parseDouble(str);
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
        String dia = binding.addData.getText().toString();
        String mes = binding.addData2.getText().toString();
        String ano = binding.addData3.getText().toString();


        String DadosEntrada = binding.editNovaEntrada.getText().toString();

        String str = formatPriceSave(binding.editNovoValor.getText().toString());
        Double ValorEntrada = Double.parseDouble(str);
        String ValorEntradaConvertido = NumberFormat.getCurrencyInstance(ptbr).format(ValorEntrada);
        String ValorEntradaDouble = String.valueOf(ValorEntrada);
        String dataEntrada = dia + mes + ano;
        String formaPagamento = binding.autoCompleteTextForm.getText().toString();
        String id = UUID.randomUUID().toString();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> entradas = new HashMap<>();
        entradas.put("dataDeEntrada", dataEntrada);
        entradas.put("ValorDeEntradaDouble", ValorEntradaDouble);
        entradas.put("ValorDeEntrada", ValorEntradaConvertido);
        entradas.put("TipoDeEntrada", DadosEntrada);
        entradas.put("id", id);

        entradas.put("formPagamento", formaPagamento);
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes)
                .document("entradas")
                .collection("nova entrada").document(id);
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
                    String yearS = new SimpleDateFormat("YYYY", new Locale("pt", "BR")).format(date);
                    binding.addData.setText(dayS + "/");
                    binding.addData2.setText(monthS+ "/");
                    binding.addData3.setText(yearS);
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
        binding.addData.setText(dia + "/");
        binding.addData2.setText(mes+ "/");
        binding.addData3.setText(ano);
    }

}
