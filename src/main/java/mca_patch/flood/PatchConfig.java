package mca_patch.flood;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PatchConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("mca-tick-usage-patch.json");
    private static volatile Values current = new Values();

    private PatchConfig() {
    }

    public static Values current() {
        return current;
    }

    public static synchronized void load() {
        Values loaded = new Values();
        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH)) {
                Values parsed = GSON.fromJson(reader, Values.class);
                if (parsed != null) {
                    loaded = parsed;
                }
            } catch (Exception exception) {
                MCATickUsagePatch.INSTANCE.getLogger().error("Could not read {}", PATH, exception);
            }
        }

        loaded.validate();
        current = loaded;
        save();
    }

    private static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(current, writer);
            }
        } catch (IOException exception) {
            MCATickUsagePatch.INSTANCE.getLogger().error("Could not write {}", PATH, exception);
        }
    }

    public enum NavigationMode {
        MCA,
        VANILLA
    }

    public static final class Values {
        public NavigationMode mode = NavigationMode.MCA;
        public boolean instrumentation = true;

        private void validate() {
            if (mode == null) {
                mode = NavigationMode.MCA;
            }
        }
    }
}
