# "Tetris"

Game "Tetris", written in Scala, using Indigo framework https://indigoengine.io

To run the project, do the following from your command line:

**Commands:**

```bash sbt runGame``` - to run the game

```bash sbt buildGame``` - to build a project

**Сontrol:**
* _Arrow Right_ - Move right
* _Arrow Left_ - Move left
* _Arrow Down_ - Speed up moving down
* _Arrow Up_ - Rotate
* _P_ - pause


**For developement:**

1. Install with npm install -g http-server.
2. Navigate to the output directory shown after running the indigo plugin.
3. Run ```bashhttp-server -c-1``` - which means "serve this directory as a static site with no caching".
4. Go to http://127.0.0.1:8080/ (or whatever http-server says in it output) and marvel at your creation..
