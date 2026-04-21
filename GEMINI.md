# Gemini CLI - Project Context: Brincadeira de Crianças

This project is a Java-based concurrency simulation demonstrating OS concepts like thread synchronization, semaphores, and the difference between blocking and busy-wait states.

## 🚀 Project Overview
- **Purpose**: Simulate multiple children (threads) competing for balls in a central basket (shared resource).
- **Core Stack**: Java 17, Gradle, Swing (Custom Rendering).
- **Architecture**:
    - `core`: Contains the simulation logic. `Cesto.java` manages the shared resource using semaphores. `Crianca.java` represents the worker threads.
    - `ui`: Handles the graphical interface using Swing's `paintComponent` for custom grid-based rendering.
    - `simulation`: Manages the overall lifecycle and engine of the simulation.
    - `config`: Global constants (Grid size, Max children, etc.).

## 🛠️ Building and Running
- **Build**: `./gradlew build`
- **Run**: `./gradlew run`
- **Clean**: `./gradlew clean`

## 🧩 Key Concurrency Concepts
- **Resource Management**: Uses a `Semaphore` for balls and spaces, and a mutex for protecting the basket count.
- **Execution Modes**:
    - **Blocking**: Threads use `acquire()` and `Object.wait(ms, ns)` for idle states, minimizing CPU usage.
    - **Busy-Wait**: Threads use active loops with `Thread.onSpinWait()` and `System.nanoTime()`, resulting in high CPU usage.
- **Thread Safety**: 
    - `ReentrantLock` (`uiLock`) is used in `Crianca.java` to synchronize state reading between worker threads and the UI rendering thread (EDT).
    - `Logger.java` is thread-safe for console output.

## 📏 Simulation Rules
- **Grid**: A 17x17 grid where children move discretely.
- **Basket**: A 5x5 central area (resource).
- **Children**: 
    - Max 20 children.
    - Deterministic spawn around the basket (skipping corners).
    - Lifecycle: Pick Ball -> Play (Move out/back) -> Return Ball -> Rest (Move out/back).
    - Movement is synchronized with timers (5 steps out, 5 steps back).

## 🎨 UI Conventions
- **Rendering**: Custom drawing in `SimulationPanel.java` at ~30-60 FPS.
- **Dynamic Layout**: Grid cells resize based on window dimensions with defined offsets (`OFFSET_X`, `OFFSET_Y`) for padding.
- **Visual Feedback**:
    - 🟢 **Green**: Playing (Brincando)
    - 🔵 **Blue**: Resting (Descansando)
    - 🟡 **Yellow**: Picking Ball (Pegando Bola)
    - 🔴 **Red**: Returning Ball (Guardando Bola)
    - Labels: ID and remaining time are positioned dynamically based on the child's spawn quadrant to avoid overlap.

## 📝 Development Notes
- **No `Thread.sleep()`**: Strictly forbidden per requirements; use high-precision timing methods.
- **Time Inputs**: All simulation times (Tb, Td) are handled as `double` seconds.
