package br.com.lucas.valli.fluxodecaixa;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessaging;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.lucas.valli.fluxodecaixa.Adapter.ViewPagerAdapter;
import br.com.lucas.valli.fluxodecaixa.FragmentActivity.FragmentDP;
import br.com.lucas.valli.fluxodecaixa.FragmentActivity.FragmentGE;
import br.com.lucas.valli.fluxodecaixa.FragmentActivity.FragmentPD;
import br.com.lucas.valli.fluxodecaixa.FragmentActivity.FragmentResumeAnual;
import br.com.lucas.valli.fluxodecaixa.FragmentActivity.FragmentResumeDiario;
import br.com.lucas.valli.fluxodecaixa.FragmentActivity.FragmentResumeMensal;
import br.com.lucas.valli.fluxodecaixa.Model.SalvarPorcentagemD;
import br.com.lucas.valli.fluxodecaixa.Model.SalvarPorcentagemE;
import br.com.lucas.valli.fluxodecaixa.Model.SalvarPorcentagemP;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityTelaPrincipalBinding;
import kotlin.jvm.internal.PropertyReference0Impl;

public class TelaPrincipal extends AppCompatActivity {

    private ActivityTelaPrincipalBinding binding;
    private String usuarioID;
    private Locale ptBr = new Locale("pt", "BR");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Date x = new Date();
    private String mes = new SimpleDateFormat("MM", new Locale("pt", "BR")).format(x);
    private String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
    private SalvarPorcentagemE salvarPorcentagemE;
    private SalvarPorcentagemP salvarPorcentagemP;
    private SalvarPorcentagemD salvarPorcentagemD;
    private Double vazio = Double.parseDouble("0.00");
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    DrawerLayout drawerLayout;
    ImageView menu, perfil;
    LinearLayout  linearHome, linearEntrada, linearSaida,
            linearContasApagar, linearContasAreceber, linearHistorico,linearSobre, linearLogout;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;




    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityTelaPrincipalBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        IniciarComponentes();
        ButtonsDrawerLayout();
        RecuperarPorcentagem();
        ControleDeGastos();
        AtivarPagerView();
        RecuperarTotalSaidasResumo();


    }

    public void AtivarPagerView(){
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.ViewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.AddFragment(new FragmentResumeDiario(), "Diário");
        viewPagerAdapter.AddFragment(new FragmentResumeMensal(), "Mensal");
        viewPagerAdapter.AddFragment(new FragmentResumeAnual(), "Anual");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        int paginaInicial = 1;
        viewPager.setCurrentItem(paginaInicial);
    }

    public void IniciarComponentes (){
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        perfil = findViewById(R.id.PerfilUsuario);
        linearHome = findViewById(R.id.LinearHome);
        linearEntrada = findViewById(R.id.LinearEntrada);
        linearSaida = findViewById(R.id.LinearSaida);
        linearContasApagar = findViewById(R.id.LinearContasApagar);
        linearContasAreceber = findViewById(R.id.LinearContasAreceber);
        linearHistorico = findViewById(R.id.LinearHistorico);
        linearSobre = findViewById(R.id.LinearSobre);
        linearLogout = findViewById(R.id.LinearLogout);
    }
    public void ButtonsDrawerLayout() {

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaPrincipal.this, PerfilUsuario.class);
                startActivity(intent);
            }
        });
        linearHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(drawerLayout);
            }
        });
        linearEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaPrincipal.this, TelaPrincipalEntradas.class);
                startActivity(intent);
            }
        });
        linearSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaPrincipal.this, TelaPrincipalSaidas.class);
                startActivity(intent);
            }
        });
        linearContasApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaPrincipal.this, ContasAPagar.class);
                startActivity(intent);
            }
        });
        linearContasAreceber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar.make(v, "Estará disponível na próxima atualização", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                snackbar.show();
            }
        });
        linearHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaPrincipal.this, PerfilHistoricos.class);
                startActivity(intent);
            }
        });
        linearSobre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        linearLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();

            }
        });
        binding.txtPorcentagemEssenciais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(TelaPrincipal.this);
                builder.setTitle("Porcentagem Desejada");

                final  EditText edittext_dialogo = new EditText(TelaPrincipal.this);
                edittext_dialogo.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(edittext_dialogo);

                builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dialogo = edittext_dialogo.getText().toString();
                        binding.txtPorcentagemEssenciais.setText(dialogo);



                        String porcentagemRecuperada = edittext_dialogo.getText().toString();
                        if (porcentagemRecuperada.equals("")){
                            Snackbar snackbar = Snackbar.make(v,"A porcentagem não é válida",Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                            snackbar.show();
                            binding.txtPorcentagemEssenciais.setText("50");
                        }else {
                            ControleDeGastos();

                            salvarPorcentagemE.salvarPorcentagemE(porcentagemRecuperada);
                            Toast.makeText(getApplicationContext(), "salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        binding.txtPorcentagemPagamentoDeDividas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(TelaPrincipal.this);
                builder.setTitle("Porcentagem Desejada");

                final  EditText edittext_dialogo = new EditText(TelaPrincipal.this);
                edittext_dialogo.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(edittext_dialogo);

                builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dialogo = edittext_dialogo.getText().toString();
                        binding.txtPorcentagemPagamentoDeDividas.setText(dialogo);


                        String porcentagemRecuperada = edittext_dialogo.getText().toString();
                        if (porcentagemRecuperada.equals("")){
                            Snackbar snackbar = Snackbar.make(v,"A porcentagem não é válida",Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                            snackbar.show();
                            binding.txtPorcentagemPagamentoDeDividas.setText("30");
                        }else {
                            ControleDeGastos();

                            salvarPorcentagemP.salvarPorcentagemP(porcentagemRecuperada);
                            Toast.makeText(getApplicationContext(), "salvo com sucesso!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        binding.txtPorcentagemDesejosPessoais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(TelaPrincipal.this);
                builder.setTitle("Porcentagem Desejada");

                final  EditText edittext_dialogo = new EditText(TelaPrincipal.this);
                edittext_dialogo.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(edittext_dialogo);

                builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dialogo = edittext_dialogo.getText().toString();
                        binding.txtPorcentagemDesejosPessoais.setText(dialogo);


                        String porcentagemRecuperada = edittext_dialogo.getText().toString();
                        if (porcentagemRecuperada.equals("")){
                            Snackbar snackbar = Snackbar.make(v,"A porcentagem não é válida",Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                            snackbar.show();
                            binding.txtPorcentagemDesejosPessoais.setText("20");
                        }else {
                            ControleDeGastos();
                            salvarPorcentagemD.salvarPorcentagemD(porcentagemRecuperada);
                            Toast.makeText(getApplicationContext(), "salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        binding.txtPorcentagemGastosE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String porcentagem = binding.txtPorcentagemGastosE.getText().toString();
                Toast.makeText(TelaPrincipal.this, "Você gastou " + porcentagem + "% " + " do seu Total de Entradas", Toast.LENGTH_SHORT ).show();
            }
        });
        binding.txtPorcentagemGastosP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String porcentagem = binding.txtPorcentagemGastosP.getText().toString();
                Toast.makeText(TelaPrincipal.this, "Você gastou " + porcentagem + "% " + " do seu Total de Entradas", Toast.LENGTH_SHORT ).show();
            }
        });
        binding.txtPorcentagemGastosD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String porcentagem = binding.txtPorcentagemGastosD.getText().toString();
                Toast.makeText(TelaPrincipal.this, "Você gastou " + porcentagem + "% " + " do seu Total de Entradas", Toast.LENGTH_SHORT ).show();
            }
        });
    }
    public void IniciarNotificacao(){

        DocumentReference documentReference =  db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                .collection("Total Contas A Pagar").document("Total");

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()){
                    Double totalContasAP = Double.parseDouble(documentSnapshot.getString("ResultadoDaSomaSaidaC"));
                    if (totalContasAP > vazio){

                        CriarNotificacao();

                    }
                }
            }
        });


    }

    public void CriarNotificacao(){
        Intent intent = new Intent(TelaPrincipal.this, ContasAPagar.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(TelaPrincipal.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);


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
                .setContentText("Você tem contas para PAGAR!!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        // Exiba a notificação
        notificationManager.notify(1, builder.build());
    }
    public void ControleDeGastos(){

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReferenceEntrada = db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("Total de Entradas")
                .document("Total");
        documentReferenceEntrada.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshotEntrada, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshotEntrada.exists()){

                    Double SomaEntrada = Double.parseDouble(documentSnapshotEntrada.getString("ResultadoDaSomaEntrada"));
                    Double PorcentagemE = Double.parseDouble(binding.txtPorcentagemEssenciais.getText().toString());
                    Double PorcentagemP = Double.parseDouble(binding.txtPorcentagemPagamentoDeDividas.getText().toString());
                    Double PorcentagemD = Double.parseDouble(binding.txtPorcentagemDesejosPessoais.getText().toString());

                    if (PorcentagemE != 0 && PorcentagemP != 0 && PorcentagemD != 0){
                        Double somaE = (SomaEntrada * PorcentagemE) /100;
                        String valorConvertidoE = NumberFormat.getCurrencyInstance(ptBr).format(somaE);
                        binding.ValorDisponivelE.setText(valorConvertidoE);

                        Double somaP = (SomaEntrada * PorcentagemP) /100;
                        String valorConvertidoP = NumberFormat.getCurrencyInstance(ptBr).format(somaP);
                        binding.ValorDisponivelP.setText(valorConvertidoP);

                        Double somaD = (SomaEntrada * PorcentagemD) /100;
                        String valorConvertidoD = NumberFormat.getCurrencyInstance(ptBr).format(somaD);
                        binding.ValorDisponivelD.setText(valorConvertidoD);

                    }
                }else {
                    String valorConvertido = NumberFormat.getCurrencyInstance(ptBr).format(vazio);

                    binding.ValorDisponivelE.setText(valorConvertido);
                    binding.ValorDisponivelP.setText(valorConvertido);
                    binding.ValorDisponivelD.setText(valorConvertido);
                }

            }
        });

    }


    public void RecuperarPorcentagem(){
        salvarPorcentagemE = new SalvarPorcentagemE(getApplicationContext());
        salvarPorcentagemP = new SalvarPorcentagemP(getApplicationContext());
        salvarPorcentagemD = new SalvarPorcentagemD(getApplicationContext());
        String recuperarE = salvarPorcentagemE.recuperarPorcentagemE();
        String recuperarP = salvarPorcentagemP.recuperarPorcentagemP();
        String recuperarD = salvarPorcentagemD.recuperarPorcentagemD();


        if (!recuperarE.equals("") && !recuperarP.equals("") && !recuperarD.equals("")){
            binding.txtPorcentagemEssenciais.setText(recuperarE);
            binding.txtPorcentagemPagamentoDeDividas.setText(recuperarP);
            binding.txtPorcentagemDesejosPessoais.setText(recuperarD);

        }
    }

    public void RecuperarTotalSaidasResumo(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //document reference total Essenciais
        DocumentReference documentReferenceE = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasE")
                .document("Total");

        //document reference total Pagamento de Dívidas
        DocumentReference documentReferenceP = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasP")
                .document("Total");

        //document reference total Desejos Pessoais
        DocumentReference documentReferenceD = db.collection(usuarioID).document(ano).collection(mes).document("saidas").collection("Total de SaidasD")
                .document("Total");

        //document reference total Entradas
        DocumentReference documentReferenceEntradas = db.collection(usuarioID).document(ano).collection(mes).document("entradas").collection("Total de Entradas")
                .document("Total");


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

                                documentReferenceEntradas.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshotEntradas, @Nullable FirebaseFirestoreException error) {

                                        if (!documentSnapshotE.exists()) {
                                            String vazioFormatado = NumberFormat.getCurrencyInstance(ptBr).format(vazio);
                                            binding.ValorGastoE.setText(vazioFormatado);
                                        }else {
                                            Double totalSaidaE = Double.parseDouble(documentSnapshotE.getString("ResultadoDaSomaSaidaE"));
                                            String totalSaidaFormatadoE = NumberFormat.getCurrencyInstance(ptBr).format(totalSaidaE);
                                            binding.ValorGastoE.setText(totalSaidaFormatadoE);

                                        }
                                        if (!documentSnapshotP.exists()) {
                                            String vazioFormatado = NumberFormat.getCurrencyInstance(ptBr).format(vazio);
                                            binding.ValorGastoP.setText(vazioFormatado);

                                        }else {
                                            Double totalSaidaP = Double.parseDouble(documentSnapshotP.getString("ResultadoDaSomaSaidaP"));
                                            String totalSaidaFormatadoP = NumberFormat.getCurrencyInstance(ptBr).format(totalSaidaP);
                                            binding.ValorGastoP.setText(totalSaidaFormatadoP);
                                        }
                                        if (!documentSnapshotD.exists()) {
                                            String vazioFormatado = NumberFormat.getCurrencyInstance(ptBr).format(vazio);
                                            binding.ValorGastoD.setText(vazioFormatado);

                                        }else {
                                            Double totalSaidaD = Double.parseDouble(documentSnapshotD.getString("ResultadoDaSomaSaidaD"));
                                            String totalSaidaFormatadoD = NumberFormat.getCurrencyInstance(ptBr).format(totalSaidaD);
                                            binding.ValorGastoD.setText(totalSaidaFormatadoD);

                                        }


                                        if (documentSnapshotEntradas.exists()) {
                                            double SomaEntrada = Double.parseDouble(documentSnapshotEntradas.getString("ResultadoDaSomaEntrada"));

                                            double PorcentagemE = Double.parseDouble(binding.txtPorcentagemEssenciais.getText().toString());
                                            double PorcentagemP = Double.parseDouble(binding.txtPorcentagemPagamentoDeDividas.getText().toString());
                                            double PorcentagemD = Double.parseDouble(binding.txtPorcentagemDesejosPessoais.getText().toString());

                                            if (PorcentagemE != 0 && PorcentagemP != 0 && PorcentagemD != 0){
                                                Double somaE = (SomaEntrada * PorcentagemE) /100;
                                                String valorConvertidoE = NumberFormat.getCurrencyInstance(ptBr).format(somaE);
                                                binding.ValorDisponivelE.setText(valorConvertidoE);

                                                Double somaP = (SomaEntrada * PorcentagemP) /100;
                                                String valorConvertidoP = NumberFormat.getCurrencyInstance(ptBr).format(somaP);
                                                binding.ValorDisponivelP.setText(valorConvertidoP);

                                                Double somaD = (SomaEntrada * PorcentagemD) /100;
                                                String valorConvertidoD = NumberFormat.getCurrencyInstance(ptBr).format(somaD);
                                                binding.ValorDisponivelD.setText(valorConvertidoD);

                                                if (documentSnapshotE.exists()){
                                                    // descobrir porcentagem partindo de outro valor E
                                                    Double totalSaidaE = Double.parseDouble(documentSnapshotE.getString("ResultadoDaSomaSaidaE"));
                                                    Double operacaoE = totalSaidaE / SomaEntrada * 100;
                                                    binding.txtPorcentagemGastosE.setText(decimalFormat.format(operacaoE));

                                                }
                                                if (documentSnapshotP.exists()){
                                                    // descobrir porcentagem partindo de outro valor P
                                                    double totalSaidaP = Double.parseDouble(documentSnapshotP.getString("ResultadoDaSomaSaidaP"));
                                                    Double operacaoP = totalSaidaP / SomaEntrada * 100;
                                                    binding.txtPorcentagemGastosP.setText(decimalFormat.format(operacaoP));
                                                }
                                                if (documentSnapshotD.exists()){
                                                    // descobrir porcentagem partindo de outro valor D
                                                    double totalSaidaD = Double.parseDouble(documentSnapshotD.getString("ResultadoDaSomaSaidaD"));
                                                    Double operacaoD = totalSaidaD / SomaEntrada * 100;
                                                    binding.txtPorcentagemGastosD.setText(decimalFormat.format(operacaoD));

                                                }
                                            }

                                        }else {

                                        }

                                    }
                                });

                            }
                        });
                    }
                });

            }
        });


    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(binding.drawerLayout);
    }



}