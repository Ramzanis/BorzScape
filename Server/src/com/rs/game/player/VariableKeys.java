package com.rs.game.player;

public class VariableKeys {

    public enum IntKey {

        KILLCOUNT(1, 0),

        DEATHCOUNT(2, 0);
        private final int uid;
        private final int defaultValue;

        IntKey(int uid, int defaultValue) {
            this.uid = uid;
            this.defaultValue = defaultValue;
        }

        public int getUID() {
            return uid;
        }

        public int getDefaultValue() {
            return defaultValue;
        }
    }

    public enum BooleanKey {

        BANK_PIN(1, false),

        ;
        private final int uid;
        private final boolean defaultValue;

        BooleanKey(int uid, boolean defaultValue) {
            this.uid = uid;
            this.defaultValue = defaultValue;
        }

        public int getUID() {
            return uid;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }
    }

    public enum LongKey {

        OVERLOAD_EFFECT(1, -1),
        PRAYER_RENEWAL_EFFECT(2, -1),

        POISON_IMMUNITY(3, -1),
        FIRE_IMMUNITY(4, -1);
        private final int uid;
        private final long defaultValue;

        LongKey(int uid, long defaultValue) {
            this.uid = uid;
            this.defaultValue = defaultValue;
        }

        public int getUID() {
            return uid;
        }

        public long getDefaultValue() {
            return defaultValue;
        }
    }

    public enum StringKey {

        EXAMPLE_STRING(1, ""),

        ;
        private final int uid;
        private final String defaultValue;

        StringKey(int uid, String defaultValue) {
            this.uid = uid;
            this.defaultValue = defaultValue;
        }

        public int getUID() {
            return uid;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }
}
