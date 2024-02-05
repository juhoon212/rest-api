package com.example.demoinflearnrestapi.events;

import lombok.*;

import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // 이게 없으면 온라인 모임
    private int basePrice; // optional
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean free;
    private EventStatus eventStatus;


}