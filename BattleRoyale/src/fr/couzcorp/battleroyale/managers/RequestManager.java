package fr.couzcorp.battleroyale.managers;

import fr.couzcorp.battleroyale.Main;

import java.util.UUID;

public class RequestManager {

    private ListManager<UUID> requesters;
    private ListManager<UUID> targets;

    public RequestManager() {
        requesters = new ListManager<>();
        targets = new ListManager<>();
    }

    public void addRequest(UUID requester, UUID target) {
        if (requesters.add(requester)) {
            targets.add(target);
        }
    }

    public void removeRequest(UUID requester, UUID target){
        int index;
        for(UUID uuid : requesters.get()){
            if(uuid == requester && targets.get().get(requesters.get().indexOf(uuid)) == target){
                index =requesters.get().indexOf(uuid);
                targets.get().remove(index);
                requesters.get().remove(index);
                return;
            }
        }
    }

    public boolean containsRequest(UUID requester, UUID target){
        for(UUID uuid : requesters.get()){
            if(uuid.equals(requester)){
                if(targets.get().get(requesters.get().indexOf(uuid)).equals(target)){
                    return true;
                }
            }
        }
        return false;
    }

    public ListManager<String> getRequestersNameForPlayer(UUID target){
        ListManager<String> list = new ListManager<>();
        for (UUID uuid : targets.get()){
            if(uuid.equals(target))list.add(Main.getInstance().getPlayer(requesters.get().get(targets.get().indexOf(uuid)).toString()).getPlayer().getName());
        }
        return list;
    }
}
