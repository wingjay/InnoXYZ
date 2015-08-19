package com.innoxyz.data.runtime.model.common;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class NamedText {
    public String text;
    public String name;

    @Override
    public String toString() {
        return text;
    }
}
