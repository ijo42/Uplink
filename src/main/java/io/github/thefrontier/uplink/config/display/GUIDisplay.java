package io.github.thefrontier.uplink.config.display;

public class GUIDisplay {

    public MainMenu mainMenu;
    public LoadingGame loadingGame;
    public InGame inGame;

    public class MainMenu{
        public String state;
        public String largeImageText;
    }
    public class LoadingGame {
        public String state;
        public String largeImageText;
        public String details;
    }
    public class InGame {
        public MultiPlayer multiPlayer;
        public SinglePlayer singlePlayer;

        public class MultiPlayer {
            public LargeImageText largeImageText;
            
            public class LargeImageText {
                public String unknown;
                public String ip;
            }

            public String state;
            public String details;
        }

        public class SinglePlayer{
            public String state;
            public String largeImageText;
            public String details;
        }
    }

}
