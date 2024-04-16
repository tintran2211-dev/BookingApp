package net.train.Booking.Security;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
//@SecurityScheme(
//        name = "Authorization",
//        in = SecuritySchemeIn.HEADER,
//        type = SecuritySchemeType.APIKEY,
//        bearerFormat = "JWT",
//        description = "Please enter your token with format:\"Bearer YOUR_TOKEN\"",
//        scheme = "bearer"
//)
public class OpenAPIConfig {
    @Value("${openapi.dev-url}")
    private String devUrl;

    @Value("${openapi.prod-url}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("tintran2211@gmail.com");
        contact.setName("TranDinhVan");
        contact.setUrl("https://www.tintran.com");

        License mitLicense = new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("Booking Hotel API")
                .version("v1.0")
                .contact(contact)
                .description("This API demo").termsOfService("https://www.tintran2342.com")
                .license(mitLicense);

        final String securitySchemeName = "Authorization";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new io.swagger.v3.oas.models.security.SecurityScheme()
                                .name(securitySchemeName)
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.APIKEY)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Please enter your token with format:\"Bearer YOUR_TOKEN\"")
                                .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER))).info(info).servers(List.of(devServer, prodServer));
    }
}
