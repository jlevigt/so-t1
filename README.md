# Brincadeira de Crianças - Simulação de Concorrência

Este projeto simula o problema de concorrência "Brincadeira de Crianças" utilizando Java 17, Gradle e JavaFX.

## Regras de Concorrência
- Cada criança é uma Thread.
- O cesto é o recurso compartilhado (produtor/consumidor).
- São utilizados apenas Semáforos para controle de acesso.
- Não é utilizado `Thread.sleep()`; a espera ocupada (busy work) é simulada com loops de CPU e `Thread.yield()`.

## Estrutura do Projeto
- `model/`: Lógica de negócio (Cesto, Criança, Estado).
- `ui/`: Interface gráfica JavaFX.
- `util/`: Utilitários (Logger thread-safe).

## Como Executar
Para rodar o projeto, você deve ter o Java 17 instalado.

```bash
# Se você já tiver o Gradle instalado:
gradle run

# Ou via wrapper (se gerado):
./gradlew run
```

## Funcionamento da UI
1. Informe a capacidade **K** do cesto e clique em **Iniciar Cesto**.
2. Informe os tempos **Tb** (Brincando) e **Td** (Descansando) em milissegundos.
3. Clique em **Criar Criança** para adicionar threads dinamicamente.
4. Acompanhe o estado das crianças na tabela e o log de eventos no final da janela.
