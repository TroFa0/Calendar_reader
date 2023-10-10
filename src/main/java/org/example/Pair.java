package org.example;

import java.util.Calendar;

public class Pair {
    private String key;
    private Calendar value;
        public Pair(String key, Calendar value){
            this.value = value;
            this.key = key;
        }
    public Calendar getValue() {
        return value;
    }

    public void setValue(Calendar value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
