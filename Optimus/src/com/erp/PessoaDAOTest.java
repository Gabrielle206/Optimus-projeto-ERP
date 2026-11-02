package com.erp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


public class PessoaDAOTest {

    private PessoaDAO pessoaDAO;
    private final String TEST_USER_ID = "junit_test_user";
    private final String TEST_USER_PASS = "junit_pass_123";
    private final String WRONG_PASS = "wrong_pass";

    @BeforeEach
    @DisplayName("Configura o ambiente: Limpa e cria um usuário de teste no DB")
    void setUp() {
    	DatabaseManager.initializeDatabase();
    	
        pessoaDAO = new PessoaDAO();
        
        pessoaDAO.excluir(TEST_USER_ID); 
        
        Pessoa testUser = new Pessoa(TEST_USER_ID, 3, "Usuário de Teste JUnit", "999999");
        testUser.setSenha(TEST_USER_PASS);
        
        pessoaDAO.salvar(testUser);
    }

    @AfterEach
    @DisplayName("Limpa o ambiente: Remove o usuário de teste do DB")
    void tearDown() {
        pessoaDAO.excluir(TEST_USER_ID);
    }

    @Test
    @DisplayName("Testa se a conta é bloqueada (brute-force) após 5 logins falhos")
    void testBruteForceLockoutLogic() {
        System.out.println("Executando teste: testBruteForceLockoutLogic");

        Pessoa pessoaLogada = pessoaDAO.autenticar(TEST_USER_ID, TEST_USER_PASS); 
        assertThat(pessoaLogada).isNotNull(); 
        assertThat(pessoaLogada.getId()).isEqualTo(TEST_USER_ID);

        for (int i = 1; i <= 4; i++) {
            System.out.println("Simulando tentativa falha #" + i);
            Pessoa falha = pessoaDAO.autenticar(TEST_USER_ID, WRONG_PASS);
            assertThat(falha).isNull(); 
        }

        Pessoa pessoaAntesDoBloqueio = pessoaDAO.buscarPorId(TEST_USER_ID);
        assertThat(pessoaAntesDoBloqueio.getTentativasFalhas()).isEqualTo(4); 
        assertThat(pessoaAntesDoBloqueio.getBloqueadoAte()).isNull(); 


        System.out.println("Simulando tentativa falha #5 (deve bloquear)");
        Pessoa falhaBloqueio = pessoaDAO.autenticar(TEST_USER_ID, WRONG_PASS);
        assertThat(falhaBloqueio).isNull();

        Pessoa pessoaBloqueada = pessoaDAO.buscarPorId(TEST_USER_ID);
        assertThat(pessoaBloqueada.getTentativasFalhas()).isEqualTo(5); 
        assertThat(pessoaBloqueada.getBloqueadoAte()).isNotNull(); 

        assertThat(pessoaBloqueada.getBloqueadoAte()).isAfter(LocalDateTime.now().plusMinutes(9)); 

        System.out.println("Simulando login CORRETO em conta bloqueada (deve falhar)");
        Pessoa falhaPorBloqueio = pessoaDAO.autenticar(TEST_USER_ID, TEST_USER_PASS);

        assertThat(falhaPorBloqueio).isNull(); 
        
        System.out.println("Resultado: Lógica de bloqueio por força bruta validada com sucesso.");
    }
}