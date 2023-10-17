package br.com.lucas.valli.fluxodecaixa.Model;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ConversorDeMoeda {

    private BigDecimal valor;
    private static final String UNIDADE_MONETARIA = "R$";
    private static final DecimalFormat FORMATO = new DecimalFormat(UNIDADE_MONETARIA + " #,###,##0.00");

    public ConversorDeMoeda(String valor){
        this.valor = new BigDecimal(valor);
    }

    public void somarCom(String valorASomar){
        this.valor = this.valor.add(new BigDecimal(valorASomar));
    }

    public BigDecimal getValor(){
        return valor;
    }
    public String getValorFormatado(){
        return FORMATO.format(valor);
    }
}
