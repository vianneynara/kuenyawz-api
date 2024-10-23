package dev.realtards.wzsnacknbites.testBases;


import dev.realtards.wzsnacknbites.controllers.AccountController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
@AutoConfigureDataJpa
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseWebMvcTest {

	@Autowired
	protected MockMvc mockMvc;
}
