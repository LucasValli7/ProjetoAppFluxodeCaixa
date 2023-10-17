package br.com.lucas.valli.fluxodecaixa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.lucas.valli.fluxodecaixa.Model.DadosEntrada;
import br.com.lucas.valli.fluxodecaixa.R;

public class AdapterDadosEntrada extends RecyclerView.Adapter<AdapterDadosEntrada.DadosViewHolder> {

    private Context context;
    private List<DadosEntrada> dadosList;

    
    
    public AdapterDadosEntrada(Context context, List<DadosEntrada> dadosList) {
        this.context = context;
        this.dadosList = dadosList;
    }


    @NonNull
    @Override
    public DadosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ItemLista;
        LayoutInflater LayoutInflater = android.view.LayoutInflater.from(context);
        ItemLista = LayoutInflater.inflate(R.layout.dados_item_entrada, parent,false);
        return new DadosViewHolder(ItemLista);

    }

    @Override
    public void onBindViewHolder(@NonNull DadosViewHolder holder, int position) {
        holder.TipoDeEntrada.setText(dadosList.get(position).getTipoDeEntrada());
        holder.ValorDeEntrada.setText(dadosList.get(position).getValorDeEntrada());

    }

    @Override
    public int getItemCount() {
        return dadosList.size();
    }

    public class DadosViewHolder extends RecyclerView.ViewHolder{

        private TextView TipoDeEntrada;
        private TextView ValorDeEntrada;


        public DadosViewHolder(@NonNull View itemView) {
            super(itemView);
            TipoDeEntrada = itemView.findViewById(R.id.Item_DadosItem_TipoDeEntrada);
            ValorDeEntrada = itemView.findViewById(R.id.Item_DadosItem_ValorDeEntrada);
        }
    }
}
