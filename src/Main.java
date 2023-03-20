import io.actions.Action;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.Movie;
import io.users.User;
import utils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(final String[] args) throws IOException {

        /*
          JSON ObjectMapper preparing.
         */
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());

        /*
          Read from JSON.
          java.io.File File = Paths.get("checker/resources/in/basic_10.json").toFile();
         */
        java.io.File myFile = Paths.get(args[0]).toFile();
        JsonNode jsonNode = objectMapper.readTree(myFile);

        // Initialize our data.
        ActiveInfo activeInfo = ActiveInfo.getInstance();
//        ActiveInfo activeInfo = new ActiveInfo();

        String arrayString = jsonNode.get("users").toString();
        activeInfo.setUsers(objectMapper.readValue(arrayString,
                new TypeReference<List<User>>() { }));

        arrayString = jsonNode.get("movies").toString();
        activeInfo.setMovies(objectMapper.readValue(arrayString,
                new TypeReference<List<Movie>>() { }));

        arrayString = jsonNode.get("actions").toString();
        activeInfo.setActions(objectMapper.readValue(arrayString,
                new TypeReference<List<Action>>() { }));

        activeInfo.initialiseDb();

        /*
          Iterating through the actions.
         */
        for (Action action : activeInfo.getActions()) {

            if (!action.getType().equals("change page") && !action.getType().equals("on page")
                    && !action.getType().equals("back") && !action.getType().equals("database")) {
                activeInfo.addErrorOutput();
                break;
            }

            switch (action.getType()) {
                case "change page" -> {
                    String newPage = action.getPage();
                    switch (newPage) {

                        case "login" -> {
                            activeInfo.changePageLogin();
                        }

                        case "register" -> {
                            activeInfo.changePageRegister();
                        }

                        case "homepage autentificat" -> {
                            activeInfo.changePageHomepageAutentificat();
                        }

                        case "upgrades" -> {
                            activeInfo.changePageUpgrades();
                        }

                        case "logout" -> {
                            activeInfo.changePageLogout();
                        }

                        case "movies" -> {
                            activeInfo.changePageMovies();
                        }

                        case "see details" -> {
                            activeInfo.changePageSeeDetails(action);
                        }

                        default -> { }
                    }
                }
                case "on page" -> {
                    String feature = action.getFeature();
                    switch (feature) {

                        case "register" -> {
                            activeInfo.onPageRegister(action);
                        }

                        case "login" -> {
                            activeInfo.onPageLogin(action);
                        }

                        case "search" -> {
                            activeInfo.onPageSearch(action);
                        }

                        case "filter" -> {
                            activeInfo.onPageFilter(action);
                        }

                        case "buy tokens" -> {
                            activeInfo.onPageBuyTokens(action);
                        }

                        case "buy premium account" -> {
                            activeInfo.onPageBuyPremiumAccount();
                        }

                        case "purchase" -> {
                            activeInfo.onPagePurchase(action);
                        }

                        case "watch" -> {
                            activeInfo.onPageWatch(action);
                        }

                        case "like" -> {
                            activeInfo.onPageLike(action);
                        }

                        case "rate" -> {
                            activeInfo.onPageRate(action);
                        }

                        case "subscribe" -> {
                            activeInfo.onPageSubscribe(action);
                        }

                        default -> { }
                    }
                }
                case "back" -> {
                    activeInfo.back(action);
                }
                case "database" -> {
                    String feature = action.getFeature();
                    switch (feature) {
                        case "add" -> {
                            activeInfo.databaseAdd(action);
                        }

                        case "delete" -> {
                            activeInfo.databaseDelete(action);
                        }

                        default -> { }
                    }
                }
                default -> { }
            }
        }

        /*
          Write to JSON.
          java.io.File resultFile = Paths.get("redundantFiles/outputExample.json").toFile();
          objectWriter.writeValue(resultFile, outputs);
         */
        objectWriter.writeValue(new File(args[1]), activeInfo.getOutputs());

    }

}
