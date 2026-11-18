import java.util.Map;

public class ProcessingMachine {
    private MachineState state;
    private ProcessingRecipe currentRecipe;
    private int remainingNights;

    public ProcessingMachine() {
        this.state = MachineState.IDLE;
        this.currentRecipe = null;
        this.remainingNights = 0;
    }

    public void startProcessing(ProcessingRecipe recipe, Bag<String> bag)
            throws MachineBusyException, InsufficientIngredientException {
        if (recipe == null || bag == null) {
            throw new IllegalArgumentException("Recipe and bag must not be null");
        }
        Map<String, Integer> required = recipe.getRequiredIngredients();
        if (required == null || required.isEmpty()) {
            throw new IllegalArgumentException("Recipe must require at least one ingredient");
        }
        if (state != MachineState.IDLE) {
            throw new MachineBusyException("Machine is busy (" + state + ")");
        }

        // Check all ingredients before deducting
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            String itemName = entry.getKey();
            Integer needed = entry.getValue();
            if (itemName == null || itemName.isBlank() || needed == null || needed <= 0) {
                throw new IllegalArgumentException("Invalid ingredient requirement in recipe");
            }
            if (bag.count(itemName) < needed) {
                throw new InsufficientIngredientException("Not enough " + itemName + " (need " + needed + ")");
            }
        }

        // Deduct ingredients from bag
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            bag.remove(entry.getKey(), entry.getValue());
        }

        this.currentRecipe = recipe;
        this.remainingNights = recipe.getRequiredNights();
        this.state = (this.remainingNights <= 0) ? MachineState.READY : MachineState.PROCESSING;
    }

    public void passNights(int nights) {
        if (state != MachineState.PROCESSING || nights <= 0) {
            return;
        }
        remainingNights -= nights;
        if (remainingNights <= 0) {
            remainingNights = 0;
            state = MachineState.READY;
        }
    }

    public String collectProduct(Bag<String> bag) throws MachineBusyException {
        if (bag == null) {
            throw new IllegalArgumentException("Bag must not be null");
        }
        if (state != MachineState.READY || currentRecipe == null) {
            throw new MachineBusyException("Product is not ready yet (" + state + ")");
        }

        String productName = currentRecipe.getProductName();
        bag.add(productName, 1);

        this.currentRecipe = null;
        this.remainingNights = 0;
        this.state = MachineState.IDLE;
        return productName;
    }

    public void restoreState(MachineState state, ProcessingRecipe recipe, int remainingNights) {
        if (state == null) {
            throw new IllegalArgumentException("Machine state must not be null");
        }
        if (state == MachineState.IDLE) {
            this.state = MachineState.IDLE;
            this.currentRecipe = null;
            this.remainingNights = 0;
        } else {
            if (recipe == null) {
                throw new IllegalArgumentException("Active recipe must not be null when machine is not IDLE");
            }
            this.state = state;
            this.currentRecipe = recipe;
            this.remainingNights = Math.max(0, remainingNights);
        }
    }

    public MachineState getState() {
        return state;
    }

    public ProcessingRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public int getRemainingNights() {
        return remainingNights;
    }
}