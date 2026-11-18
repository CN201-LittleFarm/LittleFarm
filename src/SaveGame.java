import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.MalformedInputException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Saves the farm as plain text you can open and read, e.g.
 *   day 4
 *   weather RAINY
 *   gold 380
 *   seeds Turnip 2
 *   request Turnip 3 360 4     (crop amount reward daysLeft)
 *   soil 5 6 Turnip 1 wet      (col row crop daysGrown wet/dry; "-" for an empty plot)
 *   machine PROCESSING 2 Strawberry Cake   (state nightsLeft recipe name; see SavableMachine)
 *
 * A damaged file never crashes the game: loadInto refuses it with an exception that names the line.
 */
public class SaveGame {
    private static final String HEADER = "little-farm save 1";
    private final Path file;

    public SaveGame(String fileName) {
        this.file = Paths.get(fileName);
    }

    public boolean exists() {
        return Files.exists(file);
    }

    /** A one-line description for the title menu, e.g. "Day 5, 780G", or null if there is no save. */
    public String describe() {
        if (!exists()) return null;
        String day = "?", gold = "?";
        try {
            for (String line : Files.readAllLines(file)) {
                if (line.startsWith("day ")) day = line.substring(4);
                else if (line.startsWith("gold ")) gold = line.substring(5);
            }
        } catch (IOException e) {
            return "unreadable save";
        }
        return "Day " + day + ", " + gold + "G";
    }

    public void delete() throws IOException {
        Files.deleteIfExists(file);
    }

    // ---- saving ---------------------------------------------------------------

    public void save(Farm farm, Farmer farmer, GameClock clock) throws IOException {
        save(farm, farmer, clock, null);
    }

