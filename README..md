# Testes Unitários com JUnit

Guia introdutório sobre testes unitários em Java usando o JUnit (JUnit 5 / Jupiter, com notas sobre JUnit 4 onde os nomes mudam).

---

## Por que escrever testes unitários?

- **Confiança para mudar o código**: com testes, você refatora sem medo de quebrar algo silenciosamente.
- **Documentação viva**: um teste bem escrito mostra como a classe deve se comportar, muitas vezes melhor que um comentário.
- **Detecção precoce de bugs**: erros são pegos no momento em que o código é escrito, não em produção.
- **Design melhor**: código difícil de testar geralmente é código mal desenhado (muito acoplado, com responsabilidades demais). Escrever testes força um design mais limpo.
- **Menos medo de deploy**: uma suíte de testes que passa dá segurança para lançar novas versões com frequência.

---

## Hello World no JUnit

Estrutura mínima de um projeto com Maven (`pom.xml`):

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
```

Classe a ser testada:

```java
public class Calculadora {
    public int somar(int a, int b) {
        return a + b;
    }
}
```

Primeiro teste:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraTest {

    @Test
    void deveSomarDoisNumeros() {
        Calculadora calc = new Calculadora();
        int resultado = calc.somar(2, 3);
        assertEquals(5, resultado);
    }
}
```

A anotação `@Test` marca o método como um caso de teste. O JUnit executa cada método anotado e reporta sucesso ou falha.

---

## Básico para testar

Um teste geralmente segue o padrão **AAA**:

1. **Arrange** (organizar): prepara os objetos e dados necessários.
2. **Act** (agir): executa o método que está sendo testado.
3. **Assert** (verificar): confirma que o resultado é o esperado.

```java
@Test
void exemploAAA() {
    // Arrange
    Calculadora calc = new Calculadora();

    // Act
    int resultado = calc.somar(10, 5);

    // Assert
    assertEquals(15, resultado);
}
```

Boas práticas:
- Um teste deve verificar **um único comportamento**.
- Nomes descritivos (`deveLancarExcecaoQuandoDivisorForZero`) ajudam a entender falhas rapidamente.
- Testes devem ser **independentes** entre si (a ordem de execução não deveria importar, a não ser que você configure isso explicitamente).

---

## Algumas asserções

O JUnit fornece a classe `Assertions` com vários métodos estáticos:

```java
import static org.junit.jupiter.api.Assertions.*;

@Test
void exemploDeAssercoes() {
    assertEquals(4, 2 + 2);           // valores iguais
    assertNotEquals(5, 2 + 2);        // valores diferentes
    assertTrue(3 > 2);                // condição verdadeira
    assertFalse(2 > 3);               // condição falsa
    assertNull(null);                 // é nulo
    assertNotNull("algo");            // não é nulo
    assertSame("a", "a");             // mesma referência (para Strings internas)
    assertArrayEquals(new int[]{1,2}, new int[]{1,2}); // arrays iguais

    // Várias asserções agrupadas — todas são executadas mesmo se uma falhar
    assertAll("verificações de pessoa",
        () -> assertEquals("Ana", "Ana"),
        () -> assertTrue(30 > 18)
    );
}
```

Dica: `assertEquals(esperado, atual)` — a ordem importa apenas para a mensagem de erro, não para o resultado do teste.

---

## `@BeforeEach` / `@AfterEach` e `@BeforeAll` / `@AfterAll`

(No JUnit 4 os equivalentes eram `@Before`, `@After`, `@BeforeClass`, `@AfterClass`.)

```java
import org.junit.jupiter.api.*;

class ContaTest {

    private Conta conta;

    @BeforeAll
    static void configuracaoUnica() {
        System.out.println("Executa uma vez, antes de todos os testes");
    }

    @BeforeEach
    void configuracaoPorTeste() {
        conta = new Conta(100.0);
        System.out.println("Executa antes de cada teste");
    }

    @Test
    void deveDepositar() {
        conta.depositar(50);
        assertEquals(150.0, conta.getSaldo());
    }

    @Test
    void deveSacar() {
        conta.sacar(30);
        assertEquals(70.0, conta.getSaldo());
    }

    @AfterEach
    void limpezaPorTeste() {
        System.out.println("Executa depois de cada teste");
    }

    @AfterAll
    static void limpezaFinal() {
        System.out.println("Executa uma vez, no final de tudo");
    }
}
```

