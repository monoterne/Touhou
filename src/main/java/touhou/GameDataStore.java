package touhou;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class GameDataStore {
    private static final int FORMAT_VERSION = 1;

    private final Path file;

    public GameDataStore(Path file) {
        this.file = file;
    }

    public void save(GameData data) {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (DataOutputStream output = new DataOutputStream(
                    Files.newOutputStream(file))) {
                output.writeInt(FORMAT_VERSION);
                output.writeUTF(data.difficulty());
                output.writeUTF(data.databasePassword());
                output.writeInt(data.users().size());
                for (String user : data.users()) {
                    output.writeUTF(user);
                }
                output.writeUTF(data.workingDirectory());
                output.writeUTF(data.levelDefinition());
                output.writeInt(data.score());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save game data", e);
        }
    }

    public GameData load(Path defaultWorkingDirectory) {
        if (Files.notExists(file)) {
            return GameData.defaults(defaultWorkingDirectory);
        }

        try (DataInputStream input = new DataInputStream(Files.newInputStream(file))) {
            if (input.readInt() != FORMAT_VERSION) {
                throw new IOException("Unsupported game data format");
            }
            String difficulty = input.readUTF();
            String databasePassword = input.readUTF();
            int userCount = input.readInt();
            if (userCount < 0) {
                throw new IOException("Invalid user count");
            }
            List<String> users = new ArrayList<>(userCount);
            for (int i = 0; i < userCount; i++) {
                users.add(input.readUTF());
            }
            return new GameData(
                    difficulty,
                    databasePassword,
                    users,
                    input.readUTF(),
                    input.readUTF(),
                    input.readInt());
        } catch (EOFException e) {
            throw new IllegalStateException("Game data file is incomplete", e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load game data", e);
        }
    }
}
