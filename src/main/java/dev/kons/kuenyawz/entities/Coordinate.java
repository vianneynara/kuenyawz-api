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

	public static Coordinate of(Double latitude, Double longitude) {
		return Coordinate.builder()
			.latitude(latitude)
			.longitude(longitude)
			.build();
	}

	/**
	 * Calculates the distance between another {@link Coordinate} object.
	 *
	 * @param that {@link Coordinate} object to calculate the distance from
	 * @return distance in kilometers
	 */
	public double calculateDistance(Coordinate that) {
		return calculateDistance(that.getLatitude(), that.getLongitude());
	}

	public double calculateDistance(Double lat, Double lon) {
		double R = 6371;

		double lat1 = Math.toRadians(this.latitude);
		double lon1 = Math.toRadians(this.longitude);
		double lat2 = Math.toRadians(lat);
		double lon2 = Math.toRadians(lon);

		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;

		double a = Math.pow(Math.sin(dlat / 2), 2)
			+ Math.cos(lat1) * Math.cos(lat2)
			* Math.pow(Math.sin(dlon / 2), 2);

		double c = 2 * Math.asin(Math.sqrt(a));

		return (c * R);
	}
}