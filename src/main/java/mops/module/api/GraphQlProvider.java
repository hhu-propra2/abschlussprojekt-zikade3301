package mops.module.api;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static java.nio.charset.StandardCharsets.UTF_8;

import graphql.GraphQL;
import graphql.schema.Coercing;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mops.module.database.Modulkategorie;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

@Component
@RequiredArgsConstructor
public class GraphQlProvider {

    private GraphQL graphQL;
    private final GraphQlDataFetchers graphQlDataFetchers;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    /**
     * Liest die Schemata ein.
     *
     */
    @PostConstruct
    public void init() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:schema.graphqls");
        GraphQLSchema graphQlSchema = buildSchema(resourceToString(resource));
        this.graphQL = GraphQL.newGraphQL(graphQlSchema).build();
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("modulById",
                                graphQlDataFetchers.getModulByIdDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("allModule",
                                graphQlDataFetchers.getAllModuleDataFetcher()))
                .scalar(localDateTime)
                .scalar(modulkategorie)
                .build();
    }

    public static final GraphQLScalarType localDateTime = new GraphQLScalarType(
            "LocalDateTime", "A custom scalar that handles Java's LocalDateTime",
            new Coercing<Object, Object>() {
                @Override
                public Object serialize(Object dataFetcherResult) {
                    if (dataFetcherResult instanceof LocalDateTime) {
                        return ((LocalDateTime) dataFetcherResult).toString();
                    } else {
                        throw new CoercingSerializeException(
                                "Parameter kann nicht als LocalDateTime geparsed werden!");
                    }
                }

                @Override
                public Object parseValue(Object input) {
                    return LocalDateTime.parse(input.toString());
                }

                @Override
                public Object parseLiteral(Object input) {
                    return LocalDateTime.parse(input.toString());
                }
            });

    public static final GraphQLScalarType modulkategorie = new GraphQLScalarType(
            "Modulkategorie",
            "A custom scalar that handles the Modulkategorie enum in our Modul object",
            new Coercing<Object, Object>() {
                @Override
                public Object serialize(Object dataFetcherResult) {
                    if (dataFetcherResult instanceof Modulkategorie) {
                        return dataFetcherResult.toString();
                    } else {
                        throw new CoercingSerializeException(
                                "Parameter kann nicht als Modulkategorie geparsed werden!");
                    }
                }

                @Override
                public Object parseValue(Object input) {
                    return Modulkategorie.valueOf(input.toString());
                }

                @Override
                public Object parseLiteral(Object input) {
                    return Modulkategorie.valueOf(input.toString());
                }
            });

    /**
     * Wandelt eine eingelesene UTF-8-codierte Resource in einen String um.
     *
     * @param resource Spring Resource
     * @return String
     */
    public static String resourceToString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