Isso evita repetir código de preparação/limpeza em cada método de teste.

---

## Assumptions e testes condicionais

Às vezes você quer que um teste só rode em certas condições (por exemplo, apenas em determinado sistema operacional ou ambiente). Para isso existem as **assumptions**: se a condição não for satisfeita, o teste é **abortado** (não falha, apenas é ignorado).

```java
import static org.junit.jupiter.api.Assumptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.*;

class TesteCondicionalTest {

    @Test
    void rodaSomenteSeForAmbienteDev() {
        assumeTrue("DEV".equals(System.getenv("AMBIENTE")));
        // se a condição acima for falsa, o teste é abortado aqui
        assertEquals(2, 1 + 1);
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void rodaSomenteNoLinux() {
        assertTrue(true);
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void naoRodaNoWindows() {
        assertTrue(true);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    void rodaSomenteNoAmbienteDeIntegracaoContinua() {
        assertTrue(true);
    }
}
```

Diferença chave: `assertX` faz o teste **falhar**; `assumeX` faz o teste **ser ignorado** quando a condição não é atendida.

---

## Testando exceptions

Para verificar que um método lança a exceção esperada, use `assertThrows`:

```java
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraTest {

    @Test
    void deveLancarExcecaoAoDividirPorZero() {
        Calculadora calc = new Calculadora();

        ArithmeticException excecao = assertThrows(
            ArithmeticException.class,
            () -> calc.dividir(10, 0)
        );

        assertEquals("/ by zero", excecao.getMessage());
    }
}
```

Também é possível verificar que **nenhuma** exceção é lançada com `assertDoesNotThrow`:

```java
@Test
void naoDeveLancarExcecao() {
    assertDoesNotThrow(() -> calc.somar(1, 1));
}
```

---

## Ordenando testes

Por padrão, o JUnit **não garante** uma ordem específica entre os métodos de teste (e isso é intencional — testes devem ser independentes). Mas quando é necessário forçar uma ordem (ex.: testes de integração sequenciais), use:

```java
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FluxoDePedidoTest {

    @Test
    @Order(1)
    void deveCriarPedido() {
        // ...
    }

    @Test
    @Order(2)
    void deveAdicionarItemAoPedido() {
        // ...
    }

    @Test
    @Order(3)
    void deveFinalizarPedido() {
        // ...
    }
}
```

Outras estratégias de `MethodOrderer` disponíveis: `MethodName` (ordem alfabética), `Random` (ordem aleatória, útil para expor dependências ocultas entre testes) e `DisplayName`.

---

## Recursos de teste no Visual Studio Code

Para trabalhar com JUnit no VS Code:

1. **Extension Pack for Java** (da Microsoft) — inclui suporte a Maven/Gradle, debugger e o essencial para projetos Java.
2. **Test Runner for Java** — geralmente já vem junto no pacote acima. Ele adiciona:
   - Ícones de "play" ao lado de cada método `@Test` e de cada classe de teste, para rodar individualmente ou em conjunto.
   - Uma aba **Testing** (ícone de frasco/erlenmeyer na barra lateral) que lista todos os testes do projeto em árvore.
   - Execução com um clique e exibição do resultado (verde/vermelho) diretamente no editor.
   - Suporte a **debug** de testes: é possível colocar breakpoints dentro do método de teste ou da classe testada e depurar passo a passo.
3. **CodeLens inline**: acima de cada `@Test`, aparecem links como `Run Test | Debug Test` diretamente no código.
4. **Relatórios**: os resultados também aparecem no painel "Test Results", com stack trace completo em caso de falha — útil para rastrear rapidamente qual asserção falhou.

Dica prática: configure `.vscode/settings.json` com `"java.test.config"` se quiser perfis de teste diferentes (ex.: variáveis de ambiente distintas para rodar os testes condicionais mencionados acima).

---

