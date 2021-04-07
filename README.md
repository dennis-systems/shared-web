You can find the documentation in the `docs` directory, click [here](./docs/README.md) to open the main overview.

Some of the documentation contains diagrams which are expressed as PlantUML files. To view them you can use one of these two approaches:

- use a plugin for your IDE
- display the `.png` files that where created from the `.puml` files

:information_source: If you see that a puml file is newer than the corresponding png file please run this command to create an updated png:

```
docker run --name pummel -v %CD%:/src -d plantuml/plantuml-server:tomcat
docker exec pummel java -jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/plantuml-1.2020.21.jar /src/*.puml
docker rm -f pummel
```

(This refers to information also stored in https://confluence.allianz.de/x/DJcLEQ)