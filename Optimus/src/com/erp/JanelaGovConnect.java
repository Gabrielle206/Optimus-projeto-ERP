package com.erp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;

public class JanelaGovConnect extends JFrame implements LanguageObserver {

    private static final long serialVersionUID = 1L;

    private GovConnectClient govClient;
    private JTable tabelaLicitacoes;
    private DefaultTableModel modeloTabela;
    private JTextField campoBusca;
    private JButton botaoBuscar, botaoEnviarProposta, botaoVoltar;
    private JLabel labelBusca;

    public JanelaGovConnect() {
        this.govClient = GovConnectClient.getInstance(); 
        LanguageManager.getInstance().addObserver(this); 

        initComponents();
        updateLanguage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageManager.getInstance().removeObserver(JanelaGovConnect.this);
            }
        });
    }

    private void initComponents() {
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon img = new ImageIcon("iconeerp/logo1.png");
        setIconImage(img.getImage());

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelBusca = new JLabel();
        campoBusca = new JTextField(30);
        botaoBuscar = new JButton();
        painelBusca.add(labelBusca);
        painelBusca.add(campoBusca);
        painelBusca.add(botaoBuscar);
        painelPrincipal.add(painelBusca, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaLicitacoes = new JTable(modeloTabela);
        painelPrincipal.add(new JScrollPane(tabelaLicitacoes), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoEnviarProposta = new JButton();
        botaoVoltar = new JButton();
        painelBotoes.add(botaoEnviarProposta);
        painelBotoes.add(botaoVoltar);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);

        botaoBuscar.addActionListener(e -> carregarLicitacoes());
        botaoEnviarProposta.addActionListener(e -> enviarProposta());
        botaoVoltar.addActionListener(e -> {
            LanguageManager.getInstance().removeObserver(this);
            dispose();
        });
    }

    @Override
    public void updateLanguage() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages(); 
        setTitle(messages.getString("janelaGovConnect.titulo"));
        
        labelBusca.setText(messages.getString("janelaGovConnect.label.buscar"));
        botaoBuscar.setText(messages.getString("janelaGovConnect.botao.buscar"));
        botaoEnviarProposta.setText(messages.getString("janelaGovConnect.botao.enviarProposta"));
        botaoVoltar.setText(messages.getString("janelaListagemProdutos.botao.voltar"));

        String[] colunas = {
                messages.getString("janelaGovConnect.coluna.id"),
                messages.getString("janelaGovConnect.coluna.orgao"),
                messages.getString("janelaGovConnect.coluna.objeto"),
                messages.getString("janelaGovConnect.coluna.valorMax")
        };
        modeloTabela.setColumnIdentifiers(colunas);

        modeloTabela.setRowCount(0);
    }

    private void carregarLicitacoes() {
        var languageManager = LanguageManager.getInstance();
        var locale = languageManager.getCurrentLocale();
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(locale);
        
        modeloTabela.setRowCount(0); 
        
        List<Licitacao> licitacoes = govClient.buscarLicitacoes(campoBusca.getText()); 

        for (Licitacao lic : licitacoes) { 
            modeloTabela.addRow(new Object[]{
                    lic.getId(), 
                    lic.getOrgaoPublico(), 
                    lic.getObjeto(), 
                    formatadorMoeda.format(lic.getValorMaximo()) 
            });
        }
    }

    private void enviarProposta() {
        ResourceBundle messages = LanguageManager.getInstance().getMessages();
        int linhaSelecionada = tabelaLicitacoes.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                    messages.getString("janelaGovConnect.dialogo.selecioneParaEnviar"),
                    messages.getString("janelaGovConnect.botao.enviarProposta"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String licitacaoId = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
        
        Proposta proposta = new Proposta( 
                licitacaoId,
                "Optimus ERP",
                115000.00, 
                "Proposta técnica detalhada para consultoria ERP."
        );

        boolean sucesso = govClient.enviarProposta(proposta); 

        if (sucesso) {
            JOptionPane.showMessageDialog(this,
                    String.format(messages.getString("janelaGovConnect.dialogo.propostaSucesso.mensagem"), licitacaoId),
                    messages.getString("janelaGovConnect.dialogo.propostaSucesso.titulo"),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}