package com.erp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class JanelaListagemContratos extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private ContratoDAO contratoDAO;
    private PessoaDAO pessoaDAO;
    private ContratoService contratoService;
    
    private JTable tabelaContratos;
    private DefaultTableModel modeloTabela;
    private JButton botaoNovo, botaoEditar, botaoExcluir, botaoVoltar;
    private JButton botaoAssinarEmpresa, botaoAssinarFornecedor, botaoValidarNF;

    public JanelaListagemContratos() {
        this.contratoDAO = new ContratoDAO();
        this.pessoaDAO = new PessoaDAO();
        this.contratoService = new ContratoService();
        LanguageManager.getInstance().addObserver(this);

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaListagemContratos.this);
            }
        });
    }

    private void initComponents() {
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon img = new ImageIcon("iconeerp/logo1.png");
        setIconImage(img.getImage());

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaContratos = new JTable(modeloTabela);
        painelPrincipal.add(new JScrollPane(tabelaContratos), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new BorderLayout());
        
        JPanel painelBotoesCRUD = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoNovo = new JButton();
        botaoEditar = new JButton();
        botaoExcluir = new JButton();
        botaoVoltar = new JButton();
        painelBotoesCRUD.add(botaoNovo);
        painelBotoesCRUD.add(botaoEditar);
        painelBotoesCRUD.add(botaoExcluir);
        painelBotoesCRUD.add(botaoVoltar);
        
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botaoAssinarEmpresa = new JButton();
        botaoAssinarFornecedor = new JButton();
        botaoValidarNF = new JButton();
        painelBotoesAcao.add(botaoAssinarEmpresa);
        painelBotoesAcao.add(botaoAssinarFornecedor);
        painelBotoesAcao.add(botaoValidarNF);

        painelBotoes.add(painelBotoesAcao, BorderLayout.WEST);
        painelBotoes.add(painelBotoesCRUD, BorderLayout.EAST);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);

        botaoNovo.addActionListener(e -> abrirJanelaCadastro(null));
        botaoEditar.addActionListener(e -> editarContratoSelecionado());
        botaoExcluir.addActionListener(e -> excluirContratoSelecionado());
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });

        botaoAssinarEmpresa.addActionListener(e -> executarAcaoContrato(
            "janelaListagemContratos.botao.assinarEmpresa", 
            (id) -> contratoService.assinarContrato(id, true)
        ));
        botaoAssinarFornecedor.addActionListener(e -> executarAcaoContrato(
            "janelaListagemContratos.botao.assinarFornecedor",
            (id) -> contratoService.assinarContrato(id, false)
        ));
        botaoValidarNF.addActionListener(e -> executarAcaoContrato(
            "janelaListagemContratos.botao.validarNF",
            (id) -> contratoService.validarNotaFiscal(id)
        ));
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        setTitle(messages.getString("janelaListagemContratos.titulo"));

        String[] colunas = {
            messages.getString("janelaListagemContratos.coluna.id"),
            messages.getString("janelaListagemContratos.coluna.fornecedor"),
            messages.getString("janelaListagemContratos.coluna.objeto"),
            messages.getString("janelaListagemContratos.coluna.valor"),
            messages.getString("janelaListagemContratos.coluna.status"),
            messages.getString("janelaListagemContratos.coluna.dataVencimento"),
            messages.getString("janelaListagemContratos.coluna.txHash"),
            messages.getString("janelaListagemContratos.coluna.scAddress")
        };
        modeloTabela.setColumnIdentifiers(colunas);

        botaoNovo.setText(messages.getString("janelaListagemProdutos.botao.novo"));
        botaoEditar.setText(messages.getString("janelaListagemProdutos.botao.editar"));
        botaoExcluir.setText(messages.getString("janelaListagemProdutos.botao.excluir"));
        botaoVoltar.setText(messages.getString("janelaListagemProdutos.botao.voltar"));
        
        botaoAssinarEmpresa.setText(messages.getString("janelaListagemContratos.botao.assinarEmpresa"));
        botaoAssinarFornecedor.setText(messages.getString("janelaListagemContratos.botao.assinarFornecedor"));
        botaoValidarNF.setText(messages.getString("janelaListagemContratos.botao.validarNF"));

        carregarDados();
    }

    private void carregarDados() {
        var languageManager = LanguageManager.getInstance();
        var locale = languageManager.getCurrentLocale();
        var messages = languageManager.getMessages();

        modeloTabela.setRowCount(0);

        Map<String, String> mapaNomesPessoas = new HashMap<>();
        pessoaDAO.listarTodos().forEach(p -> mapaNomesPessoas.put(p.getId(), p.getNome()));

        List<Contrato> contratos = contratoDAO.listarTodos();
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(locale);
        DateTimeFormatter formatadorData = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);

        for (Contrato contrato : contratos) {
            modeloTabela.addRow(new Object[]{
                contrato.getId(),
                mapaNomesPessoas.getOrDefault(contrato.getFornecedorId(), "N/A"),
                contrato.getObjetoContrato(),
                formatadorMoeda.format(contrato.getValor()),
                contrato.getStatus(),
                contrato.getDataVencimento().format(formatadorData),
                contrato.getBlockchainTxHash() != null ? contrato.getBlockchainTxHash().substring(0, 10) + "..." : "",
                contrato.getSmartContractAddress() != null ? contrato.getSmartContractAddress().substring(0, 10) + "..." : ""
            });
        }
    }

    private void abrirJanelaCadastro(Contrato contrato) {
        new JanelaCadastroContrato(this, contrato).setVisible(true);
        carregarDados(); 
    }

    private String getSelectedContractId() {
        int linhaSelecionada = tabelaContratos.getSelectedRow();
        if (linhaSelecionada == -1) {
            return null;
        }
        return (String) tabelaContratos.getValueAt(linhaSelecionada, 0);
    }
    
    private void executarAcaoContrato(String actionKey, java.util.function.Function<String, Boolean> serviceCall) {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        String id = getSelectedContractId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, 
                messages.getString("janelaListagemContratos.dialogo.selecioneParaAcao"), 
                messages.getString(actionKey), 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean sucesso = serviceCall.apply(id);
        if (sucesso) {
            JOptionPane.showMessageDialog(this, 
                messages.getString("janelaListagemContratos.dialogo.acaoSucesso.mensagem"), 
                messages.getString("janelaListagemContratos.dialogo.acaoSucesso.titulo"), 
                JOptionPane.INFORMATION_MESSAGE);

            carregarDados(); 
        }
    }

    private void editarContratoSelecionado() {
        var messages = LanguageManager.getInstance().getMessages();
        String id = getSelectedContractId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemTitulos.dialogo.selecioneParaEditar.mensagem"), messages.getString("janelaListagemTitulos.dialogo.selecioneParaEditar.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        Contrato contratoSelecionado = contratoDAO.buscarPorId(id);
        if (contratoSelecionado != null) {
            abrirJanelaCadastro(contratoSelecionado);
        }
    }

    private void excluirContratoSelecionado() {
        var messages = LanguageManager.getInstance().getMessages();
        String id = getSelectedContractId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemTitulos.dialogo.selecioneParaEditar.mensagem"), messages.getString("janelaListagemTitulos.dialogo.selecioneParaEditar.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int resposta = JOptionPane.showConfirmDialog(this,
                messages.getString("janelaListagemTitulos.dialogo.confirmarExclusao.mensagem"),
                messages.getString("janelaListagemTitulos.dialogo.confirmarExclusao.titulo"),
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            contratoDAO.excluir(id);
            JOptionPane.showMessageDialog(this, messages.getString("janelaListagemTitulos.dialogo.exclusaoSucesso"));
            carregarDados(); 
        }
    }
}