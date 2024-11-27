package org.strassburger.lifestealz.gui;

import net.kyori.adventure.text.format.TextColor;

public final class Constants {
    private Constants() {}

    public enum Colors {
        TITLE(0xFF6B6B),
        SUPPORT(0xFED766),
        DISCORD(0x7289DA),
        WIKI(0x4ECB71),
        REVIEW(0xFFB347),
        DESCRIPTION(0xE0E0E0),
        HOVER(0x72CEE3),
        LOVE(0xFF7F7E),
        GREY(0x555555),
        BACK(0xFF4444),
        DISABLED(0x808080);

        private final TextColor value;

        Colors(int hexColor) {
            this.value = TextColor.color(hexColor);
        }

        public TextColor getValue() {
            return value;
        }
    }

    public enum Icons {
        GUI_ICON("☠"),
        SEPARATOR(" | "),
        ARROW_LEFT("«"),
        ARROW_RIGHT("»");

        private final String icon;

        Icons(String icon) {
            this.icon = icon;
        }

        public String getValue() {
            return icon;
        }
    }

    public enum Textures {
        SUPPORT("http://textures.minecraft.net/texture/d34e063cafb467a5c8de43ec78619399f369f4a52434da8017a983cdd92516a0"),
        DISCORD("http://textures.minecraft.net/texture/92ca27cab87722896dc668c7b9b76e6fc3e2dce770583adfbe162adc94e9d82d"),
        WIKI("http://textures.minecraft.net/texture/18c0ca8fae701314330fdfdded3445e4542a3597b70d38f47d75111eb04104b8"),
        REVIEW("http://textures.minecraft.net/texture/e34a592a79397a8df3997c43091694fc2fb76c883a76cce89f0227e5c9f1dfe"),
        DEVELOPER("http://textures.minecraft.net/texture/fa2d6b3f7a49a62dcf8091f9077b68f53c4cbb136e6b96d5266f3d8a152a5165"),
        GRAVEYARD("http://textures.minecraft.net/texture/a082e31b2d7b3a3ec3c451591a6feb97d048f5392376e4e06a3fee7982571ca4"),
        GHOST("http://textures.minecraft.net/texture/e0cfbdf63bb1bf8c989140a5fc54831d84cb0f8f64b4e1df7000db041258f5a1"),
        DENY("http://textures.minecraft.net/texture/d08ee6edfa98db5eae9b9c9936e94489b2d4bbbd3d2b4b6b4885a32240613c"),
        ACCEPT("http://textures.minecraft.net/texture/9b5871c72987266e15f1be49b1ec334ef6b618e9653fb78e918abd39563dbb93"),
        ARROW_LEFT("http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23"),
        ARROW_RIGHT("http://textures.minecraft.net/texture/1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b"),
        HOME("http://textures.minecraft.net/texture/63d02cdc075bb1cc5f6fe3c7711ae4977e38b910d50ed6023df73913e5e7fcff"),
        ARROW_LEFT_DISABLED("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477"),
        ARROW_RIGHT_DISABLED("http://textures.minecraft.net/texture/f2f3a2dfce0c3dab7ee10db385e5229f1a39534a8ba2646178e37c4fa93b");

        private final String url;

        Textures(String url) {
            this.url = url;
        }

        public String getValue() {
            return url;
        }
    }

    public enum Urls {
        DISCORD("https://strassburger.org/discord/"),
        WIKI("https://lsz.strassburger.dev/"),
        REVIEW("https://www.spigotmc.org/resources/lifestealz.111469/");

        private final String url;

        Urls(String url) {
            this.url = url;
        }

        public String getValue() {
            return url;
        }
    }
}