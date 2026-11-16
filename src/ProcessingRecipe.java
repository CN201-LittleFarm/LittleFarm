import java.util.Map;

public interface ProcessingRecipe {
    String getProductName();
    Map<String, Integer> getRequiredIngredients();
    int getRequiredNights();
    int getSellPrice();
}
