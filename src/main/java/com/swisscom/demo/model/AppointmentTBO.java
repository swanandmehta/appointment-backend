package com.swisscom.demo.model;

import java.time.LocalDate;
import java.util.List;

public class AppointmentTBO {

	private final LocalDate date;
    private final List<SlotTBO> slots;

    public AppointmentTBO(final LocalDate date, final List<SlotTBO> stots) {
        this.date = date;
        this.slots = stots;
    }

    public LocalDate getDate() {
        return date;
    }

	public List<SlotTBO> getSlots() {
		return slots;
	}

}
