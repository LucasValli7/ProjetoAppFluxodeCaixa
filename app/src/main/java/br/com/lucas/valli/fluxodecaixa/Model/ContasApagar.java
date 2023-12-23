package br.com.lucas.valli.fluxodecaixa.Model;

public class ContasApagar {

    private String TipoDeSaida;
    private String ValorDeSaida;
    private String dataDeSaida;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
