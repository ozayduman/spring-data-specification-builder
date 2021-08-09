package com.github.ozayduman.specificationbuilder.dto.operation;


import com.github.ozayduman.specificationbuilder.dto.Operator;

public class CompoundOrOperation extends AbstractOperation {
    private final List<AbstractOperation> operationList;
    public CompoundOrOperation(AbstractOperation ...operations) {
        operationList =  List.of(operations);
    }

    @java.lang.Override
    protected EnumSet<Operator> allowedOperators() {
        return null;
    }

    @java.lang.Override
    public java.lang.Comparable<?>[] getOperands() {
        return new java.lang.Comparable[0];
    }
}
