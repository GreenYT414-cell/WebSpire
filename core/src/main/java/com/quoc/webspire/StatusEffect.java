package com.quoc.webspire;

public class StatusEffect {
    public enum Type {
        STRENGTH("Sức Mạnh", true),
        VULNERABLE("Lỗ Hổng", false),
        WEAK("Yếu Ớt", false),
        POISON("Độc", false);

        private final String name;
        private final boolean isBuff;

        Type(String name, boolean isBuff) {
            this.name = name;
            this.isBuff = isBuff;
        }

        public String getName() { return name; }
        public boolean isBuff() { return isBuff; }
    }

    private final Type type;
    private int amount;

    public StatusEffect(Type type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public Type getType() { return type; }
    public int getAmount() { return amount; }
    public void addAmount(int val) { this.amount += val; }
    public void setAmount(int val) { this.amount = val; }
}
