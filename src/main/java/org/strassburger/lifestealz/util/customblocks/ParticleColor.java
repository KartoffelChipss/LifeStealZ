package org.strassburger.lifestealz.util.customblocks;

import org.bukkit.Color;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Enum representing different particle colors with their RGB bounds.
 * Each color can generate a random color within its defined RGB bounds.
 */
public enum ParticleColor {
    WHITE(new Bounds(230, 255), new Bounds(230, 255), new Bounds(230, 255)),
    GRAY(new Bounds(100, 155), new Bounds(100, 155), new Bounds(100, 155)),
    RED(new Bounds(200, 255), new Bounds(0, 39), new Bounds(0, 39)),
    ORANGE(new Bounds(230, 255), new Bounds(100, 150), new Bounds(0, 19)),
    YELLOW(new Bounds(230, 255), new Bounds(230, 255), new Bounds(0, 39)),
    GREEN(new Bounds(0, 39), new Bounds(200, 255), new Bounds(0, 39)),
    BLUE(new Bounds(0, 39), new Bounds(0, 39), new Bounds(200, 255)),
    PURPLE(new Bounds(150, 230), new Bounds(0, 39), new Bounds(150, 230)),
    PINK(new Bounds(230, 255), new Bounds(100, 150), new Bounds(150, 230));

    private final Bounds redBounds;
    private final Bounds greenBounds;
    private final Bounds blueBounds;

    ParticleColor(Bounds redBounds, Bounds greenBounds, Bounds blueBounds) {
        this.redBounds = redBounds;
        this.greenBounds = greenBounds;
        this.blueBounds = blueBounds;
    }

    /**
     * Generates a random color within the RGB bounds of this ParticleColor.
     * @return a Color object with random RGB values within the defined bounds
     */
    public Color getColor() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int red = redBounds.random(rand);
        int green = greenBounds.random(rand);
        int blue = blueBounds.random(rand);
        return Color.fromRGB(red, green, blue);
    }

    /**
     * Returns a ParticleColor based on the given color name.
     * If the color name does not match any ParticleColor, it returns WHITE.
     *
     * @param colorName the name of the color
     * @return the corresponding ParticleColor or WHITE if not found
     */
    public static ParticleColor fromString(String colorName) {
        try {
            return ParticleColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return WHITE;
        }
    }

    private record Bounds(int min, int max) {
        private Bounds {
            if (min < 0 || max > 255 || min > max)
                throw new IllegalArgumentException("Color bounds must be in range 0-255 and min ≤ max");
        }

        public int random(ThreadLocalRandom rand) {
            return rand.nextInt(min, max + 1); // inclusive max
        }
    }
}