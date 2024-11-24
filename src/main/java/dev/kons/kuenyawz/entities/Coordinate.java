package dev.kons.kuenyawz.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Coordinate {

	@Column(name = "latitude", nullable = false)
	private Double latitude;

	@Column(name = "longitude", nullable = false)
	private Double longitude;

	public double calculateDistance(Coordinate that) {
		double R = 6371;

		double lat1 = Math.toRadians(this.latitude);
		double lon1 = Math.toRadians(this.longitude);
		double lat2 = Math.toRadians(that.latitude);
		double lon2 = Math.toRadians(that.longitude);

		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;

		double a = Math.pow(Math.sin(dlat / 2), 2)
			+ Math.cos(lat1) * Math.cos(lat2)
			* Math.pow(Math.sin(dlon / 2), 2);

		double c = 2 * Math.asin(Math.sqrt(a));

		return (c * R);
	}
}