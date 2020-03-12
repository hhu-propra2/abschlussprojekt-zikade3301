package mops.module.controllertests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static mops.module.controllertests.AuthenticationTokenGenerator.generateAuthenticationToken;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
@AutoConfigureMockMvc
class ModulbeauftragterControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(print()).apply(springSecurity()).build();
    }

    final String expect = "modulbeauftragter";

    @Test
    void testModulbeauftragterViewName() throws Exception{
        SecurityContextHolder.getContext().setAuthentication(generateAuthenticationToken( "orga"));

        mvc.perform(get("/module/modulbeauftragter"))
                .andExpect(view().name(expect));
    }

    @Test
    void testModulbeauftragterStatusLoggedIn() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(generateAuthenticationToken( "orga"));

        mvc.perform(get("/module/modulbeauftragter"))
                .andExpect(status().isOk());
    }

    @Test
    void testModulbeauftragterNoAccessIfNotLoggedIn() throws Exception {
        assertThrows(java.lang.AssertionError.class,
                ()->{
                    mvc.perform(get("/module/modulbeauftragter")).andExpect(view().name(expect));
                });
    }

    @Test
    void testModulbeauftragterNoAccessForStudents() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(generateAuthenticationToken( "studentin"));

        assertThrows(java.lang.AssertionError.class,
                ()->{
                    mvc.perform(get("/module/modulbeauftragter")).andExpect(view().name(expect));
                });
    }


//    TODO enable test if Christian Meter created a role for admins
//    @Test
//    void testModulbeauftragterNoAccessForAdministrator() throws Exception {
//    SecurityContextHolder.getContext().setAuthentication(generateAuthenticationToken( "administrator"));

//    assertThrows(java.lang.AssertionError.class,
//                ()->{
//        mvc.perform(get("/module/modulbeauftragter")).andExpect(view().name(expect));
//    });
//    }
}