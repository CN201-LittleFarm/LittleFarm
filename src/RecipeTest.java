
import java.util.Map;

public class RecipeTest {

    // เก็บจำนวนกรณีทดสอบที่ผ่านทั้งหมด
    private static int passed = 0;

    // ตรวจสอบผลการทดสอบ ถ้าไม่ตรงตามที่คาดหวังให้แจ้งว่าทดสอบไม่ผ่าน
    private static void check(String testName, boolean condition) {
        if (!condition) {
            throw new RuntimeException("FAILED: " + testName);
        }

        passed++;
        System.out.println("PASSED: " + testName);
    }

    // สร้างกระเป๋าวัตถุดิบสำหรับใช้ในการทดสอบ
    // กำหนดจำนวน Strawberry, Potato และ Turnip ได้เอง
    private static Bag<String> createBag(
            int strawberry, int potato, int turnip) {

        Bag<String> bag = new Bag<>();

        bag.add("Strawberry", strawberry);
        bag.add("Potato", potato);
        bag.add("Turnip", turnip);

        return bag;
    }

    public static void main(String[] args) {

        System.out.println("=== เริ่มทดสอบระบบสูตรแปรรูป ===");

        ProcessingRecipe cake = new CakeRecipe();
        ProcessingRecipe chips = new ChipsRecipe();

        // =====================================
        // 1. ทดสอบข้อมูลสูตร Strawberry Cake
        // =====================================

        check("Cake: ชื่อสินค้า",
                "Strawberry Cake".equals(cake.getProductName()));

        check("Cake: หมวดหมู่สินค้า",
                "Bakery".equals(cake.getCategory()));

        check("Cake: ระยะเวลาผลิต",
                cake.getRequiredNights() == 2);

        check("Cake: ราคาขาย",
                cake.getSellPrice() == 250);

        check("Cake: จำนวนวัตถุดิบที่ต้องใช้",
                cake.getRequiredIngredients().equals(
                        Map.of(
                                "Strawberry", 3,
                                "Potato", 2,
                                "Turnip", 2)));

        // =====================================
        // 2. ทดสอบข้อมูลสูตร Potato Chips
        // =====================================

        check("Chips: ชื่อสินค้า",
                "Potato Chips".equals(chips.getProductName()));

        check("Chips: หมวดหมู่สินค้า",
                "Snacks".equals(chips.getCategory()));

        check("Chips: ระยะเวลาผลิต",
                chips.getRequiredNights() == 1);

        check("Chips: ราคาขาย",
                chips.getSellPrice() == 120);

        check("Chips: จำนวนวัตถุดิบที่ต้องใช้",
                chips.getRequiredIngredients().equals(
                        Map.of(
                                "Potato", 3,
                                "Turnip", 1)));

        // =====================================
        // 3. ทดสอบการตรวจสอบวัตถุดิบของ Cake
        // =====================================

        // กรณีส่งกระเป๋าเป็น null ต้องคืนค่า false
        check("Cake: กระเป๋าเป็น null ต้องคืนค่า false",
                !cake.hasEnoughIngredients(null));

        // กรณีกระเป๋าไม่มีวัตถุดิบเลย ต้องคืนค่า false
        check("Cake: กระเป๋าว่าง ต้องคืนค่า false",
                !cake.hasEnoughIngredients(new Bag<>()));

        // กรณีมี Strawberry ไม่ครบ แต่มีวัตถุดิบอื่นครบ
        check("Cake: Strawberry ไม่เพียงพอ",
                !cake.hasEnoughIngredients(
                        createBag(2, 2, 2)));

        // กรณีมี Potato ไม่ครบ แต่มีวัตถุดิบอื่นครบ
        check("Cake: Potato ไม่เพียงพอ",
                !cake.hasEnoughIngredients(
                        createBag(3, 1, 2)));

        // กรณีมี Turnip ไม่ครบ แต่มีวัตถุดิบอื่นครบ
        check("Cake: Turnip ไม่เพียงพอ",
                !cake.hasEnoughIngredients(
                        createBag(3, 2, 1)));

        // กรณีมีวัตถุดิบครบทุกชนิดตามสูตร
        check("Cake: วัตถุดิบครบตามสูตร",
                cake.hasEnoughIngredients(
                        createBag(3, 2, 2)));

        // กรณีมีวัตถุดิบมากกว่าที่สูตรต้องการ
        check("Cake: วัตถุดิบมากกว่าที่ต้องการ",
                cake.hasEnoughIngredients(
                        createBag(5, 4, 3)));

        // =====================================
        // 4. ทดสอบการตรวจสอบวัตถุดิบของ Chips
        // =====================================

        // กรณีมี Potato ไม่ครบ
        check("Chips: Potato ไม่เพียงพอ",
                !chips.hasEnoughIngredients(
                        createBag(0, 2, 1)));

        // กรณีมี Turnip ไม่ครบ
        check("Chips: Turnip ไม่เพียงพอ",
                !chips.hasEnoughIngredients(
                        createBag(0, 3, 0)));

        // กรณีมีวัตถุดิบครบทุกชนิดตามสูตร
        check("Chips: วัตถุดิบครบตามสูตร",
                chips.hasEnoughIngredients(
                        createBag(0, 3, 1)));

        // กรณีมีวัตถุดิบมากกว่าที่สูตรต้องการ
        check("Chips: วัตถุดิบมากกว่าที่ต้องการ",
                chips.hasEnoughIngredients(
                        createBag(0, 5, 3)));

        // =====================================
        // 5. ทดสอบการป้องกันการแก้ไขข้อมูลสูตร
        // =====================================

        boolean mapIsReadOnly = false;

        try {
            // ทดลองแก้ไขจำนวนวัตถุดิบในสูตรจากภายนอก
            cake.getRequiredIngredients().put("Potato", 999);
        } catch (UnsupportedOperationException e) {
            // ถ้าเกิด Exception แสดงว่า Map ไม่อนุญาตให้แก้ไข
            mapIsReadOnly = true;
        }

        check("Recipe: ป้องกันการแก้ไขข้อมูลวัตถุดิบจากภายนอก",
                mapIsReadOnly);

        // =====================================
        // สรุปผลการทดสอบ
        // =====================================

        System.out.println("--------------------------------");
        System.out.println("ผ่านการทดสอบทั้งหมด: " + passed + " กรณี");
        System.out.println("=== สิ้นสุดการทดสอบระบบสูตรแปรรูป ===");
    }
}
