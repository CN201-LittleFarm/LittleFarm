import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests SaveGame on its own - no game window, no other member's code.
 * Run from the LittleFarm folder (where data/ is):
 *     javac -d out src/*.java
 *     java -cp out SaveGameTest
 * Every check prints PASS or FAIL; the program ends with exit code 1 if anything failed.
 */
public class SaveGameTest {
    private static int passed = 0, failed = 0;

    /** Stands in for the real machine, so this test needs nothing from ProcessingMachine. */
    static class MockMachine implements SavableMachine {
        private static final List<String> STATES = Arrays.asList("IDLE", "PROCESSING", "READY");
        private static final List<String> RECIPES = Arrays.asList("Strawberry Cake", "Potato Chips");
        String state = "IDLE", recipe = "-";
        int nights = 0;

        public String getSavedState() { return state; }
        public String getSavedRecipeName() { return recipe; }
        public int getSavedNights() { return nights; }

        public void restore(String newState, String recipeName, int newNights) throws FarmException {
            if (!STATES.contains(newState)) throw new IllegalArgumentException("unknown machine state '" + newState + "'");
            if (!newState.equals("IDLE") && !RECIPES.contains(recipeName)) throw new FarmException("unknown recipe '" + recipeName + "'");
            state = newState;
            recipe = recipeName;
            nights = newNights;
        }
    }

    /** A new farm, farmer and clock, ready to be filled by a load. */
    static class Game {
        Farm farm = new Farm();
        Farmer farmer = new Farmer(2, 4, 300);
        GameClock clock = new GameClock();
        MockMachine machine = new MockMachine();
    }

    public static void main(String[] args) throws Exception {
        Path temp = Files.createTempDirectory("littlefarm-test");

        System.out.println("== the sample file ==");
        Game g = new Game();
        new SaveGame("data/sample_save.txt").loadInto(g.farm, g.farmer, g.clock, g.machine);
        check("day 12, RAINY, 840 gold", g.clock.getDay() == 12 && g.clock.getWeather() == Weather.RAINY && g.farmer.getGold() == 840);
        check("seeds and basket (including 'Strawberry Cake' with a space)",
                g.farmer.getSeedBag("Potato").getSeeds() == 2 && g.farmer.getBasket().count("Strawberry Cake") == 1);
        check("strawberry in the ground, 3 days grown", g.farm.tileAt(6, 6).getCrop().getDaysGrown() == 3);
        check("machine is PROCESSING Strawberry Cake with 2 nights left",
                g.machine.state.equals("PROCESSING") && g.machine.recipe.equals("Strawberry Cake") && g.machine.nights == 2);

        System.out.println("== save, then load, gives the same game ==");
        Game a = new Game();
        a.clock.setDay(7);
        a.farmer.setGold(555);
        a.farmer.getBasket().add("Potato Chips", 2);
        a.machine.restore("READY", "Potato Chips", 0);
        Path first = temp.resolve("first.txt"), second = temp.resolve("second.txt");
        new SaveGame(first.toString()).save(a.farm, a.farmer, a.clock, a.machine);
        Game b = new Game();
        new SaveGame(first.toString()).loadInto(b.farm, b.farmer, b.clock, b.machine);
        new SaveGame(second.toString()).save(b.farm, b.farmer, b.clock, b.machine);
        check("saving the loaded game gives an identical file", Arrays.equals(Files.readAllBytes(first), Files.readAllBytes(second)));
        check("the title menu can describe the new save format",
                "Day 7, 555G".equals(new SaveGame(first.toString()).describe()));
        checkDeletedEntry(first, temp.resolve("without-soil.txt"), "soil");
        checkBlankDeletedEntry(first, temp.resolve("blank-soil.txt"), "soil");
        checkDeletedEntry(first, temp.resolve("without-villager.txt"), "villager");
        checkDeletedEntry(first, temp.resolve("without-last-entry.txt"), "machine");
        checkDeletedManifest(first, temp.resolve("without-manifest.txt"));
        checkVersion2Compatibility(first, temp.resolve("version-2.txt"));
        check("no .tmp file is left behind", !Files.exists(temp.resolve("first.txt.tmp")));

        System.out.println("== damaged files (data/save_cases/index.txt) ==");
        for (String entry : Files.readAllLines(Paths.get("data/save_cases/index.txt"))) {
            if (entry.startsWith("#") || entry.isBlank()) continue;
            String[] f = entry.split("\\|");
            checkCase(f[0], Integer.parseInt(f[1]), f[2]);
        }

        System.out.println("== other ==");
        try {
            new SaveGame(temp.resolve("nothing-here.txt").toString()).loadInto(new Farm(), new Farmer(2, 4, 300), new GameClock());
            check("a missing file is refused", false);
        } catch (UnreadableSaveException e) {
            check("a missing file -> UnreadableSaveException (cause: " + e.getCause().getClass().getSimpleName() + ")", true);
        }
        try {
            Game c = new Game();
            new SaveGame("data/save_cases/bad_02_gold_not_a_number.txt").loadInto(c.farm, c.farmer, c.clock);
        } catch (CorruptSaveException e) {
            check("the original NumberFormatException is kept as the cause", e.getCause() instanceof NumberFormatException);
            check("both exceptions are FarmExceptions", e instanceof FarmException && new UnreadableSaveException("x", null) instanceof FarmException);
        }

        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }

