# Puzzle Game

A Java Swing desktop game based on the 15-puzzle. Slide numbered image tiles into the empty space to reconstruct a picture, or use the built-in A* solver to compute and animate a solution.

## Features

- A 4 × 4 puzzle board with randomly generated solvable arrangements.
- Keyboard controls, a move counter, and a preview of the completed picture.
- Multiple picture themes, including cats, landscapes, and game artwork.
- An A* solver that runs in the background and plays the solution as an animation.
- Local account registration, login, and saved scores.

## A* solver

The solver prioritizes puzzle states using `f(n) = g(n) + h(n)`, where `g(n)` is the number of moves made and `h(n)` combines Manhattan distance with pairwise linear-conflict scoring. It uses a priority queue, a map of the best known path costs, and a packed 64-bit representation of each board. Parent links reconstruct the sequence of moves.

Search runs in a `SwingWorker`; a Swing timer animates the resulting moves without running the search on the UI thread. The solver is experimental: difficult random boards can use substantial time and memory, and shortest-path optimality is not guaranteed. Wait for solving and playback to finish before starting another solve or restarting the board.

## Run locally

Requires a desktop environment and JDK 17 or newer. There are no external library dependencies.

### macOS or Linux

From the repository root:

```bash
./run.sh
```

The script compiles the Java source into `out/` and launches the game. You can optionally supply a different picture directory:

```bash
./run.sh /path/to/pictures
```

### IntelliJ IDEA

1. Open the repository and select a JDK (17 or newer).
2. Mark `src` as the Sources Root if IntelliJ does not detect it automatically.
3. Set the run configuration's working directory to the repository root.
4. Run `App.main`.

Create a local account from the login screen, then log in. The interface currently uses Chinese labels.

## Controls

| Key | Action |
| --- | --- |
| Arrow keys or W/A/S/D | Slide a neighboring tile into the empty space |
| P | Compute and animate an A* solution |
| R | Start a new puzzle |
| C | Change the picture theme |
| Esc | Return to the login screen |
| Enter | Immediately complete the board (demo shortcut) |

Menus also provide solving, restart, picture selection, and login controls.

## Project structure

```text
src/
  App.java              Application entry point
  ui/                   Login, registration, and game windows; A* solver
  domain/               User and score data
  tool/                 Local persistence and UI helper utilities
  test/                 Manual Swing experiments
pictures/               Tile images and interface artwork
run.sh                  Compile and run script
```

Accounts and scores are saved locally in `puzzlegame-users.ser`, which is excluded from Git. Account storage is a learning/demo feature without password hashing; use a throwaway password. Generated classes and IDE settings are also excluded.
