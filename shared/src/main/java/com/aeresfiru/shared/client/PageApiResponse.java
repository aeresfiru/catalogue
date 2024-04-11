package com.aeresfiru.shared.client;

import java.io.Serializable;
import java.util.List;

public class PageApiResponse<T> implements Serializable {

    private List<T> content;
    private int size;
    private int number;
    private long totalElements;
    private int totalPages;

    public PageApiResponse(List<T> content, int size, int number, long totalElements, int totalPages) {
        this.content = content;
        this.size = size;
        this.number = number;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public PageApiResponse() {
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public String toString() {
        return "PageApiResponse{" +
                "content=" + content +
                ", size=" + size +
                ", number=" + number +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                '}';
    }
}
