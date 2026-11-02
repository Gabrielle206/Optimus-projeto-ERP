package com.erp;

public class Proposta {

    private String licitacaoId;
    private String empresaProponente; 
    private double valor;
    private String descricaoTecnica;

    public Proposta(String licitacaoId, String empresaProponente, double valor, String descricaoTecnica) {
        this.licitacaoId = licitacaoId;
        this.empresaProponente = empresaProponente;
        this.valor = valor;
        this.descricaoTecnica = descricaoTecnica;
    }

    
    public String getLicitacaoId() {
        return licitacaoId;
    }

    public String getEmpresaProponente() {
        return empresaProponente;
    }

    public double getValor() {
        return valor;
    }

    public String getDescricaoTecnica() {
        return descricaoTecnica;
    }
}