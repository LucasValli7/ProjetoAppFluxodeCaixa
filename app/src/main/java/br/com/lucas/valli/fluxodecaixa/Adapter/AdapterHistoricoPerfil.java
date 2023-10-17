package br.com.lucas.valli.fluxodecaixa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import br.com.lucas.valli.fluxodecaixa.Model.HistoricoPerfil;
import br.com.lucas.valli.fluxodecaixa.R;
import kotlin.jvm.internal.PropertyReference0Impl;

public class AdapterHistoricoPerfil extends RecyclerView.Adapter<AdapterHistoricoPerfil.HistoricoPerfilViewHolder> {

    private Context context;
    private List<HistoricoPerfil> historicoPerfilList;

    public AdapterHistoricoPerfil(Context context, List<HistoricoPerfil> historicoPerfilList) {
        this.context = context;
        this.historicoPerfilList = historicoPerfilList;
    }

    @NonNull
    @Override
    public HistoricoPerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ItemLIsta;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ItemLIsta = layoutInflater.inflate(R.layout.perfil_historicos_entradas, parent, false);
        return new HistoricoPerfilViewHolder(ItemLIsta);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricoPerfilViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class HistoricoPerfilViewHolder extends RecyclerView.ViewHolder{

        private TextView MesesEntrada;


        public HistoricoPerfilViewHolder(@NonNull View itemView) {
            super(itemView);
            MesesEntrada = itemView.findViewById(R.id.item_mesesEntradas);
        }
    }
}
