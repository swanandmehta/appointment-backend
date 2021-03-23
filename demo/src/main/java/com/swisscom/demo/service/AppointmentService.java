package com.swisscom.demo.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisscom.demo.model.AppointmentBO;
import com.swisscom.demo.model.AppointmentTBO;
import com.swisscom.demo.model.SlotTBO;

@Service
public class AppointmentService {

    public List<AppointmentTBO> getAppointments() {
        final List<AppointmentBO> appointments = getAppointmentsFromAnotherService();
        
        Map<LocalDate, List<AppointmentBO>> filteredList = appointments.stream().sorted((ap1, ap2) -> {
        	return ap1.getStart().compareTo(ap2.getStart());
        }).collect(Collectors.toMap(
        	(ap) -> {
        		return ((AppointmentBO) ap).getStart().toLocalDate();
        	},
        	(ap) -> {
        		List<AppointmentBO> combinedList = new ArrayList<>(1);
        		combinedList.add(ap);
        		return combinedList;
        	},
        	(existing, newlyFormed) -> {
        		if(existing != null && newlyFormed != null && existing.size() < 2) {
        			if(newlyFormed.get(0).getStart().isAfter(existing.get(0).getEnd()) || newlyFormed.get(0).getStart().isEqual(existing.get(0).getEnd())) {
        				existing.addAll(newlyFormed);        				
        			}
        		}

        		return existing;
        	},
        	TreeMap::new
        ));
                
        return filteredList.entrySet().stream().map(forDay -> {
        	AppointmentTBO dto = new AppointmentTBO(forDay.getKey(), forDay.getValue()
        			.stream()
        			.map(ap -> {
        				SlotTBO slot = new SlotTBO(ap.getId(), ap.getStart().toLocalTime(), ap.getEnd().toLocalTime());
        				return slot;
        			}).collect(Collectors.toList()));
        	return dto;
        }).collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // Logic to simulate appointments from another service
    // ------------------------------------------------------------------

    private List<AppointmentBO> getAppointmentsFromAnotherService() {
        try {
            return getAppointmentsFromFile("src/main/resources/appointments.json");
        } catch (IOException e) {
            e.printStackTrace();
            return  Arrays.asList();
        }
    }

    private static List<AppointmentBO> getAppointmentsFromFile(final String pathName) throws IOException {
        final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return Arrays.asList(mapper.readValue(new File(pathName), AppointmentBO[].class));
    }

}
