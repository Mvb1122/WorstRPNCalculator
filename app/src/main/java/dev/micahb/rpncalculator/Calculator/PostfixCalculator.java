package dev.micahb.rpncalculator.Calculator; /**
 * Class which acts as a post-fix calculator. It also contains classes for Operators, and exceptions for when those operators aren't satisfied or found.
 *
 * Class: CS251
 * @version ver.1.22474487139... (Added a second period to this line.)
 * @author Micah Bushman
 */
import java.io.PrintStream;
import java.util.*;

// No need to write a constructor because all of our member variables are implied to be created when the object is created.
    // Also, I made operatorMaps static and final because it seemed like a good idea.
public class PostfixCalculator
{
    private static final Map<String, Operator> operatorMaps;

    static {
        // Handy Map.of method allows us to put in up to 10 (TEN!) objects into a map right away! No need for a static block to initialize here!
            // Unfortunately... I have 11 things I need to add. So, well. Here we are, in a static block. 残念…
        Map<String, Operator> everythingExceptSQRT = Map.of(
                "+", new Operators.Addition(),
                "add", new Operators.Addition(),
                "-", new Operators.Subtraction(),
                "sub", new Operators.Subtraction(),
                "*", new Operators.Multiplication(),
                "mult", new Operators.Multiplication(),
                "/", new Operators.Division(),
                "div", new Operators.Division(),
                "=", new Operators.Print(),
                "print", new Operators.Print()
        );

        operatorMaps = new HashMap<>() {
            // Wow! A static block inside a static block! This is so silly!
                // Whoever wrote this code should be taken out back and shot.
            {
                // Copy everything from the everythingExceptSQRT map into a hashmap and then square root it.
                putAll(everythingExceptSQRT);
                put("sqrt", new Operators.SquareRoot());
                put("√", new Operators.SquareRoot());
            }
        };
    }

    public static PrintStream output;

    private final Stack<Double> stack;

    public PostfixCalculator() {
        this(System.out);
    }

    public PostfixCalculator(PrintStream output) {
        stack = new StackOfDoubles();
        PostfixCalculator.output = output;
    }

    /**
     * Finds an operator associated with a key.
     * @param key Key associated with operator.
     * @return The operator associated with the key.
     */
    public static Operator fetchOperator(String key) { return operatorMaps.get(key); }

    /**
     * Adds a number to the calculator's stack.
     * @param v The number to add.
     */
    public void storeOperand(double v) {
        stack.push(v);
    }

    /**
     * Evaluates the next operator.
     * @param next The string of the next operator.
     *
     * @throws NotEnoughArgsException When there isn't enough numbers to execute the given operator.
     * @throws OperatorMissingException When there isn't a matching operator defined.
     */
    public void evalOperator(String next) {
        // Okay, so let's find the operator we're referencing.
        Operator op = operatorMaps.get(next);
        if (op == null) {
            // We don't have an operator?? Who put in wrong code?? This is an error I think.
            throw new OperatorMissingException(next);
        }

        // Okay, now we can assume that operator is real.
            // Check that we have enough things in our stack to do the operator.
        List<Double> argsForOperator = new ArrayList<>(op.numArgs());
        for (int i = 0; i < op.numArgs(); i++) {
            // If the stack isn't empty, take the last thing on the stack and move it onto the list.
            if (!stack.isEmpty()) argsForOperator.add(0, stack.pop()); // Add at the start of the list so that way it's in the right order.
            // If the stack ends up being empty, that means we didn't have enough arguments!
            else throw new NotEnoughArgsException(next);
        }

        // Finally, we know that we have enough args and the operator is valid, so we can evaluate now.
        double result = op.eval(argsForOperator);
        stack.push(result);
    }

    private static final class Operators {
        private static class Addition implements Operator {
            @Override
            public int numArgs() {
                return 2;
            }

            @Override
            public double eval(List<Double> args) {
                // We assume that args is 2 long here.
                return args.get(0) + args.get(1);
            }
        }

        private static class Subtraction implements Operator {
            @Override
            public int numArgs() {
                return 2;
            }

            @Override
            public double eval(List<Double> args) {
                return args.get(0) - args.get(1);
            }
        }

        private static class Multiplication implements Operator {

            @Override
            public int numArgs() {
                return 2;
            }

            @Override
            public double eval(List<Double> args) {
                return args.get(0) * args.get(1);
            }
        }

        private static class Division implements Operator {
            @Override
            public int numArgs() {
                return 2;
            }

            @Override
            public double eval(List<Double> args) {
                return args.get(0) / args.get(1);
            }
        }

        private static class Print implements Operator {

            @Override
            public int numArgs() {
                return 1;
            }

            @Override
            public double eval(List<Double> args) {
                Double arg = args.get(0);
                output.println(arg);
                return arg;
            }
        }

        private static class SquareRoot implements Operator {
            @Override
            public int numArgs() {
                return 1;
            }

            @Override
            public double eval(List<Double> args) {
                return Math.sqrt(args.get(0));
            }
        }
    }

    public static class PostfixCalculatorException extends RuntimeException {
        public PostfixCalculatorException(String error) {
            super(error);
        }
    }

    public static final class OperatorMissingException extends PostfixCalculatorException {
        public OperatorMissingException(String next) {
            super("Missing operator for string: `" + next + "`!");
        }
    }

    public static final class NotEnoughArgsException extends PostfixCalculatorException {
        public NotEnoughArgsException(String next) {
            super("Do not have enough operators for operator: `" + next + "`!");
        }
    }
}
