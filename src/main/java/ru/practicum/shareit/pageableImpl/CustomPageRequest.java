package ru.practicum.shareit.pageableImpl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CustomPageRequest implements Pageable {
    private final Sort sort;
    private final int offset;
    private final int size;

    public CustomPageRequest(int offset, int size, Sort sort) {
        this.sort = sort;
        this.offset = offset;
        this.size = size;
    }

    public static CustomPageRequest of(int offset, int size) {
        return of(offset, size, Sort.unsorted());
    }

    public static CustomPageRequest of(int offset, int size, Sort sort) {
        return new CustomPageRequest(offset, size, sort);
    }

    @Override
    public int getPageNumber() {
        return offset / size;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
