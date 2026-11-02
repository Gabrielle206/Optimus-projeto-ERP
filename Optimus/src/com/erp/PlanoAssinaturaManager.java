package com.erp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlanoAssinaturaManager {

    private static final PlanoAssinaturaManager instance = new PlanoAssinaturaManager();

    public static final String PLANO_BASICO = "Basico";
    public static final String PLANO_INTERMEDIARIO = "Intermediario";
    public static final String PLANO_PREMIUM = "Premium";
    public static final String MODULO_PESSOAS = "Pessoas";
    public static final String MODULO_PRODUTOS = "Produtos";
    public static final String MODULO_TITULOS = "Titulos";
    public static final String MODULO_VENDAS = "Vendas";
    public static final String MODULO_BALANCO = "Balanco";
    public static final String MODULO_CONTRATOS = "Contratos";
    public static final String MODULO_CONFIGURACAO = "Configuracao";
    public static final String MODULO_GOVCONNECT = "GovConnect";

    private final Map<String, Set<String>> permissoesPorPlano;
    private ConfiguracaoDAO configuracaoDAO;

    private PlanoAssinaturaManager() {
        this.configuracaoDAO = new ConfiguracaoDAO(); //
        this.permissoesPorPlano = new HashMap<>();

        Set<String> permissoesBasico = new HashSet<>();
        permissoesBasico.add(MODULO_PESSOAS);
        permissoesBasico.add(MODULO_TITULOS);
        permissoesBasico.add(MODULO_BALANCO);
        permissoesBasico.add(MODULO_CONFIGURACAO);
        permissoesPorPlano.put(PLANO_BASICO, permissoesBasico);

        Set<String> permissoesIntermediario = new HashSet<>(permissoesBasico);
        permissoesIntermediario.add(MODULO_PRODUTOS);
        permissoesIntermediario.add(MODULO_VENDAS);
        permissoesPorPlano.put(PLANO_INTERMEDIARIO, permissoesIntermediario);

        Set<String> permissoesPremium = new HashSet<>(permissoesIntermediario);
        permissoesPremium.add(MODULO_CONTRATOS);
        permissoesPremium.add(MODULO_GOVCONNECT); 
        permissoesPorPlano.put(PLANO_PREMIUM, permissoesPremium);
    }

    public static PlanoAssinaturaManager getInstance() {
        return instance;
    }

    public String getPlanoAtual() {
        String plano = configuracaoDAO.buscarValor("plano_assinatura"); 
        if (plano == null || !permissoesPorPlano.containsKey(plano)) {
            System.err.println("Aviso: Plano de assinatura não encontrado ou inválido no DB! Assumindo 'Basico' por segurança.");
            return PLANO_BASICO;
        }
        return plano;
    }

    public boolean temAcessoModulo(String nomeModulo) {
        String planoAtual = getPlanoAtual();
        Set<String> modulosPermitidos = permissoesPorPlano.get(planoAtual);

        if (modulosPermitidos == null) {
            System.err.println("Erro crítico: O plano '" + planoAtual + "' não tem permissões definidas.");
            return false;
        }
        boolean temAcesso = modulosPermitidos.contains(nomeModulo);
        if (!temAcesso) {
            System.out.println("Acesso negado ao módulo '" + nomeModulo + "' (Plano atual: " + planoAtual + ")");
        }
        return temAcesso;
    }

    public String[] getNomesPlanosDisponiveis() {
        return new String[]{ PLANO_BASICO, PLANO_INTERMEDIARIO, PLANO_PREMIUM };
    }
}