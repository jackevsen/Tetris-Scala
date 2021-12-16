# "Tetris"

Game "Tetris", written in Scala, using Indigo framework https://indigoengine.io

**Setup**

To run the app locally you need electron installed globally

```npm install -g electron```

If you have this error

```TypeError: app.whenReady is not a function```

You need to update electron to newest version.

Indigo setup and configuration page: 
https://indigoengine.io/docs/quickstart/setup-and-configuration

To run the project, do the following from your command line:

**Commands:**

```sbt runGame``` - to run the game

```sbt buildGame``` - to build a project

**Сontrol:**
* _Arrow Right_ - Move right
* _Arrow Left_ - Move left
* _Arrow Down_ - Speed up moving down
* _Arrow Up_ - Rotate
* _P_ - pause


**For developement:**

1. Install a server with ```npm install -g http-server```.
2. Navigate to the output directory shown after running the indigo plugin.
3. Run ```http-server -c-1``` - which means "serve this directory as a static site with no caching".
4. Go to http://127.0.0.1:8080/ (or whatever http-server says in it output) and marvel at your creation..
