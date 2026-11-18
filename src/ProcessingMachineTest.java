import java.util.HashMap;
import java.util.Map;

public class ProcessingMachineTest {
    //******ตัวเช็ค*******

        private static void check(boolean condition, String testName) {
            if (!condition) {
                throw new AssertionError("FAILED: " + testName);
            }
            System.out.println("PASSED: " + testName);
        }
    //******ตัวเช็ค*******


    static class MockCakeRecipe implements ProcessingRecipe {
        @Override
        public String getCategory() {
            return "Bakery";
        }

        @Override
        public String getProductName() {
            return "Strawberry Cake";
        }

        @Override
        public Map<String, Integer> getRequiredIngredients() {
            Map<String, Integer> req = new HashMap<>();
            req.put("Strawberry", 3);
            req.put("Potato", 2);
            return req;
        }

        @Override
        public int getRequiredNights() {
            return 2;
        }

        @Override
        public int getSellPrice() {
            return 250;
        }

        @Override public boolean hasEnoughIngredients(Bag<String> bag) { 
            return true;
        }
    }
    
    // Main method to run the tests
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Start ProcessingMachine Test ===");
        
        //Case1 สถานะเริ่มต้น
        ProcessingMachine machine = new ProcessingMachine();
        check(machine.getState() == MachineState.IDLE, "1. สถานะเริ่มต้นต้องเป็น IDLE");
        check(machine.getRemainingNights() == 0, "1.1 จำนวนคืนที่เหลือต้องเป็น 0");
        
        //Prepare a bag with ingredients
        Bag<String> bag = new Bag<>();
        bag.add("Strawberry", 3);
        bag.add("Potato", 1);
        ProcessingRecipe cake = new MockCakeRecipe();

        //Case2 วัตถุดิบไม่ครบ -> ต้อง Error และห้ามหัก Strawberry ออก
        boolean caughtInsufficient = false;
        try {
            machine.startProcessing(cake, bag);
        } catch (InsufficientIngredientException e) {
            caughtInsufficient = true;
        }
        check(caughtInsufficient, "2. วัตถุดิบไม่เพียงพอ: ต้องโยน InsufficientIngredientException");
        check(bag.count("Strawberry") == 3, "2.1 วัตถุดิบ Strawberry ต้องยังคงอยู่ 3");
        check(machine.getState() == MachineState.IDLE, "2.2 สถานะต้องยังคงเป็น IDLE");

        //Case3 เติม Potato ให้ครบ แล้วเริ่มผลิต
        bag.add("Potato", 1);
        machine.startProcessing(cake, bag);
        check(machine.getState() == MachineState.PROCESSING, "3. สถานะต้องเปลี่ยนเป็น PROCESSING");
        check(machine.getRemainingNights() == 2, "3.1 จำนวนคืนที่เหลือต้องเป็น 2");

        //Case4 กดผลิตซ้ำตอนกำลังทำ -> ต้องโยน MachineBusyException
        boolean caughtBusyStart = false;
        try {
            machine.startProcessing(cake, bag);
        }
        catch (MachineBusyException e) {
        caughtBusyStart = true;
        }
        check(caughtBusyStart, "4. กดผลิตซ้ำตอนกำลังทำ: ต้องโยน MachineBusyException ห้ามผลิตซํ้า!");

        //Case5 กดเก็บก่อนผลิตเสร็จ -> ต้องโยน MachineBusyException
        boolean caughtEarlyCollect = false;
        try {
            machine.collectProduct(bag);
        }
        catch (MachineBusyException e) {
            caughtEarlyCollect = true;
         }
        check(caughtEarlyCollect, "5. ยังไม่ครบคืน ห้ามเก็บสินค้าก่อนกำหนด");
        
        //Case6 นอนหลับ 1 คืน -> เหลือ 1 คืน
        machine.passNights(1);
        check(machine.getRemainingNights() == 1, "6. หลังจากนอนหลับ 1 คืน เหลือคืนที่ต้องรออีก 1 คืน");
        check(machine.getState() == MachineState.PROCESSING, "6.1 สถานะยังคงเป็น PROCESSING");
    
        //Case7 นอนข้ามหลายคืน (ลองส่ง 3 คืน) -> ต้องปรับเป็น 0 และเปลี่ยนเป็น READY
        machine.passNights(3);
        check(machine.getRemainingNights() == 0, "7. นอนครบกำหนด คืนที่เหลือต้องเป็น 0 (ไม่ติดลบ)");
        check(machine.getState() == MachineState.READY, "7.1 สถานะต้องเปลี่ยนเป็น READY");
        
        //Case8 ตอน READY (ยังไม่เก็บ) ห้ามเริ่มผลิตชุดใหม่
        boolean caughtReadyBusy = false;
        try {
            machine.startProcessing(cake, bag);
        } catch (MachineBusyException e) {
            caughtReadyBusy = true;
        }
        check(caughtReadyBusy, "สินค้าค้างรอเก็บ ห้ามเริ่มผลิตชุดใหม่");

        //Case9 เก็บสินค้า -> ต้องได้ Strawberry Cake 1 ชิ้น และเปลี่ยนเป็น IDLE
        String collected = machine.collectProduct(bag);
        check("Strawberry Cake".equals(collected), "9. เก็บสินค้าได้ Strawberry Cake");
        check(bag.count("Strawberry Cake") == 1, "9.1 ในกระเป๋าต้องมี Strawberry Cake 1 ชิ้น");
        check(machine.getState() == MachineState.IDLE, "9.2 หลังเก็บสินค้าแล้ว สถานะต้องเปลี่ยนเป็น IDLE");
    
        System.out.println("=== ผ่านการทดสอบครบทุกข้อ! ===");
        
    }
}    