package com.example.FlightBooking.DTOs.Response.Airline;

import com.example.FlightBooking.Models.Planes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AirlineResponse {
    private String airlineName;
    private String logoUrl;
    private List<Planes> planes;
}
