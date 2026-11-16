package com.skl.powertok.mcc.managers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The licence guard shared by every PowerTok game. A game calls {@link #checkAccess()} first thing in its
 * {@code onEnable}; when it returns false, the game has already been disabled and the server keeps running without it.
 *
 * <p>Access comes from Streamix. When Streamix starts the server, it asks the Streamix backend for a <em>game
 * ticket</em> and passes it to the server process in the {@value #TICKET_VARIABLE} environment variable. The ticket
 * is signed with the backend's ECDSA P-256 private key; this class only holds the public key, so it needs no secret and
 * no network. A game is allowed when:
 * <ul>
 *   <li>the signature is valid;</li>
 *   <li>the ticket has not expired (it lives one hour, and is only checked when a game starts);</li>
 *   <li>the ticket was issued for this very computer (the fingerprint is recomputed here, never read from a file);</li>
 *   <li>the ticket opens every game ({@code "all": true}, the Everything plan) or lists this one in {@code games}.</li>
 * </ul>
 *
 * <p>Ticket format: {@code <payload>.<signature>}, both base64url without padding. The signature is ECDSA P-256 with
 * SHA-256 over the ASCII bytes of {@code <payload>}, in IEEE P1363 form (r then s, 32 bytes each — what WebCrypto and
 * .NET produce). The payload is JSON:
 * {@code {"v":1,"hwid":"<32 hex>","all":true,"games":["powerraids"],"iat":<seconds>,"exp":<seconds>}}.
 * The game id is the plugin name in lower case ({@code PowerRaids} → {@code powerraids}). The full contract lives in
 * the Streamix repository, {@code docs/licence-jeux.md}.
 */
public class AccessManager {

    /** The environment variable Streamix sets on the server process. */
    public static final String TICKET_VARIABLE = "POWERTOK_TICKET";

    /** Ticket format version understood by this guard. */
    private static final int TICKET_VERSION = 1;

    /** Public half of the Streamix game-ticket signing key (ECDSA P-256, X.509 SubjectPublicKeyInfo, base64). */
    private static final String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE+95c+pMzKS23vSF/lOKY0QseCuL9Ff2KdFd9mJ6fiDO11N69ew/Bt5VtCHCg0SGLGtxKgTZWpAj6VzwGH3uWBw==";

    /** Same salt as Streamix ({@code src/main/services/licence/device.ts}): both sides must compute the same fingerprint. */
    private static final String DEVICE_SALT = "minetok-device-v1";

    /** Tolerated clock difference between this computer and the backend. */
    private static final long CLOCK_SKEW_SECONDS = 300;

    private static final Pattern MACHINE_GUID = Pattern.compile("MachineGuid\\s+REG_SZ\\s+([0-9a-fA-F-]+)");

    /** The fingerprint never changes while the server runs: computed once for every game. */
    private static String cachedFingerprint;

    private final JavaPlugin plugin;

    public AccessManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Boolean checkAccess() {

        String game = plugin.getName().toLowerCase(Locale.ROOT);

        try {

            String ticket = System.getenv(TICKET_VARIABLE);

            if(ticket == null || ticket.isBlank()) {
                return(deny("Aucun ticket Streamix pour " + plugin.getName() + " : lance le serveur depuis Streamix, connecté à Internet"));
            }

            String[] parts = ticket.trim().split("\\.");

            if(parts.length != 2) {
                return(deny("Ticket Streamix mal formé"));
            }

            Signature verifier = Signature.getInstance("SHA256withECDSAinP1363Format");
            verifier.initVerify(publicKey());
            verifier.update(parts[0].getBytes(StandardCharsets.US_ASCII));

            if(!verifier.verify(Base64.getUrlDecoder().decode(parts[1]))) {
                return(deny("Ticket Streamix invalide (signature)"));
            }

            String json = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            JsonObject payload = JsonParser.parseString(json).getAsJsonObject();

            if(!payload.has("v") || payload.get("v").getAsInt() != TICKET_VERSION) {
                return(deny("Version de ticket Streamix inconnue : mets PowerTok MCC à jour"));
            }

            long now = Instant.now().getEpochSecond();

            if(!payload.has("exp") || payload.get("exp").getAsLong() < now) {
                return(deny("Ticket Streamix expiré : relance le serveur depuis Streamix"));
            }

            if(!payload.has("iat") || payload.get("iat").getAsLong() > now + CLOCK_SKEW_SECONDS) {
                return(deny("Ticket Streamix daté du futur : vérifie l'heure de ton PC"));
            }

            String fingerprint = fingerprint();

            if(fingerprint == null) {
                return(deny("Identification de ce PC impossible"));
            }

            if(!payload.has("hwid") || !fingerprint.equals(payload.get("hwid").getAsString())) {
                return(deny("Ce ticket Streamix a été délivré pour un autre PC"));
            }

            if(opens(payload, game)) {
                return(true);
            }

            return(deny(plugin.getName() + " n'est pas inclus dans ta formule Streamix"));

        }

        catch(Exception e) {
            Bukkit.getLogger().severe("[MCC Guard] Vérification impossible : " + e.getMessage());
            return(deny("Ticket Streamix illisible"));
        }

    }

    /** {@code "all": true} opens every game, present and future; otherwise the game must be listed in {@code games}. */
    private static boolean opens(JsonObject payload, String game) {

        if(payload.has("all") && payload.get("all").getAsBoolean()) {
            return(true);
        }

        if(!payload.has("games") || !payload.get("games").isJsonArray()) {
            return(false);
        }

        for(JsonElement entry : payload.getAsJsonArray("games")) {
            if(game.equals(entry.getAsString())) {
                return(true);
            }
        }

        return(false);

    }

    private Boolean deny(String reason) {
        Bukkit.getLogger().severe("[MCC Guard] " + reason);
        Bukkit.getPluginManager().disablePlugin(plugin);
        return(false);
    }

    private static PublicKey publicKey() throws GeneralSecurityException {
        byte[] encoded = Base64.getDecoder().decode(PUBLIC_KEY);
        return(KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(encoded)));
    }

    /**
     * The computer fingerprint, exactly as Streamix computes it: SHA-256 of {@code "minetok-device-v1:" + MachineGuid},
     * in lower-case hex, first 32 characters. Windows only (Streamix is a Windows app); null when it cannot be read.
     */
    private static synchronized String fingerprint() {

        if(cachedFingerprint != null) {
            return(cachedFingerprint);
        }

        String guid = machineGuid();

        if(guid == null) {
            return(null);
        }

        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest((DEVICE_SALT + ":" + guid).getBytes(StandardCharsets.UTF_8));
            cachedFingerprint = HexFormat.of().formatHex(digest).substring(0, 32);
            return(cachedFingerprint);
        }

        catch(GeneralSecurityException e) {
            return(null);
        }

    }

    private static String machineGuid() {

        if(!System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")) {
            return(null);
        }

        try {

            Process process = new ProcessBuilder("reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid")
                .redirectErrorStream(true)
                .start();

            String output;

            try(InputStream stream = process.getInputStream()) {
                output = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }

            if(!process.waitFor(8, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return(null);
            }

            Matcher found = MACHINE_GUID.matcher(output);
            return(found.find() ? found.group(1) : null);

        }

        catch(IOException e) {
            return(null);
        }

        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            return(null);
        }

    }

}
