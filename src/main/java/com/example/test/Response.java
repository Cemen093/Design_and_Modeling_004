package com.example.test;

import java.util.List;
import java.util.Map;

public class Response {
    int total;
    int totalHits;
    List<Map<String,String>> hits;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public List<Map<String, String>> getHits() {
        return hits;
    }

    public void setHits(List<Map<String, String>> hits) {
        this.hits = hits;
    }
}
