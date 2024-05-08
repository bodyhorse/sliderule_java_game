package Interfaces;

/**
 * A sör és rongy tárgyak közös tulajdonsága, hogy aktiválás után egy adott számú körig érvényes a hatásuk.
 * Ennek az interfésznek a megvalósításával tudjuk ezt a folyamatot jól nyilvántartani és kezelni.
 */
public interface Decaying {
    /**
     *  Ez a függvény a tárgy korának állításáért felel.
     */
    void age();
}
