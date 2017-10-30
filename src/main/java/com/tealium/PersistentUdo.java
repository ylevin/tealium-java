package com.tealium;

import java.io.IOException;

/**
 * Tealium data object for processing generation of standardized universal data
 * points.
 *
 * @author Chad Hartman, Jason Koo, Merritt Tidwell, Karen Tamayo, Chris Anderberg
 */
class PersistentUdo {

    private TextStorage textStorage;

    public PersistentUdo(TextStorage textStorage) {
        this.textStorage = textStorage;
    }

    /**
     * Read serialized Map from storage.
     * 
     * Maps are persisted using percent encoding so as to be dependency
     * Independent; and immune to the vulnerabilities of the native
     * serialization API.
     * 
     * @return The deserialized map.
     * @throws PersistentDataAccessException
     *             If the file doesn't exist or the map is malformed.
     */
    public Udo readOrCreateUdo(Udo defaultUdo) throws UdoSerializationException {
        Udo loadedUdo = null;

        try {
            String loadedText = this.textStorage.readText();
            try {
                loadedUdo = Udo.fromJson(loadedText);
            } catch(UdoDeserializationException e) {
                try {
                    loadedUdo = Udo.fromPercentEncoded(loadedText);
                } catch(UdoDeserializationException e2) {}
            }
        } catch(IOException e) {}

        // if unable to load existing udo, use the default
        if(loadedUdo == null) {
            loadedUdo = defaultUdo;
            this.writeUdo(loadedUdo);
        }

        return loadedUdo;
    }

    /**
     * Serialize Map to storage.
     *
     * Maps are persisted using percent encoding so as to be dependency
     * Independent; and immune to the vulnerabilities of the native
     * serialization API.
     *
     *
     * @param udo
     *            The map to serialize, this will overwrite the existing map.
     * @throws IOException
     *             If ~/.tealium/ does not exist or cannot be created.
     */
    public void writeUdo(Udo udo) throws UdoSerializationException {
        try {
            this.textStorage.writeText(udo.toJson());
        } catch(IOException e) {} // just use udo in memory if can't write
    }

    /**
     * If there is a persistent udo available to read, then return true
     *
     * @return true if exists, false otherwise
     */
    public Boolean exists() {
        return this.textStorage.exists();
    }
}
