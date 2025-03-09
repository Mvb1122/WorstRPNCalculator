package dev.micahb.rpncalculator.Calculator; /**
 * Class which just shows that I understand how a LinkedList works, I guess. Not entirely sure why this was required, but I guess it just stops people from using an array or something lol.
 *
 * Class: CS251
 * @version ver.1.22474487139... (Added a second period to this line.)
 * @author Micah Bushman
 */
import java.util.LinkedList;

public class StackOfDoubles implements Stack<Double> {
    // Must use Double (wrapper class) in order to have it work with LinkedList. Cannot use primitive double.
    private final LinkedList<Double> internalStack = new LinkedList<>();

    /**
     * Is the stack empty?
     */
    public boolean isEmpty() {
        return internalStack.isEmpty();
    }

    /**
     * Push item onto top of stack.
     *
     * @param val Item to put on top of the stack.
     */
    public void push(Double val) {
        internalStack.push(val);
    }

    /**
     * Pop the item from the top of the stack and return it.
     */
    public Double pop() {
        return internalStack.pop();
    }

    /**
     * Return the top item on the stack without removing it.
     */
    public Double peek() {
        return internalStack.peek();
    }
}
