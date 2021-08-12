package com.example.go4lunchproject.data.api;

import com.example.go4lunchproject.model.Workmate;

public class WorkmateApi {
    private Workmate workmate;
    private static WorkmateApi INSTANCE;

    public static WorkmateApi getInstance(){
        if (INSTANCE == null)
            INSTANCE = new WorkmateApi();
        
        return INSTANCE;
    }

    public Workmate getWorkmate() {
        return workmate;
    }

    public void setWorkmate(Workmate workmate) {
        this.workmate = workmate;
    }
}
