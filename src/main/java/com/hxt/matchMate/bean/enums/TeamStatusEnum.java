package com.hxt.matchMate.bean.enums;

/**
 * ClassName: TeamStatusEnum
 * Package: com.hxt.matchMate.bean.enums
 * Description:
 *          队伍状态枚举
 * @Author hxt
 * @Create 2024/9/19 16:21
 * @Version 1.0
 */
public enum TeamStatusEnum {

    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密");
    private int value;

    private String text;

    public static TeamStatusEnum getEnumByValue(Integer value){
        if(value ==null){
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if(teamStatusEnum.getValue() == value){
                return teamStatusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
