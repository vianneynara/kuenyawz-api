package dev.kons.kuenyawz.testBases;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;

@AutoConfigureDataJpa
@ExtendWith(MockitoExtension.class)
public abstract class BaseWebMvcTest {
}
