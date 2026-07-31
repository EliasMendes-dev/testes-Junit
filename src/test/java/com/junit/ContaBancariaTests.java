package com.junit;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class ContaBancariaTests {

    private ContaBancaria conta;

    @BeforeEach
    void configuracao() {
        conta = new ContaBancaria(100.0);
    }

    // ---------- EXCEPTIONS ----------

    @Test
    void deveLancarExcecaoAoSacarValorMaiorQueSaldo() {
        IllegalStateException excecao = assertThrows(
            IllegalStateException.class,
            () -> conta.sacar(500.0)
        );
        assertEquals("Saldo insuficiente", excecao.getMessage());
    }

    @Test
    void deveLancarExcecaoAoDepositarValorNegativo() {
        assertThrows(
            IllegalArgumentException.class,
            () -> conta.depositar(-10.0)
        );
    }

    @Test
    void deveLancarExcecaoAoCriarContaComSaldoNegativo() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new ContaBancaria(-50.0)
        );
    }

    @Test
    void naoDeveLancarExcecaoAoSacarValorValido() {
        assertDoesNotThrow(() -> conta.sacar(50.0));
    }

    // ---------- ASSUMPTIONS ----------

    @Test
    void deveRodarSomenteSeAmbienteForDeTeste() {
        // Se a variável de ambiente AMBIENTE não for "TESTE", o teste é abortado (não falha, é ignorado)
        assumeTrue("TESTE".equals(System.getenv("AMBIENTE")));

        conta.depositar(50.0);
        assertEquals(150.0, conta.getSaldo());
    }

    @Test
    void naoDeveRodarSeEstiverEmProducao() {
        assumeFalse("PRODUCAO".equals(System.getenv("AMBIENTE")));

        conta.sacar(30.0);
        assertEquals(70.0, conta.getSaldo());
    }

    @Test
    void deveUsarAssumingThatParaExecutarApenasUmaParte() {
        // assumingThat NÃO aborta o teste inteiro — só pula o bloco se a condição for falsa
        conta.depositar(20.0);
        assertEquals(120.0, conta.getSaldo());

        assumingThat("TESTE".equals(System.getenv("AMBIENTE")), () -> {
            // este bloco só roda se a condição for verdadeira
            conta.sacar(20.0);
            assertEquals(100.0, conta.getSaldo());
        });
    }

    // ---------- TESTES CONDICIONAIS (anotações) ----------

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void rodaSomenteNoWindows() {
        assertNotNull(conta);
    }

    @Test
    @DisabledOnOs(OS.LINUX)
    void naoRodaNoLinux() {
        assertTrue(conta.getSaldo() > 0);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    void rodaSomenteNoAmbienteDeIntegracaoContinua() {
        assertEquals(100.0, conta.getSaldo());
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "SKIP_SLOW_TESTS", matches = "true")
    void rodaAMenosQueEstejaMarcadoParaPular() {
        assertTrue(true);
    }
}
