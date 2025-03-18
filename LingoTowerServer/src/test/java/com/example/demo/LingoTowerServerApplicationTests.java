package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.lingotower.LingotowerApplication;  // חשוב להוסיף את המחלקה הראשית כאן

@SpringBootTest(classes = LingotowerApplication.class)  // הפניה למחלקה הראשית
class LingoTowerServerApplicationTests {

    @Test
    void contextLoads() {
        // כאן ניתן להוסיף בדיקות נוספות אם צריך
    }
}

