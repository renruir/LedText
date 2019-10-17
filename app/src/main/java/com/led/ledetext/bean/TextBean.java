package com.led.ledetext.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TextBean {

//    @Id(autoincrement = true)
//    private long id;
    @Index(unique = true)
    private int num;//信息号
    private int color;//颜色
    private int line1ScrollModel;//第一行文字滚动方式
    private int line2ScrollModel;//第二行问题滚动方式
    private int startpos;//显示位置
    private String line1;//第一行文字
    private String line2;//第二行文字


    @Generated(hash = 747869316)
    public TextBean(int num, int color, int line1ScrollModel, int line2ScrollModel, int startpos, String line1, String line2) {
        this.num = num;
        this.color = color;
        this.line1ScrollModel = line1ScrollModel;
        this.line2ScrollModel = line2ScrollModel;
        this.startpos = startpos;
        this.line1 = line1;
        this.line2 = line2;
    }

    @Generated(hash = 809912491)
    public TextBean() {
    }


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

    @Override
    public String toString() {
        return "color: " + getColor() + ", start position: " + getStartpos() + ", line 1 scroll model: " + getLine1ScrollModel() + ", line2 scroll model: " + getLine2ScrollModel() +
                ", line1 text: " + getLine1() + ", line2 text: " + getLine2();
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

}
