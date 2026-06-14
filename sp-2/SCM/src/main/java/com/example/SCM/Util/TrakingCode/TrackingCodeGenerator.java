package com.example.SCM.Util.TrakingCode;


import org.springframework.stereotype.Service;

@Service
public class TrackingCodeGenerator {

    public String generateTrackingCode() {

        return "TRN-" + System.currentTimeMillis();
    }

}
