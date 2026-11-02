package com.erp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.core.Robot;
import org.assertj.swing.core.BasicRobot;

import static org.assertj.core.api.Assertions.*;

public class JanelaLoginTest {

    private FrameFixture window;
    private PessoaDAO pessoaDAO;
    private Robot robot;

    private final String TEST_USER_ID = "junit_login_test";
    private final String TEST_USER_PASS = "login_pass_123";

    @BeforeEach
    @DisplayName("Cria um usuário de teste (sem MFA) e inicia a JanelaLogin")
    public void setUp() {
    	DatabaseManager.initializeDatabase();
    	
        pessoaDAO = new PessoaDAO();

        pessoaDAO.excluir(TEST_USER_ID); 

        Pessoa testUser = new Pessoa(TEST_USER_ID, 3, "Teste Login UI", "12345");
        testUser.setSenha(TEST_USER_PASS);
        testUser.setSecretKey(null); 
        pessoaDAO.salvar(testUser); 

        this.robot = BasicRobot.robotWithCurrentAwtHierarchy();

        JanelaLogin frame = GuiActionRunner.execute(() -> new JanelaLogin()); 
        
        window = new FrameFixture(this.robot, frame);
        window.show();
    }

    @AfterEach
    @DisplayName("Limpa o ambiente: Fecha o robô e remove o usuário do DB")
    public void tearDown() {
        window.cleanUp();

        if (this.robot != null) {
            this.robot.cleanUp();
        }
        
        pessoaDAO.excluir(TEST_USER_ID);
    }

    @Test
    @DisplayName("Testa um login com sucesso e verifica se a JanelaPrincipal abre")
    public void testLoginSucesso() {
        System.out.println("Executando teste: testLoginSucesso (UI)");

        window.textBox("campoUsuario").enterText(TEST_USER_ID); 

        window.textBox("campoSenha").enterText(TEST_USER_PASS); 

        window.button("botaoEntrar").click(); 

        FrameFixture mainFrame = WindowFinder.findFrame("janelaPrincipal").using(this.robot);

        mainFrame.requireVisible();
        
        window.requireNotVisible();

        System.out.println("Resultado: Teste de UI de login validado com sucesso.");
    }
}