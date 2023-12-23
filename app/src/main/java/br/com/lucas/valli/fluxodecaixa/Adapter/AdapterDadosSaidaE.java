package br.com.lucas.valli.fluxodecaixa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.lucas.valli.fluxodecaixa.Model.DadosSaidaE;
import br.com.lucas.valli.fluxodecaixa.FragmentActivity.FragmentGE;
import br.com.lucas.valli.fluxodecaixa.R;



public class AdapterDadosSaidaE extends RecyclerView.Adapter<AdapterDadosSaidaE.DadosViewHolder> {



    private List<DadosSaidaE> dadosSaidaEListE;
    private Context context;

    private FirebaseFirestore db;
    private String usuarioID;

    private Date x = new Date();
    private String mes = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(x);
    private String ano = new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(x);
    private FragmentGE fragmentGE;



    public AdapterDadosSaidaE(Context context, List<DadosSaidaE> dadosSaidaEList){
        this.context = context;
        this.dadosSaidaEListE = dadosSaidaEList;


    }


    @NonNull
    @Override
    public DadosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        itemLista = layoutInflater.inflate(R.layout.dados_item_saida_epd,parent,false);
        return new DadosViewHolder(itemLista);
    }


    @Override
    public void onBindViewHolder(@NonNull DadosViewHolder holder, int position) {

        holder.TipoDeSaida.setText(dadosSaidaEListE.get(position).getTipoDeSaida());
        holder.ValorDeSaida.setText(dadosSaidaEListE.get(position).getValorDeSaida());
        holder.dataDeSaida.setText(dadosSaidaEListE.get(position).getDataDeSaida());

    }

    public void deleteItem(int position){

        DadosSaidaE item = dadosSaidaEListE.get(position);
        db.collection(usuarioID).document(ano).collection(mes).document("saidas")
                .collection("nova saida").document("categoria").collection("Gastos Essenciais")
                .document(item.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            notifyRemovedItem(position);
                            Toast.makeText(context.getApplicationContext(), "teste", Toast.LENGTH_LONG).show();
                        }else {

                            Toast.makeText(context.getApplicationContext(), "error" + task.getException(), Toast.LENGTH_LONG);

                        }
                    }
                });

    }

    private void notifyRemovedItem(int position){
        dadosSaidaEListE.remove(position);
        notifyRemovedItem(position);
        fragmentGE.RecuperarDadosSaidasE();
    }

    @Override
    public int getItemCount() {
        return dadosSaidaEListE.size();
    }

    public class DadosViewHolder extends RecyclerView.ViewHolder{

        private TextView TipoDeSaida;
        private TextView ValorDeSaida;
        private TextView dataDeSaida;

        public DadosViewHolder(@NonNull View itemView) {
            super(itemView);
            TipoDeSaida = itemView.findViewById(R.id.txt_valorTipoSaida);
            ValorDeSaida = itemView.findViewById(R.id.txt_valorSaida);
            dataDeSaida = itemView.findViewById(R.id.item_valorDataSaida);
        }
    }
}
