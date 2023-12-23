package br.com.lucas.valli.fluxodecaixa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.lucas.valli.fluxodecaixa.Adapter.ViewPagerAdapter;
import br.com.lucas.valli.fluxodecaixa.FragmentActivity.FragmentDP;
import br.com.lucas.valli.fluxodecaixa.FragmentActivity.FragmentGE;
import br.com.lucas.valli.fluxodecaixa.FragmentActivity.FragmentPD;
import br.com.lucas.valli.fluxodecaixa.databinding.ActivityTelaPrincipalSaidasBinding;

public class TelaPrincipalSaidas extends AppCompatActivity {
    private ActivityTelaPrincipalSaidasBinding binding;

    private Date x = new Date();
    private String mes = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(x);
    private String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        AtivarPagerView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityTelaPrincipalSaidasBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        binding.tolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaPrincipalSaidas.this, TelaPrincipal.class);
                startActivity(intent);
            }
        });

    }
    public void AtivarPagerView(){
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.ViewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());


        viewPagerAdapter.AddFragment(new FragmentGE(),"Gastos essenciais");
        viewPagerAdapter.AddFragment(new FragmentPD(),"Pagamento de dívidas");
        viewPagerAdapter.AddFragment(new FragmentDP(),"desejos pessoais");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    public void AbrirCalendario() {

        Date x = new Date();
        String Mes = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(x);
        String Ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
    }
}