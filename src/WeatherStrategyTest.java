
import java.util.Random;

public class WeatherStrategyTest {

    // กำหนดผลการสุ่มเอง เพื่อให้ทดสอบได้แน่นอน
    static class FixedRandom extends Random {
        private final int fixedRoll;

        FixedRandom(int fixedRoll) {
            this.fixedRoll = fixedRoll;
        }

        @Override
        public int nextInt(int bound) {
            if (bound != 100) {
                throw new IllegalArgumentException("Expected bound 100");
            }
            return fixedRoll;
        }
    }

    private static int passed = 0;

    private static void check(String testName, Weather expected, Weather actual) {
        if (expected != actual) {
            throw new RuntimeException(
                    "FAILED: " + testName
                            + " | Expected: " + expected
                            + " | Actual: " + actual);
        }

        passed++;
        System.out.println("PASSED: " + testName);
    }

    private static void checkTrue(String testName, boolean condition) {
        if (!condition) {
            throw new RuntimeException("FAILED: " + testName);
        }

        passed++;
        System.out.println("PASSED: " + testName);
    }

    public static void main(String[] args) {

        System.out.println("=== Starting Weather Strategy Test ===");

        // 1. NormalWeatherStrategy
        WeatherStrategy normal = new NormalWeatherStrategy();

        check("Normal: roll 0 -> SUNNY",
                Weather.SUNNY, normal.forecast(new FixedRandom(0)));

        check("Normal: roll 74 -> SUNNY",
                Weather.SUNNY, normal.forecast(new FixedRandom(74)));

        check("Normal: roll 75 -> RAINY",
                Weather.RAINY, normal.forecast(new FixedRandom(75)));

        check("Normal: roll 99 -> RAINY",
                Weather.RAINY, normal.forecast(new FixedRandom(99)));

        // 2. RainyWeatherStrategy
        WeatherStrategy rainy = new RainyWeatherStrategy();

        check("Rainy: roll 0 -> SUNNY",
                Weather.SUNNY, rainy.forecast(new FixedRandom(0)));

        check("Rainy: roll 39 -> SUNNY",
                Weather.SUNNY, rainy.forecast(new FixedRandom(39)));

        check("Rainy: roll 40 -> RAINY",
                Weather.RAINY, rainy.forecast(new FixedRandom(40)));

        check("Rainy: roll 99 -> RAINY",
                Weather.RAINY, rainy.forecast(new FixedRandom(99)));

        // 3. WeatherStrategyFactory
        checkTrue("Factory: normal mode",
                WeatherStrategyFactory.create("normal") instanceof NormalWeatherStrategy);

        checkTrue("Factory: rainy mode",
                WeatherStrategyFactory.create("rainy") instanceof RainyWeatherStrategy);

        checkTrue("Factory: null mode defaults to normal",
                WeatherStrategyFactory.create(null) instanceof NormalWeatherStrategy);

        checkTrue("Factory: blank mode defaults to normal",
                WeatherStrategyFactory.create("   ") instanceof NormalWeatherStrategy);

        checkTrue("Factory: uppercase mode works",
                WeatherStrategyFactory.create("NORMAL") instanceof NormalWeatherStrategy);

        // 4. Unknown mode
        boolean exceptionThrown = false;

        try {
            WeatherStrategyFactory.create("storm");
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        checkTrue("Factory: unknown mode throws exception", exceptionThrown);

        // 5. Original Weather.forecast()
        check("Weather.forecast: roll 74 -> SUNNY",
                Weather.SUNNY, Weather.forecast(new FixedRandom(74)));

        check("Weather.forecast: roll 75 -> RAINY",
                Weather.RAINY, Weather.forecast(new FixedRandom(75)));

        System.out.println("--------------------------------");
        System.out.println("PASSED: " + passed + " tests");
        System.out.println("=== Weather Strategy Test Completed ===");
    }
}
