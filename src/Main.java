import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());

        JsonNode jsonNode = objectMapper.readTree(new File("input/example.json"));

        String arrayString = jsonNode.get("users").toString();
        List<User> users = objectMapper.readValue(arrayString, new TypeReference<List<User>>() {});

        arrayString = jsonNode.get("movies").toString();
        List<Movie> movies = objectMapper.readValue(arrayString, new TypeReference<List<Movie>>() {});

        arrayString = jsonNode.get("actions").toString();
        List<Object> actions = objectMapper.readValue(arrayString, new TypeReference<List<Object>>() {});

//        Write to JSON
        java.io.File resultFile = Paths.get("myUsers.json").toFile();
        objectWriter.writeValue(resultFile, actions);
    }
}
