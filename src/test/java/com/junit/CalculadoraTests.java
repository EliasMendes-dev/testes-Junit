package com.junit;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CalculadoraTests {

    private Calculadora calc;

    @BeforeAll
    static void configuracaoUnica() {
        // Executa uma única vez, antes de todos os testes da classe
        System.out.println(">> Iniciando a suíte de testes da Calculadora");
    }

    @BeforeEach
    void configuracaoPorTeste() {
        // Executa antes de CADA método de teste
        calc = new Calculadora();
        System.out.println("-> Nova instância de Calculadora criada");
    }

    @Test
    @Order(1)
    void deveSomarDoisNumeros() {
        int resultado = calc.somar(2, 3);
        assertEquals(5, resultado);
    }

    @Test
    @Order(2)
    void deveSomarNumerosNegativos() {
        int resultado = calc.somar(-2, -3);
        assertEquals(-5, resultado);
    }

    @Test
    @Order(3)
    void resultadoNaoDeveSerDiferenteDeCinco() {
        int resultado = calc.somar(2, 3);
        assertNotEquals(6, resultado);
    }

    @Test
    @Order(4)
    void resultadoDeveSerPositivo() {
        int resultado = calc.somar(2, 3);
        assertTrue(resultado > 0);
    }

    @Test
    @Order(5)
    void calculadoraNaoDeveSerNula() {
        assertNotNull(calc);
    }

    @AfterEach
    void limpezaPorTeste() {
        // Executa depois de CADA método de teste
        System.out.println("<- Teste finalizado\n");
    }

    @AfterAll
    static void limpezaFinal() {
        // Executa uma única vez, depois de todos os testes da classe
        System.out.println(">> Suíte de testes da Calculadora finalizada");
    }
}