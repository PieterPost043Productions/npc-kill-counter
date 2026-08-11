# NPC Kill Counter (RuneLite plugin)

Houdt per NPC-type je kills bij. Elke keer dat je een NPC doodt (via `NpcLootReceived`,
dus ook bij een lege drop) telt de plugin een kill op voor die NPC-naam. In het side-paneel
zie je per soort het aantal kills, met een reset-knop per rij en een "Reset alles"-knop
bovenaan. Kills worden opgeslagen via RuneLite's ConfigManager, dus ze blijven bewaard
na herstarten.

## Bestanden

- `src/main/java/com/nkc/NpcKillCountPlugin.java` — telt kills en beheert opslag
- `src/main/java/com/nkc/NpcKillCountPanel.java` — het side-paneel (UI)
- `src/main/java/com/nkc/NpcKillCountConfig.java` — config-group definitie
- `src/main/resources/com/nkc/icon.png` — icoon voor de sidebar

## Bouwen

Vereist: JDK 11+ en Gradle (of gebruik de Gradle wrapper als je die toevoegt).

```
cd npc-kill-counter
gradle build
```

Dit levert `build/libs/npc-kill-counter-1.0.0.jar` op.

## Installeren (sideloaden in RuneLite)

RuneLite laadt eigen plugins niet standaard vanaf schijf — dat kan alleen via
"sideloaded plugins" in developer mode:

1. Zet RuneLite in developer mode: start de client met de vlag `--developer-mode`
   (of zet `developerMode=true` in de RuneLite-instellingen, afhankelijk van je
   launcher-versie).
2. Zorg dat de map `~/.runelite/sideloaded-plugins` bestaat
   (Windows: `%USERPROFILE%\.runelite\sideloaded-plugins`).
3. Kopieer de gebouwde jar (`npc-kill-counter-1.0.0.jar`) naar die map.
4. Start RuneLite opnieuw. De plugin "NPC Kill Counter" verschijnt in de pluginlijst
   en het icoon komt in de sidebar.

Alternatief: open het project in IntelliJ samen met de RuneLite-broncode
(`runelite-client` module) en run de client vanuit de IDE met dit project op
het classpath — handig tijdens ontwikkelen/debuggen.

Wil je de plugin permanent en voor iedereen beschikbaar maken via de officiële
Plugin Hub, dan moet je 'm open-sourcen en een pull request indienen bij
https://github.com/runelite/plugin-hub — dat vereist code review door het
RuneLite-team.

## Hoe het werkt

- `NpcLootReceived` vuurt zodra jij credit krijgt voor het doden van een NPC
  (ook als er niks droppt), dus geen dubbeltelling van kills die andere spelers
  maken.
- Kills worden per `NPC.getName()` bijgehouden in een `Map<String, Integer>`,
  gesorteerd op naam, en als JSON opgeslagen via `ConfigManager`.
- Reset-knop per rij verwijdert alleen die NPC uit de map; "Reset alles" leegt
  de hele map.

## Publiceren op de RuneLite Plugin Hub

1. Maak een **publieke** GitHub-repo (bv. `npc-kill-counter`) en push deze hele map ernaartoe:
   ```
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/<jouw-gebruikersnaam>/npc-kill-counter.git
   git push -u origin main
   ```
2. `runelite-plugin.properties` heeft nu `build=standard` — de Plugin Hub build vervangt dan zelf `build.gradle`/`settings.gradle`, dus geen dependency-gedoe.
3. Fork [runelite/plugin-hub](https://github.com/runelite/plugin-hub) op GitHub.
4. Maak in jouw fork een nieuw bestand `plugins/npc-kill-counter` met:
   ```
   repository=https://github.com/<jouw-gebruikersnaam>/npc-kill-counter.git
   commit=<volledige 40-tekens commit hash van je laatste commit>
   ```
   (de hash vind je op GitHub bij Commits → laatste commit aanklikken → hash rechtsboven kopiëren)
5. Commit en push dit bestand naar je fork, open daarna een pull request naar `runelite/plugin-hub`.
6. Wacht op de CI-check (✔️/❌ naast de build) en de review. Bij wijzigingsverzoeken: pas aan en push een nieuwe commit + werk de `commit=` hash bij in dezelfde PR.
7. Voor een update later: herhaal stap 4–6 met de nieuwe commit hash.

Reviewers checken vooral op veiligheid en of de plugin niet tegen [Jagex' regels voor third-party clients](https://secure.runescape.com/m=news/third-party-client-guidelines?oldschool=1) ingaat.
