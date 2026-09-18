package mca_patch.flood;

public final class PathStats {
    private static long pathSearches;
    private static long candidateClassifications;
    private static long landNodeClassifications;
    private static long neighborHazardChecks;
    private static long collisionChecks;

    private static long tickPathSearches;
    private static long tickCandidateClassifications;
    private static long maxTickPathSearches;
    private static long maxTickCandidateClassifications;
    private static long measuredTicks;

    private PathStats() {
    }

    public static boolean enabled() {
        return PatchConfig.current().instrumentation;
    }

    public static void pathSearchStarted() {
        pathSearches++;
        tickPathSearches++;
    }

    public static void candidateClassified() {
        candidateClassifications++;
        tickCandidateClassifications++;
    }

    public static void landNodeClassified() {
        landNodeClassifications++;
    }

    public static void neighborHazardsChecked() {
        neighborHazardChecks++;
    }

    public static void collisionChecked() {
        collisionChecks++;
    }

    public static void endServerTick() {
        if (!enabled()) {
            tickPathSearches = 0;
            tickCandidateClassifications = 0;
            return;
        }
        measuredTicks++;
        maxTickPathSearches = Math.max(maxTickPathSearches, tickPathSearches);
        maxTickCandidateClassifications = Math.max(maxTickCandidateClassifications, tickCandidateClassifications);
        tickPathSearches = 0;
        tickCandidateClassifications = 0;
    }

    public static synchronized Snapshot snapshot() {
        return new Snapshot(
                pathSearches,
                candidateClassifications,
                landNodeClassifications,
                neighborHazardChecks,
                collisionChecks,
                maxTickPathSearches,
                maxTickCandidateClassifications,
                measuredTicks
        );
    }

    public static synchronized void reset() {
        pathSearches = 0;
        candidateClassifications = 0;
        landNodeClassifications = 0;
        neighborHazardChecks = 0;
        collisionChecks = 0;
        tickPathSearches = 0;
        tickCandidateClassifications = 0;
        maxTickPathSearches = 0;
        maxTickCandidateClassifications = 0;
        measuredTicks = 0;
    }

    public record Snapshot(
            long pathSearches,
            long candidateClassifications,
            long landNodeClassifications,
            long neighborHazardChecks,
            long collisionChecks,
            long maxTickPathSearches,
            long maxTickCandidateClassifications,
            long measuredTicks
    ) {
    }
}
