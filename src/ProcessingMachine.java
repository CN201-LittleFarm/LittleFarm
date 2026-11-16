import java.util.Map;

//private ไว้ไม่ให้ พวกเหลี่ยมแก้ไข state และ recipe ได้โดยตรง
public class ProcessingMachine {
    private MachineState state;
    private ProcessingRecipe currentRecipe;
    private int remainingNights;

    // Constructor
    public ProcessingMachine() {
        this.state = MachineState.IDLE; // เริ่มต้นเครื่องจะว่าง
        this.currentRecipe = null;
        this.remainingNights = 0;
    }

    // startProcessing method
    public void startProcessing(ProcessingRecipe recipe, Bag<String> bag)
            throws MachineBusyException, InsufficientIngredientException {

        // Don't Repeat if in processing
        if (state != MachineState.IDLE) {
            throw new MachineBusyException("เครื่องแปรรูปไม่ว่าง (สถานะ: " + state + ")");

        }

        // Check for sure that the bag has enough ingredients
        Map<String, Integer> required = recipe.getRequiredIngredients();
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            String itemName = entry.getKey();
            int needed = entry.getValue();

            if (bag.count(itemName) < needed) {
                throw new InsufficientIngredientException("วัตถุดิบไม่เพียงพอ: ต้องการ " + needed + " ของ " + itemName);
            }
        }

        // 3. if Full then remove the ingredients from the bag
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            bag.remove(entry.getKey(), entry.getValue());
        }

        // 4.Set the state to PROCESSING
        this.currentRecipe = recipe;
        this.remainingNights = recipe.getRequiredNights();
        this.state = (this.remainingNights <= 0) ? MachineState.READY : MachineState.PROCESSING;
    }

    // Decrement the remaining nights method
    public void passNights(int nights) {
        if (state != MachineState.PROCESSING || nights <= 0) {
            return; // If not processing, do nothing
        }
        remainingNights -= nights;
        if (remainingNights <= 0) {
            remainingNights = 0;
            state = MachineState.READY;
        }
    }

    // collectProduct method + Getters
    public String collectProduct(Bag<String> bag) throws MachineBusyException {
        if (state == MachineState.IDLE) {
            throw new MachineBusyException("เครื่องแปรรูปว่าง ไม่มีสินค้าให้เก็บ");
        }
        if (state == MachineState.PROCESSING) {
            throw new MachineBusyException("สินค้ายังไม่เสร็จ เหลืออีก " + remainingNights + " คืน");
        }

        // Add the product to the bag
        String productName = currentRecipe.getProductName();
        bag.add(productName, 1);

        // Reset the machine state(null)
        this.currentRecipe = null;
        this.remainingNights = 0;
        this.state = MachineState.IDLE;

        return productName;
    }

    // Getters for UI display
    public MachineState getState() {
        return state;
    }

    public ProcessingRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public int getRemainingNights() {
        return remainingNights;
    }

    // Setters for Save/Load State rollback
    public void restoresState(MachineState state, ProcessingRecipe recipe, int remainingNights) {
        this.state = state;
        this.currentRecipe = recipe;
        this.remainingNights = remainingNights;
    }

}
