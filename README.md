# Brincadeira de Crianças - Simulação de Concorrência

Este projeto simula o problema de concorrência "Brincadeira de Crianças" utilizando **Java 17**, **Gradle** e **Swing**. O objetivo é demonstrar conceitos de sistemas operacionais como sincronização de threads, semáforos e a diferença entre espera bloqueante e espera ocupada (busy-wait).

## 🚀 Executáveis (Download Direto)

Para facilitar o teste sem a necessidade de compilação manual, os executáveis estão disponíveis na pasta `dist/`:

- **Windows**: [BrincadeiraCriancas.exe](dist/BrincadeiraCriancas.exe) (Requer Java 17+ instalado no PATH)
- **Universal (Linux/macOS/Windows)**: [BrincadeiraCriancas.jar](dist/BrincadeiraCriancas.jar)

### Como executar o JAR:
```bash
java -jar dist/BrincadeiraCriancas.jar
```

## 🛠️ Tecnologias e Conceitos

- **Java 17+**: Requisito mínimo para execução.
- **Java Threads**: Cada criança opera em sua própria thread.
- **Sincronização**: Uso de `Semaphore` para o recurso compartilhado (cesto) e `ReentrantLock` para sincronização segura com a interface gráfica.
- **Swing (Custom Rendering)**: Visualização baseada em grade 2D com 60 FPS via `paintComponent`.
- **Modos de Execução**:
  - **Bloqueante**: Utiliza bloqueio real de threads (`acquire()`, `wait/notify`). Baixo uso de CPU.
  - **Busy-Wait**: Utiliza loops ativos e `Thread.onSpinWait()`. Alto uso de CPU.

## 📏 Regras e Visualização

- **Grid**: Malha quadriculada de 17x17 células.
- **Cesto**: Recurso central de tamanho 5x5.
- **Cores por Estado**:
  - 🟢 **Brincando** (Verde)
  - 🔵 **Descansando** (Azul)
  - 🟡 **Pegando Bola** (Amarelo)
  - 🔴 **Guardando Bola** (Vermelho)

## 📁 Estrutura do Projeto

- `core/`: Lógica central (Cesto, Criança, Máquina de Estados).
- `ui/`: Interface gráfica Swing.
- `dist/`: Binários compilados (JAR e EXE).
- `config/`: Configurações globais.

## 🔐 Sincronização com Semáforos
... (restante do conteúdo mantido) ...

O projeto utiliza o modelo **Produtor-Consumidor** para gerenciar o acesso ao cesto. São utilizados três semáforos para garantir a exclusão mútua e a sincronização de estados:

1.  **`semBolas`**: Contador de recursos (bolas disponíveis). Inicializado com `0`.
2.  **`semEspacos`**: Contador de slots vazios. Inicializado com a capacidade `k`.
3.  **`mutex`**: Binário (0 ou 1). Garante que apenas uma thread altere o contador interno por vez.

### Lógica em Pseudo-código

#### Pegar Bola (Criança quer brincar)

```pascal
procedimento pegarBola()
    P(semBolas)   // Aguarda até ter pelo menos uma bola
    P(mutex)      // Garante acesso exclusivo ao cesto

    quantidadeBolas := quantidadeBolas - 1

    V(mutex)      // Libera o cesto
    V(semEspacos) // Sinaliza que um espaço foi liberado
fim
```

#### Colocar Bola (Criança vai descansar)

```pascal
procedimento colocarBola()
    P(semEspacos) // Aguarda até ter espaço no cesto
    P(mutex)      // Garante acesso exclusivo ao cesto

    quantidadeBolas := quantidadeBolas + 1

    V(mutex)      // Libera o cesto
    V(semBolas)   // Sinaliza que uma nova bola está disponível
fim
```

> **Nota**: No modo **Bloqueante**, a operação `P()` suspende a thread. No modo **Busy-Wait**, `P()` é implementado como um loop ativo que testa o semáforo continuamente.

## ⚡ Busy-Wait e `Thread.onSpinWait()` (**Default**)

O projeto utiliza o modo **Busy-Wait** como padrão. Diferente do modo bloqueante, as threads não entram em estado de suspensão pelo sistema operacional. Elas executam um loop ativo verificando a condição de liberação do recurso.

Para otimizar esse comportamento, o projeto utiliza `Thread.onSpinWait()`. Esta chamada é fundamental para o entendimento de concorrência em nível de hardware:

- **Não cede o Time-Slice**: Ao contrário de `Thread.sleep()` ou `Object.wait()`, a thread permanece ativa no processador e não é movida para a fila de espera do escalonador do SO.
- **Sem Troca de Contexto (Context Switch)**: Como a thread nunca sai do estado *RUNNABLE*, o custo de salvar e restaurar registradores (troca de contexto) é inexistente durante a espera.
- **Diferença de Hardware**: O `onSpinWait` emite uma instrução de "hint" para o processador (como a instrução `PAUSE` em x86). Isso permite que o pipeline do processador evite violações de memória especulativas e reduza o consumo de energia enquanto mantém a prontidão para sair do loop com latência mínima.

### Referência Externa
- [Java 17 Documentation - Thread.onSpinWait()](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Thread.html#onSpinWait())

### Diferença de Desempenho
- **Bloqueante**: Uso de CPU próximo a **0-1%** (Thread suspensa).
- **Busy-Wait**: Uso de CPU sobe para **100%** de um ou mais núcleos (Thread ativa em loop).

## 🛠️ Como Executar (Desenvolvimento)

Certifique-se de ter o **JDK 17** ou superior instalado.

```bash
# Via wrapper do Gradle:
./gradlew run
```

## 🎮 Funcionamento da UI

1. **Configuração Inicial**: Defina a capacidade **k** do cesto e selecione o **Modo de Execução**. Clique em **Iniciar Simulação**.
2. **Criação de Crianças**: Após iniciar, informe os tempos **Tb** (Brincando) e **Td** (Descansando) em **segundos** (ex: `1.5`).
3. **Limite**: O sistema suporta até **20 crianças** simultâneas.
4. **Interação**: Acompanhe o movimento das crianças na grade, a contagem de bolas no cesto e os logs de sistema em tempo real na parte inferior.

## 📝 Notas de Implementação

- **Sem `Thread.sleep()`**: A simulação de tempo utiliza `wait(ms, ns)` no modo bloqueante e `nanoTime()` no modo busy-wait, cumprindo os requisitos de SO.
- **Thread Safety**: A UI lê o estado das crianças utilizando locks para garantir que a renderização nunca ocorra enquanto os dados estão sendo modificados pelas threads worker.
