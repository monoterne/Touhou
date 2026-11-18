package touhou;

import java.nio.file.Path;
import java.util.List;

public record GameData(
        String difficulty,
        String databasePassword,
        List<String> users,
        String workingDirectory,
        String levelDefinition,
        int score) {

    public GameData {
        users = List.copyOf(users);
    }

    public static GameData defaults(Path workingDirectory) {
        return new GameData(
                "normal",
                "",
                List.of("player"),
                workingDirectory.toAbsolutePath().toString(),
                "radial",
                0);
    }
}
