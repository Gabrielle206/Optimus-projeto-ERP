package com.erp;

public class Licitacao {

    private String id;
    private String orgaoPublico;
    private String objeto;
    private double valorMaximo;

    public Licitacao(String id, String orgaoPublico, String objeto, double valorMaximo) {
        this.id = id;
        this.orgaoPublico = orgaoPublico;
        this.objeto = objeto;
        this.valorMaximo = valorMaximo;
    }

    public String getId() {
        return id;
    }

    public String getOrgaoPublico() {
        return orgaoPublico;
    }

    public String getObjeto() {
        return objeto;
    }

    public double getValorMaximo() {
        return valorMaximo;
    }

    @Override
    public String toString() {
        return id + ": " + objeto + " (R$ " + valorMaximo + ")";
    }
}