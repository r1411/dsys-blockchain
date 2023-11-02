package top.kekdev.blockchain.dto.wrapper;

import java.util.ArrayList;
import java.util.List;

public class ListResponseEntity<T> {
    private List<T> items;
    private Integer count;

    public ListResponseEntity(List<T> items) {
        this.items = items;
        this.count = items.size();
    }

    public ListResponseEntity() {
        this.items = new ArrayList<>();
    }

    public void addItem(T item) {
        items.add(item);
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
