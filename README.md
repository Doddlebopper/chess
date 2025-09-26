This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
Sequence Diagram : https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo7vvorxLCaRLhhaHIwCAqQQBABh2gK2hCiKxGjDA5wwGB7yhlRbqdlh5QWOijrOgSuEsuUnp6oRr4oJRTJCZGtE8h0zCKRJiZSYJMl0bqmSxv6Jkhq60pqeU0YwGZ2g6dhyZQSWCLQpQKE8pg-4gjBJRXAMnFjhOXzTrOzbjq2fTflePmFNkPYwP2g69AFI6jAuk59KFjbhcFBxmJwq7eH4gReCg6B7gevjMMe6SZJg8UXkU1DXtIACiu5tfUbXNC0D6qE+3TZXO7a-mcQIAVlQZhfOKyQRNY0iTACH2NVAbTTlaCYXK2F6SpBkcCg3CZDN611ptylmhGhSWjIR0UoYw3oJoOido1YAKkqKrLkV66BJCtq7tCMAAOKjqytWng157ML514g11vX2KOQ0bSNP5st5-lPWgEzzambJLcgsRg6MqhnTOm3RbBGrSaSMDkmAJkUzNl3UVZMAGNw4AwIpMAAGbeMM3Fo89xjCTt5TaVhtP6fTxNgKTahEWlSkCft5oc1zKA836hjI6MACS0izCemQGncsw6AgoANhbJEM6OAByo4C0LMB647XF+nIMCbGA9FqB7qSGHzaQUiHsBm4Y0BCjbIANi9EuweUFQ9AbKDG6bdX6oFVvx3bedeygLujHsjQOZ2WOVOno5ZzA0f22+ce203KCzBnpcoOXXnObFzV+TXGf143RfW63Red6OPcY0mcUw1WGeqOMQ91ybDc523+cT6rHfO9PFc-Z4xUbtgPhQNg3DwEZhhKykOfQzksOXi1aY3g0SMo8EovPr0U9l6NTGfdsY-z6CsMBTx-5jDmr3BahNJaGS9JkJWLNNp71GF3baNMcJy3dAzCkzMcZs1UjdWi2seZ80FhAYWOMk5zwHniKWgVK44I1nguSyDRywmHuvUeu8W4Jy3sXTB6sro0VkjfYuvMBoOEQXqJWDpxb0PelcHhS5CrHz+gESwR0ELJBgAAKQgDyUGrsAjjwTo-cw8DX6pyqJSO8LQM6o3OnOIcl9gA6KgHACACEoDoMztIamxwnILRAa49AEwIFgOeDbLxPi-FRJmH0HheNYEE18uCGAAArYxaAUE43fHElACToABONlgp0ukZB0zwYzQhP9iGWVIeUchWlApu2oSLCJaA6GZPlNI1WLC9pXXworLhPCmmaxafg5gGcG4ZFSJzV2PC+kvyqXBYuwyam4PKLknkKCeHZ1PDAWOFiGyDKAqMKZ112RS2wFoTIwd9au0oWcgupyo4PyUacFRpZ8pHzXCVAIXhPFdi9LAYA2BL6EHiIke+UN3o2MHhUdqnVuq9WMIA0JqZZLcDwLCbE3lkVZJAPiuElTJI7LYXiiFhLRHsxmYdY6ocOiqACayHiOMNA-Pnk-K46iVxAA
