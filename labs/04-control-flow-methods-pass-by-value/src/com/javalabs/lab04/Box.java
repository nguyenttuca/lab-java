package com.javalabs.lab04;

/**
 * Class đơn giản có sẵn (không cần chỉnh sửa) dùng để thực nghiệm
 * cơ chế Pass-by-Value of Reference trong Lab 04.
 */
public class Box {

    private int value;
    private String label;

    public Box(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Box{value=" + value + ", label='" + label + "'}";
    }
}
