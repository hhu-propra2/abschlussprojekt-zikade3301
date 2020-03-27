package mops.module.controllertests;

import static mops.module.controllertests.AuthenticationTokenGenerator.generateAuthenticationToken;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import mops.module.database.Modul;
import mops.module.generator.ModulFaker;
import mops.module.services.ModulService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class SemesterTagControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;


    @MockBean
    ModulService modulServiceMock;



    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    private final String expect = "redirect:/module/modulbeauftragter";




    @Test
    void testSemesterTagAccessForAdministrator() throws Exception {
        SecurityContextHolder
                .getContext()
                .setAuthentication(generateAuthenticationToken("sekretariat"));

        Modul testmodul = ModulFaker.generateFakeModul();
        testmodul.setId((long) 3301);

        mvc.perform(post("/module/semesterTag")
                .param("inputTag", "SoSe2020")
                .param("idVeranstaltung", "1")
                .param("idModul", "3301"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(expect));
    }

    @Test
    void testSemesterTagNoAccessIfNotLoggedIn() throws Exception {

        Modul testmodul = ModulFaker.generateFakeModul();
        testmodul.setId((long) 3301);

        assertThrows(AssertionError.class,
                () -> {
                    mvc.perform(post("/module/semesterTag")
                            .param("inputTag", "SoSe2020")
                            .param("idVeranstaltung", "1")
                            .param("idModul", "3301"))
                            .andExpect(status().is3xxRedirection())
                            .andExpect(view().name(expect));
                });
    }

    @Test
    void testSemesterTagNoAccessForOrganizers() throws Exception {
        SecurityContextHolder
                .getContext()
                .setAuthentication(generateAuthenticationToken("orga"));

        Modul testmodul = ModulFaker.generateFakeModul();
        testmodul.setId((long) 3301);

        assertThrows(AssertionError.class,
                () -> {
                    mvc.perform(post("/module/semesterTag")
                            .param("inputTag", "SoSe2020")
                            .param("idVeranstaltung", "1")
                            .param("idModul", "3301"))
                            .andExpect(status().is3xxRedirection())
                            .andExpect(view().name(expect));
                });
    }

    @Test
    void testSemesterTagNoAccessForStudents() throws Exception {
        SecurityContextHolder
                .getContext()
                .setAuthentication(generateAuthenticationToken("studentin"));

        Modul testmodul = ModulFaker.generateFakeModul();
        testmodul.setId((long) 3301);

        assertThrows(AssertionError.class,
                () -> {
                    mvc.perform(post("/module/semesterTag")
                            .param("inputTag", "SoSe2020")
                            .param("idVeranstaltung", "1")
                            .param("idModul", "3301"))
                            .andExpect(status().is3xxRedirection())
                            .andExpect(view().name(expect));
                });
    }

    @Test
    void testSemesterTagCallsTagVeranstaltungSemester() throws Exception {
        SecurityContextHolder
                .getContext()
                .setAuthentication(generateAuthenticationToken("sekretariat"));

        Modul testmodul = ModulFaker.generateFakeModul();
        testmodul.setId((long) 3301);

        mvc.perform(post("/module/semesterTag")
                .param("inputTag", "SoSe2020")
                .param("idVeranstaltung", "1")
                .param("idModul", "3301"));
        verify(modulServiceMock)
                .tagVeranstaltungSemester(
                        "SoSe2020",
                        Long.parseLong("1"),
                        Long.parseLong("3301")
                );
    }


}