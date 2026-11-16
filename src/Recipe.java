import java.util.HashMap;
import java.util.Map;
 
// ==========================================
// 1. คลาสจัดการวัตถุดิบ (Ingredient)
// ==========================================
class Ingredient {
    private String name;
    private int quantity;

    public Ingredient(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public void addQuantity(int amount) { this.quantity += amount; }

    public boolean consume(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
            return true;
        }
        return false;
    }
}

// ==========================================
// 2. Interface สำหรับสูตรแปรรูป (Recipe Interface)
// ==========================================
interface ProcessingRecipe {
    String getRecipeName();
    String getCategory();
    Map<String, Integer> getRequiredIngredients();
    int getProductionTimeSeconds();
    int getSellingPrice();
}

// ==========================================
// 3. คลาสแม่แบบสำหรับสูตรอาหาร (Abstract Class)
// ==========================================
abstract class BaseRecipe implements ProcessingRecipe {
    protected String recipeName;
    protected String category;
    protected Map<String, Integer> requiredIngredients;
    protected int productionTimeSeconds;
    protected int sellingPrice;

    public BaseRecipe(String recipeName, String category, int productionTimeSeconds, int sellingPrice) {
        this.recipeName = recipeName;
        this.category = category;
        this.productionTimeSeconds = productionTimeSeconds;
        this.sellingPrice = sellingPrice;
        this.requiredIngredients = new HashMap<>();
    }

    public void addRequirement(String ingredientName, int amount) {
        requiredIngredients.put(ingredientName, amount);
    }

    @Override
    public String getRecipeName() { return recipeName; }

    @Override
    public String getCategory() { return category; }

    @Override
    public Map<String, Integer> getRequiredIngredients() { return requiredIngredients; }

    @Override
    public int getProductionTimeSeconds() { return productionTimeSeconds; }

    @Override
    public int getSellingPrice() { return sellingPrice; }
}

// ==========================================
// 4. คลาสสูตรสินค้าแยกตามหมวดหมู่
// ==========================================

// --- หมวดของว่าง (Snacks) ---
class FrenchFriesRecipe extends BaseRecipe {
    public FrenchFriesRecipe() {
        super("เฟรนช์ฟรายส์", "ของว่าง", 3, 40);
        addRequirement("มันฝรั่ง", 1);
    }
}

class HoneyCookieRecipe extends BaseRecipe {
    public HoneyCookieRecipe() {
        super("คุกกี้น้ำผึ้ง", "ของว่าง", 5, 90);
        addRequirement("น้ำผึ้ง", 2);
        addRequirement("แป้งสาลี", 2);
    }
}

class PopcornRecipe extends BaseRecipe {
    public PopcornRecipe() {
        super("ป๊อปคอร์น", "ของว่าง", 6, 120);
        addRequirement("ข้าวโพด", 3);
        addRequirement("ชีส", 1);
        addRequirement("น้ำตาล", 1);
    }
}

// --- หมวดแปรรูปนม (Dairy) ---
class ButterRecipe extends BaseRecipe {
    public ButterRecipe() {
        super("เนย", "แปรรูปนม", 4, 50);
        addRequirement("นม", 1);
    }
}

class CheeseRecipe extends BaseRecipe {
    public CheeseRecipe() {
        super("ชีส", "แปรรูปนม", 5, 80);
        addRequirement("นม", 2);
    }
}

class CreamRecipe extends BaseRecipe {
    public CreamRecipe() {
        super("ครีม", "แปรรูปนม", 4, 70);
        addRequirement("นม", 3);
    }
}

class YogurtRecipe extends BaseRecipe {
    public YogurtRecipe() {
        super("โยเกิร์ต", "แปรรูปนม", 5, 110);
        addRequirement("แอปเปิ้ล", 1);
        addRequirement("นม", 3);
    }
}

// --- หมวดเบเกอรี่ (Bakery) ---
class CroissantRecipe extends BaseRecipe {
    public CroissantRecipe() {
        super("ครัวซองต์", "เบเกอรี่", 8, 160);
        addRequirement("แป้งสาลี", 3);
        addRequirement("เนย", 1);
        addRequirement("ข้าวโพด", 1);
    }
}

class PotatoMuffinRecipe extends BaseRecipe {
    public PotatoMuffinRecipe() {
        super("มัฟฟินมันฝรั่ง", "เบเกอรี่", 7, 140);
        addRequirement("แป้งสาลี", 3);
        addRequirement("มันฝรั่ง", 2);
        addRequirement("นม", 2);
    }
}

// --- หมวดซุป (Soup) ---
class CornSoupRecipe extends BaseRecipe {
    public CornSoupRecipe() {
        super("ซุปข้าวโพด", "ซุป", 5, 100);
        addRequirement("ข้าวโพด", 2);
        addRequirement("นม", 1);
    }
}

// --- หมวดแยม (Jam) ---
class AppleJamRecipe extends BaseRecipe {
    public AppleJamRecipe() {
        super("แยมแอปเปิ้ล", "แยม", 4, 85);
        addRequirement("น้ำผึ้ง", 1);
        addRequirement("แอปเปิ้ล", 1);
    }
}

// --- หมวดคาเฟ่เค้ก (Cafe Cake) ---
class FreshMilkCakeRecipe extends BaseRecipe {
    public FreshMilkCakeRecipe() {
        super("เค้กนมสด", "คาเฟ่เค้ก", 10, 250);
        addRequirement("แป้งสาลี", 1);
        addRequirement("น้ำตาล", 1);
        addRequirement("นม", 1);
        addRequirement("ไข่ไก่", 1);
    }
}

