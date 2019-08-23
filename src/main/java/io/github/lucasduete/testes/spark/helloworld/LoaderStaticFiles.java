package io.github.lucasduete.testes.spark.helloworld;

import static spark.Spark.init;
import static spark.Spark.staticFiles;

public class LoaderStaticFiles {

    public static void main(String[] args) {

        // serving static files
        staticFiles.location("/public");
        staticFiles.externalLocation("/home/lucasduete/IdeaProjects/spark/hello-world/src/main/resources");

        staticFiles.header("Content-Type", "image/png");
        staticFiles.expireTime(120);

        init();



    }

}
