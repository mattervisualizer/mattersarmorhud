# matter's armor hud (1.12.2, Forge)

Minimalist vanilla-like armor widget. Inspired by uku's Armor HUD.
Renders equipped armor + durability near the hotbar via a Forge
overlay event. Structure mirrors the Clipper project so Gradle
reuses the existing cache and downloads nothing new.

## Build
Use the SAME JDK 8 you used for Clipper, and do NOT use
--refresh-dependencies (that forces re-downloads).

    $env:JAVA_HOME = "C:\Users\mattervisualizer\.jdks\liberica-full-1.8.0_482"
    .\gradlew build

Output: build\libs\mattersarmorhud-1.0.jar  ->  drop into mods.

## Tweak
src\main\java\ru\matter\visualizer\armorhud\ArmorHud.java
constants at top: SLOT_SIZE, SLOT_GAP, OFFSET_X, OFFSET_Y.