// ==========================================
// 5. ระบบบริหารจัดการโรงงานผลิต (Processing Station)
// ==========================================
class ProcessingStation {
    private Map<String, Ingredient> inventory = new HashMap<>();

    public void addInventory(String name, int amount) {
        inventory.putIfAbsent(name, new Ingredient(name, 0));
        inventory.get(name).addQuantity(amount);
    }

    public void showInventory() {
        System.out.println("=== 🎒 คลังวัตถุดิบฟาร์ม (คูณ 10) ===");
        for (Ingredient ing : inventory.values()) {
            System.out.println("- " + ing.getName() + ": " + ing.getQuantity() + " ชิ้น");
        }
        System.out.println("=========================================");
    }

    // ตรวจสอบวัตถุดิบ
    public boolean checkIngredients(ProcessingRecipe recipe) {
        System.out.println("🔍 [ตรวจสอบ] เมนู: " + recipe.getRecipeName() + " (" + recipe.getCategory() + ")");
        for (Map.Entry<String, Integer> entry : recipe.getRequiredIngredients().entrySet()) {
            String reqName = entry.getKey();
            int reqAmount = entry.getValue();
            int currentStock = inventory.containsKey(reqName) ? inventory.get(reqName).getQuantity() : 0;

            if (currentStock < reqAmount) {
                System.out.println("  ❌ ขาดวัตถุดิบ: " + reqName + " (ต้องการ " + reqAmount + ", มี " + currentStock + ")");
                return false;
            } else {
                System.out.println("  ✔️ " + reqName + " เพียงพอ (ต้องการ " + reqAmount + ", มี " + currentStock + ")");
            }
        }
        return true;
    }

    // กระบวนการผลิตและเพิ่มสินค้าสำเร็จรูปกลับเข้าคลัง (เพื่อนำไปทำเมนูอื่นต่อได้)
    public void craftItem(ProcessingRecipe recipe) {
        if (checkIngredients(recipe)) {
            // หักวัตถุดิบ
            for (Map.Entry<String, Integer> entry : recipe.getRequiredIngredients().entrySet()) {
                inventory.get(entry.getKey()).consume(entry.getValue());
            }
            System.out.println("🐾 ผลิตสำเร็จ: [" + recipe.getRecipeName() + "] ใช้เวลา " + recipe.getProductionTimeSeconds() + " วินาที | ราคาขาย: " + recipe.getSellingPrice() + " G");
            
            // นำสินค้าที่ผลิตได้เก็บเข้าคลัง (เผื่อเป็นวัตถุดิบให้เมนูอื่น เช่น เนย, ชีส)
            addInventory(recipe.getRecipeName(), 1);
            System.out.println("✨ ได้รับ [" + recipe.getRecipeName() + "] 1 ชิ้นเข้าคลัง\n");
        } else {
            System.out.println("⚠️ ไม่สามารถผลิต " + recipe.getRecipeName() + " ได้ เนื่องจากวัตถุดิบไม่ครบ\n");
        }
    }
}

// ==========================================
// 6. คลาส Main สำหรับรันและทดสอบระบบ
// ==========================================
public class Main {
    public static void main(String[] args) {
        ProcessingStation station = new ProcessingStation();

        // เติมวัตถุดิบจากฟาร์ม (จำลองค่าคูณ 10 จากผลผลิตฟาร์มพื้นฐาน)
        station.addInventory("นม", 20);       // ฟาร์มนมวัว (2x10)
        station.addInventory("แอปเปิ้ล", 10);   // ฟาร์มแอปเปิ้ล (1x10)
        station.addInventory("น้ำผึ้ง", 20);     // ฟาร์มผึ้ง (2x10)
        station.addInventory("ไข่ไก่", 10);     // ฟาร์มไข่ไก่ (1x10)
        station.addInventory("มันฝรั่ง", 20);   // ฟาร์มมันฝรั่ง (2x10)
        station.addInventory("น้ำตาล", 10);     // ฟาร์มน้ำตาล (1x10)
        station.addInventory("แป้งสาลี", 30);   // ฟาร์มแป้งสาลี (3x10)
        station.addInventory("ข้าวโพด", 30);   // ฟาร์มข้าวโพด (3x10)

        // แสดงคลังเริ่มต้น
        station.showInventory();

        // สร้าง Object ของสูตรอาหารต่างๆ
        ProcessingRecipe frenchFries = new FrenchFriesRecipe();
        ProcessingRecipe butter = new ButterRecipe();
        ProcessingRecipe cheese = new CheeseRecipe();
        ProcessingRecipe croissant = new CroissantRecipe();
        ProcessingRecipe freshMilkCake = new FreshMilkCakeRecipe();

        // --- ทดสอบระบบการผลิต ---
        
        // 1. ลองทำเฟรนช์ฟรายส์ (ของว่าง)
        station.craftItem(frenchFries);

        // 2. ลองทำเนย (แปรรูปนม) - ผลิตเสร็จจะได้ไอเทม "เนย" เข้าคลัง
        station.craftItem(butter);

        // 3. ลองทำชีส (แปรรูปนม) - ผลิตเสร็จจะได้ไอเทม "ชีส" เข้าคลัง
        station.craftItem(cheese);

        // 4. ลองทำครัวซองต์ (เบเกอรี่) - ต้องใช้ "เนย" ที่เพิ่งผลิตได้
        station.craftItem(croissant);

        // 5. ลองทำเค้กนมสด (คาเฟ่เค้ก)
        station.craftItem(freshMilkCake);

        // แสดงคลังวัตถุดิบและสินค้าคงเหลือหลังผลิต
        station.showInventory();
    }
}