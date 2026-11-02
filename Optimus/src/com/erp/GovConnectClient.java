package com.erp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GovConnectClient {

    private static final GovConnectClient instance = new GovConnectClient();
    private String tokenSimulado;

    private GovConnectClient() {

    }

    public static GovConnectClient getInstance() {
        return instance;
    }

    public String obterTokenFederado() {
        if (this.tokenSimulado != null) {
            System.out.println("[GovConnect SIM] Reutilizando token de autenticação federada.");
            return this.tokenSimulado;
        }

        System.out.println("[GovConnect SIM] Simulação: Autenticando com sistema federado...");
        this.tokenSimulado = "simulado_jwt_header." + UUID.randomUUID().toString() + ".assinatura_ficticia";
        System.out.println("[GovConnect SIM] Token obtido: " + this.tokenSimulado.substring(0, 30) + "...");
        return this.tokenSimulado;
    }

    public List<Licitacao> buscarLicitacoes(String filtro) {
        String token = obterTokenFederado();
        
        System.out.println("[GovConnect SIM] Buscando licitações com filtro: '" + filtro + "' (Token: " + token.substring(0, 30) + "...)");
        
        List<Licitacao> licitacoesFalsas = new ArrayList<>();
        
        licitacoesFalsas.add(new Licitacao(
                "ID-001", 
                "Ministério da Educação", 
                "Aquisição de 500 computadores", 
                250000.00
        ));
        licitacoesFalsas.add(new Licitacao(
                "ID-002", 
                "Ministério da Saúde", 
                "Compra de 1.000.000 de máscaras", 
                500000.00
        ));
        licitacoesFalsas.add(new Licitacao(
                "ID-003", 
                "Prefeitura de Salgueiro", 
                "Serviço de consultoria em gestão de ERP", 
                120000.00
        ));
        
        return licitacoesFalsas;
    }

    public boolean enviarProposta(Proposta proposta) {
        String token = obterTokenFederado();
        System.out.println("[GovConnect SIM] Enviando proposta para Licitação ID: " + proposta.getLicitacaoId() + "...");
        System.out.println("[GovConnect SIM] Valor da proposta: " + proposta.getValor());
        System.out.println("[GovConnect SIM] Autenticação: " + token.substring(0, 30) + "...");
        
        return true;
    }
}