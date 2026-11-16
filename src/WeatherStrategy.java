
import java.util.Random;

/**
 * Interface สำหรับกำหนดรูปแบบการสุ่มสภาพอากาศ
 * Strategy ทุกแบบต้องมีเมธอด forecast()
 */
public interface WeatherStrategy {

    // รับ Random สำหรับสุ่ม และคืนค่าสภาพอากาศที่สุ่มได้
    Weather forecast(Random rng);
}
