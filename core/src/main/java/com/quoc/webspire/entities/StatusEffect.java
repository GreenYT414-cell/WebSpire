package com.quoc.webspire.entities;

public class StatusEffect {
    public enum Type {
        POISON("Độc"),
        WEAK("Yếu Mềm"),
        VULNERABLE("Dễ Tổn Thương"),
        STRENGTH("Sức Mạnh"),
        DEXTERITY("Khéo Léo"),
        METALLICIZE("Kim Loại Hóa"),
        DEMON_FORM("Hình Thái Ác Quỷ");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
