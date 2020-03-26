package mops.module.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mops.module.database.Modul;
import mops.module.database.Veranstaltung;
import mops.module.database.Veranstaltungsform;
import mops.module.generator.ModulFaker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class PdfServiceTest {

    @Autowired
    private PdfService pdfService;

    @Test
    public void generatePdfModulhandbuchTest() throws IOException {
        List<Modul> module = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Modul modul = ModulFaker.generateFakeModul();
            modul.setId((long) i);
            module.add(modul);
        }
        ByteArrayOutputStream pdfDocument = pdfService.generatePdf(module);
        assertThat(pdfDocument.toByteArray().length).isGreaterThan(0);
        pdfDocument.close();
    }

    @Test
    public void markdownToHtmlTest() {
        String basisText = "Dieser Text sollte eine Überschrift sein!";
        String markdown = "# " + basisText;
        String html = HtmlService.markdownToHtml(markdown);
        assertThat(html).contains("<h1>" + basisText + "</h1>");
    }

    @Test
    public void pdfVeranstaltungWrapperTest() {
        Veranstaltungsform veranstaltungsform1 = new Veranstaltungsform();
        veranstaltungsform1.setForm("Vorlesung Betriebssysteme");
        veranstaltungsform1.setSemesterWochenStunden(4);

        Veranstaltungsform veranstaltungsform2 = new Veranstaltungsform();
        veranstaltungsform2.setForm("Übung");
        veranstaltungsform2.setSemesterWochenStunden(0);

        Veranstaltung veranstaltung = new Veranstaltung();
        veranstaltung.setVeranstaltungsformen(
                new HashSet<>(Arrays.asList(veranstaltungsform1, veranstaltungsform2)));

        PdfVeranstaltungWrapper veranstaltungWrapper = new PdfVeranstaltungWrapper(veranstaltung);
        Set<String> lehrveranstaltungen = veranstaltungWrapper.getLehrveranstaltungen();

        String expected1 = veranstaltungsform1.getForm() + ", "
                + veranstaltungsform1.getSemesterWochenStunden() + " SWS";
        String expected2 = veranstaltungsform2.getForm();

        assertThat(lehrveranstaltungen).contains(expected1, expected2);
    }
}