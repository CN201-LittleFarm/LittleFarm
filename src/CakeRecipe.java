
// ==========================================
// 4. คลาสสูตรสินค้าแยกตามหมวดหมู่
// ==========================================

// --- หมวดคาเฟ่เค้ก (Cafe Cake) ---
public class CakeRecipe extends BaseRecipe {
    public CakeRecipe() {
        super("Strawberry Cake", "Bakery", 2, 1070);
        addRequirement("Strawberry", 3);
        addRequirement("Potato", 2);
        addRequirement("Turnip", 2);
    }
}
