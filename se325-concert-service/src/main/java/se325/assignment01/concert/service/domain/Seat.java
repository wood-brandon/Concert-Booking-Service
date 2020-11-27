package se325.assignment01.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se325.assignment01.concert.common.dto.SeatDTO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Seat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String label;
	private boolean isBooked;
	private LocalDateTime date;
	private BigDecimal price;

	public Seat() {}

	public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal price) {
		this.label = label;
		this.isBooked = isBooked;
		this.date = date;
		this.price = price;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isBooked() {
		return isBooked;
	}

	public void setBooked(boolean booked) {
		isBooked = booked;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		Seat seat = (Seat) o;

		return new EqualsBuilder()
				.append(label, seat.label)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(label)
				.toHashCode();
	}
}
