# Brincadeira de Crianças - Simulação de Concorrência

Este projeto simula o problema de concorrência "Brincadeira de Crianças" utilizando **Java 17**, **Gradle** e **Swing**. O objetivo é demonstrar conceitos de sistemas operacionais como sincronização de threads, semáforos e a diferença entre espera bloqueante e espera ocupada (busy-wait).

## 🚀 Tecnologias e Conceitos
- **Java Threads**: Cada criança opera em sua própria thread.
- **Sincronização**: Uso de `Semaphore` para o recurso compartilhado (cesto) e `ReentrantLock` para sincronização segura com a interface gráfica.
- **Swing (Custom Rendering)**: Visualização baseada em grade 2D com 60 FPS via `paintComponent`.
- **Modos de Execução**: 
  - **Bloqueante**: Utiliza bloqueio real de threads (`acquire()`, `wait/notify`). Baixo uso de CPU.
  - **Busy-Wait**: Utiliza loops ativos e `Thread.onSpinWait()`. Alto uso de CPU.

## 📏 Regras e Visualização
- **Grid**: Malha quadriculada de 40px por célula.
- **Cesto**: Recurso central de tamanho 3x3, com capacidade imutável após o início.
- **Crianças**: Representadas por círculos de 0.5x0.5 em relação à célula, com movimento discreto (salto entre quadrados).
- **Cores por Estado**:
  - 🟢 **Brincando** (Verde)
  - 🔵 **Descansando** (Azul)
  - 🟡 **Pegando Bola** (Amarelo)
  - 🔴 **Guardando Bola** (Vermelho)

## 📁 Estrutura do Projeto
- `core/`: Lógica central (Cesto, Criança, Máquina de Estados).
- `ui/`: Interface gráfica Swing (MainFrame, SimulationPanel).
- `simulation/`: Gerenciamento do ciclo de vida da simulação.
- `config/`: Configurações globais e constantes do sistema.
- `util/`: Utilitários (Logger thread-safe).

## 🛠️ Como Executar
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
