package dev.micahb.rpncalculator.Calculator;

import java.util.*;

/**
 * Interface to describe an operator that computes a result with doubles.
 */
public interface Operator {

    /** 
     * How many arguments does this operator take?
     * @return number of arguments required by this operator.
     */
    int numArgs();

    /**
     * Evaluate this operator using the given arguments.
     * Should not modify the list of arguments.
     * @param args Argument list.
     * @return Result of the operation.
     */
    double eval(List<Double> args);
}

