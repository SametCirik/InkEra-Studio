<h1 align="center">
  InkEra Studio
</h1>

<div align="center">
  <p><strong>An open-source, professional-grade manga and comic creation studio tailored for Linux.</strong></p>
  
  <a href="https://www.gnu.org/licenses/gpl-3.0"><img src="https://img.shields.io/badge/License-GPLv3-blue.svg" alt="License: GPL v3"></a>
  <a href="https://openjfx.io/"><img src="https://img.shields.io/badge/JavaFX-21-orange.svg" alt="JavaFX"></a>
  <img src="https://img.shields.io/badge/Platform-Linux-green.svg" alt="Platform: Linux">
  <img src="https://img.shields.io/badge/Stage-Pre--Alpha-red.svg" alt="Stage: Pre--Alpha">
</div>

---

## What is InkEra Studio?

InkEra Studio is an ambitious open-source desktop application designed specifically for manga artists, comic creators, and storytellers. Built with **JavaFX 21**, it aims to bridge the gap between complex drawing software and project management tools. 

Instead of dealing with scattered image files, InkEra provides a **Print Simulation Engine** and a structured workspace. It manages your chapters, page spreads (Left/Right offset), metadata, and reading directions seamlessly, allowing you to focus entirely on your art and story.

## Screenshots

|                      Home Dashboard & Gallery                      |                    Chapter & Print Simulation Management                     |
| :----------------------------------------------------------------: | :--------------------------------------------------------------------------: |
| *(High-quality screenshot of the Main Gallery will be added here)* | *(Screenshot of the Chapter Detail / Page Spread Engine will be added here)* |

|                     Native UI & Dark Theme                     |                  Workspace (Coming Soon)                  |
| :------------------------------------------------------------: | :-------------------------------------------------------: |
| *(Screenshot of Context Menus and Dialogs will be added here)* | *(GIF of the drawing engine / canvas will be added here)* |

## Features

### Currently Implemented (v0.1-alpha)
*   **Advanced Project Scaffolding:** Automatically creates organized folder hierarchies (`.inkera/`, `Episodes/`) for new series.
*   **Print Simulation Engine:** A dynamic pagination system that calculates Left/Right page spreads based on the reading direction (Manga RTL or Comic LTR) and offset.
*   **Payload/Registry Separation:** Heavy project data is stored locally in `.inkepisode/meta.json` files, keeping the main app registry lightning fast.
*   **Dynamic Tagging System:** Modern, space-separated token input system for genre and tag management.
*   **Native Linux UI Integration:** Undecorated custom title bars with KDE/KWin compatibility and a premium dark theme.

### Planned Roadmap
*   **[v0.5-beta] Core Drawing Engine:** Hardware-accelerated canvas, basic brush engine, eraser, and layer management.
*   **[v0.8-beta] Advanced Tools:** Selection tools, vector lines, text/balloon engine, and screen-tones.
*   **[v1.0-stable] Publishing Pipeline:** Export to CBZ, PDF, and standard Webtoon vertical-scroll formats. Cloud sync integration.
*   **[v2.0] Plugin System:** Support for community-driven scripts and custom brush packs.

## Installation & Build Guide (Linux)

InkEra Studio is currently in active development. To run it from the source code, you need **Java 21 (JDK)** and **Maven** installed on your system.

**1. Clone the repository:**

```bash
git clone https://github.com/SametCirik/InkEra-Studio.git
cd InkEra-Studio/
````

**2. Compile and Run:**

```bash
mvn clean javafx:run
```

_Note: Pre-compiled AppImage and binary files for Linux/Windows will be provided in the upcoming `v0.1-alpha` release._

## Contributing

Contributions, issues, and feature requests are always welcome! We are looking for JavaFX developers, UI/UX designers, and open-source enthusiasts to help build the ultimate open-source alternative to proprietary comic software.

Please check our CONTRIBUTING.md _(coming soon)_ for detailed guidelines.

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**. See the LICENSE file for more details.

