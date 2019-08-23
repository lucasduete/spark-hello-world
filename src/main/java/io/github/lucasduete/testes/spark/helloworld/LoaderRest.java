package io.github.lucasduete.testes.spark.helloworld;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Redirect;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static spark.Spark.*;

public class LoaderRest {

    private static final Logger log = LoggerFactory.getLogger(LoaderRest.class);

    public static void main(String[] args) {
        // post
        post("/", (request, response) -> "tá nessa é");

        // get
        get("/", (request, response) -> "HU3HU3HU3HU33HU3HU3U3");
        get("/hello", (req, res) -> "Hello World");
        get("/hello/:name", (request, response) -> {
            return "Hello " + request.params(":name");
        });
        get("/say/*/to/*", ((request, response) -> {
            return "Number of splat parameters: "
                    + request.splat().length
                    + "</br>"
                    + " Param 1: "
                    + request.splat()[0]
                    + "</br>"
                    + " Param 2: "
                    + request.splat()[1];
        }));

        // function
        get("/stop", (req, res) -> {
            stop();
            return null;
        });


        // path grops
        ArrayList<String> emails = new ArrayList();
        emails.add("a@a.com");
        emails.add("b@b.com");

        ArrayList<String> usernames = new ArrayList<>();
        usernames.add("a");
        usernames.add("b");

        path("/api", () -> {
            before("*", ((request, response) -> {
                log.info("Received request to API");
            }));
            path("/email", () -> {
                get("/listall", (request, response) -> emails);
                post("/add", (request, response) -> {
                    emails.add(request.body());
                    return "added " + request.body();
                });
                delete("/delete/:email", (request, response) -> {
                    emails.remove(request.params(":email"));
                    return "removed " + request.params(":email");
                });
            });
            path("/username", () -> {
                get("/listall", (request, response) -> usernames);
                post("/add", (request, response) -> {
                    usernames.add(request.body());
                    return "added " + request.body();
                });
                delete("/delete", (request, response) -> {
                    usernames.remove(request.body());
                    return "removed " + request.body();
                });
            });
            path("/user", () -> {
                post("/add", (request, response) -> {
                    String usernameToAdd = request.queryMap().get("username").value();
                    String emailToAdd = request.queryMap().get("mail").value();

                    usernames.add(usernameToAdd);
                    emails.add(emailToAdd);

                    return "added username: " + usernameToAdd + " :: mail: " + emailToAdd;
                });
            });
        });

        // halt + filters
        path("/authentication", () -> {
            before("/*", (request, response) -> {
                if ((request.headers("Authorization") == null) ||
                        (!request.headers("Authorization").contains("Bearer "))) {

                    halt(401, "Invalid Token");
                }
            });
            post("/login", (request, response) -> {
                return "Sucessful " + request.body();
            });
            after("/*", (request, response) -> {
                response.status(200);
            });
            afterAfter("/*", (request, response) -> {
                response.type("text/plain");
            });
        });

        // redirects
        get("/redirectBrowser", (request, response) -> {
            response.redirect("https://www.google.com.br", 301);
            return "";
        });
        redirect.get("/listAllUsernames", "/api/username/listall");
        redirect.get("/listAllEmails", "/api/email/listall", Redirect.Status.SEE_OTHER);

        // custom not found (404)
        notFound((request, response) -> {
            response.type("application/json");
            return "{\"message\":\"Custom 404 error\"}";
        });

        // custom internal server error (500)
        get("/internalServerError", (request, response) -> {
            String value = null;
            return value.toUpperCase();
        });
        internalServerError((request, response) -> {
            response.type("application/json");
            return "{\"message\":\"Custom 500 handling\"}";
        });

        // exception handling
        get("/throwException", (request, response) -> {
            throw new CustomException("Custom Exception");
        });

        exception(CustomException.class, (exception, request, response) -> {
            log.error(exception.getMessage());
            log.info(LocalDateTime.now().toString());
            response.status(500);
            response.type("application/json");
            response.body("{\"error\":\"" + exception.getMessage() + "\"}");
        });

        get("/json", (request, response) -> {
            response.type("application/json");
            return User.builder()
                    .username("lucasduete")
                    .email("lucas@mail.com")
                    .build();
        }, new JsonTransformer());

        Gson gson = new Gson();
        get("/jsonLambda", (request, response) -> {
            response.type("application/json");
            return User.builder()
                    .username("lucasduete")
                    .email("lucas@mail.com")
                    .build();
        }, gson::toJson);

    }
}