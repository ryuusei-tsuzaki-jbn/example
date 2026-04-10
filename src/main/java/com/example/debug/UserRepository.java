package com.example.debug;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public List<String> findAll() {
        List<String> names = new ArrayList<>();
        names.add("alice");
        names.add("bob");
        return names;
    }
}