## Recursos de teste no Eclipse

O Eclipse tem suporte a JUnit nativo desde suas primeiras versões — não precisa instalar plugin nenhum:

1. **Ícone de "play" verde**: clique com o botão direito na classe de teste (ou em um método específico) → **Run As → JUnit Test**. Também dá pra usar o atalho `Alt+Shift+X, T`.
2. **JUnit View**: uma aba própria (**Window → Show View → Other → Java → JUnit**) mostra uma árvore com todos os testes executados — verde para sucesso, vermelho para falha — e, ao clicar em um teste que falhou, o stack trace aparece embaixo com link direto para a linha do erro.
3. **Barra de progresso**: a clássica "barra verde/vermelha" do JUnit é exibida no topo da JUnit View, dando feedback visual imediato sobre o total de testes passados/falhos.
4. **Debug de testes**: **Debug As → JUnit Test** roda os testes com o debugger ativo, permitindo colocar breakpoints tanto no teste quanto na classe testada.
5. **Re-run apenas dos testes que falharam**: na JUnit View existe um botão para rodar novamente **somente** os testes que falharam na última execução — útil quando você está corrigindo bugs específicos sem rodar a suíte inteira.
6. **Suporte a Maven**: se o projeto for Maven (com `pom.xml`), o Eclipse (via m2e) também reconhece a estrutura `src/test/java` automaticamente e permite rodar `mvn test` direto pela interface (**Run As → Maven test**).

---

## Recursos de teste no IntelliJ IDEA

O IntelliJ é conhecido por ter uma das integrações mais completas com JUnit, também sem necessidade de plugins extras (o suporte já vem embutido):

1. **Ícones de "play" na margem esquerda**: aparecem ao lado de cada classe e de cada método `@Test`. Clique para rodar; clique com o botão direito para ver opções como **Run**, **Debug**, **Run with Coverage** (roda os testes já medindo cobertura de código).
2. **Aba "Run"**: mostra os resultados em uma árvore expansível, com tempo de execução de cada teste, e permite reexecutar individualmente qualquer teste clicando duas vezes nele.
3. **Navegação rápida**: `Ctrl+Shift+T` (Windows/Linux) alterna entre a classe de teste e a classe testada e vice-versa — muito útil em projetos grandes.
4. **Live Templates**: digitar `test` + `Tab` dentro de uma classe gera automaticamente o esqueleto de um método `@Test`.
5. **Detecção de falhas com diff visual**: quando um `assertEquals` falha, o IntelliJ mostra um comparador visual lado a lado (esperado vs. atual), facilitando muito identificar a diferença exata, especialmente em strings ou objetos grandes.
6. **Rerun failed tests**: assim como no Eclipse, existe um botão na aba Run para rodar apenas os testes que falharam.
7. **Geração automática de testes**: `Alt+Insert` (ou clique direito → Generate) sobre uma classe oferece a opção **Test...**, que cria automaticamente o arquivo de teste correspondente já na pasta `src/test/java`, com o pacote correto.
8. **Cobertura de código integrada**: rodar com **Run with Coverage** mostra, linha por linha, quais partes do código foram exercitadas pelos testes — sem precisar configurar ferramentas externas como JaCoCo manualmente (embora o JaCoCo também seja suportado).

Dica prática: no IntelliJ, o arquivo `.idea/runConfigurations` guarda configurações de execução específicas (como variáveis de ambiente para os testes condicionais), similar ao que o `.vscode/settings.json` faz no VS Code.

---

## Resumo rápido

| Conceito | Anotação/Método |
|---|---|
| Marcar um teste | `@Test` |
| Antes/depois de cada teste | `@BeforeEach` / `@AfterEach` |
| Antes/depois de todos os testes | `@BeforeAll` / `@AfterAll` |
| Verificar resultado | `assertEquals`, `assertTrue`, `assertNotNull`, etc. |
| Ignorar teste condicionalmente | `assumeTrue`, `assumeFalse`, `@EnabledOnOs`, etc. |
| Testar exceções | `assertThrows`, `assertDoesNotThrow` |
| Ordenar testes | `@TestMethodOrder` + `@Order` |