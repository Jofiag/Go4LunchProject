package com.example.go4lunchproject777.data.api;

import com.example.go4lunchproject777.model.Workmate;

public class ActualWorkmateApi {
    private Workmate workmate;
    private static ActualWorkmateApi INSTANCE;

    public static ActualWorkmateApi getInstance(){
        if (INSTANCE == null)
            INSTANCE = new ActualWorkmateApi();
        
        return INSTANCE;
    }

    public Workmate getWorkmate() {
        return workmate;
    }

    public void setWorkmate(Workmate workmate) {
        this.workmate = workmate;
    }
}
