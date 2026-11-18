public class RecipeTest {
    public static void main(String[] args) {
        System.out.println("=== Starting Recipe Test ===");

        ProcessingRecipe cake = new CakeRecipe();
        ProcessingRecipe chips = new ChipsRecipe();

        //1. Test data for Cake and Chips
        if (!"Strawberry Cake".equals(cake.getProductName())) {
            throw new RuntimeException("FAILED: ข้อมูลสูตร Strawberry Cake ไม่ถูกต้อง");
        }
        if(!"Bakery".equals(cake.getCategory())) {
            throw new RuntimeException("FAILED: หมวดหมู่สูตร Strawberry Cake ไม่ถูกต้อง");
        }
        System.out.println("PASSED: 1. ข้อมูลสูตร Cake และ Chips ถูกต้อง");

        //2. ทดสอบกรณีวัตถุดิบไม่ครบตามจำนวน
        Bag<String> bag = new Bag<>();
        bag.add("Strawberry", 3);
        bag.add("Potato", 1);
        if (cake.hasEnoughIngredients(bag)) {
            throw new RuntimeException("FAILED: วัตถุดิบไม่ครบ ต้องคืนค่า false");
        }
        System.out.println("PASSED: 2. วัตถุดิบไม่ครบ -> คืนค่า false");

        //3. ทดสอบกรณีวัตถุดิบครบตามจำนวน
        bag.add("Potato", 1); //now, has 2 Potatoes
        bag.add("Turnip", 2);
        if (!cake.hasEnoughIngredients(bag)) {
            throw new RuntimeException("FAILED: วัตถุดิบครบ ต้องคืนค่า true");
        }
        System.out.println("PASSED: 3. วัตถุดิบครบ -> คืนค่า true");

        //========= Cake ==========
        // ทดสอบสูตร Potato Chips (ใช้ Potato 3, Turnip 1)
        Bag<String> chipsBag = new Bag<>();
        chipsBag.add("Potato", 2); // ขาด Potato 1 และ Turnip 1
        if (chips.hasEnoughIngredients(chipsBag)) {
            throw new RuntimeException("FAILED: วัตถุดิบ Chips ไม่ครบ ต้องคืนค่า false");
        }
        chipsBag.add("Potato", 1);
        chipsBag.add("Turnip", 1);
        if (!chips.hasEnoughIngredients(chipsBag)) {
            throw new RuntimeException("FAILED: วัตถุดิบ Chips ครบ ต้องคืนค่า true");
        }
        System.out.println("PASSED: 4. ตรวจสอบวัตถุดิบสูตร Potato Chips ถูกต้อง");

        System.out.println("=== ผ่านการทดสอบ RecipeTest ครบทุกข้อ! ===");
    }

    
}
