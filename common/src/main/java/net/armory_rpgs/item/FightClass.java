package net.armory_rpgs.item;

public enum FightClass {
    ARCANE_WIZARD("Arcane Wizard"),
    FIRE_WIZARD("Fire Wizard"),
    FROST_WIZARD("Frost Wizard"),
    PRIEST("Priest"),
    PALADIN("Paladin"),
    ROGUE("Rogue"),
    WARRIOR("Warrior"),
    ARCHER("Archer");

    final String translation;

    FightClass(String translation) {
        this.translation = translation;
    }
}
