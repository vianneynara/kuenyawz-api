package dev.realtards.wzsnacknbites.utils.idgenerator;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * This class is used to generate unique Snow Flake IDs.
 * Claude AI helped me create this class.
 * I honestly have no idea how this works.
 */
@Slf4j
public class SnowFlakeIdGenerator implements IdentifierGenerator {
	private static final long EPOCH = 1729468800000L; // 2024-10-21 00:00:00 UTC
	private static final long WORKER_ID_BITS = 5L;
	private static final long DATACENTER_ID_BITS = 5L;
	private static final long SEQUENCE_BITS = 12L;

	// max values
	// `~(-1L << WORKER_ID_BITS)` creates a bitmask with the rightmost WORKER_ID_BITS set to 1.
	private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
	// This creates a bitmask for the sequence number, used to ensure it stays within its allocated bits.
	private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

	// shift constants
	private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
	private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
	private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

	private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

	private final long workerId;
	private final long datacenterId;
	private long sequence = 0L;
	private long lastTimestamp = -1L;

	public SnowFlakeIdGenerator() {
		// next time, we can make this configurable via application properties
		this(0, 0);
	}

	public SnowFlakeIdGenerator(long workerId, long datacenterId) {
		// checkers
		if (workerId > MAX_WORKER_ID || workerId < 0) {
			log.error("Worker ID can't be greater than {} or less than 0", MAX_WORKER_ID);
			throw new IllegalArgumentException("Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0");
		}
		if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
			log.error("Datacenter ID can't be greater than {} or less than 0", MAX_DATACENTER_ID);
			throw new IllegalArgumentException("Datacenter ID can't be greater than " + MAX_DATACENTER_ID + " or less than 0");
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;
	}

	/**
	 * This method will be used by Spring Data JPA to generate the ID for an entity that uses this generator.
	 * @param sharedSessionContractImplementor idk
	 * @param o idk
	 * @return idk
	 */
	@Override
	public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
		return nextId();
	}

	/**
	 * This calculates the next ID based on the current timestamp.
	 * @return {@code long} value of the next snowflake ID.
	 */
	private synchronized long nextId() {
		long timestamp = timeGen();

		// prevent anomalies in the system clock, where clock moved backwards
		if (timestamp < lastTimestamp) {
			log.error("[SNOWFLAKE GENERATOR] Clock moved backwards. Refusing to generate id");
			throw new RuntimeException("[SNOWFLAKE GENERATOR] Clock moved backwards. Refusing to generate id");
		}

		// if the timestamp is the same as the last timestamp:
		// 1. Increment the sequence number
		// 2. `&` is bitwise AND. This ensures sequence within the mask (allocated bits)
		// 3. If the sequence is 0 (overflows), wait until the next millisecond
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & SEQUENCE_MASK;
			if (sequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else { // if we're in the new millisecond, reset the sequence
			sequence = 0L;
		}

		// update the last timestamp
		lastTimestamp = timestamp;

		// the magic:
		// 1. `(timestamp - EPOCH)` gets the milliseconds since the epoch
		// 2. `<< TIMESTAMP_LEFT_SHIFT` shifts the bits to the left by the allocated bits
		// for the timestamp to make room for the datacenter ID, worker ID, and sequence
		// 3. `|` is bitwise OR. This combines the timestamp, datacenter ID, worker ID, and sequence
		return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT) |
			(datacenterId << DATACENTER_ID_SHIFT) |
			(workerId << WORKER_ID_SHIFT) |
			sequence;
	}

	/**
	 * This method will wait until the next millisecond to generate the ID.
	 * @param lastTimestamp The last timestamp
	 * @return {@code long} value of the next millisecond.
	 */
	private long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * Retrieves the system current time in milliseconds.
	 * @return {@code long} value of the current time in milliseconds.
	 */
	private long timeGen() {
		return System.currentTimeMillis();
	}
}
