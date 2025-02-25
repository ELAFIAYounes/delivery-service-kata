package com.delivery.config;

import com.delivery.domain.entity.TimeSlot;
import com.delivery.domain.mapper.TimeSlotMapper;
import com.delivery.domain.repository.TimeSlotRepository;
import com.delivery.service.timeslot.TimeSlotService;
import com.delivery.service.timeslot.impl.TimeSlotServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public TimeSlotRepository timeSlotRepository() {
        return mock(TimeSlotRepository.class);
    }

    @Bean
    @Primary
    public TimeSlotService timeSlotService(TimeSlotRepository timeSlotRepository) {
        return new TimeSlotServiceImpl(timeSlotRepository);
    }

    @Bean
    @Primary
    public TimeSlotMapper timeSlotMapper() {
        return new TimeSlotMapper();
    }
}
