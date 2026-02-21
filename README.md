# ♟️ Java OOP Chess Engine

Desktop chess engine built with **Java** and **Swing**. 
This project uses **Object-Oriented Programming (OOP)** and was bridged backend logic with a functional GUI.

## 🚀 Key Features
* **Full OOP Design:** Uses Inheritance and Polymorphism to handle different piece rules.
* **Interactive UI:** Click-to-move functionality using Java Swing `MouseListeners`.
* **Smart Rendering:** High-res pieces using Unicode symbols (no heavy image files needed).
* **Validation:** Custom math logic to enforce legal moves for Knights and Rooks.

## 🏗️ Architecture
I used a **Model-View** separation to keep the code organized:
* **`core`**: Handles the board grid and square states.
* **`pieces`**: Contains the abstract `Piece` class and specific logic for each chessman.
* **`gui`**: Manages the window, grid rendering, and click detection.


## 🛠️ Tech Stack
* **Language:** Java 17+
* **GUI Framework:** Java Swing
* **IDE:** VS Code
* **Patterns:** Abstract Classes, Method Overriding, Encapsulation.

## 🔧 How to Run
1. Open the project in **VS Code**.
2. Make sure the **Java Extension Pack** is installed.
3. Run `Main.java` to launch the game.

## 📈 Next Steps
- [ ] Add path-checking (so Rooks can't jump over pieces).
- [ ] Add the Pawn, Bishop, Queen, and King.
- [ ] Implement Check/Checkmate detection.
