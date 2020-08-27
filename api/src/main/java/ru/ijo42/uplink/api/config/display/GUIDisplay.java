package ru.ijo42.uplink.api.config.display;

public class GUIDisplay {

    public MainMenu mainMenu;
    public LoadingGame loadingGame;
    public InGame inGame;

    public static class MainMenu {
        public String state;
        public String largeImageText;
    }

    public static class LoadingGame {
        public String state;
        public String largeImageText;
        public String details;
    }

    public static class InGame {
        public MultiPlayer multiPlayer;
        public SinglePlayer singlePlayer;

        public static class MultiPlayer {
            public LargeImageText largeImageText;
            public String state;
            public String details;

            public static class LargeImageText {
                public String unknown;
                public String ip;
            }
        }

        public static class SinglePlayer {
            public String state;
            public String largeImageText;
            public String details;
        }
    }

}
