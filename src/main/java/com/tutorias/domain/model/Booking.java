package com.tutorias.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private int bookingId;
    private int userId;
    private int scheduleId;
    private LocalDateTime scheduleDateTime;
    private Integer rating;
}