    /** @param machine the processing machine, or null while there is none */
    public void save(Farm farm, Farmer farmer, GameClock clock, SavableMachine machine) throws IOException {
        // write to a temporary file first: a crash half-way must not destroy the good save (Week 11)
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(temp, StandardCharsets.UTF_8))) {
            out.println(HEADER);
            out.println("day " + clock.getDay());
            out.println("weather " + clock.getWeather().name());
            out.println("gold " + farmer.getGold());
            out.println("earned " + farmer.getTotalEarned());
            out.println("can " + farmer.getCan().getWater());
            for (String name : CropFactory.NAMES) out.println("seeds " + name + " " + farmer.getSeedBag(name).getSeeds());
            for (Map.Entry<String, Integer> entry : farmer.getBasket().asMap().entrySet()) {
                out.println("basket " + entry.getKey() + " " + entry.getValue());
            }
            Villager villager = farm.getVillager();
            out.println("villager " + villager.getFriendship() + " " + villager.getRequestsDone());
            Request request = villager.getRequest();
            if (request != null) {
                out.println("request " + request.getCropName() + " " + request.getAmount() + " "
                        + request.getReward() + " " + request.getDaysLeft());
            }
            for (int r = 0; r < Farm.ROWS; r++) {
                for (int c = 0; c < Farm.COLS; c++) {
                    Tile tile = farm.tileAt(c, r);
                    if (tile.getType() != TileType.SOIL) continue;
                    Crop crop = tile.getCrop();
                    out.println("soil " + c + " " + r + " " + (crop == null ? "- 0" : crop.getName() + " " + crop.getDaysGrown())
                            + (tile.isWatered() ? " wet" : " dry"));
                }
            }
            if (machine != null) {
                out.println("machine " + machine.getSavedState() + " " + machine.getSavedNights() + " " + machine.getSavedRecipeName());
            }
        }
        Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE);   // the swap happens all at once, or not at all
    }

    // ---- loading --------------------------------------------------------------

    /** Fills a brand-new farm, farmer and clock from the file. */
    public void loadInto(Farm farm, Farmer farmer, GameClock clock)
            throws UnreadableSaveException, CorruptSaveException {
        loadInto(farm, farmer, clock, null);
    }

    /**
     * @param machine where to put the processing machine, or null to skip "machine" lines
     * @throws UnreadableSaveException the file is missing, empty, or not UTF-8 text
     * @throws CorruptSaveException    a line is wrong; says which one. The farm is then only half filled - throw it away.
     */
    public void loadInto(Farm farm, Farmer farmer, GameClock clock, SavableMachine machine)
            throws UnreadableSaveException, CorruptSaveException {
        List<String> lines = readLines();
        if (!lines.get(0).replace("﻿", "").trim().equals(HEADER)) {   // ﻿: the invisible mark some editors add
            throw new CorruptSaveException(1, lines.get(0), "this is not a Little Farm save", null);
        }

        Set<String> seen = new HashSet<>();   // which entries the file had, to notice a deleted line
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).trim().split(" ");
            try {
                switch (parts[0]) {
                    case "day":     clock.setDay(count(parts[1])); break;
                    case "weather": clock.setWeather(Weather.valueOf(parts[1])); break;
                    case "gold":    farmer.setGold(count(parts[1])); break;
                    case "earned":  farmer.setTotalEarned(count(parts[1])); break;
                    case "villager":
                        farm.getVillager().restore(count(parts[1]), count(parts[2]));
                        break;
                    case "request":
                        farm.getVillager().setRequest(new Request(parts[1], Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                        break;
                    case "can":    farmer.getCan().setWater(count(parts[1])); break;
                    case "seeds":
                        farmer.getSeedBag(parts[1]).setSeeds(count(parts[2]));
                        seen.add("seeds " + parts[1]);
                        break;
                    case "basket": loadBasket(farmer, lines.get(i)); break;
                    case "soil":   loadSoil(farm, parts); break;
                    case "machine": loadMachine(machine, lines.get(i)); break;
                    case "":       break;   // blank line
                    default:       throw new IllegalArgumentException("unknown entry '" + parts[0] + "'");
                }
                seen.add(parts[0]);
            } catch (NumberFormatException e) {            // specific first, then the general ones (Week 10)
                throw new CorruptSaveException(i + 1, lines.get(i), "a whole number was expected", e);
            } catch (IndexOutOfBoundsException e) {
                throw new CorruptSaveException(i + 1, lines.get(i), "a value is missing", e);
            } catch (IllegalArgumentException | FarmException e) {
                throw new CorruptSaveException(i + 1, lines.get(i), e.getMessage(), e);
            }
        }
        checkNothingIsMissing(seen);
    }

    /** Reads the file as UTF-8 and turns the ways that can fail into one exception of our own (Week 10: translate, keep the cause). */
    private List<String> readLines() throws UnreadableSaveException {
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new UnreadableSaveException("there is no save file", e);
        } catch (MalformedInputException e) {
            throw new UnreadableSaveException("the save file is not UTF-8 text", e);
        } catch (IOException e) {
            throw new UnreadableSaveException("the save file could not be read", e);
        }
        if (lines.isEmpty()) throw new UnreadableSaveException("the save file is empty", null);
        return lines;
    }

    /** A whole number that is not negative. A non-number throws NumberFormatException by itself. */
    private static int count(String text) {
        int number = Integer.parseInt(text);
        if (number < 0) throw new IllegalArgumentException("must not be negative");
        return number;
    }

    private void loadSoil(Farm farm, String[] parts) throws FarmException {
        Tile tile = farm.tileAt(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        if (tile == null) throw new FarmException("soil is off the map");
        String wetness = parts[5];
        if (!wetness.equals("wet") && !wetness.equals("dry")) throw new FarmException("expected wet or dry, not '" + wetness + "'");
        if (tile.getType() != TileType.SOIL) tile.till();
        if (wetness.equals("wet")) tile.soak();
        if (parts[3].equals("-")) return;
        Crop crop = CropFactory.create(parts[3]);
        crop.setDaysGrown(count(parts[4]));
        tile.plant(crop);
    }

    /** basket name amount - the amount is the LAST word, because a product such as "Strawberry Cake" has a space in its name. */
    private void loadBasket(Farmer farmer, String line) {
        String rest = line.trim().substring("basket".length()).trim();
        int lastSpace = rest.lastIndexOf(' ');
        farmer.getBasket().add(rest.substring(0, lastSpace), count(rest.substring(lastSpace + 1)));
    }

    /** machine STATE nights recipe-name - the name is everything after the third space, because it may contain spaces. */
    private void loadMachine(SavableMachine machine, String line) throws FarmException {
        String[] f = line.trim().split(" ", 4);
        int nights = count(f[2]);
        String recipeName = f[3];
        if (machine != null) machine.restore(f[1], recipeName, nights);
    }

    /** Deleting a line leaves nothing to point at, so say which entry is gone instead. */
    private void checkNothingIsMissing(Set<String> seen) throws CorruptSaveException {
        String[] required = {"day", "weather", "gold", "earned", "can", "villager"};
        for (String entry : required) {
            if (!seen.contains(entry)) throw missing(entry);
        }
        for (String crop : CropFactory.NAMES) {
            if (!seen.contains("seeds " + crop)) throw missing("seeds " + crop);
        }
    }

    private static CorruptSaveException missing(String entry) {
        return new CorruptSaveException(0, "", "missing entry '" + entry + "' (line deleted?)", null);
    }
}
