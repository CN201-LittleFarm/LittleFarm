// ==========================================
// 4. คลาสสูตรสินค้าแยกตามหมวดหมู่
// ==========================================
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseRecipe implements ProcessingRecipe {
    protected String productName;
    protected String category;
    protected Map<String, Integer> requiredIngredients;
    protected int requiredNights;
    protected int sellPrice;

    public BaseRecipe(String productName, String category, int requiredNights, int sellPrice) {
        this.productName = productName;
        this.category = category;
        this.requiredNights = requiredNights;
        this.sellPrice = sellPrice;
        this.requiredIngredients = new HashMap<>();
    }

    protected void addRequirement(String ingredientName, int amount) {
        requiredIngredients.put(ingredientName, amount);
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public Map<String, Integer> getRequiredIngredients() {
        return Collections.unmodifiableMap(requiredIngredients);
    }

    @Override
    public int getRequiredNights() {
        return requiredNights;
    }

    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    @Override 
    public boolean hasEnoughIngredients(Bag<String> bag) {
        if (bag == null) {
            return false;
        }

        for (Map.Entry<String, Integer> entry : getRequiredIngredients().entrySet()) {
            if (bag.count(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
}
