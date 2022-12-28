package com.ruoyi.rabbit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum DelayTypeEnum {
    // 30分钟
    DELAY_30m(1),
    // 1小时
    DELAY_60m(2);

    private Integer type;

    public static DelayTypeEnum getDelayTypeEnum(Integer type) {
        if (Objects.equals(type, DELAY_30m.type)) {
            return DELAY_30m;
        }
        if (Objects.equals(type, DELAY_60m.type)) {
            return DELAY_60m;
        }
        return null;
    }

}
