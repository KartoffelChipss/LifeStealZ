package org.strassburger.lifestealz.gui;

// org.strassburger.lifestealz.gui developed by abb3v

import net.kyori.adventure.text.format.TextColor;

public final class Constants {
    // Prevent instantiation
    private Constants() {}

    public static final class Colors {
        public static final TextColor TITLE = TextColor.color(0xFF6B6B);
        public static final TextColor SUPPORT = TextColor.color(0xFED766);
        public static final TextColor DISCORD = TextColor.color(0x7289DA);
        public static final TextColor WIKI = TextColor.color(0x4ECB71);
        public static final TextColor REVIEW = TextColor.color(0xFFB347);
        public static final TextColor DESCRIPTION = TextColor.color(0xE0E0E0);
        public static final TextColor HOVER = TextColor.color(0x72CEE3);
        public static final TextColor LOVE = TextColor.color(0xFF7F7E);
        public static final TextColor GREY = TextColor.color(0x555555);
        public static final TextColor BACK = TextColor.color(0xFF4444);
        public static final TextColor DISABLED = TextColor.color(0x808080);
    }

    public static final class Icons {
        public static final String GUI_ICON = "☠";
        public static final String SEPARATOR = " | ";
        public static final String ARROW_LEFT = "«";
        public static final String ARROW_RIGHT = "»";
    }

    public static final class Urls {
        public static final String DISCORD = "https://strassburger.org/discord/";
        public static final String WIKI = "https://lsz.strassburger.dev/";
        public static final String REVIEW = "https://www.spigotmc.org/resources/lifestealz.111469/";
    }

    public static final class Textures {
        public static final String SUPPORT_HEAD = "http://textures.minecraft.net/texture/d34e063cafb467a5c8de43ec78619399f369f4a52434da8017a983cdd92516a0";
        public static final String DISCORD_HEAD = "http://textures.minecraft.net/texture/92ca27cab87722896dc668c7b9b76e6fc3e2dce770583adfbe162adc94e9d82d";
        public static final String WIKI_HEAD = "http://textures.minecraft.net/texture/18c0ca8fae701314330fdfdded3445e4542a3597b70d38f47d75111eb04104b8";
        public static final String REVIEW_HEAD = "http://textures.minecraft.net/texture/e34a592a79397a8df3997c43091694fc2fb76c883a76cce89f0227e5c9f1dfe";
        public static final String DEVELOPER_HEAD = "http://textures.minecraft.net/texture/fa2d6b3f7a49a62dcf8091f9077b68f53c4cbb136e6b96d5266f3d8a152a5165";
        public static final String GRAVEYARD_HEAD = "http://textures.minecraft.net/texture/a082e31b2d7b3a3ec3c451591a6feb97d048f5392376e4e06a3fee7982571ca4";

        public static final String ARROW_LEFT = "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23";
        public static final String ARROW_RIGHT = "http://textures.minecraft.net/texture/1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b";
        public static final String HOME_HEAD = "http://textures.minecraft.net/texture/63d02cdc075bb1cc5f6fe3c7711ae4977e38b910d50ed6023df73913e5e7fcff";
        public static final String ARROW_LEFT_DISABLED = "http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477";
        public static final String ARROW_RIGHT_DISABLED = "http://textures.minecraft.net/texture/f2f3a2dfce0c3dab7ee10db385e5229f1a39534a8ba2646178e37c4fa93b";
    }
}