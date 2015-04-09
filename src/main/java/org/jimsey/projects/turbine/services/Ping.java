package org.jimsey.projects.turbine.services;

import org.springframework.stereotype.Service;

@Service
public class Ping {

    public long ping() {
        return System.nanoTime();
    }
    
}
