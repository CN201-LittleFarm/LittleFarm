
import java.util.Locale;

/**
 * Factory สำหรับสร้าง WeatherStrategy
 * โดยเลือกจากชื่อโหมดที่ผู้เล่นส่งเข้ามาตอนเริ่มเกม
 */
public class WeatherStrategyFactory {

    public static WeatherStrategy create(String mode) {

        // ถ้าไม่ได้ระบุโหมด ให้ใช้โหมดปกติ
        if (mode == null || mode.isBlank()) {
            return new NormalWeatherStrategy();
        }

        // แปลงเป็นตัวพิมพ์เล็ก เพื่อให้ NORMAL และ Normal ใช้ได้เหมือนกัน
        String selectedMode = mode.toLowerCase(Locale.ROOT);

        // เลือก Strategy ตามคำสั่ง
        switch (selectedMode) {
            case "normal":
                return new NormalWeatherStrategy();

            case "rainy":
                return new RainyWeatherStrategy();

            default:
                throw new IllegalArgumentException(
                    "Unknown weather mode: " + mode
                    + ". Please use normal or rainy."
                );
        }
    }
}
