package me.hsgamer.topin.data.value;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A combination of UUID and BigDecimal
 */
public abstract class PairDecimal implements Comparable<PairDecimal> {

    private final UUID uuid;
    private BigDecimal value = new BigDecimal(0);

    /**
     * Create a new pair
     *
     * @param uuid the unique id
     */
    protected PairDecimal(UUID uuid) {
        this.uuid = uuid;
        update();
    }

    /**
     * Get the unique id
     *
     * @return the unique id
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Get the value
     *
     * @return the value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Set the value
     *
     * @param value the value
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Called when updating the value
     */
    public abstract void update();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PairDecimal) {
            PairDecimal pairDecimal = (PairDecimal) obj;
            return this.value.equals(pairDecimal.value) && this.uuid.equals(pairDecimal.uuid);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return uuid.hashCode() - value.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(PairDecimal pairDecimal) {
        return value.compareTo(pairDecimal.value);
    }
}
