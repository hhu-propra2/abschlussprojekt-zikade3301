package mops.module.wrapper;

import static mops.module.wrapper.PdfModulWrapper.getSafeString;
import static mops.module.wrapper.PdfModulWrapper.safeAppend;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mops.module.database.Veranstaltung;
import mops.module.database.Veranstaltungsform;
import mops.module.database.Zusatzfeld;
import mops.module.services.HtmlService;

/**
 * Formatiert die Daten aus dem Veranstaltungsobjekt passend für das PDF-Template.
 */
@RequiredArgsConstructor
public class PdfVeranstaltungWrapper {

    private final Veranstaltung veranstaltung;

    /**
     * Formatiert "Lehrveranstaltungs"-Strings, die aufgezählt dargestellt werden sollen.
     *
     * @return Menge von Lehrveranstaltungs-Strings
     */
    public Set<String> getLehrveranstaltungenEnumeration() {
        Set<String> lehrveranstaltungen = new HashSet<>();

        Set<Veranstaltungsform> veranstaltungsformEnumeration = veranstaltung
                .getVeranstaltungsformen()
                .stream()
                .filter(v -> v.getSemesterWochenStunden() > 0)
                .collect(Collectors.toSet());
        for (Veranstaltungsform veranstaltungsform : veranstaltungsformEnumeration) {
            String veranstaltungsformString = "";
            veranstaltungsformString = safeAppend(veranstaltungsformString,
                    veranstaltungsform.getForm());

            veranstaltungsformString += ", " + veranstaltungsform.getSemesterWochenStunden()
                    + " SWS";
            lehrveranstaltungen.add(veranstaltungsformString);
        }
        return lehrveranstaltungen;
    }

    /**
     * Formatiert den Freitext-Teil des Lehrveranstaltungsabschnitts mit Markdown-Support.
     * Wenn das Feld komplett leer ist, wird ein Spiegelstrich zurückgegeben.
     * @return Formatierter String
     */
    public Set<String> getLehrveranstaltungenFreeText() {
        if (veranstaltung.getVeranstaltungsformen().isEmpty()) {
            return new HashSet<>(Arrays.asList("—"));
        }

        Set<String> veranstaltungsformFreeText = veranstaltung
                .getVeranstaltungsformen()
                .stream()
                .filter(v -> v.getSemesterWochenStunden() == 0)
                .map(v -> HtmlService.markdownToHtml(v.getForm()))
                .collect(Collectors.toSet());

        return veranstaltungsformFreeText;
    }

    public String getTitel() {
        return getSafeString(
                veranstaltung.getTitel());
    }

    public String getLeistungspunkte() {
        return getSafeString(
                veranstaltung.getLeistungspunkte());
    }

    public String getInhalte() {
        return HtmlService.markdownToHtml(getSafeString(
                veranstaltung.getBeschreibung().getInhalte()));
    }

    public String getLernergebnisse() {
        return HtmlService.markdownToHtml(getSafeString(
                veranstaltung.getBeschreibung().getLernergebnisse()));
    }

    public String getLiteratur() {
        return HtmlService.markdownToHtml(getSafeString(
                veranstaltung.getBeschreibung().getLiteratur()));
    }

    public String getVerwendbarkeit() {
        return HtmlService.markdownToHtml(getSafeString(
                veranstaltung.getBeschreibung().getVerwendbarkeit()));
    }

    public String getTeilnahmevoraussetzungen() {
        return HtmlService.markdownToHtml(getSafeString(
                veranstaltung.getVoraussetzungenTeilnahme()));
    }

    public String getVoraussetzungenBestehen() {
        return HtmlService.markdownToHtml(getSafeString(
                veranstaltung.getBeschreibung().getVoraussetzungenBestehen()));
    }

    public String getHaeufigkeit() {
        return getSafeString(
                veranstaltung.getBeschreibung().getHaeufigkeit());
    }

    /**
     * Gibt mit Markdown formatierte Zusatzfelder zurück.
     *
     * @return Set von Zusatzfeldern
     */
    public Set<Zusatzfeld> getZusatzfelder() {
        return veranstaltung.getZusatzfelder()
                .stream()
                .map(zusatzfeld -> zusatzfeldEnableMarkdown(zusatzfeld))
                .collect(Collectors.toSet());
    }

    private static Zusatzfeld zusatzfeldEnableMarkdown(Zusatzfeld zusatzfeld) {
        Zusatzfeld neuesZusatzfeld = new Zusatzfeld();
        neuesZusatzfeld.setTitel(zusatzfeld.getTitel());
        neuesZusatzfeld.setInhalt(HtmlService.markdownToHtml(zusatzfeld.getInhalt()));
        return neuesZusatzfeld;
    }
}
