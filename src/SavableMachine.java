/**
 * The only thing SaveGame needs to know about the processing machine.
 * The machine implements this small API without exposing its internal fields to the save code.
 *
 * Save line:  machine <STATE> <nightsLeft> <recipe name>      e.g.  machine PROCESSING 2 Strawberry Cake
 *             machine IDLE 0 -                                (an idle machine has no recipe)
 * The recipe name comes last because a name may contain spaces.
 */
public interface SavableMachine {
    /** "IDLE", "PROCESSING" or "READY" */
    String getSavedState();

    /** The recipe's product name, or "-" when the machine is idle. */
    String getSavedRecipeName();

    int getSavedNights();

    /**
     * Puts the machine back as it was.
     * @throws IllegalArgumentException if the state is not one of the three, or
     * @throws FarmException            if no recipe has that name (the loader adds the line number)
     */
    void restore(String state, String recipeName, int nights) throws FarmException;
}
