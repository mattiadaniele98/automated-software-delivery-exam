package it.scuola.materie_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MaterieServiceApplicationTests {				//Test di default che Initializr genera.

	@Test
	void contextLoads() {
	}

}
/**
 * Molto probabilmente fallisce in CI (senza Postgres attivo) perché @SpringBootTest tenta di connettersi al database reale.
 * Per farlo funzionare senza database si userebbe H2 in-memory nel profilo test.
 */