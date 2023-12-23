package br.com.lucas.valli.fluxodecaixa.Model;

import android.content.Context;
import android.content.SharedPreferences;

public class SalvarTotalE {

    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private final String TOTAL_E = "arquivo.totalE";

    private final String CHAVE_E = "TOTAL_E";


    public SalvarTotalE(Context c) {
        this.context = c;
        preferences = context.getSharedPreferences(TOTAL_E, 0);

        editor = preferences.edit();
    }

    public void salvarTotalE(String salvarAnotação){
        editor.putString(CHAVE_E, salvarAnotação);
        editor.commit();
    }

    public String recuperarTotalE(){
        return preferences.getString(CHAVE_E, "");

    }
}
