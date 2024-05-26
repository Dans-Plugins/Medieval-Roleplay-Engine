docker build . -t wp-dev-container
docker run -it -v $(pwd)/../:/workspaces/Medieval-Roleplay-Engine wp-dev-container /bin/bash
