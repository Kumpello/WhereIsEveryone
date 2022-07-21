package com.kumpello.whereiseveryone.model;

public enum UserType {
    EMAIL("EMAIL"), GOOGLE("GOOGLE");

    String type;
    UserType(String type) {
        this.type = type;
    }
}
