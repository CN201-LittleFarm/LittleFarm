
import java.util.Random;

/**
 * กลยุทธ์การสุ่มอากาศแบบฝนตกบ่อย
 * Sunny มีโอกาส 40% และ Rainy มีโอกาส 60%
 */
public class RainyWeatherStrategy implements WeatherStrategy {

    @Override
    public Weather forecast(Random rng) {

        // สุ่มตัวเลขตั้งแต่ 0 ถึง 99
        int roll = rng.nextInt(100);

        // ตัวเลข 0-39 คือ Sunny 
        if (roll < 40) {
            return Weather.SUNNY;
        }

        // ตัวเลข 40-99 คือ Rainy 
        return Weather.RAINY;
    }
}
