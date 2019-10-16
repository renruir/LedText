package com.led.ledetext;

import androidx.annotation.NonNull;

public class TextModel {
    private int color;
    private int line1ScrollModel;
    private int line2ScrollModel;
    private int startpos;
    private String line1;
    private String line2;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getLine1ScrollModel() {
        return line1ScrollModel;
    }

    public void setLine1ScrollModel(int line1ScrollModel) {
        this.line1ScrollModel = line1ScrollModel;
    }

    public int getLine2ScrollModel() {
        return line2ScrollModel;
    }

    public void setLine2ScrollModel(int line2ScrollModel) {
        this.line2ScrollModel = line2ScrollModel;
    }

    public int getStartpos() {
        return startpos;
    }

    public void setStartpos(int startpos) {
        this.startpos = startpos;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    @NonNull
    @Override
    public String toString() {
        return "color: " + getColor() + ", start position: " + getStartpos() + ", line 1 scroll model: " + getLine1ScrollModel() + ", line2 scroll model: " + getLine2ScrollModel() +
        ", line1 text: " + getLine1() + ", line2 text: " + getLine2();
    }
}
