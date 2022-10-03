package com.kkb.cubemall.juc.idea;

import java.util.List;
import java.util.Objects;

public class idea_hotkey {

    //alt+enter 变为forEach结构
    public void lambda() {
        List<String> lists = getStrings();
        lists.stream().filter(Objects::nonNull).forEach(System.out::println);
    }


    public List<String> getStrings() {
        return null;
    }

}
