
import java.util.Random;

/**
 * กลยุทธ์การสุ่มอากาศแบบปกติ
 * Sunny มีโอกาส 75% และ Rainy มีโอกาส 25%
 */
public class NormalWeatherStrategy implements WeatherStrategy {

    @Override
    public Weather forecast(Random rng) {

        // สุ่มตัวเลขตั้งแต่ 0 ถึง 99
        int roll = rng.nextInt(100);

        // ตัวเลข 0-74 คือ Sunny 
        if (roll < 75) {
            return Weather.SUNNY;
        }

        // ตัวเลข 75-99 คือ Rainy 
        return Weather.RAINY;
    }
}
