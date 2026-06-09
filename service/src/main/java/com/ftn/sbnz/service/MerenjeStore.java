package com.ftn.sbnz.service;

import com.ftn.sbnz.model.MerenjeEvent;
import org.springframework.stereotype.Component;

@Component
public class MerenjeStore {

    private volatile MerenjeEvent latest;

    public void setLatest(MerenjeEvent event) {
        this.latest = event;
    }

    public MerenjeEvent getLatest() {
        return latest;
    }

    public boolean hasData() {
        return latest != null;
    }
}
