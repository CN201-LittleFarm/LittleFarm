import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        // อ่านชื่อโหมดจากคำสั่งตอนเปิดเกม
        // ถ้าไม่ได้ระบุ ให้ใช้โหมด normal
        String mode = args.length > 0 ? args[0] : "normal";

        // ให้ Factory เลือก Strategy ตามโหมดที่ได้รับ
        WeatherStrategy weatherStrategy;

        try {
            weatherStrategy = WeatherStrategyFactory.create(mode);
        } catch (IllegalArgumentException e) {
            // ถ้าผู้เล่นใส่ชื่อโหมดที่ไม่มีอยู่ ให้แจ้งวิธีใช้
            System.out.println(e.getMessage());
            return;
        }

        // Swing ต้องสร้างหน้าต่างบน Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {

            // ส่ง Strategy ที่เลือกไปให้ GamePanel
            GamePanel panel = new GamePanel(weatherStrategy);

            JFrame frame = new JFrame("Little Farm - CN201");
            frame.add(panel);
            frame.pack();
            frame.setResizable(true);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            panel.requestFocusInWindow();
            panel.start();
        });
    }
}
