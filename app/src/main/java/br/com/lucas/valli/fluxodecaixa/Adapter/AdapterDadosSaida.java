package br.com.lucas.valli.fluxodecaixa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.lucas.valli.fluxodecaixa.Model.DadosSaida;
import br.com.lucas.valli.fluxodecaixa.R;

public class AdapterDadosSaida extends RecyclerView.Adapter<AdapterDadosSaida.DadosViewHolder> {

    private List<DadosSaida> dadosSaidaList;
    private Context context;


    public AdapterDadosSaida(Context context, List<DadosSaida> dadosSaidaList){
        this.context = context;
        this.dadosSaidaList = dadosSaidaList;

    }

    @NonNull
    @Override
    public DadosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        itemLista = layoutInflater.inflate(R.layout.dados_item_saida,parent,false);
        return new DadosViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull DadosViewHolder holder, int position) {
        holder.TipoDeSaida.setText(dadosSaidaList.get(position).getTipoDeSaida());
        holder.ValorDeSaida.setText(dadosSaidaList.get(position).getValorDeSaida());

    }

    @Override
    public int getItemCount() {
        return dadosSaidaList.size();
    }

    public class DadosViewHolder extends RecyclerView.ViewHolder{

        private TextView TipoDeSaida;
        private TextView ValorDeSaida;

        public DadosViewHolder(@NonNull View itemView) {
            super(itemView);
            TipoDeSaida = itemView.findViewById(R.id.Item_DadosItem_TipoDeSaida);
            ValorDeSaida = itemView.findViewById(R.id.Item_DadosItem_ValorDeSaida);
        }
    }
}
