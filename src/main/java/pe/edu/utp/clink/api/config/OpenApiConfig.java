package pe.edu.utp.clink.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI clinkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CLINK API")
                        .description("""
                                API REST para la gestión de enlaces acortados con enfoque en marketing digital.
                                
                                **Funcionalidades:**
                                - Registro y autenticación con JWT
                                - Creación de enlaces con alias personalizado
                                - Estadísticas de clics por dispositivo y fuente
                                - Etiquetas y campañas (Premium)
                                - Generación de códigos QR
                                - Exportación de reportes a Excel (Premium)
                                - Administración de usuarios (Admin)
                                
                                **Autenticación:** usa el endpoint `/api/v1/auth/login` para obtener el token,
                                luego haz clic en el botón **Authorize** e ingresa `Bearer <token>`.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo CLINK - UTP")
                                .email("carlos@clink.pe"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa el token JWT obtenido en /api/v1/auth/login")));
    }
}