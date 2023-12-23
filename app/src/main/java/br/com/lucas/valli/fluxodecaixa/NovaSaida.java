package br.com.lucas.valli.fluxodecaixa;

import static br.com.lucas.valli.fluxodecaixa.Model.ConversorDeMoeda.formatPriceSave;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
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
import br.com.lucas.valli.fluxodecaixa.Model.SalvarPorcentagemD;
import br.com.lucas.valli.fluxodecaixa.Model.SalvarPorcentagemE;
import br.com.lucas.valli.fluxodecaixa.Model.SalvarPorcentagemP;
import br.com.lucas.valli.fluxodecaixa.Model.SalvarTotalE;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityNovaSaidaBinding;

public class NovaSaida extends AppCompatActivity {
    private ActivityNovaSaidaBinding binding;
    private String usuarioID;
    private Locale ptbr = new Locale("pt", "BR");
    private Date x = new Date();
    private String mes2 = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(x);
    private String ano2 = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
    private String dia2 = new SimpleDateFormat("dd", new Locale("pt", "BR")).format(x);
    private String dataFormat = dia2 +"/" + mes2 +"/"+ ano2;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItem;

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
        FormaPagamento();
        Categoria();

        binding.editNovoValor.addTextChangedListener(new ConversorDeMoeda(binding.editNovoValor));

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String novaSaida = binding.editNovaSaida.getText().toString();
                String novoValor = binding.editNovoValor.getText().toString();
                String categoria = binding.autoCompleteTextCategoria.getText().toString();
                if (novaSaida.isEmpty() || novoValor.isEmpty() || categoria.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v,"Preencha todos os campos",Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    snackbar.show();
                } else {
                    Intent intent = new Intent(NovaSaida.this, TelaPrincipal.class);
                    Toast.makeText(NovaSaida.this,"Salvo com Sucesso",Toast.LENGTH_SHORT).show();
                    finish();
                    EnviarDadosListaSaidaBD();
                    EnviarTotalBD(v);
                }

            }
        });

        binding.tolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
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

        String [] formaPagamento = {"Cheque", "Dinheiro", "Cartão de Crédito"};

        autoCompleteTextView = findViewById(R.id.auto_complete_text_form);
        adapterItem = new ArrayAdapter<String>(this,R.layout.nova_saida_item, formaPagamento);
        autoCompleteTextView.setAdapter(adapterItem);

        autoCompleteTextView.setInputType(InputType.TYPE_NULL);
        autoCompleteTextView.setFocusable(false);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(NovaSaida.this, Item+ " selecionado", Toast.LENGTH_SHORT).show();

            }
        });

    }
    public void Categoria(){
        String [] categoria = {"Gastos Essenciais", "Pagamento de Dívidas", "Desejos Pessoais"};

        autoCompleteTextView = findViewById(R.id.auto_complete_text_categoria);
        adapterItem = new ArrayAdapter<String>(this,R.layout.nova_saida_item, categoria);
        autoCompleteTextView.setAdapter(adapterItem);

        autoCompleteTextView.setInputType(InputType.TYPE_NULL);
        autoCompleteTextView.setFocusable(false);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(NovaSaida.this, Item+ " selecionado", Toast.LENGTH_SHORT).show();

            }
        });



    }


    public void EnviarTotalBD(View view) {

        String mes = binding.addData2.getText().toString();
        String ano = binding.addData3.getText().toString();


        String str = formatPriceSave(binding.editNovoValor.getText().toString());
        String epd = binding.autoCompleteTextCategoria.getText().toString();

        // somar total
        Double valor = Double.parseDouble(getIntent().getExtras().getString("valor"));
        Double valor1 = Double.parseDouble(str);

        Double soma = valor + valor1;
        String Valor = String.valueOf(soma);

        // somar total E
        Double valorE = Double.parseDouble(getIntent().getExtras().getString("valorE"));
        Double valor1E = Double.parseDouble(str);

        Double somaE = valorE + valor1E;
        String ValorE = String.valueOf(somaE);

        // somar total P
        Double valorP = Double.parseDouble(getIntent().getExtras().getString("valorP"));
        Double valor1P = Double.parseDouble(str);

        Double somaP = valorP + valor1P;
        String ValorP = String.valueOf(somaP);

        // somar total D
        Double valorD = Double.parseDouble(getIntent().getExtras().getString("valorD"));
        Double valor1D = Double.parseDouble(str);

        Double somaD = valorD + valor1D;
        String ValorD = String.valueOf(somaD);

        // somar total C
        Double valorC = Double.parseDouble(getIntent().getExtras().getString("valorC"));
        Double valor1C = Double.parseDouble(str);

        Double somaC = valorC + valor1C;
        String ValorC = String.valueOf(somaC);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //HasMap total
        Map<String, Object> valorTotal = new HashMap<>();
        valorTotal.put("ResultadoDaSomaSaida", Valor);
        // HasMap total E
        Map<String, Object> valorTotalE = new HashMap<>();
        valorTotalE.put("ResultadoDaSomaSaidaE", ValorE);
        // HasMap Total P
        Map<String, Object> valorTotalP = new HashMap<>();
        valorTotalP.put("ResultadoDaSomaSaidaP", ValorP);
        // HasMap Total D
        Map<String, Object> valorTotalD = new HashMap<>();
        valorTotalD.put("ResultadoDaSomaSaidaD", ValorD);
        // HasMap Contas À Pagar
        Map<String, Object> valorTotalC = new HashMap<>();
        valorTotalC.put("ResultadoDaSomaSaidaC", ValorC);

        // recuperar total(geral)
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

        if (binding.checksSim.isChecked()) {

            db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                    .collection("Total Contas A Pagar").document("Total").set(valorTotalC).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

        // se categoria for "" eu quero que crie determidado HasMap
        if (epd.equals("Gastos Essenciais")) {

            db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasE")
                    .document("Total").set(valorTotalE).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        } else if (epd.equals("Pagamento de Dívidas")) {
            db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasP")
                    .document("Total").set(valorTotalP).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else if (epd.equals("Desejos Pessoais")) {
            db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasD")
                    .document("Total").set(valorTotalD).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else {
            Snackbar snackbar = Snackbar.make(view,"Por favor, escolha uma categoria",Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {finish();}
                    });
            snackbar.show();
        }


    }

    public void EnviarDadosListaSaidaBD(){
        String dia = binding.addData.getText().toString();
        String mes = binding.addData2.getText().toString();
        String ano = binding.addData3.getText().toString();


        String DadosSaida = binding.editNovaSaida.getText().toString();

        String str = formatPriceSave(binding.editNovoValor.getText().toString());
        Double ValorSaida = Double.parseDouble(str);
        String ValorSaidaDouble = String.valueOf(ValorSaida); // valor convertido para string para recuperar em fragmento
        String ValorSaidaConvertido = NumberFormat.getCurrencyInstance(ptbr).format(ValorSaida);
        String id = UUID.randomUUID().toString();

        String dataSaida = dataFormat;
        String formaPagamento = binding.autoCompleteTextForm.getText().toString();
        String categoria = binding.autoCompleteTextCategoria.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> saidas = new HashMap<>();
        saidas.put("TipoDeSaida", DadosSaida);
        saidas.put("ValorDeSaidaDouble", ValorSaidaDouble);
        saidas.put("ValorDeSaida", ValorSaidaConvertido);
        saidas.put("dataDeSaida", dataSaida);
        saidas.put("id", id);

        saidas.put("formPagamento", formaPagamento);
        saidas.put("categoria", categoria);
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                .collection("nova saida").document("categoria").collection(categoria).document(id);

        DocumentReference documentReferenceContasApagar = db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                .collection("ContasApagar").document(id);
        documentReference.set(saidas).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        if (binding.checksSim.isChecked()){
            documentReferenceContasApagar.set(saidas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }
    public void PassarDataAutomatica(){
        Date x = new Date();
        String dia = new SimpleDateFormat("dd", new Locale("pt", "BR")).format(x);
        String mes = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(x);
        String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
        binding.addData.setText(dia + "/");
        binding.addData2.setText(mes+ "/");
        binding.addData3.setText(ano);
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
                    binding.addData.setText(dayS + "/");
                    binding.addData2.setText(monthS+ "/");
                    binding.addData3.setText(yearS);
                }catch (ParseException e){

                }
            }
        },dia, mes, ano);
        dpd.show();

    }
    public void DefiniAlarme(){
        // Criar uma intent para abrir o aplicativo de relógio/alarme
        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);

        // Configurar os parâmetros do alarme
        alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, 07); // Hora do alarme (24 horas)
        alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, 12); // Minutos do alarme
        alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, "Alarme Personalizado"); // Mensagem do alarme

        // Configurar outros parâmetros opcionais, se necessário
        // alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        // Iniciar a intent para abrir o aplicativo de relógio/alarme
        startActivity(alarmIntent);
    }

    public void IniciarNotificacao(){

        Intent intent = new Intent(NovaSaida.this, ContasAPagar.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(NovaSaida.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);


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
    public void AbrirRelogio(){

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            }
        };

        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(NovaSaida.this, onTimeSetListener, hora, minuto,true);
        timePickerDialog.show();
    }

}