    /** Loads one file from save_cases and checks that the right exception, naming the right line, comes out. */
    private static void checkCase(String file, int line, String expected) {
        Game g = new Game();
        try {
            new SaveGame("data/save_cases/" + file).loadInto(g.farm, g.farmer, g.clock, g.machine);
            check(file + " -> loads fine", expected.equals("OK"));
        } catch (CorruptSaveException e) {
            check(file + " -> " + e.getMessage(), expected.equals("CorruptSaveException") && e.getLineNumber() == line);
        } catch (UnreadableSaveException e) {
            check(file + " -> " + e.getMessage(), expected.equals("UnreadableSaveException"));
        } catch (Exception e) {
            check(file + " -> CRASHED with " + e, false);   // anything else escaping is exactly what must never happen
        }
    }

    private static void checkDeletedEntry(Path save, Path damagedSave, String entryName) throws Exception {
        List<String> lines = new ArrayList<>(Files.readAllLines(save));
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(" ", 3);
            if (parts.length != 3 || !parts[0].equals("entry") || !parts[2].startsWith(entryName + " ")) continue;

            int expectedLine = Integer.parseInt(parts[1]) + 2;
            String expectedDescription = entryName;
            if (entryName.equals("soil")) {
                String[] soil = parts[2].split(" ");
                expectedDescription = "soil (" + soil[1] + "," + soil[2] + ")";
            }
            lines.remove(i);
            Files.write(damagedSave, lines, java.nio.charset.StandardCharsets.UTF_8);
            Game g = new Game();
            try {
                new SaveGame(damagedSave.toString()).loadInto(g.farm, g.farmer, g.clock, g.machine);
                check("deleting " + entryName + " is rejected", false);
            } catch (CorruptSaveException e) {
                check("deleting " + entryName + " reports its type and missing line",
                        e.getLineNumber() == expectedLine && e.getMessage().contains(expectedDescription));
            }
            return;
        }
        check("test save contains a " + entryName + " entry", false);
    }

    private static void checkDeletedManifest(Path save, Path damagedSave) throws Exception {
        List<String> lines = new ArrayList<>(Files.readAllLines(save));
        lines.remove(1);
        Files.write(damagedSave, lines, java.nio.charset.StandardCharsets.UTF_8);
        Game g = new Game();
        try {
            new SaveGame(damagedSave.toString()).loadInto(g.farm, g.farmer, g.clock, g.machine);
            check("deleting the manifest is rejected", false);
        } catch (CorruptSaveException e) {
            check("deleting the manifest reports line 2 and its name",
                    e.getLineNumber() == 2 && e.getMessage().contains("manifest"));
        }
    }

    private static void checkBlankDeletedEntry(Path save, Path damagedSave, String entryName) throws Exception {
        List<String> lines = new ArrayList<>(Files.readAllLines(save));
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(" ", 3);
            if (parts.length != 3 || !parts[0].equals("entry") || !parts[2].startsWith(entryName + " ")) continue;

            int expectedLine = Integer.parseInt(parts[1]) + 2;
            String[] payload = parts[2].split(" ");
            String expectedDescription = entryName.equals("soil")
                    ? "soil (" + payload[1] + "," + payload[2] + ")" : entryName;
            lines.set(i, "");
            Files.write(damagedSave, lines, java.nio.charset.StandardCharsets.UTF_8);
            Game g = new Game();
            try {
                new SaveGame(damagedSave.toString()).loadInto(g.farm, g.farmer, g.clock, g.machine);
                check("blanking " + entryName + " is rejected", false);
            } catch (CorruptSaveException e) {
                check("blanking " + entryName + " reports its type and line",
                        e.getLineNumber() == expectedLine && e.getMessage().contains(expectedDescription));
            }
            return;
        }
        check("test save contains a " + entryName + " entry to blank", false);
    }

    private static void checkVersion2Compatibility(Path save, Path version2Save) throws Exception {
        List<String> lines = new ArrayList<>(Files.readAllLines(save));
        String[] header = lines.get(0).split(" ");
        lines.set(0, "little-farm save 2 records " + header[4]);
        lines.remove(1);
        Files.write(version2Save, lines, java.nio.charset.StandardCharsets.UTF_8);
        Game g = new Game();
        new SaveGame(version2Save.toString()).loadInto(g.farm, g.farmer, g.clock, g.machine);
        check("version 2 saves remain loadable",
                g.clock.getDay() == 7 && g.farmer.getGold() == 555
                        && g.farmer.getBasket().count("Potato Chips") == 2);
    }

    private static void check(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "PASS  " : "FAIL  ") + name);
    }
}
