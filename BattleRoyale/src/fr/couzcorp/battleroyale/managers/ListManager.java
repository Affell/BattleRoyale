package fr.couzcorp.battleroyale.managers;

import fr.couzcorp.battleroyale.models.PlayerObject;

import java.util.ArrayList;
import java.util.List;

public class ListManager<T> {

    private List<T> list;

    public ListManager() {
        list = new ArrayList<>();
    }

    public List<T> get() {
        return list;
    }

    public boolean add(T item) {
        if(!list.contains(item)) {
            return list.add(item);
        }else{
            return false;
        }
    }

    public boolean remove(T item) {
        return list.remove(item);
    }
    public ListManager<T> removeReturn(T item) {
        remove(item);
        return this;
    }

    public int size(){
        return list.size();
    }

    public ListManager<T> removeDuplicates(){
        if(list.size() > 0 && list.get(0).getClass().equals(PlayerObject.class)) {
            ListManager<T> list_ = new ListManager<>();
            for (T o : list) {
                if (!list_.get().contains(o)) list_.add(o);
            }
            return list_;
        }else{
            return this;
        }
    }


}
