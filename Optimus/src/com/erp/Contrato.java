package com.erp;

import java.time.LocalDate;

public class Contrato {

    private String id;
    private String fornecedorId;
    private String objetoContrato;
    private double valor;
    private LocalDate dataCriacao;
    private LocalDate dataAssinaturaEmpresa;
    private LocalDate dataAssinaturaFornecedor;
    private LocalDate dataVencimento;
    private String status;
    private String blockchainTxHash;
    private String smartContractAddress;
    private boolean notaFiscalValidada;

    public Contrato(String id, String fornecedorId, String objetoContrato, double valor, 
                    LocalDate dataCriacao, LocalDate dataVencimento, String status) {
        this.id = id;
        this.fornecedorId = fornecedorId;
        this.objetoContrato = objetoContrato;
        this.valor = valor;
        this.dataCriacao = dataCriacao;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.notaFiscalValidada = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(String fornecedorId) { this.fornecedorId = fornecedorId; }

    public String getObjetoContrato() { return objetoContrato; }
    public void setObjetoContrato(String objetoContrato) { this.objetoContrato = objetoContrato; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDate getDataAssinaturaEmpresa() { return dataAssinaturaEmpresa; }
    public void setDataAssinaturaEmpresa(LocalDate dataAssinaturaEmpresa) { this.dataAssinaturaEmpresa = dataAssinaturaEmpresa; }

    public LocalDate getDataAssinaturaFornecedor() { return dataAssinaturaFornecedor; }
    public void setDataAssinaturaFornecedor(LocalDate dataAssinaturaFornecedor) { this.dataAssinaturaFornecedor = dataAssinaturaFornecedor; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBlockchainTxHash() { return blockchainTxHash; }
    public void setBlockchainTxHash(String blockchainTxHash) { this.blockchainTxHash = blockchainTxHash; }

    public String getSmartContractAddress() { return smartContractAddress; }
    public void setSmartContractAddress(String smartContractAddress) { this.smartContractAddress = smartContractAddress; }

    public boolean isNotaFiscalValidada() { return notaFiscalValidada; }
    public void setNotaFiscalValidada(boolean notaFiscalValidada) { this.notaFiscalValidada = notaFiscalValidada; }
}