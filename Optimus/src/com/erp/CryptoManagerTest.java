package com.erp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class CryptoManagerTest {

    private CryptoManager cryptoManager;

    @BeforeEach
    @DisplayName("Obtém a instância do CryptoManager antes de cada teste")
    void setUp() {
        this.cryptoManager = CryptoManager.getInstance();
    }

    @Test
    @DisplayName("Testa o fluxo de Criptografar e Decifrar dados")
    void testEncryptDecryptFlow() {
        System.out.println("Executando teste: testEncryptDecryptFlow");
        
        String originalData = "1234.50";
        String encryptedData = cryptoManager.encrypt(originalData);
        String decryptedData = cryptoManager.decrypt(encryptedData);

        assertThat(encryptedData).isNotNull().isNotEqualTo(originalData);
        assertThat(decryptedData).isEqualTo(originalData);
        
        System.out.println("Resultado: Fluxo Encrypt/Decrypt validado com sucesso.");
    }

    @Test
    @DisplayName("Testa o fluxo de Hashing e Verificação de Senhas")
    void testPasswordHashingFlow() {
        System.out.println("Executando teste: testPasswordHashingFlow");

        String plainPassword = "senhaForte123";
        String wrongPassword = "senhaFraca456";
        String hashedPassword = cryptoManager.hashPassword(plainPassword);

        assertThat(hashedPassword).isNotNull().isNotEqualTo(plainPassword);

        boolean isCorrect = cryptoManager.checkPassword(plainPassword, hashedPassword);
        assertThat(isCorrect).isTrue();

        boolean isWrong = cryptoManager.checkPassword(wrongPassword, hashedPassword);
        assertThat(isWrong).isFalse();
        
        System.out.println("Resultado: Fluxo de Hash/Check de Senha validado com sucesso.");
    }
}