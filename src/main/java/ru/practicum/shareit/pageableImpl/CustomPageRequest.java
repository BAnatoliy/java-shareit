package ru.practicum.shareit.pageableImpl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

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
    @NonNull
    public Sort getSort() {
        return sort;
    }

    @Override
    @NonNull
    public Pageable next() {
        return new CustomPageRequest(this.getPageNumber() + 1, this.getPageSize(), this.getSort());
    }

    @Override
    @NonNull
    @Deprecated
    public Pageable previousOrFirst() {
        return this.hasPrevious() ? this.previous() : this.first();
    }

    @Deprecated
    public Pageable previous() {
        return this.getPageNumber() == 0 ? this : new CustomPageRequest(
                this.getPageNumber() * this.getPageSize() - 1, this.getPageSize(), this.getSort());
    }

    @Override
    @NonNull
    @Deprecated
    public Pageable first() {
        return new CustomPageRequest(0, this.getPageSize(), this.getSort());
    }

    @Override
    @NonNull
    @Deprecated
    public Pageable withPage(int pageNumber) {
        return new CustomPageRequest(pageNumber * this.getPageSize(), this.getPageSize(), this.getSort());
    }

    @Override
    @Deprecated
    public boolean hasPrevious() {
        return false;
    }
}
