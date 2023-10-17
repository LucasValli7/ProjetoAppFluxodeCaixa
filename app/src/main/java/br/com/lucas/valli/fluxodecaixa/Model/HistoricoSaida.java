package br.com.lucas.valli.fluxodecaixa.Model;

public class HistoricoSaida {

    private String TipoDeSaida;
    private String ValorDeSaida;
    private String dataDeSaida;

    public String getTipoDeSaida() {
        return TipoDeSaida;
    }

    public void setTipoDeSaida(String tipoDeSaida) {
        TipoDeSaida = tipoDeSaida;
    }

    public String getValorDeSaida() {
        return ValorDeSaida;
    }

    public void setValorDeSaida(String valorDeSaida) {
        ValorDeSaida = valorDeSaida;
    }

    public String getDataDeSaida() {
        return dataDeSaida;
    }

    public void setDataDeSaida(String dataDeSaida) {
        this.dataDeSaida = dataDeSaida;
    }
}
