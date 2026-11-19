
public class ProcessingMachineTest {

    // นับจำนวนกรณีทดสอบที่ผ่านทั้งหมด
    private static int passed = 0;

    // ตรวจสอบเงื่อนไข ถ้าไม่เป็นไปตามที่คาดหวังให้แจ้งว่าทดสอบไม่ผ่าน
    private static void check(String testName, boolean condition) {
        if (!condition) {
            throw new AssertionError("FAILED: " + testName);
        }

        passed++;
        System.out.println("PASSED: " + testName);
    }

    public static void main(String[] args) throws Exception {

        System.out.println("=== เริ่มทดสอบระบบเครื่องแปรรูป ===");

        ProcessingMachine machine = new ProcessingMachine();

        // =====================================
        // 1. ทดสอบสถานะเริ่มต้นของเครื่อง
        // =====================================

        check("เครื่องเริ่มต้นต้องมีสถานะ IDLE",
                machine.getState() == MachineState.IDLE);

        check("จำนวนคืนที่เหลือต้องเริ่มต้นเป็น 0",
                machine.getRemainingNights() == 0);

        check("ยังไม่มีสูตรที่กำลังผลิต",
                machine.getCurrentRecipe() == null);

        // =====================================
        // 2. ทดสอบกรณีวัตถุดิบไม่เพียงพอ
        // =====================================

        ProcessingRecipe cake = new CakeRecipe();

        Bag<String> bag = new Bag<>();
        bag.add("Strawberry", 3);
        bag.add("Potato", 1);
        bag.add("Turnip", 2);

        boolean caughtInsufficient = false;

        try {
            machine.startProcessing(cake, bag);
        } catch (InsufficientIngredientException e) {
            caughtInsufficient = true;
        }

        check("วัตถุดิบไม่ครบต้องเกิด InsufficientIngredientException",
                caughtInsufficient);

        // ตรวจสอบว่าวัตถุดิบทุกชนิดยังอยู่ครบ
        check("Strawberry ต้องไม่ถูกหักออก",
                bag.count("Strawberry") == 3);

        check("Potato ต้องไม่ถูกหักออก",
                bag.count("Potato") == 1);

        check("Turnip ต้องไม่ถูกหักออก",
                bag.count("Turnip") == 2);

        check("เมื่อวัตถุดิบไม่ครบ เครื่องต้องยังเป็น IDLE",
                machine.getState() == MachineState.IDLE);

        // =====================================
        // 3. ทดสอบการเริ่มผลิตเมื่อวัตถุดิบครบ
        // =====================================

        // เติม Potato ให้ครบตามสูตร
        bag.add("Potato", 1);

        machine.startProcessing(cake, bag);

        check("เริ่มผลิตแล้วสถานะต้องเป็น PROCESSING",
                machine.getState() == MachineState.PROCESSING);

        check("จำนวนคืนที่ต้องรอต้องเป็น 2",
                machine.getRemainingNights() == 2);

        check("เครื่องต้องเก็บสูตร Cake ที่กำลังผลิต",
                machine.getCurrentRecipe() == cake);

        // ตรวจสอบว่าหักวัตถุดิบออกจากกระเป๋าแล้ว
        check("Strawberry ต้องถูกหักออก 3 ชิ้น",
                bag.count("Strawberry") == 0);

        check("Potato ต้องถูกหักออก 2 ชิ้น",
                bag.count("Potato") == 0);

        check("Turnip ต้องถูกหักออก 2 ชิ้น",
                bag.count("Turnip") == 0);

        // =====================================
        // 4. ทดสอบการเริ่มผลิตซ้ำขณะเครื่องกำลังทำงาน
        // =====================================

        boolean caughtBusyStart = false;

        try {
            machine.startProcessing(cake, bag);
        } catch (MachineBusyException e) {
            caughtBusyStart = true;
        }

        check("ห้ามเริ่มผลิตซ้ำขณะเครื่องกำลังทำงาน",
                caughtBusyStart);

        // =====================================
        // 5. ทดสอบการเก็บสินค้าก่อนผลิตเสร็จ
        // =====================================

        boolean caughtEarlyCollect = false;

        try {
            machine.collectProduct(bag);
        } catch (MachineBusyException e) {
            caughtEarlyCollect = true;
        }

        check("ห้ามเก็บสินค้าก่อนผลิตเสร็จ",
                caughtEarlyCollect);

        // =====================================
        // 6. ทดสอบการนับจำนวนคืน
        // =====================================

        // ส่งค่า 0 จำนวนคืนต้องไม่เปลี่ยน
        machine.passNights(0);

        check("ส่งจำนวนคืนเป็น 0 แล้วเวลาที่เหลือต้องไม่เปลี่ยน",
                machine.getRemainingNights() == 2);

        // ส่งค่าติดลบ จำนวนคืนต้องไม่เปลี่ยน
        machine.passNights(-1);

        check("ส่งจำนวนคืนติดลบแล้วเวลาที่เหลือต้องไม่เปลี่ยน",
                machine.getRemainingNights() == 2);

        // ผ่านไป 1 คืน ต้องเหลืออีก 1 คืน
        machine.passNights(1);

        check("ผ่านไป 1 คืน ต้องเหลือเวลาอีก 1 คืน",
                machine.getRemainingNights() == 1);

        check("เครื่องยังต้องมีสถานะ PROCESSING",
                machine.getState() == MachineState.PROCESSING);

        // ผ่านไป 3 คืน ซึ่งมากกว่าจำนวนคืนที่เหลือ
        machine.passNights(3);

        check("จำนวนคืนที่เหลือต้องไม่ติดลบ",
                machine.getRemainingNights() == 0);

        check("เมื่อผลิตเสร็จสถานะต้องเปลี่ยนเป็น READY",
                machine.getState() == MachineState.READY);

        // =====================================
        // 7. ทดสอบการเริ่มผลิตใหม่ขณะมีสินค้ารอเก็บ
        // =====================================

        boolean caughtReadyBusy = false;

        try {
            machine.startProcessing(cake, bag);
        } catch (MachineBusyException e) {
            caughtReadyBusy = true;
        }

        check("ห้ามเริ่มผลิตใหม่ขณะที่มีสินค้ารอเก็บ",
                caughtReadyBusy);

        // =====================================
        // 8. ทดสอบการเก็บสินค้าที่ผลิตเสร็จ
        // =====================================

        String collected = machine.collectProduct(bag);

        check("ต้องได้รับสินค้า Strawberry Cake",
                "Strawberry Cake".equals(collected));

        check("กระเป๋าต้องมี Strawberry Cake เพิ่ม 1 ชิ้น",
                bag.count("Strawberry Cake") == 1);

        check("หลังเก็บสินค้าแล้วเครื่องต้องกลับเป็น IDLE",
                machine.getState() == MachineState.IDLE);

        check("หลังเก็บสินค้าแล้วสูตรปัจจุบันต้องเป็น null",
                machine.getCurrentRecipe() == null);

        check("หลังเก็บสินค้าแล้วจำนวนคืนที่เหลือต้องเป็น 0",
                machine.getRemainingNights() == 0);

        // =====================================
        // 9. ทดสอบการเก็บสินค้าขณะเครื่องว่าง
        // =====================================

        boolean caughtIdleCollect = false;

        try {
            machine.collectProduct(bag);
        } catch (MachineBusyException e) {
            caughtIdleCollect = true;
        }

        check("ห้ามเก็บสินค้าเมื่อเครื่องอยู่ในสถานะ IDLE",
                caughtIdleCollect);

        // =====================================
        // 10. ทดสอบการใช้สูตร Potato Chips
        // =====================================

        ProcessingRecipe chips = new ChipsRecipe();

        Bag<String> chipsBag = new Bag<>();
        chipsBag.add("Potato", 3);
        chipsBag.add("Turnip", 1);

        machine.startProcessing(chips, chipsBag);

        check("เริ่มผลิต Chips แล้วสถานะต้องเป็น PROCESSING",
                machine.getState() == MachineState.PROCESSING);

        check("สูตร Chips ต้องใช้เวลาผลิต 1 คืน",
                machine.getRemainingNights() == 1);

        check("วัตถุดิบ Potato ต้องถูกหักออก",
                chipsBag.count("Potato") == 0);

        check("วัตถุดิบ Turnip ต้องถูกหักออก",
                chipsBag.count("Turnip") == 0);

        // ผ่านไป 1 คืน ผลิต Chips เสร็จ
        machine.passNights(1);

        check("ผลิต Chips ครบ 1 คืนแล้วต้องเป็น READY",
                machine.getState() == MachineState.READY);

        String collectedChips = machine.collectProduct(chipsBag);

        check("ต้องได้รับสินค้า Potato Chips",
                "Potato Chips".equals(collectedChips));

        check("กระเป๋าต้องมี Potato Chips 1 ชิ้น",
                chipsBag.count("Potato Chips") == 1);

        check("เก็บ Chips แล้วเครื่องต้องกลับเป็น IDLE",
                machine.getState() == MachineState.IDLE);

        // =====================================
        // 11. ทดสอบการนับคืนขณะเครื่องว่าง
        // =====================================

        machine.passNights(5);

        check("ผ่านไปหลายคืนขณะเครื่องว่าง สถานะต้องยังเป็น IDLE",
                machine.getState() == MachineState.IDLE);

        check("ผ่านไปหลายคืนขณะเครื่องว่าง จำนวนคืนต้องยังเป็น 0",
                machine.getRemainingNights() == 0);

        // =====================================
        // สรุปผลการทดสอบ
        // =====================================

        System.out.println("--------------------------------");
        System.out.println("ผ่านการทดสอบทั้งหมด: " + passed + " กรณี");
        System.out.println("=== สิ้นสุดการทดสอบระบบเครื่องแปรรูป ===");
    }
}
