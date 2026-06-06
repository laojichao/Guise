package com.houvven.guise.module

/**
 * Interface for preset configuration adapters that map user-visible labels to their
 * corresponding underlying values. Implementations define a fixed set of preset options
 * that can be displayed in selection UIs (e.g., spinners, dropdown menus).
 */
interface PresetAdapter {
    /** The human-readable display label for this preset option. */
    val label: String
    /** The underlying machine-readable value associated with this preset option. */
    val value: String
}