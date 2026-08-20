package net.armory_rpgs.client;

public class ArmoryClientConfig { public ArmoryClientConfig() { }

    /// Whether armor emissives are allowed to burn past the brightness of their own texture, the way a
    /// spell projectile does. Turned off, they still glow - just no brighter than the texture itself,
    /// a plain fullbright glow and the cheaper of the two, being one draw pass instead of two.
    public boolean allow_high_luminance_armor = true;
}
