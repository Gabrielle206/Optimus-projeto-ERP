package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContratoDAO {

    public void salvar(Contrato contrato) {
        String sql = "INSERT INTO Contratos(id, fornecedor_id, objeto_contrato, valor, data_criacao, " +
                     "data_vencimento, status, nota_fiscal_validada) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            CryptoManager crypto = CryptoManager.getInstance();

            pstmt.setString(1, contrato.getId());
            pstmt.setString(2, contrato.getFornecedorId());
            pstmt.setString(3, contrato.getObjetoContrato());
            pstmt.setString(4, crypto.encrypt(String.valueOf(contrato.getValor())));
            pstmt.setString(5, contrato.getDataCriacao().toString());
            pstmt.setString(6, contrato.getDataVencimento().toString());
            pstmt.setString(7, contrato.getStatus());
            pstmt.setInt(8, contrato.isNotaFiscalValidada() ? 1 : 0);
            
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao salvar contrato: " + e.getMessage());
        }
    }

    public void atualizar(Contrato contrato) {
        String sql = "UPDATE Contratos SET fornecedor_id = ?, objeto_contrato = ?, valor = ?, data_criacao = ?, " +
                     "data_assinatura_empresa = ?, data_assinatura_fornecedor = ?, data_vencimento = ?, " +
                     "status = ?, blockchain_tx_hash = ?, smart_contract_address = ?, nota_fiscal_validada = ? " +
                     "WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            CryptoManager crypto = CryptoManager.getInstance();

            pstmt.setString(1, contrato.getFornecedorId());
            pstmt.setString(2, contrato.getObjetoContrato());
            pstmt.setString(3, crypto.encrypt(String.valueOf(contrato.getValor())));
            pstmt.setString(4, contrato.getDataCriacao().toString());
            
            pstmt.setString(5, contrato.getDataAssinaturaEmpresa() != null ? contrato.getDataAssinaturaEmpresa().toString() : null);
            pstmt.setString(6, contrato.getDataAssinaturaFornecedor() != null ? contrato.getDataAssinaturaFornecedor().toString() : null);
            pstmt.setString(7, contrato.getDataVencimento().toString());
            pstmt.setString(8, contrato.getStatus());
            pstmt.setString(9, contrato.getBlockchainTxHash());
            pstmt.setString(10, contrato.getSmartContractAddress());
            pstmt.setInt(11, contrato.isNotaFiscalValidada() ? 1 : 0);
            pstmt.setString(12, contrato.getId());
            
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar contrato: " + e.getMessage());
        }
    }

    public Contrato buscarPorId(String id) {
        String sql = "SELECT * FROM Contratos WHERE id = ?";
        Contrato contrato = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                contrato = parseContrato(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar contrato por ID: " + e.getMessage());
        }
        return contrato;
    }

    public List<Contrato> listarTodos() {
        List<Contrato> contratos = new ArrayList<>();
        String sql = "SELECT * FROM Contratos";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                contratos.add(parseContrato(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar contratos: " + e.getMessage());
        }
        return contratos;
    }

    public void excluir(String id) {
        String sql = "DELETE FROM Contratos WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao excluir contrato: " + e.getMessage());
        }
    }

    private Contrato parseContrato(ResultSet rs) throws SQLException {
        CryptoManager crypto = CryptoManager.getInstance();
        
        String valorCifrado = rs.getString("valor");
        double valorDecifrado = 0.0;
        try {
            valorDecifrado = Double.parseDouble(crypto.decrypt(valorCifrado));
        } catch (NumberFormatException e) {
            System.err.println("Erro ao decifrar valor do contrato: " + rs.getString("id"));
        }

        Contrato contrato = new Contrato(
                rs.getString("id"),
                rs.getString("fornecedor_id"),
                rs.getString("objeto_contrato"),
                valorDecifrado,
                LocalDate.parse(rs.getString("data_criacao")),
                LocalDate.parse(rs.getString("data_vencimento")),
                rs.getString("status")
        );

        String dataAssinaturaEmpresa = rs.getString("data_assinatura_empresa");
        if (dataAssinaturaEmpresa != null) {
            contrato.setDataAssinaturaEmpresa(LocalDate.parse(dataAssinaturaEmpresa));
        }

        String dataAssinaturaFornecedor = rs.getString("data_assinatura_fornecedor");
        if (dataAssinaturaFornecedor != null) {
            contrato.setDataAssinaturaFornecedor(LocalDate.parse(dataAssinaturaFornecedor));
        }

        contrato.setBlockchainTxHash(rs.getString("blockchain_tx_hash"));
        contrato.setSmartContractAddress(rs.getString("smart_contract_address"));
        contrato.setNotaFiscalValidada(rs.getInt("nota_fiscal_validada") == 1);
        
        return contrato;
    }
}