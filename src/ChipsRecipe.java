
// ==========================================
// 4. คลาสสูตรสินค้าแยกตามหมวดหมู่
// ==========================================

// --- หมวดของว่าง (Snacks) ---
public class ChipsRecipe extends BaseRecipe {
    public ChipsRecipe() {
        super("Potato Chips", "Snacks", 1, 600);
        addRequirement("Turnip", 1);  // 1 วัน
        addRequirement("Potato", 3);  // 3 วัน
    }
}
