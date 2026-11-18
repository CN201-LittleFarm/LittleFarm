// ==========================================
// 3. คลาสแม่แบบสำหรับสูตรอาหาร (Abstract Class)
// ==========================================
import java.util.Map;

public interface ProcessingRecipe {
    String getProductName();
    String getCategory();
    Map<String, Integer> getRequiredIngredients();
    int getRequiredNights();
    int getSellPrice();
    boolean hasEnoughIngredients(Bag<String> bag);
}
