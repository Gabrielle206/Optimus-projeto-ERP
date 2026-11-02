package com.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {

    public void salvar(Pessoa pessoa) {
        String sql = "INSERT INTO Pessoas(id, nome, tipo, telefone, senha, cpf, endereco, secret_key) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            CryptoManager crypto = CryptoManager.getInstance();

            pstmt.setString(1, pessoa.getId());
            pstmt.setString(2, crypto.encrypt(pessoa.getNome()));
            pstmt.setInt(3, pessoa.getTipo());
            pstmt.setString(4, crypto.encrypt(pessoa.getTelefone()));
            if (pessoa.getSenha() != null && !pessoa.getSenha().isEmpty()) {
                pstmt.setString(5, crypto.hashPassword(pessoa.getSenha()));
            } else {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            }
            pstmt.setString(6, crypto.encrypt(pessoa.getCpf()));
            pstmt.setString(7, crypto.encrypt(pessoa.getEndereco()));
            pstmt.setString(8, pessoa.getSecretKey());
            
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao salvar pessoa: " + e.getMessage());
        }
    }

    public void atualizar(Pessoa pessoa) {
        StringBuilder sql = new StringBuilder("UPDATE Pessoas SET nome = ?, tipo = ?, telefone = ?, cpf = ?, endereco = ?");
        if (pessoa.getSenha() != null && !pessoa.getSenha().isEmpty()) {
            sql.append(", senha = ?");
        }
        if (pessoa.getSecretKey() != null) {
            sql.append(", secret_key = ?");
        }
        sql.append(" WHERE id = ?");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            CryptoManager crypto = CryptoManager.getInstance();
            int index = 1;

            pstmt.setString(index++, crypto.encrypt(pessoa.getNome()));
            pstmt.setInt(index++, pessoa.getTipo());
            pstmt.setString(index++, crypto.encrypt(pessoa.getTelefone()));
            pstmt.setString(index++, crypto.encrypt(pessoa.getCpf()));
            pstmt.setString(index++, crypto.encrypt(pessoa.getEndereco()));

            if (pessoa.getSenha() != null && !pessoa.getSenha().isEmpty()) {
                pstmt.setString(index++, crypto.hashPassword(pessoa.getSenha()));
            }
            if (pessoa.getSecretKey() != null) {
                pstmt.setString(index++, pessoa.getSecretKey());
            }
            pstmt.setString(index, pessoa.getId());
            
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar pessoa: " + e.getMessage());
        }
    }

    public Pessoa buscarPorId(String id) {
        String sql = "SELECT * FROM Pessoas WHERE id = ?";
        Pessoa pessoa = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            CryptoManager crypto = CryptoManager.getInstance();
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                pessoa = new Pessoa(
                        rs.getString("id"),
                        rs.getInt("tipo"),
                        crypto.decrypt(rs.getString("nome")),
                        crypto.decrypt(rs.getString("telefone")));
                
                pessoa.setSenha(rs.getString("senha")); 
                pessoa.setTentativasFalhas(rs.getInt("tentativas_falhas"));
                pessoa.setSecretKey(rs.getString("secret_key"));
                pessoa.setCpf(crypto.decrypt(rs.getString("cpf")));
                pessoa.setEndereco(crypto.decrypt(rs.getString("endereco")));

                String bloqueadoStr = rs.getString("bloqueado_ate");
                if (bloqueadoStr != null) {
                    pessoa.setBloqueadoAte(LocalDateTime.parse(bloqueadoStr));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pessoa por ID: " + e.getMessage());
        }
        return pessoa;
    }

    public List<Pessoa> listarTodos() {
        List<Pessoa> pessoas = new ArrayList<>();
        String sql = "SELECT * FROM Pessoas";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            CryptoManager crypto = CryptoManager.getInstance();
            while (rs.next()) {
                Pessoa pessoa = new Pessoa(
                        rs.getString("id"),
                        rs.getInt("tipo"),
                        crypto.decrypt(rs.getString("nome")),
                        crypto.decrypt(rs.getString("telefone")));
                
                pessoa.setCpf(crypto.decrypt(rs.getString("cpf")));
                pessoa.setEndereco(crypto.decrypt(rs.getString("endereco")));
                
                pessoas.add(pessoa);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar pessoas: " + e.getMessage());
        }
        return pessoas;
    }
    
    public Pessoa autenticar(String id, String senha) {
        Pessoa pessoa = buscarPorId(id);
        
        if (pessoa == null || pessoa.getTipo() != 3) {
            return null;
        }
        if (pessoa.getBloqueadoAte() != null && LocalDateTime.now().isBefore(pessoa.getBloqueadoAte())) {
            return null;
        }
        
        CryptoManager crypto = CryptoManager.getInstance();
        if (crypto.checkPassword(senha, pessoa.getSenha())) {
            pessoa.setTentativasFalhas(0);
            pessoa.setBloqueadoAte(null);
            atualizarSeguranca(pessoa);
            return pessoa;
        } else {
            int tentativas = pessoa.getTentativasFalhas() + 1;
            pessoa.setTentativasFalhas(tentativas);
            if (tentativas >= 5) {
                pessoa.setBloqueadoAte(LocalDateTime.now().plusMinutes(10));
            }
            atualizarSeguranca(pessoa);
            return null;
        }
    }
    
    public void excluir(String id) {
        String sql = "DELETE FROM Pessoas WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao excluir pessoa: " + e.getMessage());
        }
    }

    public void atualizarSeguranca(Pessoa pessoa) {
        String sql = "UPDATE Pessoas SET tentativas_falhas = ?, bloqueado_ate = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pessoa.getTentativasFalhas());
            if (pessoa.getBloqueadoAte() != null) {
                pstmt.setString(2, pessoa.getBloqueadoAte().toString());
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }
            pstmt.setString(3, pessoa.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar segurança da pessoa: " + e.getMessage());
        }
    }
}